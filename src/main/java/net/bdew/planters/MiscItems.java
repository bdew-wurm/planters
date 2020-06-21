package net.bdew.planters;

import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.shared.constants.IconConstants;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;

import java.io.IOException;

public class MiscItems {
    public static int stumpId, basketEmptyId, basketClothId, basketMixedId, basketMagicId, magicShroomId;
    public static ItemTemplate stump, basketEmpty, basketCloth, basketMixed, basketMagic, magicShroom;

    private static void registerStump() throws IOException {
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
    }

    private static void registerBasketEmpty() throws IOException {
        basketEmpty = new ItemTemplateBuilder("bdew.planters.basket.empty")
                .name("wicker basket", "wicker baskets", "A wicker basket. Can be used to pack your picnic food or any other miscellaneous items.")
                .modelName("model.container.basket.")
                .imageNumber((short) IconConstants.ICON_CONTAINER_XMAS_LUNCHBOX)
                .itemTypes(new short[]{
                        ItemTypes.ITEM_TYPE_HOLLOW,
                        ItemTypes.ITEM_TYPE_WOOD,
                        ItemTypes.ITEM_TYPE_PLANTABLE,
                        ItemTypes.ITEM_TYPE_DECORATION,
                        ItemTypes.ITEM_TYPE_TURNABLE,
                        ItemTypes.ITEM_TYPE_REPAIRABLE,
                        ItemTypes.ITEM_TYPE_COLORABLE,
                        ItemTypes.ITEM_TYPE_NAMED,
                })
                .material(Materials.MATERIAL_WOOD_BIRCH)
                .decayTime(3024000L)
                .dimensions(30, 50, 50)
                .weightGrams(300)
                .behaviourType((short) 1)
                .primarySkill(SkillList.CARPENTRY_FINE)
                .difficulty(30)
                .build();

        basketEmpty.setDyeAmountGrams(100);

        basketEmptyId = basketEmpty.getTemplateId();

        CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY_FINE, ItemList.shaft, ItemList.shaft, basketEmptyId, false, false, 0f, true, true, CreationCategories.CONTAINER)
                .addRequirement(new CreationRequirement(1, ItemList.thatch, 5, true));

    }

    private static void registerBasketCloth() throws IOException {
        basketCloth = new ItemTemplateBuilder("bdew.planters.basket.cloth")
                .name("wicker and fabric basket", "wicker baskets", "A wicker basket with some fabric. Can be used to pack your picnic food or any other miscellaneous items.")
                .modelName("model.container.basket.fabric.")
                .imageNumber((short) IconConstants.ICON_CONTAINER_XMAS_LUNCHBOX)
                .itemTypes(new short[]{
                        ItemTypes.ITEM_TYPE_HOLLOW,
                        ItemTypes.ITEM_TYPE_WOOD,
                        ItemTypes.ITEM_TYPE_PLANTABLE,
                        ItemTypes.ITEM_TYPE_DECORATION,
                        ItemTypes.ITEM_TYPE_TURNABLE,
                        ItemTypes.ITEM_TYPE_REPAIRABLE,
                        ItemTypes.ITEM_TYPE_COLORABLE,
                        ItemTypes.ITEM_TYPE_NAMED,
                        ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR,
                })
                .material(Materials.MATERIAL_WOOD_BIRCH)
                .decayTime(3024000L)
                .dimensions(30, 50, 50)
                .weightGrams(300)
                .behaviourType((short) 1)
                .primarySkill(SkillList.CARPENTRY_FINE)
                .difficulty(30)
                .build();

        basketCloth.setDyeAmountGrams(100, 80);
        basketCloth.setSecondryItem("cloth");

        basketClothId = basketCloth.getTemplateId();

        CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY_FINE, ItemList.shaft, ItemList.shaft, basketClothId, false, false, 0f, true, true, CreationCategories.CONTAINER)
                .addRequirement(new CreationRequirement(1, ItemList.thatch, 5, true))
                .addRequirement(new CreationRequirement(2, ItemList.clothYard, 1, true));
    }

    private static void registerBasketMixed() throws IOException {
        basketMixed = new ItemTemplateBuilder("bdew.planters.basket.mixed")
                .name("mixed mushroom basket", "mixed mushroom baskets", "A decorative basket containing a selection of mushrooms.")
                .modelName("model.container.basket.mushroom.")
                .imageNumber((short) IconConstants.ICON_CONTAINER_XMAS_LUNCHBOX)
                .itemTypes(new short[]{
                        ItemTypes.ITEM_TYPE_WOOD,
                        ItemTypes.ITEM_TYPE_PLANTABLE,
                        ItemTypes.ITEM_TYPE_DECORATION,
                        ItemTypes.ITEM_TYPE_TURNABLE,
                        ItemTypes.ITEM_TYPE_REPAIRABLE,
                        ItemTypes.ITEM_TYPE_COLORABLE,
                        ItemTypes.ITEM_TYPE_NAMED,
                        ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR,
                })
                .material(Materials.MATERIAL_WOOD_BIRCH)
                .decayTime(3024000L)
                .dimensions(30, 50, 50)
                .weightGrams(300)
                .behaviourType((short) 1)
                .primarySkill(SkillList.CARPENTRY_FINE)
                .difficulty(30)
                .build();

        basketMixed.setDyeAmountGrams(100, 80);
        basketMixed.setSecondryItem("cloth");

        basketMixedId = basketMixed.getTemplateId();

        CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY_FINE, ItemList.shaft, ItemList.shaft, basketMixedId, false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.thatch, 5, true))
                .addRequirement(new CreationRequirement(2, ItemList.clothYard, 1, true))
                .addRequirement(new CreationRequirement(3, ItemList.mushroomBlack, 1, true))
                .addRequirement(new CreationRequirement(4, ItemList.mushroomBlue, 1, true))
                .addRequirement(new CreationRequirement(5, ItemList.mushroomBrown, 1, true))
                .addRequirement(new CreationRequirement(6, ItemList.mushroomGreen, 1, true))
                .addRequirement(new CreationRequirement(7, ItemList.mushroomRed, 1, true))
                .addRequirement(new CreationRequirement(8, ItemList.mushroomYellow, 1, true));
    }

    private static void registerBasketMagic() throws IOException {
        basketMagic = new ItemTemplateBuilder("bdew.planters.basket.magic")
                .name("magic mushroom basket", "magic mushroom baskets", "A decorative basket containing a pile of magic mushrooms.")
                .modelName("model.container.basket.mushroom.magic.")
                .imageNumber((short) IconConstants.ICON_CONTAINER_XMAS_LUNCHBOX)
                .itemTypes(new short[]{
                        ItemTypes.ITEM_TYPE_WOOD,
                        ItemTypes.ITEM_TYPE_PLANTABLE,
                        ItemTypes.ITEM_TYPE_DECORATION,
                        ItemTypes.ITEM_TYPE_TURNABLE,
                        ItemTypes.ITEM_TYPE_REPAIRABLE,
                        ItemTypes.ITEM_TYPE_COLORABLE,
                        ItemTypes.ITEM_TYPE_NAMED,
                        ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR,
                })
                .material(Materials.MATERIAL_WOOD_BIRCH)
                .decayTime(3024000L)
                .dimensions(30, 50, 50)
                .weightGrams(300)
                .behaviourType((short) 1)
                .primarySkill(SkillList.CARPENTRY_FINE)
                .difficulty(30)
                .build();

        basketMagic.setDyeAmountGrams(100, 80);
        basketMagic.setSecondryItem("cloth");

        basketMagicId = basketMagic.getTemplateId();

        CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY_FINE, ItemList.shaft, ItemList.shaft, basketMagicId, false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.thatch, 5, true))
                .addRequirement(new CreationRequirement(2, ItemList.clothYard, 1, true))
                .addRequirement(new CreationRequirement(3, magicShroomId, 5, true));
    }

    private static void registerMagicShroom() throws IOException {
        magicShroom = new ItemTemplateBuilder("bdew.planters.magicshroom")
                .name("magic mushroom", "magic mushroom", "A weird looking mushroom. It sparkles with magical energy.")
                .modelName("model.food.mushroom.magic.")
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


    public static void register() throws IOException {
        registerStump();
        registerBasketEmpty();
        registerBasketCloth();
        registerBasketMixed();

        if (PlantersMod.magicMushrooms) {
            registerMagicShroom();
            registerBasketMagic();
        }
    }

    public static float stumpSizeMod(Item item) {
        return item.getAuxData() + 1;
    }
}
