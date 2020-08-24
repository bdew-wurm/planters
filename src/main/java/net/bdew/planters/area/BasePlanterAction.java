package net.bdew.planters.area;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.structures.Blocker;
import com.wurmonline.server.structures.Blocking;
import com.wurmonline.server.structures.BlockingResult;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import net.bdew.planters.PlanterItem;
import net.bdew.wurm.betterfarm.api.IItemAction;

public abstract class BasePlanterAction implements IItemAction {
    protected final boolean isForTreesAndBushes;

    public BasePlanterAction(boolean isForTreesAndBushes) {
        this.isForTreesAndBushes = isForTreesAndBushes;
    }

    abstract boolean checkRole(VillageRole role, Item target);

    @Override
    public boolean checkSkill(Creature performer, float needed) {
        if (isForTreesAndBushes)
            return performer.getSkills().getSkillOrLearn(SkillList.FORESTRY).getRealKnowledge() >= needed;
        else
            return performer.getSkills().getSkillOrLearn(SkillList.FARMING).getRealKnowledge() >= needed;
    }

    @Override
    public boolean canStartOn(Creature performer, Item source, Item target) {
        return performer.isPlayer() && target.getParentOrNull() == null && PlanterItem.isPlanter(target) && PlanterItem.isTreeOrBushPlanter(target) == isForTreesAndBushes;
    }

    @Override
    public boolean canActOn(Creature performer, Item source, Item target, boolean sendMsg) {
        if (!canStartOn(performer, source, target)) return false;

        VolaTile vt = Zones.getTileOrNull(target.getTilePos(), target.isOnSurface());
        if (vt == null) return false;

        final BlockingResult blockers = Blocking.getBlockerBetween(performer, performer.getPosX(), performer.getPosY(), target.getPosX(), target.getPosY(), performer.getPositionZ(), target.getPosZ(), performer.isOnSurface(), target.isOnSurface(), false, Blocker.TYPE_ALL, -1L, performer.getBridgeId(), -10L, false);
        if (blockers != null && blockers.getFirstBlocker() != null) {
            if (sendMsg)
                performer.getCommunicator().sendNormalServerMessage(String.format("You decide to skip the %s since a %s blocks you.", target.getName().toLowerCase(), blockers.getFirstBlocker().getName().toLowerCase()));
            return false;
        }

        if (performer.getPower() < 2) {
            Village village = vt.getVillage();
            if (village != null) {
                VillageRole role = village.getRoleFor(performer);
                if (role == null || !checkRole(role, target)) {
                    if (sendMsg)
                        performer.getCommunicator().sendNormalServerMessage(String.format("You decide to skip the %s as it's against local laws.", target.getName().toLowerCase()));
                    return false;
                }
            }
        }

        return true;
    }
}
