package net.bdew.planters;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GenMappings {
    static class Mapping {
        public final String key, val;

        public Mapping(String key, String val) {
            this.key = key;
            this.val = val;
        }
    }

    private static HashMap<Plantable, String> texNames = new HashMap<>();

    static {
        texNames.put(Plantable.Carrots, "carrot");
        texNames.put(Plantable.Tomatoes, "tomato");
        texNames.put(Plantable.Peapods, "peas");
        texNames.put(Plantable.Strawberries, "strawberry");
    }

    private static ArrayList<Mapping> mappings = new ArrayList<>();
    private static int longest = 0;
    private static PrintStream output = System.out;

    private static void addMapping(String key, String val) {
        mappings.add(new Mapping(key, val));
        if (key.length() > longest) longest = key.length();
    }

    private static void emitSection(String title) {
        output.println(String.format("########## %s #########", title));
        output.println();
        mappings.forEach(m -> output.println(String.format("%-" + longest + "s = %s", m.key, m.val)));
        output.println();
        longest = 0;
        mappings.clear();
    }

    private static void generateVariants(String key, String model, String woodName, String dirtname) {
        addMapping(PlanterItem.BASEMODEL + key, String.format("%s.dae", model));
        addMapping(PlanterItem.BASEMODEL + key + ".decayed", String.format("%s.dae?%s.texture=woodbridge_decay.png", model, woodName));
        addMapping(PlanterItem.BASEMODEL + key + ".winter", String.format("%s.dae?%s.texture=farm_winter.jpg", model, dirtname));
        addMapping(PlanterItem.BASEMODEL + key + ".decayed.winter", String.format("%s.dae?%s.texture=woodbridge_decay.png&%s.texture=farm_winter.jpg", model, woodName, dirtname));
    }

    private static void generateVariantsSprite(String key, String model, String woodName, String dirtname, String spriteTex, String spriteFile) {
        addMapping(PlanterItem.BASEMODEL + key, String.format("%s.dae?%s.texture=%s", model, spriteTex, spriteFile));
        addMapping(PlanterItem.BASEMODEL + key + ".decayed", String.format("%s.dae?%s.texture=%s&%s.texture=woodbridge_decay.png", model, spriteTex, spriteFile, woodName));
        addMapping(PlanterItem.BASEMODEL + key + ".winter", String.format("%s.dae?%s.texture=%s&%s.texture=farm_winter.jpg", model, spriteTex, spriteFile, dirtname));
        addMapping(PlanterItem.BASEMODEL + key + ".decayed.winter", String.format("%s.dae?%s.texture=%s&%s.texture=woodbridge_decay.png&%s.texture=farm_winter.jpg", model, spriteTex, spriteFile, woodName, dirtname));
    }

    private static String fixedTexName(Plantable plant) {
        if (texNames.containsKey(plant)) return texNames.get(plant) + ".dds";
        return plant.modelName + "dds";
    }

    public static void main(String[] args) {
        addMapping(PlanterItem.BASEMODEL, "planterbed.dae");
        generateVariants("dirt", "planterbed", "oakplank", "farmwurm");
        emitSection("Base");
        for (Plantable plant : Plantable.values()) {
            if (plant.modelName.contains("mushroom")) {
                continue; //skip for now
            } else if (plant == Plantable.Cabbage) {
                generateVariants(plant.modelName + "young", "cabbage-tended", "oakplank", "farmwurm");
                generateVariants(plant.modelName + "young.untended", "cabbage-young", "oakplank", "farmwurm");
                generateVariants(plant.modelName + "ripe", "cabbage-ripe", "oakplank", "farmwurm");
                generateVariants(plant.modelName + "ripe.wilted", "cabbage-wilted", "oakplank", "farmwurm");
            } else if (plant == Plantable.Pumpkin) {
                generateVariants(plant.modelName + "young", "pumpkin-tended", "oakplank", "farmwurm");
                generateVariants(plant.modelName + "young.untended", "pumpkin-young", "oakplank", "farmwurm");
                generateVariants(plant.modelName + "ripe", "pumpkin-ripe", "oakplank", "farmwurm");
                generateVariants(plant.modelName + "ripe.wilted", "pumpkin-wilted", "oakplank", "farmwurm");
            } else {
                // Sprite crops
                generateVariantsSprite(plant.modelName + "young", "sprite-tended", "oakplank", "farmwurm", "sprite_wheat", fixedTexName(plant));
                generateVariantsSprite(plant.modelName + "young.untended", "sprite-young", "oakplank", "farmwurm", "sprite_wheat", fixedTexName(plant));
                generateVariantsSprite(plant.modelName + "ripe", "sprite-ripe", "oakplank", "farmwurm", "sprite_wheat", fixedTexName(plant));
                generateVariantsSprite(plant.modelName + "ripe.wilted", "sprite-wilted", "oakplank", "farmwurm", "sprite_wheat", fixedTexName(plant));
            }
            emitSection(plant.displayName);
        }
    }
}
