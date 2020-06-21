package net.bdew.planters.gen;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class RenameModels {
    private static String pickState(String[] parts, int idx) {
        String state = parts[idx];
        if (state.equals("old")) state = "wilted";
        if (state.equals("young") && parts[idx + 1].equals("tended")) state = "tended";
        return state;
    }

    private static void doRename(File in, File outDir, String... parts) {
        String outName = String.join("-", parts) + ".wom";
        File out = new File(outDir, outName);
        if (out.exists()) {
            System.err.println(String.format("Duplicate output (%s -> %s)", in.getName(), outName));
            System.exit(1);
        }
        if (!in.renameTo(out)) {
            System.err.println(String.format("Rename failed (%s -> %s)", in.getName(), outName));
            System.exit(1);
        }
        System.out.println(String.format("%s -> %s", in.getName(), outName));
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: RenameModels <inpath> <outpath>");
            System.exit(1);
        }
        System.out.println(String.format("Renaming %s -> %s", args[0], args[1]));
        File dir = new File(args[0]);
        File out = new File(args[1]);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.canRead() || !file.getName().endsWith(".wom")) continue;
            String[] sp = file.getName().replace(" .", ".").toLowerCase().split("[ .]");
            if (sp.length > 1 && sp[0].equals("item")) {
                doRename(file, out, "shroom", "item");
            } else if (sp.length > 2 && sp[0].equals("wicker")) {
                String kind = "empty";
                if (sp.length > 3) {
                    if (sp[2].equals("magic") || sp[2].equals("mixed"))
                        kind = sp[2];
                    else if (sp[3].equals("fabric"))
                        kind = sp[3];
                }
                doRename(file, out, "basket", kind);
            } else if (sp.length > 4 && sp[4].equals("mushroom")) {
                String color = sp[3];
                String material = sp[sp.length - 2].equals("stone") ? "stone" : "wood";
                if (color.equals("magic") && sp.length > 5 && sp[5].equals("not")) {
                    doRename(file, out, "magic", material);
                } else {
                    String state = pickState(sp, 5);
                    doRename(file, out, "shroom", color, material, state);
                }
            } else if (sp.length > 3 && (sp[3].equals("pumpkin") || sp[3].equals("cabbage"))) {
                String crop = sp[3];
                String state = pickState(sp, 4);
                String material = sp[sp.length - 2].equals("stone") ? "stone" : "wood";
                doRename(file, out, crop, material, state);
            } else if (sp.length > 1 && sp[1].equals("sprite")) {
                if (sp[2].equals("water")) {
                    String state = pickState(sp, 3);
                    String material = sp[sp.length - 2].equals("stone") ? "stone" : "wood";
                    doRename(file, out, "water", material, state);
                } else {
                    String state = pickState(sp, 2);
                    String material = sp[sp.length - 2].equals("stone") ? "stone" : "wood";
                    doRename(file, out, "sprite", material, state);
                }
            } else if (sp.length > 3 && sp[3].startsWith("unfinished")) {
                String material = sp[sp.length - 2].equals("stone") ? "stone" : "wood";
                if (material.equals("stone"))
                    doRename(file, out, "magic", material, "unfinished");
                else
                    doRename(file, out, "unfinished", material);
            } else if (sp.length > 3 && sp[3].equals("generics")) {
                String material = sp[sp.length - 2].equals("stone") ? "stone" : "wood";
                doRename(file, out, "unfinished", material);
            } else if (sp.length > 3 && sp[3].equals("magic")) {
                String material = sp[sp.length - 2].equals("stone") ? "stone" : "wood";
                doRename(file, out, "magic", material, "unfinished");
            } else if (sp.length > 3 && !sp[3].startsWith("cultivated") && !sp[3].startsWith("decayed")) {
                String material = sp[sp.length - 2].equals("stone") ? "stone" : "wood";
                doRename(file, out, "planter", material);
            } else {
                System.out.println(Arrays.toString(sp) + " [SKIP]");
            }

        }
    }
}
