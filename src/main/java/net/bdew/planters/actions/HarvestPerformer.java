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
import com.wurmonline.server.villages.VillageRole;
import net.bdew.planters.*;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;

public class HarvestPerformer implements ActionPerformer {
    @Override
    public short getActionId() {
        return Actions.HARVEST;
    }

    public static boolean canUse(Creature performer, Item source, Item target) {
        if (performer.isPlayer() && PlanterItem.isPlanter(target) && target.getAuxData() != 0 && target.getParentId() == -10L && PlanterItem.getGrowthStage(target) == 5) {
            Plantable crop = PlanterItem.getPlantable(target);
            if (crop == null) return false;
            if (crop.needsScythe && (source == null || source.getTemplateId() != ItemList.scythe)) return false;
            return Utils.checkRoleAllows(performer, target, VillageRole::mayHarvestFields);
        } else return false;
    }

    @Override
    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        return this.action(action, performer, null, target, num, counter);
    }

    public static boolean actionStart(Creature performer, Item source, Item target) {
        Server.getInstance().broadCastAction(performer.getName() + " starts harvesting the planter.", performer, 5);
        performer.getCommunicator().sendNormalServerMessage("You start harvesting the planter.");
        performer.playAnimation("farm", false);
        performer.getStatus().modifyStamina(-1000f);
        return true;
    }

    public static boolean actionEnd(Creature performer, Item source, Item target, byte rarity, boolean replant, boolean stack) {
        Plantable crop = Plantable.getFromId(target.getAuxData());
        if (crop == null || PlanterItem.getGrowthStage(target) != 5)
            return true;

        performer.getStatus().modifyStamina(-2000f);

        if (crop.needsScythe)
            Methods.sendSound(performer, "sound.work.farming.scythe");
        else
            Methods.sendSound(performer, "sound.work.farming.harvest");

        if (rarity != 0) performer.playPersonalSound("sound.fx.drumroll");

        Skill farming = performer.getSkills().getSkillOrLearn(SkillList.FARMING);
        double power = farming.skillCheck(crop.difficulty, 0.0, false, 10);
        byte itemRarity = 0;

        if (crop.needsScythe && source != null) {
            itemRarity = source.getRarity();
            try {
                Skill tool = performer.getSkills().getSkillOrLearn(source.getPrimarySkill());
                tool.skillCheck(crop.difficulty, source, 0.0, false, 10);
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

        try {
            int enc = ReflectionUtil.getPrivateField(performer, ReflectionUtil.getField(Creature.class, "encumbered"));
            if (performer.getCarriedWeight() + quantity * ItemTemplateFactory.getInstance().getTemplate(crop.cropItem).getWeightGrams() > enc) {
                performer.getCommunicator().sendNormalServerMessage("You stop harvesting as carrying more produce would make you encumbered.");
                return false;
            }
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchTemplateException e) {
            PlantersMod.logException("Error checking encumbered", e);
        }

        if (crop.planterType != PlanterType.NORMAL) {
            replant = false;
        }

        if (replant) {
            if (quantity <= 1) {
                quantity = 0;
                performer.getCommunicator().sendNormalServerMessage("You use up all the yield of " + crop.displayName + " to replant.");
            } else {
                quantity -= 1;
                performer.getCommunicator().sendNormalServerMessage("You managed to get a yield of " + quantity + " " + crop.displayName + " after using some to replant.");
            }
            Server.getInstance().broadCastAction(performer.getName() + " has harvested and replanted the planter.", performer, 5);
        } else {
            performer.getCommunicator().sendNormalServerMessage("You managed to get a yield of " + quantity + " " + crop.displayName + ".");
            Server.getInstance().broadCastAction(performer.getName() + " has harvested the planter.", performer, 5);
        }

        if (crop.planterType != PlanterType.NORMAL) {
            performer.getCommunicator().sendNormalServerMessage(String.format("You can't replant %s as it requires something special to grow.", target.getTemplate().getName().toLowerCase()));
        }

        if (crop.cropItem == ItemList.cotton && quantity >= 5) {
            performer.achievement(544);
        }

        if (quantity > 0) {
            if (stack) {
                Utils.addStackedItems(performer.getInventory(), crop.cropItem, ql, quantity, crop.displayName);
            } else {
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
            }
        }


        if (replant) {
            int replantPower = (int) (100.0 - farming.getKnowledge() + ql + rarity * 50);
            PlanterItem.updateData(target, crop, 0, true, 0, replantPower);
            PlanterTracker.addPlanter(target);
        } else PlanterItem.clearData(target);


        if (crop.needsScythe && source != null) {
            if (source.setDamage(source.getDamage() + 0.0015F * source.getDamageModifier())) {
                performer.getCommunicator().sendNormalServerMessage(String.format("Your %s broke!", source.getName().toLowerCase()));
                return false;
            }
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
            actionStart(performer, source, target);
            int time = Actions.getStandardActionTime(performer, performer.getSkills().getSkillOrLearn(SkillList.FARMING), null, 0.0);
            performer.sendActionControl("harvesting", true, time);
            action.setTimeLeft(time);
        } else if (counter * 10f > action.getTimeLeft()) {
            byte rarity = action.getRarity();
            actionEnd(performer, source, target, rarity, false, false);
            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }
        return propagate(action, ActionPropagation.CONTINUE_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }
}
