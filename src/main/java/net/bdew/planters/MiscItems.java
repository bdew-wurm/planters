package net.bdew.planters;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.items.Materials;
import com.wurmonline.shared.constants.IconConstants;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;

import java.io.IOException;

public class MiscItems {
    public static int stumpId, magicShroomId;
    public static ItemTemplate stump, magicShroom;

    public static void register() throws IOException {
        stump = new ItemTemplateBuilder("bdew.planters.stump")
                .name("tree stump", "stumps", "A stump that used to be a tree, now it just sits here in sadness.")
                .modelName("model.stump.")
                .imageNumber((short) IconConstants.ICON_WOOD_LOG)
                .itemTypes(new short[]{
                        ItemTypes.ITEM_TYPE_WOOD,
                        ItemTypes.ITEM_TYPE_REPAIRABLE,
                        ItemTypes.ITEM_TYPE_PLANTABLE,
                        ItemTypes.ITEM_TYPE_TRANSPORTABLE,
                        ItemTypes.ITEM_TYPE_DECORATION,
                        ItemTypes.ITEM_TYPE_TURNABLE,
                        ItemTypes.ITEM_TYPE_COLORABLE,
                })
                .decayTime(9072000L)
                .dimensions(40, 40, 200)
                .weightGrams(40000)
                .material(Materials.MATERIAL_WOOD_BIRCH)
                .behaviourType((short) 1)
                .build();

        stumpId = stump.getTemplateId();

        if (PlantersMod.magicMushrooms) {
            magicShroom = new ItemTemplateBuilder("bdew.planters.magicshroom")
                    .name("magic mushroom", "magic mushroom", "A weird looking mushroom. It sparkles with magical energy.")
                    .modelName("model.food.mushroom.black.")
                    .imageNumber((short) IconConstants.ICON_FOOD_MUSHROOM_BLACK)
                    .itemTypes(new short[]{
                            ItemTypes.ITEM_TYPE_FOOD,
                            ItemTypes.ITEM_TYPE_PLANTABLE,
                            ItemTypes.ITEM_TYPE_DECORATION_WHEN_PLANTED,
                            ItemTypes.ITEM_TYPE_TURNABLE,
                            ItemTypes.ITEM_TYPE_BULK,
                    })
                    .decayTime(28800L)
                    .dimensions(1, 1, 1)
                    .weightGrams(10)
                    .behaviourType((short) 1)
                    .build();

            magicShroomId = magicShroom.getTemplateId();
        }
    }

    public static float stumpSizeMod(Item item) {
        return item.getAuxData() + 1;
    }
}
