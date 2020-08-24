package net.bdew.planters.actions;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.villages.VillageRole;
import net.bdew.planters.*;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;

public class ChopPerformer implements ActionPerformer {
    @Override
    public short getActionId() {
        return Actions.CHOP;
    }

    public static boolean canUse(Creature performer, Item source, Item target) {
        return performer.isPlayer()
                && PlanterItem.isPlanter(target)
                && PlanterItem.isTreeOrBushPlanter(target)
                && target.getAuxData() != 0
                && target.getParentId() == -10L
                && (source.getTemplateId() == ItemList.hatchet)
                && Utils.checkRoleAllows(performer, target, VillageRole::mayChopDownAllTrees);
    }


    public static boolean actionStart(Creature performer, Item source, Item target) {
        Plantable crop = PlanterItem.getPlantable(target);
        if (crop == null) return true;
        String what = crop.planterType == PlanterType.TREE ? "tree" : "bush";
        performer.getCommunicator().sendNormalServerMessage(String.format("You start to chop down the %s in the planter.", what));
        Server.getInstance().broadCastAction(String.format("%s starts to chop down the %s in the planter.", performer.getName(), what), performer, 5);
        performer.getStatus().modifyStamina(-1000f);
        performer.playAnimation("farm", false);
        return true;
    }

    public static boolean actionEnd(Creature performer, Item source, Item target, byte rarity) {
        Plantable crop = PlanterItem.getPlantable(target);
        if (crop == null) return true;
        String what = crop.planterType == PlanterType.TREE ? "tree" : "bush";

        Methods.sendSound(performer, String.format("sound.work.woodcutting%d", Server.rand.nextInt(3) + 1));

        Skill cutting = performer.getSkills().getSkillOrLearn(SkillList.WOODCUTTING);
        double power = cutting.skillCheck(15, source, 0, false, 10);
        float ql = (float) Math.max(1, Math.min(100.0f, power));

        if (rarity > 0) performer.playPersonalSound("sound.fx.drumroll");

        try {
            Item harvested = ItemFactory.createItem(ItemList.scrapwood, ql, crop.material, rarity, null);
            harvested.setWeight((1 + PlanterItem.getGrowthStage(target)) * (crop.planterType == PlanterType.TREE ? 2000 : 500), true);
            performer.getInventory().insertItem(harvested);
        } catch (FailedException | NoSuchTemplateException e) {
            PlantersMod.logException("Error creating scrap", e);
        }

        performer.getCommunicator().sendNormalServerMessage(String.format("You chop down the %s.", what));
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
            int time = Actions.getQuickActionTime(performer, performer.getSkills().getSkillOrLearn(SkillList.WOODCUTTING), null, 0.0);
            performer.sendActionControl("cutting down", true, time);
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
