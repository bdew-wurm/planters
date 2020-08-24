package net.bdew.planters.area;

import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.villages.VillageRole;
import net.bdew.planters.Plantable;
import net.bdew.planters.PlanterItem;
import net.bdew.planters.PlanterType;
import net.bdew.planters.actions.PlantPerformer;
import net.bdew.wurm.betterfarm.api.ActionEntryOverride;

public class PlanterActionPlant extends BasePlanterAction {
    public PlanterActionPlant() {
        super(true);
    }

    private final ActionEntryOverride override = new ActionEntryOverride(Actions.PLANT, "Plant", "planting", null);

    @Override
    public ActionEntryOverride getOverride(Creature performer, Item source, Item target) {
        return override;
    }

    private Item findSprout(Item container, PlanterType type) {
        for (Item item : container.getAllItems(true)) {
            if (item.getTemplateId() == ItemList.sprout) {
                Plantable crop = Plantable.getFromMaterial(item.getMaterial());
                if (crop != null && crop.planterType == type)
                    return item;
            } else if (item.isHollow()) {
                Item found = findSprout(item, type);
                if (found != null) return found;
            }
        }
        return null;
    }

    @Override
    public boolean checkSkill(Creature performer, float needed) {
        return performer.getSkills().getSkillOrLearn(SkillList.GARDENING).getRealKnowledge() >= needed;
    }

    @Override
    boolean checkRole(VillageRole role, Item target) {
        return role.mayPlantSprouts();
    }

    @Override
    public boolean canStartOn(Creature performer, Item source, Item target) {
        return super.canStartOn(performer, source, target) &&
                source != null && source.isHollow();
    }

    @Override
    public boolean canActOn(Creature performer, Item source, Item target, boolean sendMsg) {
        if (!super.canActOn(performer, source, target, sendMsg)) return false;

        if (target.getAuxData() != 0) {
            if (sendMsg)
                performer.getCommunicator().sendNormalServerMessage(String.format("You decide to skip the %s as something already grows in it.", target.getName().toLowerCase()));
            return false;
        }

        if (findSprout(source, PlanterItem.getPlanterType(target.getTemplateId())) == null) {
            if (sendMsg)
                performer.getCommunicator().sendNormalServerMessage(String.format("You decide to skip the %s as you have nothing to plant there.", target.getName().toLowerCase()));
            return false;
        }

        return true;
    }

    @Override
    public float getActionTime(Creature performer, Item source, Item target) {
        return Actions.getQuickActionTime(performer, performer.getSkills().getSkillOrLearn(SkillList.GARDENING), null, 0.0);
    }

    @Override
    public boolean actionStarted(Creature performer, Item source, Item target) {
        Item sprout = findSprout(source, PlanterItem.getPlanterType(target.getTemplateId()));
        return sprout != null && PlantPerformer.actionStart(performer, sprout, target);
    }

    @Override
    public boolean actionCompleted(Creature performer, Item source, Item target, byte rarity) {
        Item spro = findSprout(source, PlanterItem.getPlanterType(target.getTemplateId()));
        return spro != null && PlantPerformer.actionEnd(performer, spro, target, rarity);
    }
}
