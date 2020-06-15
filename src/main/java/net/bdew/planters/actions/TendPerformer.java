package net.bdew.planters.actions;

import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.RuneUtilities;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.villages.Villages;
import net.bdew.planters.Plantable;
import net.bdew.planters.PlanterItem;
import net.bdew.planters.PlantersMod;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;

public class TendPerformer implements ActionPerformer {
    @Override
    public short getActionId() {
        return Actions.FARM;
    }

    public static boolean canUse(Creature performer, Item source, Item target) {
        if (PlanterItem.isPlanter(target) && target.getAuxData() != 0 && target.getParentId() == -10L && source.getTemplateId() == ItemList.rake &&
                PlanterItem.getGrowthStage(target) < 5 && !PlanterItem.isTended(target)) {
            Village village = Villages.getVillage(target.getTileX(), target.getTileY(), target.isOnSurface());
            if (village == null) return true;
            VillageRole role = village.getRoleFor(performer);
            return role != null && role.mayFarm();
        } else return false;
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
            Server.getInstance().broadCastAction(performer.getName() + " starts tending the planter.", performer, 5);
            performer.getCommunicator().sendNormalServerMessage("You start removing weeds and otherwise put the planter in good order.");
            int time = Actions.getQuickActionTime(performer, performer.getSkills().getSkillOrLearn(SkillList.FARMING), null, 0.0);
            performer.sendActionControl("farming", true, time);
            source.setDamage(source.getDamage() + 0.0005f * source.getDamageModifier());
            performer.getStatus().modifyStamina(-1500f);
            action.setTimeLeft(time);
        } else {
            if (action.justTickedSecond()) {
                source.setDamage(source.getDamage() + 0.0003f * source.getDamageModifier());
                performer.getStatus().modifyStamina(-2000f);
                if (action.mayPlaySound())
                    Methods.sendSound(performer, "sound.work.farming.rake");
            }

            if (counter * 10f > action.getTimeLeft()) {
                if (action.getRarity() != 0) performer.playPersonalSound("sound.fx.drumroll");
                double power = 0.0;
                double bonus = 0.0;
                try {
                    Skill tool = performer.getSkills().getSkillOrLearn(source.getPrimarySkill());
                    bonus = tool.skillCheck(crop.difficulty, source, 0.0, false, counter) / 10.0;
                } catch (NoSuchSkillException e) {
                    PlantersMod.logWarning("Missing skill for tool " + source.toString());
                }
                power = Math.max(0.0, performer.getSkills().getSkillOrLearn(SkillList.FARMING).skillCheck(crop.difficulty, source, bonus, false, counter));

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

                tendPower += (int) (power * 2.0 + action.getRarity() * 110 + source.getRarity() * 10);

                if (source.getSpellEffects() != null) {
                    final float extraChance = source.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_FARMYIELD) - 1f;
                    if (extraChance > 0f && Server.rand.nextFloat() < extraChance) {
                        performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " seems to have an extra effect on the planter.");
                        tendPower += 100;
                        if (tendCount < 5) tendCount++;
                    }
                }

                PlanterItem.updateData(target, crop, PlanterItem.getGrowthStage(target), true, tendCount, tendPower);

                return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            }
        }

        return propagate(action, ActionPropagation.CONTINUE_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }

}
