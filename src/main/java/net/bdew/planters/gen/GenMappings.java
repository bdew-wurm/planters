package net.bdew.planters.gen;

import net.bdew.planters.Plantable;
import net.bdew.planters.PlanterItem;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GenMappings {
    static class Mapping {
        public final String key, val;

        public Mapping(String key, String val) {
            this.key = key;
            this.val = val;
        }
    }

    static class ResURL {
        public final String file;
        public final Map<String, String> overrides;

        public ResURL(String file, Map<String, String> overrides) {
            this.file = file;
            this.overrides = overrides;
        }

        public ResURL(String file) {
            this(file, new HashMap<>());
        }

        public ResURL override(String key, String val) {
            Map<String, String> mc = new HashMap<>(overrides);
            mc.put(key, val);
            return new ResURL(file, mc);
        }

        public ResURL tex(String matName, String texName) {
            return override(matName + ".texture", texName);
        }

        public String build() {
            if (overrides.isEmpty()) return file;
            return file + "?" +
                    overrides.entrySet().stream()
                            .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                            .collect(Collectors.joining("&"));
        }
    }

    private static ResURL model(String fn) {
        return new ResURL(fn);
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

    private static void addMapping(String key, ResURL val) {
        if (key.endsWith(".")) key = key.substring(0, key.length() - 1);
        mappings.add(new Mapping(key, val.build()));
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

    private static void generateVariants(String key, String suffix, ResURL base, String matName, String decayMatTex, String dirtName, String winterTex, String decaySoilTex) {
        ResURL decay = base.tex(matName, decayMatTex);
        addMapping(PlanterItem.BASEMODEL + key + suffix, base);
        addMapping(PlanterItem.BASEMODEL + key + ".decayed" + suffix, decaySoilTex == null ? decay : decay.tex(dirtName, decaySoilTex));
        addMapping(PlanterItem.BASEMODEL + key + ".winter" + suffix, base.tex(dirtName, winterTex));
        addMapping(PlanterItem.BASEMODEL + key + ".decayed.winter" + suffix, decay.tex(dirtName, winterTex));
    }

    private static void generateVariantsSprite(String key, String suffix, ResURL base, String matName, String decayMatTex, String dirtName, String spriteTex, String spriteFile) {
        ResURL spr = base.tex(spriteTex, spriteFile);
        addMapping(PlanterItem.BASEMODEL + key + suffix, spr);
        addMapping(PlanterItem.BASEMODEL + key + ".decayed" + suffix, spr.tex(matName, decayMatTex).tex(dirtName, "farmland.jpg"));
        addMapping(PlanterItem.BASEMODEL + key + ".winter" + suffix, spr.tex(dirtName, "farm_winter.jpg"));
        addMapping(PlanterItem.BASEMODEL + key + ".decayed.winter" + suffix, spr.tex(matName, decayMatTex).tex(dirtName, "farm_winter.jpg"));
    }

    private static void generateStages(Plantable plant, String base, String soilMat, String winterSoilTex, String decaySoilTex) {
        generateVariants(plant.modelName + "young", "", model(String.format("%s-wood-tended.wom", base)), "oakplank", "woodbridge_decay.png", soilMat, winterSoilTex, decaySoilTex);
        generateVariants(plant.modelName + "young.untended", "", model(String.format("%s-wood-young.wom", base)), "oakplank", "woodbridge_decay.png", soilMat, winterSoilTex, decaySoilTex);
        generateVariants(plant.modelName + "ripe", "", model(String.format("%s-wood-ripe.wom", base)), "oakplank", "woodbridge_decay.png", soilMat, winterSoilTex, decaySoilTex);
        generateVariants(plant.modelName + "ripe.wilted", "", model(String.format("%s-wood-wilted.wom", base)), "oakplank", "woodbridge_decay.png", soilMat, winterSoilTex, decaySoilTex);

        generateVariants(plant.modelName + "young", ".stone", model(String.format("%s-stone-tended.wom", base)), "stone", "SmallStoneDmg.jpg", soilMat, winterSoilTex, decaySoilTex);
        generateVariants(plant.modelName + "young.untended", ".stone", model(String.format("%s-stone-young.wom", base)), "stone", "SmallStoneDmg.jpg", soilMat, winterSoilTex, decaySoilTex);
        generateVariants(plant.modelName + "ripe", ".stone", model(String.format("%s-stone-ripe.wom", base)), "stone", "SmallStoneDmg.jpg", soilMat, winterSoilTex, decaySoilTex);
        generateVariants(plant.modelName + "ripe.wilted", ".stone", model(String.format("%s-stone-wilted.wom", base)), "stone", "SmallStoneDmg.jpg", soilMat, winterSoilTex, decaySoilTex);
    }

    private static void generateStagesSprite(Plantable plant, String base) {
        generateVariantsSprite(plant.modelName + "young", "", model(String.format("%s-wood-tended.wom", base)), "oakplank", "woodbridge_decay.png", "farmwurm", "sprite_wheat", fixedTexName(plant));
        generateVariantsSprite(plant.modelName + "young.untended", "", model(String.format("%s-wood-young.wom", base)), "oakplank", "woodbridge_decay.png", "farmwurm", "sprite_wheat", fixedTexName(plant));
        generateVariantsSprite(plant.modelName + "ripe", "", model(String.format("%s-wood-ripe.wom", base)), "oakplank", "woodbridge_decay.png", "farmwurm", "sprite_wheat", fixedTexName(plant));
        generateVariantsSprite(plant.modelName + "ripe.wilted", "", model(String.format("%s-wood-wilted.wom", base)), "oakplank", "woodbridge_decay.png", "farmwurm", "sprite_wheat", fixedTexName(plant));

        generateVariantsSprite(plant.modelName + "young", ".stone", model(String.format("%s-stone-tended.wom", base)), "stone", "SmallStoneDmg.jpg", "farmwurm", "sprite_wheat", fixedTexName(plant));
        generateVariantsSprite(plant.modelName + "young.untended", ".stone", model(String.format("%s-stone-young.wom", base)), "stone", "SmallStoneDmg.jpg", "farmwurm", "sprite_wheat", fixedTexName(plant));
        generateVariantsSprite(plant.modelName + "ripe", ".stone", model(String.format("%s-stone-ripe.wom", base)), "stone", "SmallStoneDmg.jpg", "farmwurm", "sprite_wheat", fixedTexName(plant));
        generateVariantsSprite(plant.modelName + "ripe.wilted", ".stone", model(String.format("%s-stone-wilted.wom", base)), "stone", "SmallStoneDmg.jpg", "farmwurm", "sprite_wheat", fixedTexName(plant));
    }

    private static String fixedTexName(Plantable plant) {
        if (texNames.containsKey(plant)) return "crops/" + texNames.get(plant) + ".dds";
        return "crops/" + plant.modelName + "dds";
    }

    public static void main(String[] args) {
        addMapping(PlanterItem.BASEMODEL, model("planter-wood.wom"));
        addMapping(PlanterItem.BASEMODEL + "unfinished", model("unfinished-wood.wom"));
        addMapping(PlanterItem.BASEMODEL + "unfinished.stone", model("unfinished-stone.wom"));
        generateVariants("dirt", "", model("planter-wood.wom"), "oakplank", "woodbridge_decay.png", "dirtwurm", "farm_winter.jpg", null);
        generateVariants("dirt", ".stone", model("planter-stone.wom"), "stone", "SmallStoneDmg.jpg", "dirtwurm", "farm_winter.jpg", null);
        emitSection("Base");
        for (Plantable plant : Plantable.values()) {
            if (plant.modelName.contains("mushroom")) {
                String color = plant.modelName.split("\\.")[1];
                generateStages(plant, "shroom-" + color, "Soil", "snow_winter.jpg", null);
            } else if (plant == Plantable.Cabbage) {
                generateStages(plant, "cabbage", "farmwurm", "farm_winter.jpg", "farmland.jpg");
            } else if (plant == Plantable.Pumpkin) {
                generateStages(plant, "pumpkin", "farmwurm", "farm_winter.jpg", "farmland.jpg");
            } else {
                // Sprite crops
                if (plant.water) {
                    generateStagesSprite(plant, "water");
                } else {
                    generateStagesSprite(plant, "sprite");
                }
            }
            emitSection(plant.displayName);
        }
    }
}
