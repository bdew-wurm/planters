package net.bdew.planters.actions;

import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.RuneUtilities;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.villages.VillageRole;
import net.bdew.planters.Plantable;
import net.bdew.planters.PlanterItem;
import net.bdew.planters.Utils;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;

public class TendPerformer implements ActionPerformer {
    @Override
    public short getActionId() {
        return Actions.FARM;
    }

    public static boolean canUse(Creature performer, Item source, Item target) {
        return performer.isPlayer()
                && PlanterItem.isPlanter(target)
                && target.getAuxData() != 0
                && target.getParentId() == -10L
                && source.getTemplateId() == ItemList.rake
                && PlanterItem.getGrowthStage(target) < 5
                && !PlanterItem.isTended(target)
                && Utils.checkRoleAllows(performer, target, VillageRole::mayTendFields);
    }


    public static boolean actionStart(Creature performer, Item source, Item target) {
        Server.getInstance().broadCastAction(performer.getName() + " starts tending the planter.", performer, 5);
        performer.getCommunicator().sendNormalServerMessage("You start removing weeds and otherwise put the planter in good order.");
        performer.getStatus().modifyStamina(-1500f);
        performer.playAnimation("farm", false);
        return true;
    }

    public static boolean actionEnd(Creature performer, Item source, Item target, byte rarity) {
        Plantable crop = Plantable.getFromId(target.getAuxData());
        if (crop == null) return true;

        Methods.sendSound(performer, "sound.work.farming.rake");

        if (rarity != 0) performer.playPersonalSound("sound.fx.drumroll");

        Skill tool = performer.getSkills().getSkillOrLearn(SkillList.RAKE);

        double bonus = tool.skillCheck(crop.difficulty, source, 0.0, false, 10f) / 10.0;
        double power = Math.max(0.0, performer.getSkills().getSkillOrLearn(SkillList.FARMING).skillCheck(crop.difficulty, source, bonus, false, 10f));

        if (power <= 0.0)
            performer.getCommunicator().sendNormalServerMessage("The planter is tended.");
        else if (power < 25.0)
            performer.getCommunicator().sendNormalServerMessage("The planter is now tended.");
        else if (power < 50.0)
            performer.getCommunicator().sendNormalServerMessage("The planter looks better after your tending.");
        else if (power < 75.0)
            performer.getCommunicator().sendNormalServerMessage("The planter is now groomed.");
        else
            performer.getCommunicator().sendNormalServerMessage("The planter is now nicely groomed.");

        int tendPower = PlanterItem.getTendPower(target);
        int tendCount = PlanterItem.getTendCount(target);

        tendPower += (int) (power * 2.0 + rarity * 110 + source.getRarity() * 10);

        if (source.getSpellEffects() != null) {
            final float extraChance = source.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_FARMYIELD) - 1f;
            if (extraChance > 0f && Server.rand.nextFloat() < extraChance) {
                performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " seems to have an extra effect on the planter.");
                tendPower += 100;
                if (tendCount < 5) tendCount++;
            }
        }

        PlanterItem.updateData(target, crop, PlanterItem.getGrowthStage(target), true, tendCount, tendPower);

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

        Plantable crop = Plantable.getFromId(target.getAuxData());
        if (crop == null)
            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

        if (counter == 1f) {
            int time = Actions.getQuickActionTime(performer, performer.getSkills().getSkillOrLearn(SkillList.FARMING), null, 0.0);
            performer.sendActionControl("farming", true, time);
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
