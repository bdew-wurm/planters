package net.bdew.planters.actions;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.villages.VillageRole;
import net.bdew.planters.*;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;

public class PickSproutPerformer implements ActionPerformer {
    @Override
    public short getActionId() {
        return Actions.PICKSPROUT;
    }

    public static boolean canUse(Creature performer, Item source, Item target) {
        if (performer.isPlayer() && PlanterItem.isPlanter(target) && target.getAuxData() != 0 && target.getParentId() == -10L) {
            Plantable crop = PlanterItem.getPlantable(target);
            if (crop == null || (crop.planterType != PlanterType.BUSH && crop.planterType != PlanterType.TREE))
                return false;
            if (!PlanterItem.isTreeSprouting(target)) return false;
            if (source == null || source.getTemplateId() != ItemList.sickle) return false;
            return Utils.checkRoleAllows(performer, target, VillageRole::mayPickSprouts);
        } else return false;
    }

    @Override
    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        return this.action(action, performer, null, target, num, counter);
    }

    public static boolean actionStart(Creature performer, Item source, Item target) {
        Server.getInstance().broadCastAction(performer.getName() + " starts picking sprouts the planter.", performer, 5);
        performer.getCommunicator().sendNormalServerMessage("You start picking sprouts the planter.");
        performer.playAnimation("take", false);
        performer.getStatus().modifyStamina(-1000f);
        return true;
    }

    public static boolean actionEnd(Creature performer, Item source, Item target, byte rarity) {
        Plantable crop = Plantable.getFromId(target.getAuxData());
        if (crop == null || (crop.planterType != PlanterType.BUSH && crop.planterType != PlanterType.TREE))
            return false;

        if (!PlanterItem.isTreeSprouting(target)) return true;

        Skill forestry = performer.getSkills().getSkillOrLearn(SkillList.FORESTRY);
        Skill sickle = performer.getSkills().getSkillOrLearn(SkillList.SICKLE);

        ItemTemplate tpl;

        try {
            tpl = ItemTemplateFactory.getInstance().getTemplate(ItemList.sprout);
        } catch (NoSuchTemplateException e) {
            performer.getCommunicator().sendNormalServerMessage(String.format("You fail to harvest the %s. You realize something is wrong with the world.", target.getName().toLowerCase()));
            PlantersMod.logException("Error getting sprout template", e);
            return false;
        }

        if (!performer.canCarry(tpl.getWeightGrams()) || !performer.getInventory().mayCreatureInsertItem()) {
            performer.getCommunicator().sendNormalServerMessage("You would not be able to carry the sprouts. You need to drop some things first.");
            return false;
        }

        double bonus = Math.max(1.0, sickle.skillCheck(1.0, source, 0.0, false, 10));
        double power = forestry.skillCheck(1.0, source, bonus, false, 10);

        try {
            float modifier = 1.0f;
            if (source.getSpellEffects() != null) {
                modifier = source.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
            }

            if (rarity != 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }

            final Item sprout = ItemFactory.createItem(ItemList.sprout, Math.max(1.0f, Math.min(100.0f, (float) power * modifier + source.getRarity())), crop.material, rarity, null);
            if (power < 0.0) {
                sprout.setDamage((float) (-power) / 2.0f);
            }
            performer.getInventory().insertItem(sprout, true);

            performer.getCommunicator().sendNormalServerMessage("You cut a sprout from the planter.");
            Server.getInstance().broadCastAction(String.format("%s cuts a sprout off a planter.", performer.getName()), performer, 5);
        } catch (FailedException | NoSuchTemplateException e) {
            PlantersMod.logException("Error making sprout", e);
            performer.getCommunicator().sendNormalServerMessage("You fail to pick the sprout. You realize something is wrong with the world.");
            return false;
        }

        SoundPlayer.playSound("sound.forest.branchsnap", target.getTileX(), target.getTileY(), true, 2.0f);

        PlanterItem.updateTreeData(target, crop, PlanterItem.getGrowthStage(target), PlanterItem.getTreeSubstage(target), PlanterItem.isTreeHarvestable(target), false);
        PlanterTracker.addPlanter(target);

        if (source.setDamage(source.getDamage() + 0.003f * source.getDamageModifier())) {
            performer.getCommunicator().sendNormalServerMessage(String.format("Your %s broke!", source.getName()));
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
            int skill = PlanterItem.isTreeOrBushPlanter(target) ? SkillList.FORESTRY : SkillList.FARMING;
            actionStart(performer, source, target);
            int time = Actions.getStandardActionTime(performer, performer.getSkills().getSkillOrLearn(skill), null, 0.0);
            performer.sendActionControl("harvesting", true, time);
            action.setTimeLeft(time);
        } else if (counter * 10f > action.getTimeLeft()) {
            byte rarity = action.getRarity();
            actionEnd(performer, source, target, rarity);
            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }
        return propagate(action, ActionPropagation.CONTINUE_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }
}
