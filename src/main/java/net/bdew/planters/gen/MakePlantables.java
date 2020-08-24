package net.bdew.planters.gen;

import com.wurmonline.mesh.BushData;
import com.wurmonline.mesh.TreeData;
import com.wurmonline.shared.util.StringUtilities;
import net.bdew.planters.Plantable;

public class MakePlantables {
    public static String getBushProduce(final BushData.BushType type) {
        switch (type) {
            case LAVENDER:
                return "ItemList.flowerLavender";
            case ROSE:
                return "ItemList.flowerRose";
            case GRAPE:
                return "ItemList.grapesGreen";
            case CAMELLIA:
                return "ItemList.leavesCamellia";
            case OLEANDER:
                return "ItemList.leavesOleander";
            case HAZELNUT:
                return "ItemList.nutHazel";
            case RASPBERRY:
                return "ItemList.raspberries";
            case BLUEBERRY:
                return "ItemList.blueberry";
            case LINGONBERRY:
                return "ItemList.lingonberry";
            default:
                return "-1";
        }
    }

    public static String getTreeProduce(final TreeData.TreeType type) {
        switch (type) {
            case MAPLE:
                return "ItemList.sapMaple";
            case APPLE:
                return "ItemList.appleGreen";
            case LEMON:
                return "ItemList.lemon";
            case OLIVE:
                return "ItemList.olive";
            case CHERRY:
                return "ItemList.cherries";
            case CHESTNUT:
                return "ItemList.chestnut";
            case WALNUT:
                return "ItemList.walnut";
            case PINE:
                return "ItemList.pineNuts";
            case OAK:
                return "ItemList.acorn";
            case ORANGE:
                return "ItemList.orange";
            default:
                return "-1";
        }
    }

    public static void main(String[] args) {
        int n = Plantable.MagicMushroom.number + 1;

        for (TreeData.TreeType tree : TreeData.TreeType.values()) {
            System.out.println(String.format("%s(%d, \"%s\", PlanterType.TREE, ItemList.sprout, %s, \"%s\", %d, (byte) %d, false, false),",
                    StringUtilities.raiseFirstLetter(tree.name().toLowerCase()) + "Tree",
                    n++,
                    tree.name().toLowerCase(),
                    getTreeProduce(tree),
                    tree.getModelResourceName(5),
                    tree.getDifficulty(),
                    tree.getMaterial()
            ));
        }
        for (BushData.BushType tree : BushData.BushType.values()) {
            System.out.println(String.format("%s(%d, \"%s\", PlanterType.BUSH, ItemList.sprout, %s, \"%s\", %d, (byte) %d, false, false),",
                    StringUtilities.raiseFirstLetter(tree.name().toLowerCase()) + "Bush",
                    n++,
                    tree.name().toLowerCase(),
                    getBushProduce(tree),
                    tree.getModelResourceName(5),
                    tree.getDifficulty(),
                    tree.getMaterial()
            ));
        }

    }
}
