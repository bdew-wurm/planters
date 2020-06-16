package net.bdew.planters;

import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.shared.constants.IconConstants;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.items.ModItems;

import java.io.IOException;

public class PlanterItem {
    public static ItemTemplate woodTemplate, stoneTemplate;
    public static int woodId, stoneId;

    public static final String BASEMODEL = "model.structure.farmbox.";

    private static final String[] AGES = new String[]{
            "freshly sown",
            "sprouting",
            "growing",
            "halfway",
            "almost ripe",
            "ripe",
            "wilted"
    };

    public static void register() throws IOException {
        woodTemplate = new ItemTemplateBuilder("bdew.planters.wood")
                .name("large planter", "large planters", "A large wooden planter suitable for growing crops.")
                .modelName(BASEMODEL)
                .imageNumber((short) IconConstants.ICON_MARBLE_PLANTER)
                .itemTypes(new short[]{
                        ItemTypes.ITEM_TYPE_WOOD,
                        ItemTypes.ITEM_TYPE_REPAIRABLE,
                        ItemTypes.ITEM_TYPE_PLANTABLE,
                        ItemTypes.ITEM_TYPE_TRANSPORTABLE,
                        ItemTypes.ITEM_TYPE_NOTAKE,
                        ItemTypes.ITEM_TYPE_TURNABLE,
                        ItemTypes.ITEM_TYPE_NOMOVE,
                        ItemTypes.ITEM_TYPE_ONE_PER_TILE,
                        ItemTypes.ITEM_TYPE_TILE_ALIGNED,
                        ItemTypes.ITEM_TYPE_HASDATA,
                        ItemTypes.ITEM_TYPE_NORENAME,
                        ItemTypes.ITEM_TYPE_COLORABLE,
                })
                .decayTime(9072000L)
                .dimensions(200, 200, 30)
                .weightGrams(10000)
                .material(Materials.MATERIAL_WOOD_BIRCH)
                .behaviourType((short) 1)
                .primarySkill(SkillList.CARPENTRY)
                .difficulty(70)
                .build();

        woodTemplate.setDyeAmountGrams(1000);

        woodId = woodTemplate.getTemplateId();

        stoneTemplate = new ItemTemplateBuilder("bdew.planters.stone")
                .name("large stone planter", "large planters", "A large stone planter suitable for growing crops.")
                .modelName(BASEMODEL)
                .imageNumber((short) IconConstants.ICON_MARBLE_PLANTER)
                .itemTypes(new short[]{
                        ItemTypes.ITEM_TYPE_STONE,
                        ItemTypes.ITEM_TYPE_REPAIRABLE,
                        ItemTypes.ITEM_TYPE_PLANTABLE,
                        ItemTypes.ITEM_TYPE_TRANSPORTABLE,
                        ItemTypes.ITEM_TYPE_NOTAKE,
                        ItemTypes.ITEM_TYPE_TURNABLE,
                        ItemTypes.ITEM_TYPE_NOMOVE,
                        ItemTypes.ITEM_TYPE_ONE_PER_TILE,
                        ItemTypes.ITEM_TYPE_TILE_ALIGNED,
                        ItemTypes.ITEM_TYPE_HASDATA,
                        ItemTypes.ITEM_TYPE_NORENAME,
                        ItemTypes.ITEM_TYPE_COLORABLE,
                })
                .decayTime(9072000L)
                .dimensions(200, 200, 30)
                .weightGrams(20000)
                .material(Materials.MATERIAL_STONE)
                .behaviourType((short) 1)
                .primarySkill(SkillList.MASONRY)
                .difficulty(70)
                .build();

        stoneTemplate.setDyeAmountGrams(1000);

        stoneId = stoneTemplate.getTemplateId();

        CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY, ItemList.woodBeam, ItemList.woodBeam, woodId, false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.woodBeam, 9, true))
                .addRequirement(new CreationRequirement(2, ItemList.nailsIronLarge, 4, true))
                .addRequirement(new CreationRequirement(3, ItemList.dirtPile, 1, true));

        CreationEntryCreator.createAdvancedEntry(SkillList.MASONRY, ItemList.rock, ItemList.rock, stoneId, false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.rock, 4, true))
                .addRequirement(new CreationRequirement(2, ItemList.mortar, 2, true))
                .addRequirement(new CreationRequirement(3, ItemList.dirtPile, 1, true));

        ModItems.addModelNameProvider(woodId, new PlanterModelProvider());
        ModItems.addModelNameProvider(stoneId, new PlanterModelProvider());
    }

    public static boolean isPlanter(int templateId) {
        return templateId == woodId || templateId == stoneId;
    }

    public static boolean isPlanter(Item item) {
        return isPlanter(item.getTemplateId());
    }

    public static void updateData(Item item, Plantable crop, int growthStage, boolean tended, int tendCount, int tendPower) {
        StringBuilder description = new StringBuilder();

        if (growthStage >= 0 && growthStage <= 6) {
            description.append(AGES[growthStage]);
            if (growthStage < 5 && !tended) {
                description.append(", ").append("untended");
            }
        }

        VolaTile vt = Zones.getOrCreateTile(item.getTilePos(), item.isOnSurface());
        vt.makeInvisible(item);
        item.setAuxData((byte) crop.number);
        item.setName(item.getTemplate().getName() + " - " + crop.displayName);
        item.setDescription(description.toString());
        item.setData((growthStage & 0xFF) | (tended ? 0x100 : 0), (tendCount & 0xFF) | (tendPower << 8));
        vt.makeVisible(item);
    }

    public static void clearData(Item item) {
        VolaTile vt = Zones.getOrCreateTile(item.getTilePos(), item.isOnSurface());
        vt.makeInvisible(item);
        item.setAuxData((byte) 0);
        item.setData(0, 0);
        item.setName(item.getTemplate().getName());
        item.setDescription("");
        vt.makeVisible(item);
    }

    public static Plantable getPlantable(Item item) {
        if (item.getAuxData() <= 0) return null;
        return Plantable.getFromId(item.getAuxData());
    }

    public static int getGrowthStage(Item item) {
        return item.getData1() & 0xFF;
    }

    public static boolean isTended(Item item) {
        return (item.getData1() & 0x100) != 0;
    }

    public static int getTendCount(Item item) {
        return item.getData2() & 0xFF;
    }

    public static int getTendPower(Item item) {
        return item.getData2() >> 8;
    }

    public static boolean needsPolling(Item item) {
        return (item.getTemplateId() == woodId || item.getTemplateId() == stoneId) &&
                item.getParentId() == -10L && item.getAuxData() > 0 &&
                getGrowthStage(item) < (PlantersMod.canWilt ? 6 : 5);
    }
}
