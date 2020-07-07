package net.bdew.planters;

import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.zones.VirtualZone;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.shared.constants.IconConstants;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.items.ModItems;
import org.gotti.wurmunlimited.modsupport.items.ModelNameProvider;

import java.io.IOException;
import java.util.Arrays;

public class PlanterItem {
    public static ItemTemplate wood, stone, magicWood, magicStone;
    public static int woodId, stoneId, magicWoodId, magicStoneId;

    public static final String BASEMODEL = "model.structure.farmbox.";

    private static final int FLAG_TENDED = 0x100;
    private static final int FLAG_INFECTED = 0x200;

    private static final String[] AGES = new String[]{
            "freshly sown",
            "sprouting",
            "growing",
            "halfway",
            "almost ripe",
            "ripe",
            "wilted"
    };

    private static void registerNormalWood(ModelNameProvider modelProvider) throws IOException {
        wood = new ItemTemplateBuilder("bdew.planters.wood")
                .name("large planter", "large wood planters", "A large wooden planter suitable for growing crops.")
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
                        ItemTypes.ITEM_TYPE_DECORATION,
                })
                .decayTime(9072000L)
                .dimensions(200, 200, 30)
                .weightGrams(10000)
                .material(Materials.MATERIAL_WOOD_BIRCH)
                .behaviourType((short) 1)
                .primarySkill(SkillList.CARPENTRY)
                .difficulty(70)
                .build();

        wood.setDyeAmountGrams(1000);

        woodId = wood.getTemplateId();

        CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY, ItemList.woodBeam, ItemList.woodBeam, woodId, false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.woodBeam, 9, true))
                .addRequirement(new CreationRequirement(2, ItemList.nailsIronLarge, 4, true))
                .addRequirement(new CreationRequirement(3, ItemList.dirtPile, 1, true));

        ModItems.addModelNameProvider(woodId, modelProvider);
    }

    private static void registerNormalStone(ModelNameProvider modelProvider) throws IOException {
        stone = new ItemTemplateBuilder("bdew.planters.stone")
                .name("large stone planter", "large stone planters", "A large stone planter suitable for growing crops.")
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
                        ItemTypes.ITEM_TYPE_DECORATION,
                })
                .decayTime(9072000L)
                .dimensions(200, 200, 30)
                .weightGrams(20000)
                .material(Materials.MATERIAL_STONE)
                .behaviourType((short) 1)
                .primarySkill(SkillList.MASONRY)
                .difficulty(70)
                .build();

        stone.setDyeAmountGrams(1000);

        stoneId = stone.getTemplateId();

        CreationEntryCreator.createAdvancedEntry(SkillList.MASONRY, ItemList.rock, ItemList.rock, stoneId, false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.rock, 4, true))
                .addRequirement(new CreationRequirement(2, ItemList.mortar, 2, true))
                .addRequirement(new CreationRequirement(3, ItemList.dirtPile, 1, true));

        ModItems.addModelNameProvider(stoneId, modelProvider);
    }


    private static void registerMagicWood(ModelNameProvider modelProvider) throws IOException {
        magicWood = new ItemTemplateBuilder("bdew.planters.wood.magic")
                .name("magic planter", "magic wood planters", "A large wooden planter suitable for growing magical mushrooms.")
                .modelName(BASEMODEL + "magic.")
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
                        ItemTypes.ITEM_TYPE_DECORATION,
                })
                .decayTime(9072000L)
                .dimensions(200, 200, 30)
                .weightGrams(10000)
                .material(Materials.MATERIAL_WOOD_BIRCH)
                .behaviourType((short) 1)
                .primarySkill(SkillList.CARPENTRY)
                .difficulty(70)
                .build();

        magicWood.setDyeAmountGrams(1000);

        magicWoodId = magicWood.getTemplateId();

        CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY, ItemList.woodBeam, ItemList.woodBeam, magicWoodId, false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.woodBeam, 9, true))
                .addRequirement(new CreationRequirement(2, ItemList.nailsIronLarge, 4, true))
                .addRequirement(new CreationRequirement(3, ItemList.moss, 5, true))
                .addRequirement(new CreationRequirement(4, MiscItems.stumpId, 1, true))
                .addRequirement(new CreationRequirement(5, ItemList.sourceCrystal, 4, true));

        ModItems.addModelNameProvider(magicWoodId, modelProvider);
    }

    private static void registerMagicStone(ModelNameProvider modelProvider) throws IOException {
        magicStone = new ItemTemplateBuilder("bdew.planters.stone.magic")
                .name("magic stone planter", "magic stone planters", "A large stone planter suitable for growing magical mushrooms.")
                .modelName(BASEMODEL + "magic.")
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
                        ItemTypes.ITEM_TYPE_DECORATION,
                })
                .decayTime(9072000L)
                .dimensions(200, 200, 30)
                .weightGrams(20000)
                .material(Materials.MATERIAL_STONE)
                .behaviourType((short) 1)
                .primarySkill(SkillList.MASONRY)
                .difficulty(70)
                .build();

        magicStone.setDyeAmountGrams(1000);

        magicStoneId = magicStone.getTemplateId();

        CreationEntryCreator.createAdvancedEntry(SkillList.MASONRY, ItemList.rock, ItemList.rock, magicStoneId, false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.rock, 4, true))
                .addRequirement(new CreationRequirement(2, ItemList.mortar, 2, true))
                .addRequirement(new CreationRequirement(3, ItemList.moss, 5, true))
                .addRequirement(new CreationRequirement(4, MiscItems.stumpId, 1, true))
                .addRequirement(new CreationRequirement(5, ItemList.sourceCrystal, 4, true));

        ModItems.addModelNameProvider(magicStoneId, modelProvider);
    }

    public static void register() throws IOException {
        ModelNameProvider normalModelProvider = new PlanterModelProvider(PlanterItem.BASEMODEL);
        registerNormalWood(normalModelProvider);
        registerNormalStone(normalModelProvider);
        if (PlantersMod.magicMushrooms) {
            ModelNameProvider magicModelProvider = new PlanterModelProvider(PlanterItem.BASEMODEL + "magic.");
            registerMagicWood(magicModelProvider);
            registerMagicStone(magicModelProvider);
        }
    }

    public static boolean isPlanter(int templateId) {
        return templateId == woodId || templateId == stoneId || (PlantersMod.magicMushrooms && (templateId == magicStoneId || templateId == magicWoodId));
    }

    public static boolean isPlanter(Item item) {
        return isPlanter(item.getTemplateId());
    }

    public static void updateData(Item item, Plantable crop, int growthStage, boolean tended, int tendCount, int tendPower) {
        VolaTile vt = Zones.getOrCreateTile(item.getTilePos(), item.isOnSurface());
        vt.makeInvisible(item);
        item.setAuxData((byte) crop.number);
        boolean infected = isInfected(item);
        item.setData((growthStage & 0xFF) | (tended ? FLAG_TENDED : 0) | (infected ? FLAG_INFECTED : 0), (tendCount & 0xFF) | (tendPower << 8));
        updateName(item, crop, growthStage, tended, infected);
        vt.makeVisible(item);
    }

    public static void clearData(Item item) {
        VolaTile vt = Zones.getOrCreateTile(item.getTilePos(), item.isOnSurface());
        vt.makeInvisible(item);
        item.setAuxData((byte) 0);
        boolean infected = isInfected(item);
        item.setData((infected ? FLAG_INFECTED : 0), 0);
        updateName(item, null, 0, false, infected);
        vt.makeVisible(item);
    }

    public static void updateName(Item item, Plantable crop, int growthStage, boolean tended, boolean infected) {
        StringBuilder nameBuilder = new StringBuilder();
        StringBuilder descBuilder = new StringBuilder();

        if (infected) nameBuilder.append("infected ");
        nameBuilder.append(item.getTemplate().getName());

        if (crop != null) {
            nameBuilder.append(" - ").append(crop.displayName);
            if (growthStage >= 0 && growthStage <= 6) {
                descBuilder.append(AGES[growthStage]);
                if (growthStage < 5 && !tended) {
                    descBuilder.append(", ").append("untended");
                }
            }
        }

        item.setName(nameBuilder.toString());
        item.setDescription(descBuilder.toString());
    }

    public static Plantable getPlantable(Item item) {
        if (item.getAuxData() <= 0) return null;
        return Plantable.getFromId(item.getAuxData());
    }

    public static int getGrowthStage(Item item) {
        return item.getData1() & 0xFF;
    }

    public static boolean isTended(Item item) {
        return (item.getData1() & FLAG_TENDED) != 0;
    }

    public static boolean isInfected(Item item) {
        return (item.getData1() != -1) && (item.getData1() & FLAG_INFECTED) != 0;
    }

    public static void setInfected(Item item, boolean infected) {
        VolaTile vt = Zones.getOrCreateTile(item.getTilePos(), item.isOnSurface());
        vt.makeInvisible(item);
        Plantable crop = getPlantable(item);
        int growthStage = getGrowthStage(item);
        boolean tended = isTended(item);
        item.setData1((growthStage & 0xFF) | (tended ? FLAG_TENDED : 0) | (infected ? FLAG_INFECTED : 0));
        updateName(item, crop, growthStage, tended, infected);
        vt.makeVisible(item);

        Arrays.stream(vt.getWatchers())
                .map(VirtualZone::getWatcher)
                .filter(creature -> creature != null && creature.isPlayer())
                .forEach(p -> p.getCommunicator().sendAttachEffect(item.getWurmId(), (byte) (infected ? 8 : 11), (byte) 0, (byte) 0, (byte) 0, (byte) 0));
    }

    public static int getTendCount(Item item) {
        return item.getData2() & 0xFF;
    }

    public static int getTendPower(Item item) {
        return item.getData2() >> 8;
    }

    public static boolean needsPolling(Item item) {
        return isPlanter(item) && item.getParentId() == -10L && item.getAuxData() > 0 &&
                getGrowthStage(item) < (PlantersMod.canWilt ? 6 : 5);
    }

    public static PlanterType getPlanterType(int tpl) {
        if (tpl == woodId || tpl == stoneId)
            return PlanterType.NORMAL;
        if (PlantersMod.magicMushrooms && (tpl == magicWoodId || tpl == magicStoneId))
            return PlanterType.MAGIC;
        return null;
    }
}
