package net.bdew.planters.actions;

import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.villages.Villages;
import net.bdew.planters.PlanterItem;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;

public class CultivatePerformer implements ActionPerformer {
    @Override
    public short getActionId() {
        return Actions.CULTIVATE;
    }

    public static boolean canUse(Creature performer, Item source, Item target) {
        if (target.getTemplateId() == PlanterItem.id && target.getAuxData() != 0 && target.getParentId() == -10L &&
                (source.getTemplateId() == ItemList.shovel || source.getTemplateId() == ItemList.rake)) {
            Village village = Villages.getVillage(target.getTileX(), target.getTileY(), target.isOnSurface());
            if (village == null) return true;
            VillageRole role = village.getRoleFor(performer);
            return role != null && role.mayFarm();
        } else return false;
    }

    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
        if (!canUse(performer, source, target))
            if (target.getTemplateId() == PlanterItem.id)
                return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            else
                return propagate(action, ActionPropagation.SERVER_PROPAGATION, ActionPropagation.ACTION_PERFORMER_PROPAGATION);

        if (counter == 1.0f) {
            performer.getCommunicator().sendNormalServerMessage("You start to cultivate the planter.");
            Server.getInstance().broadCastAction(performer.getName() + " starts to cultivate the planter.", performer, 5);
            int time = Actions.getQuickActionTime(performer, performer.getSkills().getSkillOrLearn(SkillList.FARMING), null, 0.0);
            performer.sendActionControl("cultivating", true, time);
            action.setTimeLeft(time);
        } else {
            if (counter * 10.0f > action.getTimeLeft()) {
                performer.getCommunicator().sendNormalServerMessage("The planter is cultivated and ready to sow now.");
                PlanterItem.clearData(target);
                return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            }
        }

        return propagate(action, ActionPropagation.CONTINUE_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }

}
