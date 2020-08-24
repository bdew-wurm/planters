package net.bdew.planters;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.RuneUtilities;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.villages.Villages;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Utils {
    public static boolean checkRoleAllows(Creature performer, Item target, Predicate<VillageRole> check) {
        if (performer.getPower() >= 2) return true;
        Village village = Villages.getVillage(target.getTileX(), target.getTileY(), target.isOnSurface());
        if (village == null) return true;
        VillageRole role = village.getRoleFor(performer);
        return role != null && check.test(role);
    }

    public static Item getExistingProduce(Item container, int template) {
        for (Item item : container.getAllItems(true)) {
            if (item.getTemplateId() == template && item.getAuxData() == 0 && item.getRarity() == 0)
                return item;
        }
        return null;
    }

    public static void addStackedItems(Item container, int template, float ql, float amount, String name) {
        Item existing = getExistingProduce(container, template);
        if (existing != null) {
            int addWeight = (int) (amount * existing.getTemplate().getWeightGrams());
            int sumWeight = existing.getWeightGrams() + addWeight;
            float sumQl = (existing.getQualityLevel() * existing.getWeightGrams() / sumWeight) + (ql * addWeight / sumWeight);
            existing.setWeight(sumWeight, false);
            existing.setQualityLevel(sumQl);
            if (!existing.getName().contains("pile of"))
                existing.setName("pile of " + name);
            existing.sendUpdate();
        } else {
            final Item result;
            try {
                result = ItemFactory.createItem(template, ql, null);
                if (amount > 1) {
                    result.setWeight((int) (result.getTemplate().getWeightGrams() * amount), false);
                    result.setName("pile of " + name);
                }
                container.insertItem(result, true, false);
            } catch (FailedException | NoSuchTemplateException e) {
                PlantersMod.logException("Error creating stacked item", e);
            }
        }
    }

    public static void sendPlanterTree(Player player, Item item, Plantable plant) {
        if (player.hasLink()) {
            try {
                final ByteBuffer bb = player.getCommunicator().getConnection().getBuffer();

                int growthStage = PlanterItem.getGrowthStage(item);

                float zOffs = 0.4f;
                float zBase = 0.9f;
                float scale = 1;

                if (plant.planterType == PlanterType.BUSH) {
                    zOffs = 0.6f;
                    scale = 0.7f;
                }

                if (growthStage == 0) scale *= 0.5f;
                if (growthStage == 1) scale *= 0.75f;
                if (growthStage > 2) scale *= (growthStage + 3) / 4f;

                if (plant == Plantable.ThornBush) {
                    scale *= 0.15f;
                    zOffs += 0.2f;
                    zBase += 0.3f;
                }
                if (plant == Plantable.LavenderBush) {
                    scale *= 0.4f;
                    zOffs = 0.4f;
                    zBase += 0.2f;
                }
                if (plant == Plantable.GrapeBush) {
                    scale *= 0.5f;
                    zBase += 0.2f;
                }
                if (plant == Plantable.RoseBush) scale *= 0.8f;
                if (plant == Plantable.CamelliaBush) scale *= 0.6f;
                if (plant == Plantable.HazelnutBush) scale *= 0.6f;
                if (plant == Plantable.RaspberryBush) {
                    zOffs -= 0.3f;
                }
                if (plant == Plantable.OleanderBush) {
                    scale *= 0.7f;
                    zOffs -= 0.2f;
                }
                if (plant == Plantable.BlueberryBush) {
                    scale *= 0.7f;
                    zOffs -= 0.1f;
                }
                if (plant == Plantable.LingonberryBush) {
                    scale *= 0.4f;
                    zBase += 0.28f;
                    zOffs -= 0.6f;
                }
                if (plant == Plantable.OakTree && growthStage > 2) {
                    scale *= 0.6f;
                    zOffs -= 0.5f;
                }
                if (plant == Plantable.WillowTree && growthStage > 2) {
                    scale *= 1.3f;
                    zBase += 0.5f;
                }
                if (plant == Plantable.FirTree && growthStage > 2) scale *= 1.5f;
                if (plant == Plantable.OliveTree && growthStage > 2) scale *= 1.5f;
                if (plant == Plantable.LemonTree && growthStage > 2) scale *= 2f;
                if (plant == Plantable.CedarTree && growthStage > 2) {
                    scale *= 0.9f;
                }
                if (plant == Plantable.AppleTree && growthStage > 2) {
                    scale *= 1.5f;
                    zBase += 0.75f;
                }
                if (plant == Plantable.OrangeTree && growthStage > 2) scale *= 2f;
                if (plant == Plantable.OliveTree && growthStage > 2) {
                    zBase += 0.5f;
                    scale *= 0.8f;
                }
                if (plant == Plantable.PineTree && growthStage > 2) {
                    zBase += 0.7f;
                    scale *= 0.8f;
                }
                if (plant == Plantable.BirchTree && growthStage > 2) zBase -= 0.4f;

                bb.put((byte) (-9));

                bb.putLong(item.getWurmId() + 8);
                bb.putFloat(0); //x
                bb.putFloat(0); //y
                bb.putFloat(0); //r
                bb.putFloat(zBase + zOffs * scale); //z

                byte[] tempStringArr = String.format("planted %s %s [%d - %.1f]", plant.displayName, plant.planterType == PlanterType.BUSH ? "bush" : "tree", growthStage, scale)
                        .getBytes(StandardCharsets.UTF_8);
                bb.put((byte) tempStringArr.length);
                bb.put(tempStringArr);

                tempStringArr = "".getBytes(StandardCharsets.UTF_8);
                bb.put((byte) tempStringArr.length);
                bb.put(tempStringArr);


                tempStringArr = String.format("%s%s", plant.modelName, growthStage < 3 ? "young." : "").getBytes(StandardCharsets.UTF_8);
                bb.put((byte) tempStringArr.length);
                bb.put(tempStringArr);

                bb.put((byte) (item.isOnSurface() ? 0 : -1));

                bb.put((byte) 0);

                tempStringArr = "".getBytes(StandardCharsets.UTF_8);
                bb.put((byte) tempStringArr.length);
                bb.put(tempStringArr);

                bb.putShort(item.getImageNumber());

                bb.put((byte) 0);

                bb.putFloat(scale * 5);
                bb.putLong(item.onBridge());
                bb.put(item.getRarity());

                bb.put((byte) 2);
                bb.putLong(item.getWurmId());

                bb.put((byte) 0);

                player.getCommunicator().getConnection().flush();
            } catch (Exception ex) {
                PlantersMod.logException(String.format("Failed to send item %s (%d) to player %s (%d)", player.getName(), player.getWurmId(), item.getName(), item.getWurmId()), ex);
                player.setLink(false);
            }
        }
    }

    public static void removePlanterTree(Player player, Item item) {
        if (player != null && player.hasLink()) {
            try {
                ByteBuffer bb = player.getCommunicator().getConnection().getBuffer();
                bb.put((byte) 10);
                bb.putLong(item.getWurmId() + 8);
                player.getCommunicator().getConnection().flush();
            } catch (Exception ex) {
                PlantersMod.logException(String.format("Failed to send remove item %s (%d) to player %s (%d)", player.getName(), player.getWurmId(), item.getName(), item.getWurmId()), ex);
                player.setLink(false);
            }
        }
    }

    public static boolean checkFillBucket(Creature performer, Item bucket, int fillTemplate, int fillAmount, boolean sendMessage) {
        if (fillAmount > performer.getCarryingCapacityLeft()) {
            if (sendMessage)
                performer.getCommunicator().sendNormalServerMessage("You stop harvesting as you wouldn't be able to carry all the liquid.");
            return false;
        }

        if (fillAmount > bucket.getFreeVolume()) {
            if (sendMessage)
                performer.getCommunicator().sendNormalServerMessage("You stop harvesting as your bucket is too full.");
            return false;
        }

        for (Item check : bucket.getItems()) {
            if (check.getTemplateId() != fillTemplate) {
                if (sendMessage)
                    performer.getCommunicator().sendNormalServerMessage("You stop harvesting as your bucket contains something else.");
                return false;
            }
        }

        return true;
    }

    public static void fillBucket(Creature performer, Item bucket, int fillTemplate, int fillAmount, float ql, byte rarity) throws NoSuchTemplateException, FailedException {
        Methods.sendSound(performer, "sound.liquid.fillcontainer.bucket");
        for (Item existing : bucket.getItems()) {
            if (existing.getTemplateId() == fillTemplate) {
                int sumWeight = existing.getWeightGrams() + fillAmount;
                float sumQl = (existing.getQualityLevel() * existing.getWeightGrams() / sumWeight) + (ql * fillAmount / sumWeight);
                existing.setWeight(sumWeight, true);
                existing.setQualityLevel(sumQl);
                if (existing.getRarity() > rarity)
                    existing.setRarity(rarity);
                return;
            }
        }
        final Item harvested = ItemFactory.createItem(fillTemplate, ql, rarity, null);
        harvested.setWeight(fillAmount, true);
        bucket.insertItem(harvested);
    }

    public static int maxTreeBushHarvest(Item planter, double skill, Item tool) {
        int bonus = 0;
        if (tool.getSpellEffects() != null) {
            final float extraChance = tool.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_FARMYIELD) - 1.0f;
            if (extraChance > 0.0f && Server.rand.nextFloat() < extraChance) {
                ++bonus;
            }
        }
        return Math.min(PlanterItem.getGrowthStage(planter) - 1, (int) (skill + 28.0) / 27 + bonus);
    }

    public static class ItemTypeSet {
        private Set<Short> values;

        public ItemTypeSet(Set<Short> values) {
            this.values = values;
        }

        public ItemTypeSet with(Short... vals) {
            HashSet<Short> copy = new HashSet<>(values);
            copy.addAll(Arrays.asList(vals));
            return new ItemTypeSet(copy);
        }

        public short[] array() {
            short[] res = new short[values.size()];
            int p = 0;
            for (Short v : values)
                res[p++] = v;
            return res;
        }

        public static ItemTypeSet from(Short... vals) {
            HashSet<Short> copy = new HashSet<>(Arrays.asList(vals));
            return new ItemTypeSet(copy);
        }

        public static ItemTypeSet merge(ItemTypeSet... sets) {
            return new ItemTypeSet(Arrays.stream(sets).flatMap(s -> s.values.stream()).collect(Collectors.toSet()));
        }
    }
}
