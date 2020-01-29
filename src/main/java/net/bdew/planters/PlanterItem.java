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
    public static ItemTemplate template;
    public static int id;

    private static final String BASEMODEL = "model.structure.farmbox.";

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
        template = new ItemTemplateBuilder("bdew.water.planter")
                .name("large planter", "large planters", "A large planter suitable for growing crops.")
                .modelName(BASEMODEL)
                .imageNumber((short) IconConstants.ICON_MARBLE_PLANTER)
                .itemTypes(new short[]{
                        ItemTypes.ITEM_TYPE_WOOD,
                        ItemTypes.ITEM_TYPE_REPAIRABLE,
                        ItemTypes.ITEM_TYPE_PLANTABLE,
                        ItemTypes.ITEM_TYPE_TRANSPORTABLE,
                        ItemTypes.ITEM_TYPE_NOTAKE,
                        ItemTypes.ITEM_TYPE_TURNABLE,
                        ItemTypes.ITEM_TYPE_ONE_PER_TILE,
                        ItemTypes.ITEM_TYPE_TILE_ALIGNED,
                        ItemTypes.ITEM_TYPE_HASDATA,
                })
                .decayTime(9072000L)
                .dimensions(200, 200, 30)
                .weightGrams(10000)
                .material(Materials.MATERIAL_WOOD_BIRCH)
                .behaviourType((short) 1)
                .primarySkill(SkillList.CARPENTRY)
                .difficulty(70)
                .build();

        id = template.getTemplateId();

        CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY, ItemList.woodBeam, ItemList.woodBeam, id, false, false, 0.0F, true, true, CreationCategories.DECORATION)
                .addRequirement(new CreationRequirement(1, ItemList.woodBeam, 9, true))
                .addRequirement(new CreationRequirement(2, ItemList.nailsIronLarge, 4, true))
                .addRequirement(new CreationRequirement(3, ItemList.dirtPile, 1, true));

        ModItems.addModelNameProvider(id, (item -> {
            StringBuilder sb = new StringBuilder(BASEMODEL);
            Plantable plant = getPlantable(item);

            if (plant != null) {
                sb.append(plant.modelName);

                int growth = getGrowthStage(item);
                if (growth < 5) {
                    sb.append("young.");
                    if (!isTended(item))
                        sb.append("untended.");
                } else {
                    sb.append("ripe.");
                    if (growth > 5) {
                        sb.append("wilted.");
                    }
                }
            } else sb.append("dirt.");

            if (item.getDamage() >= 50f)
                sb.append("decayed.");

            return sb.toString();
        }));
    }

    public static void updateData(Item item, Plantable crop, int growthStage, boolean tended) {
        if (crop != null) {
            StringBuilder name = new StringBuilder(template.getName());
            name.append(" - ").append(crop.displayName);
            if (growthStage >= 0 && growthStage <= 6) {
                name.append(" (").append(AGES[growthStage]);
                if (growthStage < 5 && !tended) {
                    name.append(", ").append("untended");
                }
                name.append(")");
            }
            VolaTile vt = Zones.getOrCreateTile(item.getTilePos(), item.isOnSurface());
            vt.makeInvisible(item);
            item.setAuxData((byte) crop.number);
            item.setName(name.toString());
            item.setData1(growthStage & 0xFF | (tended ? 0x100 : 0));
            vt.makeVisible(item);
        } else {
            VolaTile vt = Zones.getOrCreateTile(item.getTilePos(), item.isOnSurface());
            vt.makeInvisible(item);
            item.setAuxData((byte) 0);
            item.setData(0, 0);
            item.setName(template.getName());
            vt.makeVisible(item);
        }
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
}