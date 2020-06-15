package net.bdew.planters;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.Items;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.items.*;
import org.gotti.wurmunlimited.modloader.interfaces.MessagePolicy;

import java.util.Arrays;
import java.util.Optional;

public class GmCommands {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<Boolean> forceWinter = Optional.empty();

    private static void spawnTestPlanter(int id, int tileX, int tileY, Plantable plant, int age, boolean tended, float damage) {
        try {
            Item itm = ItemFactory.createItem(id, 99f, tileX * 4f + 2f, tileY * 4f + 2f, 0, true, id == PlanterItem.woodId ? Materials.MATERIAL_WOOD_BIRCH : Materials.MATERIAL_STONE, (byte) 0, -10L, null);
            itm.setDamage(damage);
//            itm.setColor(WurmColor.createColor(255, 1, 127));
//            itm.setColor(WurmColor.createColor(Server.rand.nextInt(255) + 1, Server.rand.nextInt(255) + 1, Server.rand.nextInt(255) + 1));
            if (plant != null)
                PlanterItem.updateData(itm, plant, age, tended, 0, 0);
        } catch (NoSuchTemplateException | FailedException e) {
            throw new RuntimeException(e);
        }
    }

    public static MessagePolicy handle(Communicator communicator, String message, String title) {
        if (message.equals("#testplanters")) {
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
            return MessagePolicy.DISCARD;
        } else if (message.equals("#deleteplanters")) {
            Arrays.stream(Items.getAllItems())
                    .filter(PlanterItem::isPlanter)
                    .forEach(i -> Items.destroyItem(i.getWurmId()));
            communicator.sendNormalServerMessage("Deleted all planters.");
            return MessagePolicy.DISCARD;
        } else if (message.startsWith("#planterwinter")) {
            String[] args = message.split(" ");
            boolean needsReload = true;
            if (args.length < 2 || (!args[1].equalsIgnoreCase("on") && !args[1].equalsIgnoreCase("off") && !args[1].equalsIgnoreCase("disable"))) {
                communicator.sendNormalServerMessage("Usage: #planterwinter <on|off|disable>");
                needsReload = false;
            } else if (args[1].equalsIgnoreCase("on")) {
                forceWinter = Optional.of(true);
                communicator.sendNormalServerMessage("Winter forced to ON");
            } else if (args[1].equalsIgnoreCase("off")) {
                forceWinter = Optional.of(false);
                communicator.sendNormalServerMessage("Winter forced to OFF");
            } else {
                forceWinter = Optional.empty();
                communicator.sendNormalServerMessage("Winter returned to normal");
            }
            if (needsReload) {
                try {
                    communicator.player.createVisionArea();
                    Server.getInstance().addCreatureToPort(communicator.player);
                } catch (Exception e) {
                    PlantersMod.logException("error in createVisionArea", e);
                }
            }
            return MessagePolicy.DISCARD;
        }
        return MessagePolicy.PASS;
    }
}
