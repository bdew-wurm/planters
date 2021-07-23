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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlanterItem {
    public static ItemTemplate normalWood, normalStone;
    public static ItemTemplate magicWood, magicStone;
    public static ItemTemplate treeWood, treeStone, treePottery, treeMarble, treeRendered, treeBrick, treeSandstone, treeSlate;
    public static ItemTemplate bushWood, bushMetal;

    public static final String BASEMODEL = "model.structure.farmbox.";

    private static final int FLAG_TENDED = 0x100;
    private static final int FLAG_INFECTED = 0x200;

    private static final int TREE_FLAG_HARVESTABLE = 0x01;
    private static final int TREE_FLAG_SPROUTING = 0x02;

    public static final String[] AGES = new String[]{
            "freshly sown",
            "sprouting",
            "growing",
            "halfway",
            "almost ripe",
            "ripe",
            "wilted"
    };

    public static final String[] TREE_AGES = new String[]{
            "sprout",
            "young",
            "growing",
            "mature",
            "old",
            "very old",
            "-unused-"
    };


    private static final Map<Integer, PlanterType> typeMap = new HashMap<>();

    private static ItemTemplate registerPlanter(PlanterType type, String id, String modelSuffix, String name, String plural, String desc, int skill, byte material, Utils.ItemTypeSet types) throws IOException {

        ItemTemplate tpl = new ItemTemplateBuilder(id)
                .name(name, plural, desc)
                .modelName(BASEMODEL + modelSuffix)
                .imageNumber((short) IconConstants.ICON_TOOL_CAULDRON)
                .itemTypes(types.array())
                .decayTime(9072000L)
                .dimensions(120, 120, 50)
                .weightGrams(10000)
                .material(material)
                .behaviourType((short) 1)
                .primarySkill(skill)
                .difficulty(70)
                .build();

        if (type == PlanterType.TREE || type == PlanterType.BUSH) {
            tpl.setDyeAmountGrams(1000, 500);
            tpl.setSecondryItem("frame");
        } else {
            tpl.setDyeAmountGrams(1000);
        }

        typeMap.put(tpl.getTemplateId(), type);

        return tpl;
    }

    private static void registerPlanters() throws IOException {
        Utils.ItemTypeSet baseTypes = Utils.ItemTypeSet.from(
                ItemTypes.ITEM_TYPE_REPAIRABLE,
                ItemTypes.ITEM_TYPE_PLANTABLE,
                ItemTypes.ITEM_TYPE_TRANSPORTABLE,
                ItemTypes.ITEM_TYPE_NOTAKE,
                ItemTypes.ITEM_TYPE_TURNABLE,
                ItemTypes.ITEM_TYPE_HASDATA,
                ItemTypes.ITEM_TYPE_NORENAME,
                ItemTypes.ITEM_TYPE_COLORABLE,
                ItemTypes.ITEM_TYPE_DECORATION,
                ItemTypes.ITEM_TYPE_DESTROYABLE
        );

        Utils.ItemTypeSet onePerTile = baseTypes.with(
                ItemTypes.ITEM_TYPE_NOMOVE,
                ItemTypes.ITEM_TYPE_ONE_PER_TILE,
                ItemTypes.ITEM_TYPE_TILE_ALIGNED
        );

        Utils.ItemTypeSet fourPerTile = baseTypes.with(
                ItemTypes.ITEM_TYPE_FOUR_PER_TILE
        );

        normalWood = registerPlanter(PlanterType.NORMAL, "bdew.planters.wood", "",
                "large planter", "wooden large planters", "A large wooden planter suitable for growing crops.",
                SkillList.CARPENTRY, Materials.MATERIAL_WOOD_BIRCH, onePerTile.with(ItemTypes.ITEM_TYPE_WOOD));

        normalStone = registerPlanter(PlanterType.NORMAL, "bdew.planters.stone", "",
                "large stone planter", "stone large planters", "A large stone planter suitable for growing crops.",
                SkillList.MASONRY, Materials.MATERIAL_STONE, onePerTile.with(ItemTypes.ITEM_TYPE_STONE));

        // ==== TREES ====

        treeWood = registerPlanter(PlanterType.TREE, "bdew.planters.wood.tree", "tree.wood.",
                "tree planter", "wooden tree planters", "A large wooden planter suitable for growing trees.",
                SkillList.CARPENTRY, Materials.MATERIAL_WOOD_BIRCH, onePerTile.with(ItemTypes.ITEM_TYPE_WOOD, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR));

        treeStone = registerPlanter(PlanterType.TREE, "bdew.planters.stone.tree", "tree.stone.",
                "stone tree planter", "stone tree planters", "A large stone planter suitable for growing trees.",
                SkillList.MASONRY, Materials.MATERIAL_STONE, onePerTile.with(ItemTypes.ITEM_TYPE_STONE, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR));

        treePottery = registerPlanter(PlanterType.TREE, "bdew.planters.pottery.tree", "tree.pottery.",
                "pottery tree planter", "pottery tree planters", "A large pottery planter suitable for growing trees.",
                SkillList.MASONRY, Materials.MATERIAL_STONE, onePerTile.with(ItemTypes.ITEM_TYPE_POTTERY, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR));

        treeMarble = registerPlanter(PlanterType.TREE, "bdew.planters.marble.tree", "tree.marble.",
                "marble tree planter", "marble tree planters", "A large marble planter suitable for growing trees.",
                SkillList.MASONRY, Materials.MATERIAL_MARBLE, onePerTile.with(ItemTypes.ITEM_TYPE_STONE, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR));

        treeRendered = registerPlanter(PlanterType.TREE, "bdew.planters.clay.tree", "tree.clay.",
                "clay tree planter", "clay tree planters", "A large clay planter suitable for growing trees.",
                SkillList.MASONRY, Materials.MATERIAL_CLAY, onePerTile.with(ItemTypes.ITEM_TYPE_STONE, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR));

        treeBrick = registerPlanter(PlanterType.TREE, "bdew.planters.brick.tree", "tree.brick.",
                "brick tree planter", "brick tree planters", "A large brick planter suitable for growing trees.",
                SkillList.MASONRY, Materials.MATERIAL_STONE, onePerTile.with(ItemTypes.ITEM_TYPE_STONE, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR));

        treeSandstone = registerPlanter(PlanterType.TREE, "bdew.planters.sandstone.tree", "tree.sand.",
                "sandstone tree planter", "sandstone tree planters", "A large sandstone planter suitable for growing trees.",
                SkillList.MASONRY, Materials.MATERIAL_SANDSTONE, onePerTile.with(ItemTypes.ITEM_TYPE_STONE, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR));

        treeSlate = registerPlanter(PlanterType.TREE, "bdew.planters.slate.tree", "tree.slate.",
                "slate tree planter", "slate tree planters", "A large slate planter suitable for growing trees.",
                SkillList.MASONRY, Materials.MATERIAL_SLATE, onePerTile.with(ItemTypes.ITEM_TYPE_STONE, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR));

        // ==== BUSH ====

        bushWood = registerPlanter(PlanterType.BUSH, "bdew.planters.wood.bush", "bush.wood.",
                "bush planter", "wooden bush planters", "A large wooden planter suitable for growing bushs.",
                SkillList.CARPENTRY, Materials.MATERIAL_WOOD_BIRCH, fourPerTile.with(ItemTypes.ITEM_TYPE_WOOD, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR));

        bushMetal = registerPlanter(PlanterType.BUSH, "bdew.planters.metal.bush", "bush.metal.",
                "bush planter", "metal bush planters", "A large metal planter suitable for growing bushs.",
                SkillList.SMITHING_BLACKSMITHING, Materials.MATERIAL_IRON, fourPerTile.with(ItemTypes.ITEM_TYPE_METAL, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR));

        // ==== MAGIC ====

        if (PlantersMod.magicMushrooms) {
            magicWood = registerPlanter(PlanterType.MAGIC, "bdew.planters.wood.magic", "magic.",
                    "magic planter", "wooden magic planters", "A large wooden planter suitable for growing magical mushrooms.",
                    SkillList.CARPENTRY, Materials.MATERIAL_WOOD_BIRCH, onePerTile.with(ItemTypes.ITEM_TYPE_WOOD));

            magicStone = registerPlanter(PlanterType.MAGIC, "bdew.planters.stone.magic", "magic.",
                    "stone magic planter", "stone magic planters", "A large wooden planter suitable for growing magical mushrooms.",
                    SkillList.MASONRY, Materials.MATERIAL_STONE, onePerTile.with(ItemTypes.ITEM_TYPE_STONE));
        }
    }

    private static void addRecipes() {
        CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY, ItemList.woodBeam, ItemList.woodBeam, normalWood.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.woodBeam, 9, true))
                .addRequirement(new CreationRequirement(2, ItemList.nailsIronLarge, 4, true))
                .addRequirement(new CreationRequirement(3, ItemList.dirtPile, 1, true));

        CreationEntryCreator.createAdvancedEntry(SkillList.MASONRY, ItemList.rock, ItemList.rock, normalStone.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.rock, 4, true))
                .addRequirement(new CreationRequirement(2, ItemList.mortar, 2, true))
                .addRequirement(new CreationRequirement(3, ItemList.dirtPile, 1, true));

        CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY, ItemList.plank, ItemList.nailsIronLarge, treeWood.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.plank, 20, true))
                .addRequirement(new CreationRequirement(2, ItemList.nailsIronLarge, 4, true))
                .addRequirement(new CreationRequirement(3, ItemList.dirtPile, 5, true));

        CreationEntryCreator.createAdvancedEntry(SkillList.MASONRY, ItemList.stoneBrick, ItemList.mortar, treeBrick.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.plank, 10, true))
                .addRequirement(new CreationRequirement(2, ItemList.stoneBrick, 10, true))
                .addRequirement(new CreationRequirement(3, ItemList.mortar, 10, true))
                .addRequirement(new CreationRequirement(4, ItemList.dirtPile, 5, true));

        CreationEntryCreator.createAdvancedEntry(SkillList.MASONRY, ItemList.brickPottery, ItemList.mortar, treePottery.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.plank, 10, true))
                .addRequirement(new CreationRequirement(2, ItemList.brickPottery, 10, true))
                .addRequirement(new CreationRequirement(3, ItemList.mortar, 10, true))
                .addRequirement(new CreationRequirement(4, ItemList.dirtPile, 5, true));

        CreationEntryCreator.createAdvancedEntry(SkillList.MASONRY, ItemList.rock, ItemList.mortar, treeStone.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.rock, 10, true))
                .addRequirement(new CreationRequirement(2, ItemList.roundedBrick, 10, true))
                .addRequirement(new CreationRequirement(3, ItemList.mortar, 10, true))
                .addRequirement(new CreationRequirement(4, ItemList.dirtPile, 5, true));

        CreationEntryCreator.createAdvancedEntry(SkillList.MASONRY, ItemList.slateBrick, ItemList.mortar, treeSlate.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.rock, 10, true))
                .addRequirement(new CreationRequirement(2, ItemList.slateBrick, 10, true))
                .addRequirement(new CreationRequirement(3, ItemList.mortar, 10, true))
                .addRequirement(new CreationRequirement(4, ItemList.dirtPile, 5, true));

        CreationEntryCreator.createAdvancedEntry(SkillList.MASONRY, ItemList.rock, ItemList.mortar, treeRendered.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.rock, 10, true))
                .addRequirement(new CreationRequirement(2, ItemList.clay, 10, true))
                .addRequirement(new CreationRequirement(3, ItemList.mortar, 10, true))
                .addRequirement(new CreationRequirement(4, ItemList.dirtPile, 5, true));

        CreationEntryCreator.createAdvancedEntry(SkillList.MASONRY, ItemList.sandstoneBrick, ItemList.mortar, treeSandstone.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.rock, 10, true))
                .addRequirement(new CreationRequirement(2, ItemList.sandstoneBrick, 10, true))
                .addRequirement(new CreationRequirement(3, ItemList.mortar, 10, true))
                .addRequirement(new CreationRequirement(4, ItemList.dirtPile, 5, true));

        CreationEntryCreator.createAdvancedEntry(SkillList.MASONRY, ItemList.marbleBrick, ItemList.mortar, treeMarble.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.rock, 10, true))
                .addRequirement(new CreationRequirement(2, ItemList.marbleBrick, 10, true))
                .addRequirement(new CreationRequirement(3, ItemList.mortar, 10, true))
                .addRequirement(new CreationRequirement(4, ItemList.dirtPile, 5, true));

        CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY, ItemList.plank, ItemList.ironBand, bushWood.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.plank, 10, true))
                .addRequirement(new CreationRequirement(2, ItemList.metalRivet, 10, true))
                .addRequirement(new CreationRequirement(3, ItemList.ironBand, 4, true))
                .addRequirement(new CreationRequirement(4, ItemList.dirtPile, 1, true));

        for (final int lumpId : ItemFactory.metalLumpList) {
            CreationEntryCreator.createAdvancedEntry(SkillList.SMITHING_BLACKSMITHING, lumpId, ItemList.hammerWood, bushMetal.getTemplateId(), true, false, 0f, false, true, CreationCategories.DECORATION)
                    .addRequirement(new CreationRequirement(1, ItemList.sheetIron, 4, true))
                    .addRequirement(new CreationRequirement(2, ItemList.metalRivet, 10, true))
                    .addRequirement(new CreationRequirement(3, ItemList.ironBand, 4, true))
                    .addRequirement(new CreationRequirement(4, ItemList.dirtPile, 1, true));
        }

        if (PlantersMod.magicMushrooms) {
            CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY, ItemList.woodBeam, ItemList.woodBeam, magicWood.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                    .addRequirement(new CreationRequirement(1, ItemList.woodBeam, 9, true))
                    .addRequirement(new CreationRequirement(2, ItemList.nailsIronLarge, 4, true))
                    .addRequirement(new CreationRequirement(3, ItemList.moss, 5, true))
                    .addRequirement(new CreationRequirement(4, MiscItems.stumpId, 1, true))
                    .addRequirement(new CreationRequirement(5, ItemList.sourceCrystal, 4, true));

            CreationEntryCreator.createAdvancedEntry(SkillList.MASONRY, ItemList.rock, ItemList.rock, magicStone.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                    .addRequirement(new CreationRequirement(1, ItemList.rock, 4, true))
                    .addRequirement(new CreationRequirement(2, ItemList.mortar, 2, true))
                    .addRequirement(new CreationRequirement(3, ItemList.moss, 5, true))
                    .addRequirement(new CreationRequirement(4, MiscItems.stumpId, 1, true))
                    .addRequirement(new CreationRequirement(5, ItemList.sourceCrystal, 4, true));
        }
    }


    public static void register() throws IOException {
        registerPlanters();
        addRecipes();
        ModelNameProvider modelProvider = new PlanterModelProvider();
        typeMap.keySet().forEach(id -> ModItems.addModelNameProvider(id, modelProvider));
    }

    public static Set<Integer> allTemplateIds() {
        return typeMap.keySet();
    }

    public static boolean isPlanter(int templateId) {
        return typeMap.containsKey(templateId);
    }

    public static boolean isPlanter(Item item) {
        return isPlanter(item.getTemplateId());
    }

    public static PlanterType getPlanterType(int tpl) {
        return typeMap.get(tpl);
    }

    public static boolean needsTopItem(Item item) {
        PlanterType type = getPlanterType(item.getTemplateId());
        return (type == PlanterType.TREE || type == PlanterType.BUSH) && item.getAuxData() != 0;
    }

    public static void updateData(Item item, Plantable crop, int growthStage, boolean tended, int tendCountOrHarvestability, int tendPowerOrSubstage) {
        VolaTile vt = Zones.getOrCreateTile(item.getTilePos(), item.isOnSurface());
        vt.makeInvisible(item);
        item.setAuxData((byte) crop.number);
        boolean infected = isInfected(item);
        item.setData((growthStage & 0xFF) | (tended ? FLAG_TENDED : 0) | (infected ? FLAG_INFECTED : 0), (tendCountOrHarvestability & 0xFF) | (tendPowerOrSubstage << 8));
        updateName(item, crop, growthStage, tended, infected, false, false);
        vt.makeVisible(item);
    }

    public static void updateTreeData(Item item, Plantable crop, int growthStage, int subStage, boolean harvestable, boolean sprouting) {
        VolaTile vt = Zones.getOrCreateTile(item.getTilePos(), item.isOnSurface());
        vt.makeInvisible(item);
        item.setAuxData((byte) crop.number);
        boolean infected = isInfected(item);
        item.setData((growthStage & 0xFF) | (infected ? FLAG_INFECTED : 0), (harvestable ? TREE_FLAG_HARVESTABLE : 0) | (sprouting ? TREE_FLAG_SPROUTING : 0) | (subStage << 8));
        updateName(item, crop, growthStage, false, infected, harvestable, sprouting);
        vt.makeVisible(item);
    }

    public static void clearData(Item item) {
        VolaTile vt = Zones.getOrCreateTile(item.getTilePos(), item.isOnSurface());
        vt.makeInvisible(item);
        item.setAuxData((byte) 0);
        boolean infected = isInfected(item);
        item.setData((infected ? FLAG_INFECTED : 0), 0);
        updateName(item, null, 0, false, infected, false, false);
        vt.makeVisible(item);
    }

    public static void updateName(Item item, Plantable crop, int growthStage, boolean tended, boolean infected, boolean harvestable, boolean sprouting) {
        StringBuilder nameBuilder = new StringBuilder();
        StringBuilder descBuilder = new StringBuilder();

        if (infected) nameBuilder.append("infected ");

        if (crop != null) {
            nameBuilder.append(crop.displayName).append(" planter");
            if (isTreeOrBushPlanter(item)) {
                if (growthStage >= 0 && growthStage <= 5) {
                    descBuilder.append(TREE_AGES[growthStage]);
                    if (harvestable && crop.cropItem > 0) descBuilder.append(", harvestable");
                    if (sprouting) descBuilder.append(", sprouting");
                }
            } else {
                if (growthStage >= 0 && growthStage <= 6) {
                    descBuilder.append(AGES[growthStage]);
                    if (growthStage < 5 && !tended) {
                        descBuilder.append(", ").append("untended");
                    }
                }
            }
        } else {
            nameBuilder.append(item.getTemplate().getName());
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
        updateName(item, crop, growthStage, tended, infected, isTreeHarvestable(item), isTreeSprouting(item));
        vt.makeVisible(item);

        Arrays.stream(vt.getWatchers())
                .map(VirtualZone::getWatcher)
                .filter(creature -> creature != null && creature.isPlayer())
                .forEach(p -> p.getCommunicator().sendAttachEffect(item.getWurmId(), (byte) (infected ? 8 : 11), (byte) 0, (byte) 0, (byte) 0, (byte) 0));
    }

    public static int getFarmTendCount(Item item) {
        return item.getData2() & 0xFF;
    }

    public static int getFarmTendPower(Item item) {
        return item.getData2() >> 8;
    }

    public static int getTreeSubstage(Item item) {
        return item.getData2() >> 8;
    }

    public static boolean isTreeHarvestable(Item item) {
        return (item.getData2() & TREE_FLAG_HARVESTABLE) != 0;
    }

    public static boolean isTreeSprouting(Item item) {
        return (item.getData2() & TREE_FLAG_SPROUTING) != 0;
    }

    public static boolean needsPolling(Item item) {
        if (!isPlanter(item) || item.getParentId() != -10L || item.getAuxData() == 0) return false;
        if (isTreeOrBushPlanter(item))
            return getGrowthStage(item) < 5 || !isTreeHarvestable(item) || !isTreeSprouting(item);
        else
            return getGrowthStage(item) < (PlantersMod.canWilt ? 6 : 5);
    }

    public static boolean isTreeOrBushPlanter(Item item) {
        return isTreeOrBushPlanter(item.getTemplateId());
    }

    public static boolean isTreeOrBushPlanter(int tplId) {
        PlanterType type = getPlanterType(tplId);
        return type == PlanterType.TREE || type == PlanterType.BUSH;
    }
}
