package net.bdew.planters;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.villages.Villages;

import java.util.function.Predicate;

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
}
