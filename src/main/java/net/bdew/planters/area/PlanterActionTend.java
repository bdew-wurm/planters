package net.bdew.planters.area;

import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.villages.VillageRole;
import net.bdew.planters.PlanterItem;
import net.bdew.planters.actions.TendPerformer;

public class PlanterActionTend extends BasePlanterAction {
    public PlanterActionTend() {
        super(false);
    }

    @Override
    boolean checkRole(VillageRole role, Item target) {
        return role.mayTendFields();
    }

    @Override
    public float getActionTime(Creature performer, Item source, Item target) {
        return Actions.getQuickActionTime(performer, performer.getSkills().getSkillOrLearn(SkillList.FARMING), null, 0.0);
    }

    @Override
    public boolean canStartOn(Creature performer, Item source, Item target) {
        return super.canStartOn(performer, source, target) && source != null && source.getTemplateId() == ItemList.rake;
    }

    @Override
    public boolean canActOn(Creature performer, Item source, Item target, boolean sendMsg) {
        if (!super.canActOn(performer, source, target, sendMsg)) return false;
        if (PlanterItem.isTreeOrBushPlanter(target)) return false;
        if (target.getAuxData() == 0) {
            if (sendMsg) {
                performer.getCommunicator().sendNormalServerMessage(String.format("You skip the %s as nothing is growing there.", target.getName().toLowerCase()));
            }
            return false;
        }
        if (PlanterItem.getGrowthStage(target) == 0) {
            if (sendMsg) {
                performer.getCommunicator().sendNormalServerMessage(String.format("You skip the %s, as it's too young.", target.getName().toLowerCase()));
            }
            return false;
        }
        if (PlanterItem.getGrowthStage(target) == 6) {
            if (sendMsg) {
                performer.getCommunicator().sendNormalServerMessage(String.format("You skip the weeds in the %s, they are far past saving.", target.getName().toLowerCase()));
            }
            return false;
        }
        if (PlanterItem.isTended(target)) {
            if (sendMsg) {
                performer.getCommunicator().sendNormalServerMessage(String.format("You skip the %s as it's already tended.", target.getName().toLowerCase()));
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean actionStarted(Creature performer, Item source, Item target) {
        return TendPerformer.actionStart(performer, source, target);
    }

    @Override
    public boolean actionCompleted(Creature performer, Item source, Item target, byte rarity) {
        return TendPerformer.actionEnd(performer, source, target, rarity);
    }
}
