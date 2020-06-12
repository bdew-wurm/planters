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

    private static void generateVariants(String key, String model, String woodName, String dirtName, String winterTex) {
        addMapping(PlanterItem.BASEMODEL + key, String.format("%s", model));
        addMapping(PlanterItem.BASEMODEL + key + ".decayed", String.format("%s?%s.texture=woodbridge_decay.png&%s.texture=farmland.jpg", model, woodName, dirtName));
        addMapping(PlanterItem.BASEMODEL + key + ".winter", String.format("%s?%s.texture=%s", model, dirtName, winterTex));
        addMapping(PlanterItem.BASEMODEL + key + ".decayed.winter", String.format("%s?%s.texture=woodbridge_decay.png&%s.texture=%s", model, woodName, dirtName, winterTex));
    }

    private static void generateVariantsSprite(String key, String model, String woodName, String dirtName, String spriteTex, String spriteFile) {
        addMapping(PlanterItem.BASEMODEL + key, String.format("%s?%s.texture=%s", model, spriteTex, spriteFile));
        addMapping(PlanterItem.BASEMODEL + key + ".decayed", String.format("%s?%s.texture=%s&%s.texture=woodbridge_decay.png&%s.texture=farmland.jpg", model, spriteTex, spriteFile, woodName, dirtName));
        addMapping(PlanterItem.BASEMODEL + key + ".winter", String.format("%s?%s.texture=%s&%s.texture=farm_winter.jpg", model, spriteTex, spriteFile, dirtName));
        addMapping(PlanterItem.BASEMODEL + key + ".decayed.winter", String.format("%s?%s.texture=%s&%s.texture=woodbridge_decay.png&%s.texture=farm_winter.jpg", model, spriteTex, spriteFile, woodName, dirtName));
    }

    private static String fixedTexName(Plantable plant) {
        if (texNames.containsKey(plant)) return "crops/" + texNames.get(plant) + ".dds";
        return "crops/" + plant.modelName + "dds";
    }

    public static void main(String[] args) {
        addMapping(PlanterItem.BASEMODEL, "planter.wom");
        addMapping(PlanterItem.BASEMODEL + "unfinished", "planter-unfinished.wom");
        generateVariants("dirt", "planter.wom", "oakplank", "farmwurm", "farm_winter.jpg");
        emitSection("Base");
        for (Plantable plant : Plantable.values()) {
            if (plant.modelName.contains("mushroom")) {
                String color = plant.modelName.split("\\.")[1];
                generateVariants(plant.modelName + "young", String.format("shroom-%s-tended.wom", color), "oakplank", "Soil","dirt_winter.jpg");
                generateVariants(plant.modelName + "young.untended", String.format("shroom-%s-young.wom", color), "oakplank", "Soil","dirt_winter.jpg");
                generateVariants(plant.modelName + "ripe", String.format("shroom-%s-ripe.wom", color), "oakplank", "Soil","dirt_winter.jpg");
                generateVariants(plant.modelName + "ripe.wilted", String.format("shroom-%s-wilted.wom", color), "oakplank", "Soil","dirt_winter.jpg");
            } else if (plant == Plantable.Cabbage) {
                generateVariants(plant.modelName + "young", "cabbage-tended.wom", "oakplank", "farmwurm", "farm_winter.jpg");
                generateVariants(plant.modelName + "young.untended", "cabbage-young.wom", "oakplank", "farmwurm", "farm_winter.jpg");
                generateVariants(plant.modelName + "ripe", "cabbage-ripe.wom", "oakplank", "farmwurm", "farm_winter.jpg");
                generateVariants(plant.modelName + "ripe.wilted", "cabbage-wilted.wom", "oakplank", "farmwurm", "farm_winter.jpg");
            } else if (plant == Plantable.Pumpkin) {
                generateVariants(plant.modelName + "young", "pumpkin-tended.wom", "oakplank", "farmwurm", "farm_winter.jpg");
                generateVariants(plant.modelName + "young.untended", "pumpkin-young.wom", "oakplank", "farmwurm", "farm_winter.jpg");
                generateVariants(plant.modelName + "ripe", "pumpkin-ripe.wom", "oakplank", "farmwurm", "farm_winter.jpg");
                generateVariants(plant.modelName + "ripe.wilted", "pumpkin-wilted.wom", "oakplank", "farmwurm", "farm_winter.jpg");
            } else {
                // Sprite crops
                generateVariantsSprite(plant.modelName + "young", "sprite-tended.wom", "oakplank", "farmwurm", "sprite_wheat", fixedTexName(plant));
                generateVariantsSprite(plant.modelName + "young.untended", "sprite-young.wom", "oakplank", "farmwurm", "sprite_wheat", fixedTexName(plant));
                generateVariantsSprite(plant.modelName + "ripe", "sprite-ripe.wom", "oakplank", "farmwurm", "sprite_wheat", fixedTexName(plant));
                generateVariantsSprite(plant.modelName + "ripe.wilted", "sprite-wilted.wom", "oakplank", "farmwurm", "sprite_wheat", fixedTexName(plant));
            }
            emitSection(plant.displayName);
        }
    }
}
