package net.bdew.planters.actions;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import net.bdew.planters.PlanterItem;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlanterBehaviour implements BehaviourProvider {
    private final List<ActionEntry> digStumpAction;

    public PlanterBehaviour() {
        digStumpAction = Collections.singletonList(new ActionEntry(Actions.DIG, "Dig up", "digging"));
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
        if (performer.isPlayer() && target != null && PlanterItem.isPlanter(target)) {
            List<ActionEntry> list = new ArrayList<>();
            if (HarvestPerformer.canUse(performer, null, target)) list.add(Actions.actionEntrys[Actions.HARVEST]);
            return list;
        } else return null;
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {
        if (performer.isPlayer() && target != null && PlanterItem.isPlanter(target)) {
            List<ActionEntry> list = new ArrayList<>();
            if (SowPerformer.canUse(performer, source, target)) list.add(Actions.actionEntrys[Actions.SOW]);
            if (CultivatePerformer.canUse(performer, source, target)) list.add(Actions.actionEntrys[Actions.CULTIVATE]);
            if (TendPerformer.canUse(performer, source, target)) list.add(Actions.actionEntrys[Actions.FARM]);
            if (HarvestPerformer.canUse(performer, source, target)) list.add(Actions.actionEntrys[Actions.HARVEST]);
            return list;
        } else if (performer.isPlayer() && target != null && DigStumpPerformer.canUse(performer, source, target)) {
            return digStumpAction;
        } else return null;
    }
}
