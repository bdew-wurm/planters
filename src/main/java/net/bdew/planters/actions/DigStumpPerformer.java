package net.bdew.planters.actions;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.Items;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.villages.VillageRole;
import net.bdew.planters.MiscItems;
import net.bdew.planters.PlantersMod;
import net.bdew.planters.Utils;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;

public class DigStumpPerformer implements ActionPerformer {
    @Override
    public short getActionId() {
        return Actions.DIG;
    }

    public static boolean canUse(Creature performer, Item source, Item target) {
        return performer.isPlayer()
                && target.getTemplateId() == ItemList.treeStump
                && source.getTemplateId() == ItemList.shovel
                && target.getParentOrNull() == null
                && Utils.checkRoleAllows(performer, target, VillageRole::mayTerraform);
    }

    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
        if (target.getTemplateId() != ItemList.treeStump || source.getTemplateId() != ItemList.shovel)
            return propagate(action, ActionPropagation.ACTION_PERFORMER_PROPAGATION, ActionPropagation.SERVER_PROPAGATION);
        if (!canUse(performer, source, target))
            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION, ActionPropagation.NO_SERVER_PROPAGATION);
        if (!performer.canCarry(target.getWeightGrams() / 4)) {
            performer.getCommunicator().sendAlertServerMessage("The stump is too heavy for you to carry.");
            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION, ActionPropagation.NO_SERVER_PROPAGATION);
        }

        if (counter == 1f) {
            Server.getInstance().broadCastAction(performer.getName() + " starts digging up a stump.", performer, 5);
            performer.getCommunicator().sendNormalServerMessage("You start digging up the stump.");
            int time = Actions.getSlowActionTime(performer, performer.getSkills().getSkillOrLearn(SkillList.WOODCUTTING), null, 0.0);
            performer.sendActionControl("digging", true, time);
            source.setDamage(source.getDamage() + 0.0005f * source.getDamageModifier());
            performer.getStatus().modifyStamina(-1500f);
            action.setTimeLeft(time);
        } else {
            if (action.justTickedSecond()) {
                source.setDamage(source.getDamage() + 0.0003f * source.getDamageModifier());
                performer.getStatus().modifyStamina(-2000f);
                if (action.mayPlaySound())
                    Methods.sendSound(performer, String.format("sound.work.digging%d", Server.rand.nextInt(3) + 1));
            }
            if (counter * 10f > action.getTimeLeft()) {
                try {
                    Item stump = ItemFactory.createItem(MiscItems.stumpId, target.getQualityLevel(), target.getMaterial(), target.getRarity(), performer.getName());
                    stump.setAuxData(target.getAuxData());
                    stump.setWeight(target.getWeightGrams() / 4, false);
                    if (!performer.getInventory().insertItem(stump, true)) {
                        performer.getCommunicator().sendNormalServerMessage("You are unable to lift the stump and decide to leave it in the ground.");
                    } else {
                        Items.destroyItem(target.getWurmId());
                        performer.getCommunicator().sendNormalServerMessage("You free the stump from the ground and take it.");
                    }
                } catch (FailedException | NoSuchTemplateException e) {
                    performer.getCommunicator().sendAlertServerMessage("Something seems to be wrong with the world and the stump refuses to budge.");
                    PlantersMod.logException("Error creating stump", e);
                }

                return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            }
        }

        return propagate(action, ActionPropagation.CONTINUE_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

    }
}
