package net.bdew.planters;

import java.io.PrintStream;
import java.util.ArrayList;

public class GenMappings {
    static class Mapping {
        public final String key, val;

        public Mapping(String key, String val) {
            this.key = key;
            this.val = val;
        }
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

    public static void main(String[] args) {
        addMapping(PlanterItem.BASEMODEL, "planterbed.dae");
        generateVariants("dirt", "planterbed", "oakplank", "dirtwurm");
        emitSection("Base");
        for (Plantable plant : Plantable.values()) {
            if (plant.modelName.contains("mushroom")) {
                continue; //skip for now
            } else if (plant == Plantable.Cabbage) {
                generateVariants(plant.modelName + "young", "cabbage-tended", "oakplank", "dirtwurm");
                generateVariants(plant.modelName + "young.untended", "cabbage-young", "oakplank", "dirtwurm");
                generateVariants(plant.modelName + "ripe", "cabbage-ripe", "oakplank", "dirtwurm");
                generateVariants(plant.modelName + "ripe.wilted", "cabbage-wilted", "oakplank", "dirtwurm");
            } else if (plant == Plantable.Pumpkin) {
                generateVariants(plant.modelName + "young", "pumpkin-tended", "oakplank", "dirtwurm");
                generateVariants(plant.modelName + "young.untended", "pumpkin-young", "oakplank", "dirtwurm");
                generateVariants(plant.modelName + "ripe", "pumpkin-ripe", "oakplank", "dirtwurm");
                generateVariants(plant.modelName + "ripe.wilted", "pumpkin-wilted", "oakplank", "dirtwurm");
            } else {
                // Sprite crops
                continue; //skip for now
            }
            emitSection(plant.displayName);
        }
    }
}
