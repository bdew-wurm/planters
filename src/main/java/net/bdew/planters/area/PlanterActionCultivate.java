package net.bdew.planters.area;

import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.villages.VillageRole;
import net.bdew.planters.actions.CultivatePerformer;

public class PlanterActionCultivate extends BasePlanterAction {
    public PlanterActionCultivate(boolean isForTreesAndBushes) {
        super(isForTreesAndBushes);
    }

    @Override
    boolean checkRole(VillageRole role, Item target) {
        if (isForTreesAndBushes)
            return role.mayChopDownAllTrees();
        else
            return role.mayCultivate();
    }

    @Override
    public boolean canStartOn(Creature performer, Item source, Item target) {
        return super.canStartOn(performer, source, target) && source != null
                && (source.getTemplateId() == ItemList.rake || source.getTemplateId() == ItemList.shovel);
    }

    @Override
    public boolean canActOn(Creature performer, Item source, Item target, boolean sendMsg) {
        if (!super.canActOn(performer, source, target, sendMsg)) return false;
        if (target.getAuxData() == 0) {
            if (sendMsg) {
                performer.getCommunicator().sendNormalServerMessage(String.format("You skip the %s as nothing is growing there.", target.getName().toLowerCase()));
            }
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
        return CultivatePerformer.actionStart(performer, source, target);
    }

    @Override
    public boolean actionCompleted(Creature performer, Item source, Item target, byte rarity) {
        return CultivatePerformer.actionEnd(performer, source, target, rarity);
    }
}
