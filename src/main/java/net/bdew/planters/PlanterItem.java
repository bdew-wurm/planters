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
import java.util.*;

public class PlanterItem {
    public static ItemTemplate normalWood, normalStone;
    public static ItemTemplate magicWood, magicStone;
    public static ItemTemplate treeWood, treeStone, treePottery, treeMarble, treeRendered, treeBrick, treeSandstone, treeSlate;
    public static ItemTemplate bushWood, bushMetal;

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

    private static final Map<Integer, PlanterType> typeMap = new HashMap<>();

    private static final Collection<Short> baseTypes = Arrays.asList(
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
            ItemTypes.ITEM_TYPE_DECORATION
    );

    private static ItemTemplate registerPlanter(PlanterType type, String id, String modelSuffix, String name, String desc, String plural, int skill, byte material, short[] types) throws IOException {
        short[] allTypes = new short[types.length + baseTypes.size()];
        System.arraycopy(types, 0, allTypes, 0, types.length);
        int p = types.length;
        for (Short t : baseTypes) allTypes[p++] = t;

        PlantersMod.logInfo(String.format("Adding planter %s with types %s", id, Arrays.toString(allTypes)));

        ItemTemplate tpl = new ItemTemplateBuilder(id)
                .name(name, plural, desc)
                .modelName(BASEMODEL + modelSuffix)
                .imageNumber((short) IconConstants.ICON_MARBLE_PLANTER)
                .itemTypes(allTypes)
                .decayTime(9072000L)
                .dimensions(200, 200, 30)
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
        normalWood = registerPlanter(PlanterType.NORMAL, "bdew.planters.wood", "",
                "large planter", "wooden large planters", "A large wooden planter suitable for growing crops.",
                SkillList.CARPENTRY, Materials.MATERIAL_WOOD_BIRCH, new short[]{ItemTypes.ITEM_TYPE_WOOD});

        normalStone = registerPlanter(PlanterType.NORMAL, "bdew.planters.stone", "",
                "large stone planter", "stone large planters", "A large stone planter suitable for growing crops.",
                SkillList.MASONRY, Materials.MATERIAL_STONE, new short[]{ItemTypes.ITEM_TYPE_STONE});

        // ==== TREES ====

        treeWood = registerPlanter(PlanterType.TREE, "bdew.planters.wood.tree", "tree.wood.",
                "tree planter", "wooden tree planters", "A large wooden planter suitable for growing trees.",
                SkillList.CARPENTRY, Materials.MATERIAL_WOOD_BIRCH, new short[]{ItemTypes.ITEM_TYPE_WOOD, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR});

        treeStone = registerPlanter(PlanterType.TREE, "bdew.planters.stone.tree", "tree.stone.",
                "stone tree planter", "stone tree planters", "A large stone planter suitable for growing trees.",
                SkillList.MASONRY, Materials.MATERIAL_STONE, new short[]{ItemTypes.ITEM_TYPE_STONE, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR});

        treePottery = registerPlanter(PlanterType.TREE, "bdew.planters.pottery.tree", "tree.pottery.",
                "pottery tree planter", "pottery tree planters", "A large pottery planter suitable for growing trees.",
                SkillList.MASONRY, Materials.MATERIAL_STONE, new short[]{ItemTypes.ITEM_TYPE_POTTERY, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR});

        treeMarble = registerPlanter(PlanterType.TREE, "bdew.planters.marble.tree", "tree.marble.",
                "marble tree planter", "marble tree planters", "A large marble planter suitable for growing trees.",
                SkillList.MASONRY, Materials.MATERIAL_MARBLE, new short[]{ItemTypes.ITEM_TYPE_STONE, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR});

        treeRendered = registerPlanter(PlanterType.TREE, "bdew.planters.clay.tree", "tree.clay.",
                "clay tree planter", "clay tree planters", "A large clay planter suitable for growing trees.",
                SkillList.MASONRY, Materials.MATERIAL_CLAY, new short[]{ItemTypes.ITEM_TYPE_STONE, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR});

        treeBrick = registerPlanter(PlanterType.TREE, "bdew.planters.brick.tree", "tree.brick.",
                "brick tree planter", "brick tree planters", "A large brick planter suitable for growing trees.",
                SkillList.MASONRY, Materials.MATERIAL_STONE, new short[]{ItemTypes.ITEM_TYPE_STONE, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR});

        treeSandstone = registerPlanter(PlanterType.TREE, "bdew.planters.sandstone.tree", "tree.sand.",
                "sandstone tree planter", "sandstone tree planters", "A large sandstone planter suitable for growing trees.",
                SkillList.MASONRY, Materials.MATERIAL_SANDSTONE, new short[]{ItemTypes.ITEM_TYPE_STONE, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR});

        treeSlate = registerPlanter(PlanterType.TREE, "bdew.planters.slate.tree", "tree.slate.",
                "slate tree planter", "slate tree planters", "A large slate planter suitable for growing trees.",
                SkillList.MASONRY, Materials.MATERIAL_SLATE, new short[]{ItemTypes.ITEM_TYPE_STONE, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR});

        // ==== BUSH ====

        bushWood = registerPlanter(PlanterType.BUSH, "bdew.planters.wood.bush", "bush.wood.",
                "bush planter", "wooden bush planters", "A large wooden planter suitable for growing bushs.",
                SkillList.CARPENTRY, Materials.MATERIAL_WOOD_BIRCH, new short[]{ItemTypes.ITEM_TYPE_WOOD, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR});

        bushMetal = registerPlanter(PlanterType.BUSH, "bdew.planters.metal.bush", "bush.metal.",
                "bush planter", "metal bush planters", "A large metal planter suitable for growing bushs.",
                SkillList.MASONRY, Materials.MATERIAL_IRON, new short[]{ItemTypes.ITEM_TYPE_METAL, ItemTypes.ITEM_TYPE_SUPPORTS_SECONDARY_COLOR});

        // ==== MAGIC ====

        if (PlantersMod.magicMushrooms) {
            magicWood = registerPlanter(PlanterType.MAGIC, "bdew.planters.wood.magic", "magic.",
                    "magic planter", "wooden magic planters", "A large wooden planter suitable for growing magical mushrooms.",
                    SkillList.CARPENTRY, Materials.MATERIAL_WOOD_BIRCH, new short[]{ItemTypes.ITEM_TYPE_WOOD});

            magicStone = registerPlanter(PlanterType.MAGIC, "bdew.planters.stone.magic", "magic.",
                    "stone magic planter", "stone magic planters", "A large wooden planter suitable for growing magical mushrooms.",
                    SkillList.MASONRY, Materials.MATERIAL_WOOD_BIRCH, new short[]{ItemTypes.ITEM_TYPE_STONE});
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

        CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY, ItemList.woodBeam, ItemList.woodBeam, treeWood.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.woodBeam, 9, true))
                .addRequirement(new CreationRequirement(2, ItemList.nailsIronLarge, 4, true))
                .addRequirement(new CreationRequirement(3, ItemList.dirtPile, 1, true));

        CreationEntryCreator.createAdvancedEntry(SkillList.MASONRY, ItemList.rock, ItemList.rock, treeStone.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.rock, 4, true))
                .addRequirement(new CreationRequirement(2, ItemList.mortar, 2, true))
                .addRequirement(new CreationRequirement(3, ItemList.dirtPile, 1, true));

        CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY, ItemList.woodBeam, ItemList.woodBeam, bushWood.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.woodBeam, 9, true))
                .addRequirement(new CreationRequirement(2, ItemList.nailsIronLarge, 4, true))
                .addRequirement(new CreationRequirement(3, ItemList.dirtPile, 1, true));

        CreationEntryCreator.createAdvancedEntry(SkillList.MASONRY, ItemList.rock, ItemList.rock, bushMetal.getTemplateId(), false, false, 0f, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.rock, 4, true))
                .addRequirement(new CreationRequirement(2, ItemList.mortar, 2, true))
                .addRequirement(new CreationRequirement(3, ItemList.dirtPile, 1, true));

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
}
