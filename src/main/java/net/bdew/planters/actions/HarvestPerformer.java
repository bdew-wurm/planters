package net.bdew.planters.actions;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.*;
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

public class HarvestPerformer implements ActionPerformer {
    @Override
    public short getActionId() {
        return Actions.HARVEST;
    }

    public static boolean canUse(Creature performer, Item source, Item target) {
        if (target.getTemplateId() == PlanterItem.id && target.getAuxData() != 0 && target.getParentId() == -10L && PlanterItem.getGrowthStage(target) == 5) {
            Plantable crop = PlanterItem.getPlantable(target);
            if (crop == null) return false;
            if (crop.needsScythe && (source == null || source.getTemplateId() != ItemList.scythe)) return false;
            Village village = Villages.getVillage(target.getTileX(), target.getTileY(), target.isOnSurface());
            if (village == null) return true;
            VillageRole role = village.getRoleFor(performer);
            return role != null && role.mayFarm();
        } else return false;
    }

    @Override
    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        return this.action(action, performer, null, target, num, counter);
    }

    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
        if (!canUse(performer, source, target))
            if (target.getTemplateId() == PlanterItem.id)
                return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            else
                return propagate(action, ActionPropagation.SERVER_PROPAGATION, ActionPropagation.ACTION_PERFORMER_PROPAGATION);

        Plantable crop = Plantable.getFromId(target.getAuxData());
        if (crop == null)
            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

        if (counter == 1f) {
            Server.getInstance().broadCastAction(performer.getName() + " starts harvesting the planter.", performer, 5);
            performer.getCommunicator().sendNormalServerMessage("You start harvesting the planter.");
            int time = Actions.getStandardActionTime(performer, performer.getSkills().getSkillOrLearn(SkillList.FARMING), null, 0.0);
            performer.sendActionControl("harvesting", true, time);
            performer.getStatus().modifyStamina(-1000f);
            action.setTimeLeft(time);
        } else {
            if (action.justTickedSecond()) {
                if (crop.needsScythe && source != null) {
                    source.setDamage(source.getDamage() + 0.0003f * source.getDamageModifier());
                }
                performer.getStatus().modifyStamina(-2000f);
                if (action.mayPlaySound()) {
                    if (crop.needsScythe)
                        Methods.sendSound(performer, "sound.work.farming.scythe");
                    else
                        Methods.sendSound(performer, "sound.work.farming.harvest");
                }
            }
            if (counter * 10f > action.getTimeLeft()) {
                byte rarity = action.getRarity();
                if (rarity != 0) performer.playPersonalSound("sound.fx.drumroll");
                Skill farming = performer.getSkills().getSkillOrLearn(SkillList.FARMING);
                double power = farming.skillCheck(crop.difficulty, 0.0, false, counter);
                byte itemRarity = 0;

                if (crop.needsScythe && source != null) {
                    itemRarity = source.getRarity();
                    try {
                        Skill tool = performer.getSkills().getSkillOrLearn(source.getPrimarySkill());
                        tool.skillCheck(crop.difficulty, source, 0.0, false, counter);
                    } catch (NoSuchSkillException e) {
                        PlantersMod.logWarning("Missing skill for tool " + source.toString());
                    }
                }

                float planterRarityMod = 1f + target.getRarity() * 0.2f;

                float knowledge = (float) farming.getKnowledge(0.0);
                float ql = (knowledge + (100f - knowledge) * ((float) power / 500f)) * planterRarityMod;

                int farmedCount = PlanterItem.getTendCount(target);
                int farmedChance = PlanterItem.getTendPower(target);
                short resource = (short) (farmedChance + rarity * 110 + itemRarity * 50 + Math.min(5, farmedCount) * 50);

                float div = 100f - knowledge / 15f;
                short bonusYield = (short) (resource / div / 1.5f);
                float baseYield = knowledge / 15f;
                int quantity = (int) ((baseYield + bonusYield + PlantersMod.extraHarvest) * planterRarityMod);

                PlanterItem.clearData(target);

                Server.getInstance().broadCastAction(performer.getName() + " has harvested the planter.", performer, 5);

                if (crop.needsScythe && source != null && source.getSpellEffects() != null) {
                    final float modifier = source.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                    ql *= modifier;
                }

                if (quantity == 0)
                    quantity = 1;

                if (quantity == 1 && farmedCount > 0)
                    ++quantity;

                if (quantity == 2 && farmedCount >= 4)
                    ++quantity;

                if (quantity <= 1) {
                    performer.getCommunicator().sendNormalServerMessage("You realize you harvested in perfect time, tending the planter would have resulted in a better yield.");
                } else {
                    performer.getCommunicator().sendNormalServerMessage("You realize you harvested in perfect time. The harvest is of top quality.");
                }

                performer.getCommunicator().sendNormalServerMessage(String.format("You managed to get a yield of %d %s.", quantity, crop.displayName));

                if (crop.cropItem == ItemList.cotton && quantity >= 5) {
                    performer.achievement(544);
                }

                try {
                    for (int x = 0; x < quantity; ++x) {
                        final Item result = ItemFactory.createItem(crop.cropItem, Math.max(Math.min(ql, 100f), 1f), null);
                        if (!performer.getInventory().insertItem(result, true)) {
                            performer.getCommunicator().sendNormalServerMessage("You can't carry the harvest. It falls to the ground and is ruined!");
                        }
                    }
                } catch (NoSuchTemplateException | FailedException e) {
                    PlantersMod.logException("Error creating harvest", e);
                }

                return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            }
        }
        return propagate(action, ActionPropagation.CONTINUE_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }

}
