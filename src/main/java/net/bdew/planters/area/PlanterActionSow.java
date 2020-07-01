package net.bdew.planters.area;

import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.villages.VillageRole;
import net.bdew.planters.Plantable;
import net.bdew.planters.PlanterItem;
import net.bdew.planters.PlanterType;
import net.bdew.planters.actions.SowPerformer;

public class PlanterActionSow extends BasePlanterAction {

    private Item findSeed(Item container, PlanterType type) {
        for (Item item : container.getAllItems(true)) {
            if (Plantable.findSeed(item.getTemplateId(), type) != null) {
                return item;
            } else if (item.isHollow()) {
                Item found = findSeed(item, type);
                if (found != null) return found;
            }
        }
        return null;
    }

    @Override
    boolean checkRole(VillageRole role) {
        return role.maySowFields();
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

        if (findSeed(source, PlanterItem.getPlanterType(target.getTemplateId())) == null) {
            if (sendMsg)
                performer.getCommunicator().sendNormalServerMessage(String.format("You decide to skip the %s as you have nothing to plant there.", target.getName().toLowerCase()));
            return false;
        }

        return true;
    }

    @Override
    public float getActionTime(Creature performer, Item source, Item target) {
        return Actions.getQuickActionTime(performer, performer.getSkills().getSkillOrLearn(SkillList.FARMING), null, 0.0);
    }

    @Override
    public boolean actionStarted(Creature performer, Item source, Item target) {
        Item seed = findSeed(source, PlanterItem.getPlanterType(target.getTemplateId()));
        return seed != null && SowPerformer.actionStart(performer, seed, target);
    }

    @Override
    public boolean actionCompleted(Creature performer, Item source, Item target, byte rarity) {
        Item seed = findSeed(source, PlanterItem.getPlanterType(target.getTemplateId()));
        return seed != null && SowPerformer.actionEnd(performer, seed, target, rarity);
    }
}
