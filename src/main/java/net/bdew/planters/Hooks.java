package net.bdew.planters;

import com.wurmonline.server.*;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.sounds.SoundPlayer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Hooks {
    static class DelayedEffect {
        public final long playerId;
        public int delay;
        public final TickingPlayerEffect eff;

        public DelayedEffect(long playerId, int delay, TickingPlayerEffect eff) {
            this.playerId = playerId;
            this.delay = delay;
            this.eff = eff;
        }
    }

    private static final HashSet<DelayedEffect> effects = new HashSet<>();
    private static long lastPolledPlanters = System.currentTimeMillis();

    public static void addItemLoading(Item item) {
        if (PlanterItem.isPlanter(item) && PlanterItem.needsPolling(item)) {
            PlanterTracker.addPlanter(item);
        }
    }

    public static void serverTick() {
        if (!effects.isEmpty()) pollEffects();
        pollPlanters();
    }

    private static void pollEffects() {
        int count = effects.size();
        long start = System.currentTimeMillis();
        Iterator<DelayedEffect> it = effects.iterator();
        while (it.hasNext()) {
            DelayedEffect ef = it.next();
            if (--ef.delay <= 0) {
                try {
                    Player p = Players.getInstance().getPlayer(ef.playerId);
                    Optional<Integer> res = ef.eff.apply(p);
                    if (res.isPresent()) {
                        ef.delay = res.get();
                    } else {
                        it.remove();
                    }
                } catch (NoSuchPlayerException e) {
                    it.remove();
                } catch (Throwable t) {
                    PlantersMod.logException("Error in effect", t);
                    it.remove();
                }
            }
        }
        PlantersMod.logInfo(String.format("Polled %d effects, took %dms, remaining %d", count, System.currentTimeMillis() - start, effects.size()));
    }

    private static void pollPlanters() {
        long start = System.currentTimeMillis();

        if (start - lastPolledPlanters < Servers.localServer.getFieldGrowthTime())
            return;

        lastPolledPlanters = start;

        List<Item> planters = PlanterTracker.getPlanters();
        if (planters.isEmpty()) return;

        for (Item planter : planters) {
            Plantable crop = PlanterItem.getPlantable(planter);
            if (crop == null) {
                PlanterTracker.removePlanter(planter);
                continue;
            }

            if (crop.planterType == PlanterType.BUSH || crop.planterType == PlanterType.TREE) {
                int stage = PlanterItem.getGrowthStage(planter);
                int substage = PlanterItem.getTreeSubstage(planter);
                boolean harvestable = PlanterItem.isTreeHarvestable(planter);
                boolean sprouting = PlanterItem.isTreeSprouting(planter);

                if (substage >= PlantersMod.treeGrowthTicks) {
                    if (stage < 5) stage++;
                    substage = 0;
                    harvestable = stage > 2;
                    sprouting = stage > 3;
                } else {
                    substage += 1;
                }

                PlanterItem.updateTreeData(planter, crop, stage, substage, harvestable, sprouting);
            } else {
                int stage = PlanterItem.getGrowthStage(planter);
                int tendPower = PlanterItem.getFarmTendPower(planter);
                int tendCount = PlanterItem.getFarmTendCount(planter);

                if (stage == 5 && PlantersMod.canWilt && Server.rand.nextFloat() < 0.5f) stage = 6;
                if (stage < 5) stage++;

                if (crop.planterType == PlanterType.MAGIC && !PlanterItem.isTended(planter)) {
                    if (Server.rand.nextFloat() < PlantersMod.magicUntendedDeathChance) {
                        stage = 6;
                    }
                }

                PlanterItem.updateData(planter, crop, stage, stage >= 5, tendCount, tendPower);
            }

            if (!PlanterItem.needsPolling(planter)) PlanterTracker.removePlanter(planter);

            if (WurmCalendar.isNight())
                SoundPlayer.playSound("sound.ambient.night.crickets", planter.getTileX(), planter.getTileY(), planter.isOnSurface(), 0f);
            else
                SoundPlayer.playSound("sound.birdsong.bird2", planter.getTileX(), planter.getTileY(), planter.isOnSurface(), 0f);
        }

        PlantersMod.logInfo(String.format("Polled %d planters, took %dms, remaining %d", planters.size(), System.currentTimeMillis() - start, PlanterTracker.trackedCount()));
    }

    public static void sendItemHook(Communicator comm, Item item) {
        if (item.getTemplateId() == MiscItems.magicShroomId || item.getTemplateId() == MiscItems.basketMagicId || (PlanterItem.getPlanterType(item.getTemplateId()) == PlanterType.MAGIC &&
                item.getAuxData() != 0 && PlanterItem.getGrowthStage(item) < 6)) {
            comm.sendRemoveEffect(item.getWurmId());
            if (item.getTemplateId() == MiscItems.magicShroomId) {
                comm.sendAttachEffect(item.getWurmId(), (byte) 0, (byte) 1, (byte) 128, (byte) 128, (byte) 100);
            } else {
                if (PlanterItem.isPlanter(item) && PlanterItem.isInfected(item))
                    comm.sendAttachEffect(item.getWurmId(), (byte) 0, (byte) 255, (byte) 1, (byte) 1, (byte) 255);
                else
                    comm.sendAttachEffect(item.getWurmId(), (byte) 0, (byte) 1, (byte) 255, (byte) 255, (byte) 255);
            }
        }
        if (PlanterItem.needsTopItem(item))
            Utils.sendPlanterTree(comm.getPlayer(), item, PlanterItem.getPlantable(item));
    }

    public static void removeItemHook(Communicator comm, Item item) {
        if (item.getTemplateId() == MiscItems.magicShroomId || item.getTemplateId() == MiscItems.basketMagicId || PlanterItem.getPlanterType(item.getTemplateId()) == PlanterType.MAGIC) {
            comm.sendRemoveEffect(item.getWurmId());
        }
        if (PlanterItem.needsTopItem(item))
            Utils.removePlanterTree(comm.getPlayer(), item);
    }

    public static void addTickingEffect(long wurmId, int delay, TickingPlayerEffect eff) {
        effects.add(new DelayedEffect(wurmId, delay, eff));
    }

    public static boolean isPlanterTopItem(long id) {
        try {
            Item actual = Items.getItem(id - 8);
            return PlanterItem.isPlanter(actual);
        } catch (NoSuchItemException e) {
            return false;
        }
    }
}
