package net.bdew.planters.actions;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import net.bdew.planters.PlanterItem;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;

public class PreventPlanterPerformer implements ActionPerformer {
    private final short actionId;

    public PreventPlanterPerformer(short actionId) {
        this.actionId = actionId;
    }

    @Override
    public short getActionId() {
        return actionId;
    }

    private boolean shouldPrevent(Item target) {
        return PlanterItem.isPlanter(target) && target.getAuxData() != 0;
    }

    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
        return action(action, performer, target, num, counter);
    }

    @Override
    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        if (shouldPrevent(target)) {
            performer.getCommunicator().sendAlertServerMessage("The planter needs to be empty before you can move it.");
            return propagate(action, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION, ActionPropagation.FINISH_ACTION);
        } else {
            return propagate(action, ActionPropagation.ACTION_PERFORMER_PROPAGATION, ActionPropagation.SERVER_PROPAGATION);
        }
    }
}