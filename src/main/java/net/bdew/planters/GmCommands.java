package net.bdew.planters;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.Items;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.items.*;
import com.wurmonline.shared.constants.ItemMaterials;
import org.gotti.wurmunlimited.modloader.interfaces.MessagePolicy;

import java.util.Arrays;
import java.util.Optional;
import java.util.StringTokenizer;

public class GmCommands {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<Boolean> forceWinter = Optional.empty();

    private static void spawnTestPlanter(int id, int tileX, int tileY, byte material, Plantable plant, int age, boolean tended, float damage) {
        try {
            Item itm = ItemFactory.createItem(id, 99f, tileX * 4f + 2f, tileY * 4f + 2f, 0, true, material, (byte) 0, -10L, null);
            itm.setDamage(damage);
            if (plant != null)
                PlanterItem.updateData(itm, plant, age, tended, 0, 0);
        } catch (NoSuchTemplateException | FailedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void spawnBasePlanters(int id, int tileX, int tileY, byte material) {
        spawnTestPlanter(id, tileX, tileY++, material, null, 0, false, 0);
        spawnTestPlanter(id, tileX, tileY++, material, null, 0, false, 75);
        try {
            Item itm = ItemFactory.createItem(ItemList.unfinishedItem, 99f, tileX * 4f + 2f, tileY * 4f + 2f, 0, true, material, (byte) 0, -10L, null);
            itm.setRealTemplate(id);
            itm.updateIfGroundItem();
        } catch (NoSuchTemplateException | FailedException e) {
            throw new RuntimeException(e);
        }

    }

    private static int spawnPlantersRow(int x, int y, int tpl, byte material, Plantable plant) {
        if (plant.planterType == PlanterType.TREE || plant.planterType == PlanterType.BUSH) {
            spawnTestPlanter(tpl, x, y += 2, material, plant, 0, false, 0);
            spawnTestPlanter(tpl, x, y += 2, material, plant, 1, false, 0);
            spawnTestPlanter(tpl, x, y += 2, material, plant, 3, false, 0);
            spawnTestPlanter(tpl, x, y += 2, material, plant, 5, false, 0);
            spawnTestPlanter(tpl, x, y += 2, material, plant, 0, false, 75f);
            spawnTestPlanter(tpl, x, y += 2, material, plant, 1, false, 75f);
            spawnTestPlanter(tpl, x, y += 2, material, plant, 3, false, 75f);
            spawnTestPlanter(tpl, x, y += 2, material, plant, 5, false, 75f);
        } else {
            spawnTestPlanter(tpl, x, y++, material, plant, 3, false, 0);
            spawnTestPlanter(tpl, x, y++, material, plant, 3, true, 0);
            spawnTestPlanter(tpl, x, y++, material, plant, 5, false, 0);
            spawnTestPlanter(tpl, x, y++, material, plant, 6, false, 0);
            spawnTestPlanter(tpl, x, y++, material, plant, 3, false, 75f);
            spawnTestPlanter(tpl, x, y++, material, plant, 3, true, 75f);
            spawnTestPlanter(tpl, x, y++, material, plant, 5, false, 75f);
            spawnTestPlanter(tpl, x, y++, material, plant, 6, false, 75f);
        }
        return y;
    }

    private static void spawnPlanters(Communicator communicator) {
        int py = communicator.player.getTileY();
        int x = communicator.player.getTileX();
        int y = py;

        spawnBasePlanters(PlanterItem.woodId, x++, y, ItemMaterials.MATERIAL_WOOD_CEDAR);
        spawnBasePlanters(PlanterItem.stoneId, x++, y, ItemMaterials.MATERIAL_STONE);

        if (PlantersMod.magicMushrooms) {
            spawnBasePlanters(PlanterItem.magicWoodId, x++, y, ItemMaterials.MATERIAL_WOOD_CEDAR);
            spawnBasePlanters(PlanterItem.magicStoneId, x++, y, ItemMaterials.MATERIAL_STONE);
        }

        spawnBasePlanters(PlanterItem.treeWoodId, x++, y, ItemMaterials.MATERIAL_WOOD_CEDAR);
        spawnBasePlanters(PlanterItem.treeStoneId, x++, y, ItemMaterials.MATERIAL_STONE);
        spawnBasePlanters(PlanterItem.bushWoodId, x++, y, ItemMaterials.MATERIAL_WOOD_CEDAR);
        spawnBasePlanters(PlanterItem.bushStoneId, x++, y, ItemMaterials.MATERIAL_STONE);

        for (Plantable plant : Plantable.values()) {
            y = py;
            if (plant.planterType == PlanterType.NORMAL) {
                y = spawnPlantersRow(x, y, PlanterItem.woodId, ItemMaterials.MATERIAL_WOOD_CEDAR, plant);
                y = spawnPlantersRow(x, y, PlanterItem.stoneId, ItemMaterials.MATERIAL_STONE, plant);
            } else if (plant.planterType == PlanterType.MAGIC && PlantersMod.magicMushrooms) {
                y = spawnPlantersRow(x, y, PlanterItem.magicWoodId, ItemMaterials.MATERIAL_WOOD_CEDAR, plant);
                y = spawnPlantersRow(x, y, PlanterItem.magicStoneId, ItemMaterials.MATERIAL_STONE, plant);
            } else if (plant.planterType == PlanterType.TREE) {
                y = spawnPlantersRow(x, y, PlanterItem.treeWoodId, ItemMaterials.MATERIAL_WOOD_CEDAR, plant) + 1;
                y = spawnPlantersRow(x, y, PlanterItem.treeStoneId, ItemMaterials.MATERIAL_STONE, plant);
                x++;
            } else if (plant.planterType == PlanterType.BUSH) {
                y = spawnPlantersRow(x, y, PlanterItem.bushWoodId, ItemMaterials.MATERIAL_WOOD_CEDAR, plant) + 1;
                y = spawnPlantersRow(x, y, PlanterItem.bushStoneId, ItemMaterials.MATERIAL_STONE, plant);
                x++;
            }
            x++;
        }

        communicator.sendNormalServerMessage("Spawned test planters.");
    }

    private static void deletePlanters(Communicator communicator) {
        Arrays.stream(Items.getAllItems())
                .filter(item -> PlanterItem.isPlanter(item) || (item.getTemplateId() == ItemList.unfinishedItem && PlanterItem.isPlanter(item.getRealTemplateId())))
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

    private static void setInfected(Communicator communicator, String arg) {
        boolean infected;
        if (arg.equalsIgnoreCase("on")) {
            infected = true;
            communicator.sendNormalServerMessage("All planters are now infected.");
        } else if (arg.equalsIgnoreCase("off")) {
            infected = false;
            communicator.sendNormalServerMessage("All planters are now clean.");
        } else {
            communicator.sendAlertServerMessage("Usage: #planters infected <on|off>");
            return;
        }
        Arrays.stream(Items.getAllItems())
                .filter(PlanterItem::isPlanter)
                .forEach(i -> PlanterItem.setInfected(i, infected));
    }

    public static MessagePolicy handle(Communicator communicator, String message, String title) {
        if (message.startsWith("#planters")) {
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
                    case "infected":
                        if (tokens.hasMoreTokens()) {
                            setInfected(communicator, tokens.nextToken());
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
            communicator.sendAlertServerMessage(" #planters infected <on|off>");
            return MessagePolicy.DISCARD;
        }
        return MessagePolicy.PASS;
    }
}
