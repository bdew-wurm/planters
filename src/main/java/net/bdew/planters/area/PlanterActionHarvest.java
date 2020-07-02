package net.bdew.planters.area;

import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.villages.VillageRole;
import net.bdew.planters.Plantable;
import net.bdew.planters.PlanterItem;
import net.bdew.planters.actions.HarvestPerformer;

public class PlanterActionHarvest extends BasePlanterAction {
    private final boolean replant;

    public PlanterActionHarvest(boolean replant) {
        this.replant = replant;
    }

    @Override
    boolean checkRole(VillageRole role) {
        return role.mayHarvestFields();
    }

    @Override
    public boolean canActOn(Creature performer, Item source, Item target, boolean sendMsg) {
        if (!super.canActOn(performer, source, target, sendMsg)) return false;
        if (target.getAuxData() == 0) {
            if (sendMsg) {
                performer.getCommunicator().sendNormalServerMessage(String.format("You skip the %s as nothing is growing there.", target.getName().toLowerCase()));
            }
            return false;
        }
        if (PlanterItem.getGrowthStage(target) == 6) {
            if (sendMsg) {
                performer.getCommunicator().sendNormalServerMessage(String.format("You skip the weeds in the %s, they are far past harvesting.", target.getName().toLowerCase()));
            }
            return false;
        }
        if (PlanterItem.getGrowthStage(target) != 5) {
            if (sendMsg) {
                performer.getCommunicator().sendNormalServerMessage(String.format("You skip the %s as it's not ready to harvest.", target.getName().toLowerCase()));
            }
            return false;
        }
        Plantable crop = PlanterItem.getPlantable(target);
        if (crop == null) return false;
        if (crop.needsScythe && (source == null || source.getTemplateId() != ItemList.scythe)) {
            if (sendMsg) {
                performer.getCommunicator().sendNormalServerMessage(String.format("You skip the %s as it needs a scythe to harvest.", target.getName().toLowerCase()));
            }
            return false;
        }
        return true;
    }

    @Override
    public float getActionTime(Creature performer, Item source, Item target) {
        return Actions.getStandardActionTime(performer, performer.getSkills().getSkillOrLearn(SkillList.FARMING), null, 0.0);
    }

    @Override
    public boolean actionStarted(Creature performer, Item source, Item target) {
        return HarvestPerformer.actionStart(performer, source, target);
    }

    @Override
    public boolean actionCompleted(Creature performer, Item source, Item target, byte rarity) {
        return HarvestPerformer.actionEnd(performer, source, target, rarity, replant, true);
    }
}
