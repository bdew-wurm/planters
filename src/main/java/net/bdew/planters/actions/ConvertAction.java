package net.bdew.planters.actions;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import net.bdew.planters.Plantable;
import net.bdew.planters.PlanterItem;
import net.bdew.planters.Utils;
import org.gotti.wurmunlimited.modsupport.actions.*;

import java.util.Arrays;
import java.util.List;

public class ConvertAction implements ModAction, BehaviourProvider, ActionPerformer {
    private final ActionEntry actionEntry;

    public ConvertAction() {
        actionEntry = new ActionEntryBuilder((short) ModActions.getNextActionId(), "Convert", "converting", new int[]{
                48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                36 /* ACTION_TYPE_ALWAYS_USE_ACTIVE_ITEM */,
        }).range(4).build();
        ModActions.registerAction(actionEntry);
    }

    @Override
    public short getActionId() {
        return actionEntry.getNumber();
    }

    @Override
    public BehaviourProvider getBehaviourProvider() {
        return this;
    }

    @Override
    public ActionPerformer getActionPerformer() {
        return this;
    }

    public static Plantable getAltPlantable(Item planter) {
        Plantable plantable = PlanterItem.getPlantable(planter);
        if (plantable != null && plantable.altVersion >= 0)
            return Plantable.getFromId(plantable.altVersion);
        else
            return null;
    }

    public static boolean canUse(Creature performer, Item source, Item target) {
        return performer.isPlayer()
                && source.getTemplateId() == ItemList.sourceSalt &&
                source.getWeightGrams() >= source.getTemplate().getWeightGrams()
                && PlanterItem.isPlanter(target)
                && target.getAuxData() != 0
                && target.getParentId() == -10L
                && getAltPlantable(target) != null
                && Utils.checkRoleAllows(performer, target, VillageRole::mayPlantSprouts);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {
        if (canUse(performer, source, target)) {
            Plantable plantable = getAltPlantable(target);
            if (plantable != null) {
                return Arrays.asList(new ActionEntry(getActionId(), String.format("Convert to %s", plantable.displayName), ""));
            }
        }
        return null;
    }

    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
        if (!canUse(performer, source, target))
            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

        Plantable currentPlantabe = PlanterItem.getPlantable(target);
        Plantable targetPlantable = getAltPlantable(target);

        if (currentPlantabe != null && targetPlantable != null) {
            source.setWeight(source.getWeightGrams() - source.getTemplate().getWeightGrams(), true);
            performer.getCommunicator().sendNormalServerMessage(String.format("You spread some %s on the %s planter, turning it into %s!", source.getName(), currentPlantabe.displayName, targetPlantable.displayName));
            VolaTile vt = Zones.getOrCreateTile(target.getTilePos(), target.isOnSurface());
            vt.makeInvisible(target);
            target.setAuxData((byte) targetPlantable.number);
            PlanterItem.updateName(target, targetPlantable, PlanterItem.getGrowthStage(target), PlanterItem.isTended(target), PlanterItem.isInfected(target), PlanterItem.isTreeHarvestable(target), PlanterItem.isTreeSprouting(target));
            vt.makeVisible(target);
        }

        return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }

}
