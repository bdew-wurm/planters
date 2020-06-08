package net.bdew.planters;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.items.*;
import org.gotti.wurmunlimited.modloader.interfaces.MessagePolicy;

import java.util.Arrays;

public class GmCommands {
    private static void spawnTestPlanter(int tileX, int tileY, Plantable plant, int age, boolean tended, float damage) {
        try {
            Item itm = ItemFactory.createItem(PlanterItem.id, 99f, tileX * 4f + 2f, tileY * 4f + 2f, 0, true, Materials.MATERIAL_WOOD_BIRCH, (byte) 0, -10L, null);
            itm.setDamage(damage);
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

            spawnTestPlanter(x, y++, null, 0, false, 0);
            spawnTestPlanter(x, y++, null, 0, false, 75);

            try {
                Item itm = ItemFactory.createItem(ItemList.unfinishedItem, 99f, x * 4f + 2f, y * 4f + 2f, 0, true, Materials.MATERIAL_WOOD_BIRCH, (byte) 0, -10L, null);
                itm.setRealTemplate(PlanterItem.id);
            } catch (NoSuchTemplateException | FailedException e) {
                throw new RuntimeException(e);
            }

            for (Plantable plant : Plantable.values()) {
                x++;
                y = py;
                spawnTestPlanter(x, y++, plant, 3, false, 0);
                spawnTestPlanter(x, y++, plant, 3, true, 0);
                spawnTestPlanter(x, y++, plant, 5, false, 0);
                spawnTestPlanter(x, y++, plant, 6, false, 0);
                spawnTestPlanter(x, y++, plant, 3, false, 75f);
                spawnTestPlanter(x, y++, plant, 3, true, 75f);
                spawnTestPlanter(x, y++, plant, 5, false, 75f);
                spawnTestPlanter(x, y++, plant, 6, false, 75f);
            }
            communicator.sendNormalServerMessage("Spawned test planters.");
            return MessagePolicy.DISCARD;
        } else if (message.equals("#deleteplanters")) {
            Arrays.stream(Items.getAllItems())
                    .filter(i -> i.getTemplateId() == PlanterItem.id)
                    .forEach(i -> Items.destroyItem(i.getWurmId()));
            communicator.sendNormalServerMessage("Deleted all planters.");
            return MessagePolicy.DISCARD;
        }
        return MessagePolicy.PASS;
    }
}
