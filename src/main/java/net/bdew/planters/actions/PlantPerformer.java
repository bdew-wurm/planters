package net.bdew.planters.actions;

import com.wurmonline.server.Items;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.players.AchievementList;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.villages.VillageRole;
import net.bdew.planters.Plantable;
import net.bdew.planters.PlanterItem;
import net.bdew.planters.PlanterTracker;
import net.bdew.planters.Utils;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;

public class PlantPerformer implements ActionPerformer {
    @Override
    public short getActionId() {
        return Actions.PLANT;
    }

    public static boolean canUse(Creature performer, Item source, Item target) {
        if (performer.isPlayer() && PlanterItem.isPlanter(target) && target.getAuxData() == 0 && target.getParentId() == -10L && source.getTemplateId() == ItemList.sprout) {
            Plantable crop = Plantable.getFromMaterial(source.getMaterial());
            if (crop == null || crop.planterType != PlanterItem.getPlanterType(target.getTemplateId())) return false;
            return Utils.checkRoleAllows(performer, target, VillageRole::mayPlantSprouts);
        } else return false;
    }

    public static boolean actionStart(Creature performer, Item source, Item target) {
        Plantable crop = Plantable.getFromMaterial(source.getMaterial());
        if (crop == null || crop.planterType != PlanterItem.getPlanterType(target.getTemplateId())) return false;

        performer.getCommunicator().sendNormalServerMessage("You start planting the sprout.");
        Server.getInstance().broadCastAction(performer.getName() + " starts to plant a sprout.", performer, 5);

        performer.playAnimation("drop", false);

        return true;
    }

    public static boolean actionEnd(Creature performer, Item source, Item target, byte rarity) {
        Plantable crop = Plantable.getFromMaterial(source.getMaterial());
        if (crop == null || crop.planterType != PlanterItem.getPlanterType(target.getTemplateId())) return false;

        if (rarity != 0) performer.playPersonalSound("sound.fx.drumroll");

        Skill gardening = performer.getSkills().getSkillOrLearn(SkillList.GARDENING);
        gardening.skillCheck(1.0f + source.getDamage(), source.getCurrentQualityLevel(), false, 10);

        SoundPlayer.playSound("sound.forest.branchsnap", target.getTileX(), target.getTileY(), target.isOnSurface(), 0.0f);

        PlanterItem.updateTreeData(target, crop, 0, 0, false, false);

        performer.achievement(AchievementList.ACH_PLANTING);
        performer.getStatus().modifyStamina(-1000.0f);
        performer.getCommunicator().sendNormalServerMessage("You plant the sprout.");
        Server.getInstance().broadCastAction(performer.getName() + " plants a sprout.", performer, 5);
        Items.destroyItem(source.getWurmId());

        PlanterTracker.addPlanter(target);
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
            if (!actionStart(performer, source, target))
                return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            int time = Actions.getQuickActionTime(performer, performer.getSkills().getSkillOrLearn(SkillList.GARDENING), null, 0.0);
            performer.sendActionControl("planting", true, time);
            action.setTimeLeft(time);
        } else {
            if (counter * 10f > action.getTimeLeft()) {
                actionEnd(performer, source, target, action.getRarity());
                return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            }
        }

        return propagate(action, ActionPropagation.CONTINUE_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }
}
