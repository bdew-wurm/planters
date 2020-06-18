package net.bdew.planters.actions;

import com.wurmonline.server.Items;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.shared.util.MulticolorLineSegment;
import net.bdew.planters.Hooks;
import net.bdew.planters.MiscItems;
import net.bdew.planters.MushroomEatEffect;
import net.bdew.planters.PlantersMod;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EatMagicShroomPerformer implements ActionPerformer {
    @Override
    public short getActionId() {
        return Actions.EAT;
    }

    private final static List<String> messages = Arrays.asList(
            "your blood starts boiling",
            "electricity shoots through your spine",
            "start dancing",
            "your head explodes",
            "feel your hair change color",
            "feel drool dripping from your chin",
            "look nervously around",
            "watch out for assassins",
            "become one with nature",
            "your hands start twitching",
            "open your third eye",
            "scratch your nose",
            "inhale deeply and greedily",
            "wake up screaming",
            "join the spirits of Valrei",
            "know they are out to get you",
            "think about strawberries",
            "start laughing. HA HA HAAA",
            "see the stars dancing",
            "your teeth start growing",
            "embrace the darkness",
            "your lungs feel with dread",
            "levitate for a second",
            "abandon all hope",
            "pretend it's nothing",
            "your ears twitch",
            "drown in your sorrow",
            "prepare for unforeseen consequences",
            "contemplate murder",
            "run from the light",
            "avoid them noticing",
            "hope somebody cares"
    );


    @Override
    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        if (!performer.isPlayer() || target.getTemplateId() != MiscItems.magicShroomId)
            return propagate(action, ActionPropagation.SERVER_PROPAGATION, ActionPropagation.ACTION_PERFORMER_PROPAGATION);

        List<MulticolorLineSegment> segments = new ArrayList<>();
        segments.add(new MulticolorLineSegment("You put the mushroom in your mouth and", (byte) 0));
        Arrays.stream(messages.get(Server.rand.nextInt(messages.size())).split(" "))
                .map(s -> new MulticolorLineSegment(" " + s, (byte) Server.rand.nextInt(22)))
                .forEach(segments::add);
        segments.add(new MulticolorLineSegment("!", (byte) 0));
        performer.getCommunicator().sendColoredMessageEvent(segments);

        Server.getInstance().broadCastAction(String.format("%s puts something in %s mouth.", performer.getName(), performer.getHisHerItsString()), performer, 5);

        Hooks.addTickingEffect(performer.getWurmId(), Server.rand.nextInt(500) + 100, new MushroomEatEffect(PlantersMod.magicMushroomFavorPerQL * target.getQualityLevel() / 10f, 10, 50));

        if (PlantersMod.magicMushroomKarmaPerQL > 0) {
            performer.modifyKarma((int) (PlantersMod.magicMushroomKarmaPerQL * target.getQualityLevel()));
        }

        Items.destroyItem(target.getWurmId());

        return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION, ActionPropagation.NO_SERVER_PROPAGATION);
    }
}
