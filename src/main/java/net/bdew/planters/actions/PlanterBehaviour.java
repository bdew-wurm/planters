package net.bdew.planters.actions;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import net.bdew.planters.PlanterItem;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;

import java.util.ArrayList;
import java.util.List;

public class PlanterBehaviour implements BehaviourProvider {
    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
        if (performer instanceof Player && target != null && target.getTemplateId() == PlanterItem.id) {
            List<ActionEntry> list = new ArrayList<>();
            if (HarvestPerformer.canUse(performer, null, target)) list.add(Actions.actionEntrys[Actions.HARVEST]);
            return list;
        } else return null;
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {
        if (performer instanceof Player && target != null && target.getTemplateId() == PlanterItem.id) {
            List<ActionEntry> list = new ArrayList<>();
            if (SowPerformer.canUse(performer, source, target)) list.add(Actions.actionEntrys[Actions.SOW]);
            if (CultivatePerformer.canUse(performer, source, target)) list.add(Actions.actionEntrys[Actions.CULTIVATE]);
            if (TendPerformer.canUse(performer, source, target)) list.add(Actions.actionEntrys[Actions.FARM]);
            if (HarvestPerformer.canUse(performer, source, target)) list.add(Actions.actionEntrys[Actions.HARVEST]);
            return list;
        } else return null;
    }
}
