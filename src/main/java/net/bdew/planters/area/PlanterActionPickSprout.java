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
import net.bdew.planters.actions.PickSproutPerformer;
import net.bdew.wurm.betterfarm.api.ActionEntryOverride;

public class PlanterActionPickSprout extends BasePlanterAction {
    public PlanterActionPickSprout() {
        super(true);
    }

    private final ActionEntryOverride override = new ActionEntryOverride(Actions.PICKSPROUT, "Pick sprout", "picking", null);

    @Override
    public ActionEntryOverride getOverride(Creature performer, Item source, Item target) {
        return override;
    }

    @Override
    boolean checkRole(VillageRole role, Item target) {
        return role.mayPickSprouts();
    }

    @Override
    public boolean canStartOn(Creature performer, Item source, Item target) {
        return super.canStartOn(performer, source, target) && source != null && source.getTemplateId() == ItemList.sickle;
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

        Plantable crop = PlanterItem.getPlantable(target);
        if (crop == null) return false;

        if (!PlanterItem.isTreeSprouting(target)) {
            if (sendMsg) {
                performer.getCommunicator().sendNormalServerMessage(String.format("You skip the %s as it's not sprouting.", target.getName().toLowerCase()));
            }
            return false;
        }

        return true;
    }

    @Override
    public float getActionTime(Creature performer, Item source, Item target) {
        int skill = PlanterItem.isTreeOrBushPlanter(target) ? SkillList.FORESTRY : SkillList.FARMING;
        return Actions.getStandardActionTime(performer, performer.getSkills().getSkillOrLearn(skill), null, 0.0);
    }

    @Override
    public boolean actionStarted(Creature performer, Item source, Item target) {
        return HarvestPerformer.actionStart(performer, source, target);
    }

    @Override
    public boolean actionCompleted(Creature performer, Item source, Item target, byte rarity) {
        return PickSproutPerformer.actionEnd(performer, source, target, rarity);
    }
}
