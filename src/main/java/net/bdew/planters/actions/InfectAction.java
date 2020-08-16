package net.bdew.planters.actions;

import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.villages.VillageRole;
import net.bdew.planters.PlanterItem;
import net.bdew.planters.Utils;
import org.gotti.wurmunlimited.modsupport.actions.*;

import java.util.Collections;
import java.util.List;

public class InfectAction implements ModAction, BehaviourProvider, ActionPerformer {
    private final ActionEntry actionEntry;
    private final List<ActionEntry> infectMenu, cleanseMenu;

    public InfectAction() {
        actionEntry = new ActionEntryBuilder((short) ModActions.getNextActionId(), "Infect", "infecting", new int[]{
                0 /* ACTION_TYPE_QUICK */,
                48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                37 /* ACTION_TYPE_NEVER_USE_ACTIVE_ITEM */,
        }).range(4).build();
        ModActions.registerAction(actionEntry);

        infectMenu = Collections.singletonList(actionEntry);
        cleanseMenu = Collections.singletonList(new ActionEntry(actionEntry.getNumber(), "Cleanse", "cleansing"));
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

    private boolean canUse(Creature performer, Item target) {
        return performer.isPlayer() && target != null
                && target.getParentOrNull() == null
                && PlanterItem.isPlanter(target)
                && Utils.checkRoleAllows(performer, target, VillageRole::mayTendFields)
                && performer.getDeity() != null
                && performer.getDeity().isHateGod() != PlanterItem.isInfected(target);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
        return getBehavioursFor(performer, null, target);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {
        if (canUse(performer, target))
            if (performer.getDeity().isHateGod())
                return infectMenu;
            else
                return cleanseMenu;
        else
            return null;
    }

    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
        return action(action, performer, target, num, counter);
    }

    @Override
    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        if (!canUse(performer, target))
            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

        if (performer.getDeity().isHateGod()) {
            performer.getCommunicator().sendNormalServerMessage("You let your hate twist and corrupt the planter.");
            Server.getInstance().broadCastAction(String.format("%s performs a dark ritual on a planter.", performer.getName()), performer, 5);
            PlanterItem.setInfected(target, true);
        } else {
            performer.getCommunicator().sendNormalServerMessage("You let your light heal and cleanse the planter.");
            Server.getInstance().broadCastAction(String.format("%s performs a healing ritual on a planter.", performer.getName()), performer, 5);
            PlanterItem.setInfected(target, false);
        }

        return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }
}
