package net.bdew.planters;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.Items;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.items.*;
import org.gotti.wurmunlimited.modloader.interfaces.MessagePolicy;

import java.util.Arrays;
import java.util.Optional;
import java.util.StringTokenizer;

public class GmCommands {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<Boolean> forceWinter = Optional.empty();

    private static void spawnTestPlanter(int id, int tileX, int tileY, Plantable plant, int age, boolean tended, float damage) {
        try {
            Item itm = ItemFactory.createItem(id, 99f, tileX * 4f + 2f, tileY * 4f + 2f, 0, true, id == PlanterItem.woodId ? Materials.MATERIAL_WOOD_BIRCH : Materials.MATERIAL_STONE, (byte) 0, -10L, null);
            itm.setDamage(damage);
            if (plant != null)
                PlanterItem.updateData(itm, plant, age, tended, 0, 0);
        } catch (NoSuchTemplateException | FailedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void spawnPlanters(Communicator communicator) {
        int py = communicator.player.getTileY();
        int x = communicator.player.getTileX();
        int y = py;

        spawnTestPlanter(PlanterItem.woodId, x, y++, null, 0, false, 0);
        spawnTestPlanter(PlanterItem.woodId, x, y++, null, 0, false, 75);

        try {
            Item itm = ItemFactory.createItem(ItemList.unfinishedItem, 99f, x * 4f + 2f, (y++) * 4f + 2f, 0, true, Materials.MATERIAL_WOOD_BIRCH, (byte) 0, -10L, null);
            itm.setRealTemplate(PlanterItem.woodId);
        } catch (NoSuchTemplateException | FailedException e) {
            throw new RuntimeException(e);
        }

        spawnTestPlanter(PlanterItem.stoneId, x, y++, null, 0, false, 0);
        spawnTestPlanter(PlanterItem.stoneId, x, y++, null, 0, false, 75);

        try {
            Item itm = ItemFactory.createItem(ItemList.unfinishedItem, 99f, x * 4f + 2f, y * 4f + 2f, 0, true, Materials.MATERIAL_STONE, (byte) 0, -10L, null);
            itm.setRealTemplate(PlanterItem.stoneId);
        } catch (NoSuchTemplateException | FailedException e) {
            throw new RuntimeException(e);
        }

        for (Plantable plant : Plantable.values()) {
            x++;
            y = py;
            spawnTestPlanter(PlanterItem.woodId, x, y++, plant, 3, false, 0);
            spawnTestPlanter(PlanterItem.woodId, x, y++, plant, 3, true, 0);
            spawnTestPlanter(PlanterItem.woodId, x, y++, plant, 5, false, 0);
            spawnTestPlanter(PlanterItem.woodId, x, y++, plant, 6, false, 0);
            spawnTestPlanter(PlanterItem.woodId, x, y++, plant, 3, false, 75f);
            spawnTestPlanter(PlanterItem.woodId, x, y++, plant, 3, true, 75f);
            spawnTestPlanter(PlanterItem.woodId, x, y++, plant, 5, false, 75f);
            spawnTestPlanter(PlanterItem.woodId, x, y++, plant, 6, false, 75f);

            spawnTestPlanter(PlanterItem.stoneId, x, y++, plant, 3, false, 0);
            spawnTestPlanter(PlanterItem.stoneId, x, y++, plant, 3, true, 0);
            spawnTestPlanter(PlanterItem.stoneId, x, y++, plant, 5, false, 0);
            spawnTestPlanter(PlanterItem.stoneId, x, y++, plant, 6, false, 0);
            spawnTestPlanter(PlanterItem.stoneId, x, y++, plant, 3, false, 75f);
            spawnTestPlanter(PlanterItem.stoneId, x, y++, plant, 3, true, 75f);
            spawnTestPlanter(PlanterItem.stoneId, x, y++, plant, 5, false, 75f);
            spawnTestPlanter(PlanterItem.stoneId, x, y++, plant, 6, false, 75f);
        }

        communicator.sendNormalServerMessage("Spawned test planters.");
    }

    private static void deletePlanters(Communicator communicator) {
        Arrays.stream(Items.getAllItems())
                .filter(item -> PlanterItem.isPlanter(item) || (item.getTemplateId() == ItemList.unfinishedItem && PlanterItem.isPlanter(item.getTemplateId())))
                .forEach(i -> Items.destroyItem(i.getWurmId()));
        communicator.sendNormalServerMessage("Deleted all planters.");
    }

    private static void colorPlanters(Communicator communicator, String arg) {
        if (arg.equalsIgnoreCase("random")) {
            colorPlantersRandom();
        } else if (arg.equalsIgnoreCase("pink")) {
            colorPlantersSet(WurmColor.createColor(255, 1, 127));
        } else if (arg.equalsIgnoreCase("remove")) {
            colorPlantersSet(-1);
        } else {
            communicator.sendAlertServerMessage("Usage: #planters paint <pink|random|remove>");
        }
    }

    private static void colorPlantersSet(int color) {
        Arrays.stream(Items.getAllItems())
                .filter(PlanterItem::isPlanter)
                .forEach(i -> i.setColor(color));
    }

    private static void colorPlantersRandom() {
        Arrays.stream(Items.getAllItems())
                .filter(PlanterItem::isPlanter)
                .forEach(i -> i.setColor(WurmColor.createColor(Server.rand.nextInt(255) + 1, Server.rand.nextInt(255) + 1, Server.rand.nextInt(255) + 1)));
    }

    private static void setWinter(Communicator communicator, String arg) {
        if (arg.equalsIgnoreCase("on")) {
            forceWinter = Optional.of(true);
            communicator.sendNormalServerMessage("Winter forced to ON");
        } else if (arg.equalsIgnoreCase("off")) {
            forceWinter = Optional.of(false);
            communicator.sendNormalServerMessage("Winter forced to OFF");
        } else if (arg.equalsIgnoreCase("disable")) {
            forceWinter = Optional.empty();
            communicator.sendNormalServerMessage("Winter returned to normal");
        } else {
            communicator.sendAlertServerMessage("Usage: #planters winter <on|off|disable>");
            return;
        }
        try {
            communicator.player.createVisionArea();
            Server.getInstance().addCreatureToPort(communicator.player);
        } catch (Exception e) {
            PlantersMod.logException("error in createVisionArea", e);
        }
    }

    public static MessagePolicy handle(Communicator communicator, String message, String title) {
        if (message.startsWith("#planters ")) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            if (tokens.hasMoreTokens()) {
                String cmd = tokens.nextToken().trim();
                switch (cmd) {
                    case "test":
                        spawnPlanters(communicator);
                        return MessagePolicy.DISCARD;
                    case "delete":
                        deletePlanters(communicator);
                        return MessagePolicy.DISCARD;
                    case "winter":
                        if (tokens.hasMoreTokens()) {
                            setWinter(communicator, tokens.nextToken());
                            return MessagePolicy.DISCARD;
                        }
                        break;
                    case "paint":
                        if (tokens.hasMoreTokens()) {
                            colorPlanters(communicator, tokens.nextToken());
                            return MessagePolicy.DISCARD;
                        }
                        break;
                }
            }
            communicator.sendAlertServerMessage("Usage:");
            communicator.sendAlertServerMessage(" #planters test");
            communicator.sendAlertServerMessage(" #planters delete");
            communicator.sendAlertServerMessage(" #planters winter <on|off|disable>");
            communicator.sendAlertServerMessage(" #planters paint <pink|random|remove>");
            return MessagePolicy.DISCARD;
        }
        return MessagePolicy.PASS;
    }
}
