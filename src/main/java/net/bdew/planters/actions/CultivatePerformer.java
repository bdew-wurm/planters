package net.bdew.planters.actions;

import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.villages.VillageRole;
import net.bdew.planters.PlanterItem;
import net.bdew.planters.PlanterTracker;
import net.bdew.planters.Utils;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;

public class CultivatePerformer implements ActionPerformer {
    @Override
    public short getActionId() {
        return Actions.CULTIVATE;
    }

    public static boolean canUse(Creature performer, Item source, Item target) {
        return performer.isPlayer()
                && PlanterItem.isPlanter(target)
                && target.getAuxData() != 0
                && target.getParentId() == -10L
                && (source.getTemplateId() == ItemList.shovel || source.getTemplateId() == ItemList.rake)
                && Utils.checkRoleAllows(performer, target, VillageRole::mayCultivate);
    }


    public static boolean actionStart(Creature performer, Item source, Item target) {
        performer.getCommunicator().sendNormalServerMessage("You start to cultivate the planter.");
        Server.getInstance().broadCastAction(performer.getName() + " starts to cultivate the planter.", performer, 5);
        performer.getStatus().modifyStamina(-1000f);
        performer.playAnimation("farm", false);
        return true;
    }

    public static boolean actionEnd(Creature performer, Item source, Item target, byte rarity) {
        Methods.sendSound(performer, String.format("sound.work.digging%d", Server.rand.nextInt(3) + 1));

        Skill digging = performer.getSkills().getSkillOrLearn(SkillList.DIGGING);
        digging.skillCheck(14.0D, source, 0.0D, false, 10);

        performer.getCommunicator().sendNormalServerMessage("The planter is cultivated and ready to sow now.");
        PlanterItem.clearData(target);
        PlanterTracker.removePlanter(target);

        if (source.setDamage(source.getDamage() + 0.0015F * source.getDamageModifier())) {
            performer.getCommunicator().sendNormalServerMessage(String.format("Your %s broke!", source.getName().toLowerCase()));
            return false;
        }

        return true;
    }


    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
        if (!canUse(performer, source, target))
            if (PlanterItem.isPlanter(target))
                return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            else
                return propagate(action, ActionPropagation.SERVER_PROPAGATION, ActionPropagation.ACTION_PERFORMER_PROPAGATION);

        if (counter == 1f) {
            int time = Actions.getQuickActionTime(performer, performer.getSkills().getSkillOrLearn(SkillList.FARMING), null, 0.0);
            performer.sendActionControl("cultivating", true, time);
            action.setTimeLeft(time);
            actionStart(performer, source, target);
        } else {
            if (counter * 10f > action.getTimeLeft()) {
                actionEnd(performer, source, target, action.getRarity());
                return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            }
        }

        return propagate(action, ActionPropagation.CONTINUE_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }
}
