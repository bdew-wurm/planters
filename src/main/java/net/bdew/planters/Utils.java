package net.bdew.planters;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.NoSuchTemplateException;
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

                bb.put((byte) (-9));

                bb.putLong(item.getWurmId() + 8);
                bb.putFloat(0); //x
                bb.putFloat(0); //y
                bb.putFloat(0); //r
                bb.putFloat(0.5f); //z

                int growthStage = PlanterItem.getGrowthStage(item);
                float size = 5;
                if (growthStage == 0) size *= 0.5f;
                if (growthStage > 1) size *= (growthStage + 5) / 8f;

                if (plant == Plantable.ThornBush) size *= 0.5f;
                if (plant == Plantable.OakTree) size *= 0.7f;

                byte[] tempStringArr = String.format("planted %s %s [%d - %.1f]", plant.displayName, plant.planterType == PlanterType.BUSH ? "bush" : "tree", growthStage, size)
                        .getBytes(StandardCharsets.UTF_8);
                bb.put((byte) tempStringArr.length);
                bb.put(tempStringArr);

                tempStringArr = "".getBytes(StandardCharsets.UTF_8);
                bb.put((byte) tempStringArr.length);
                bb.put(tempStringArr);


                tempStringArr = String.format("%s%s", plant.modelName, growthStage < 2 ? "young." : "").getBytes(StandardCharsets.UTF_8);
                bb.put((byte) tempStringArr.length);
                bb.put(tempStringArr);

                bb.put((byte) (item.isOnSurface() ? 0 : -1));

                bb.put((byte) 0);

                tempStringArr = "".getBytes(StandardCharsets.UTF_8);
                bb.put((byte) tempStringArr.length);
                bb.put(tempStringArr);

                bb.putShort(item.getImageNumber());

                bb.put((byte) 0);

                bb.putFloat(size);
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
                bb.putLong(item.getWurmId() + 1);
                player.getCommunicator().getConnection().flush();
            } catch (Exception ex) {
                PlantersMod.logException(String.format("Failed to send remove item %s (%d) to player %s (%d)", player.getName(), player.getWurmId(), item.getName(), item.getWurmId()), ex);
                player.setLink(false);
            }
        }
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
