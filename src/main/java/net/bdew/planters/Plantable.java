package net.bdew.planters;

import com.wurmonline.server.items.ItemList;

public enum Plantable {
    BlackMushroom(1, "black mushroom", PlanterType.NORMAL, ItemList.mushroomBlack, ItemList.mushroomBlack, "mushroom.black", 50, (byte) -1, false, false),
    RedMushroom(2, "red mushroom", PlanterType.NORMAL, ItemList.mushroomRed, ItemList.mushroomRed, "mushroom.red", 40, (byte) -1, false, false),
    BrownMushroom(3, "brown mushroom", PlanterType.NORMAL, ItemList.mushroomBrown, ItemList.mushroomBrown, "mushroom.brown", 30, (byte) -1, false, false),
    BlueMushroom(4, "blue mushroom", PlanterType.NORMAL, ItemList.mushroomBlue, ItemList.mushroomBlue, "mushroom.blue", 20, (byte) -1, false, false),
    YellowMushroom(5, "yellow mushroom", PlanterType.NORMAL, ItemList.mushroomYellow, ItemList.mushroomYellow, "mushroom.yellow", 10, (byte) -1, false, false),
    GreenMushroom(6, "green mushroom", PlanterType.NORMAL, ItemList.mushroomGreen, ItemList.mushroomGreen, "mushroom.green", 5, (byte) -1, false, false),
    Cabbage(7, "cabbage", PlanterType.NORMAL, ItemList.cabbageSeeds, ItemList.cabbage, "cabbage", 35, (byte) -1, false, false),
    Pumpkin(8, "pumpkin", PlanterType.NORMAL, ItemList.pumpkinSeed, ItemList.pumpkin, "pumpkin", 15, (byte) -1, false, false),
    Barley(9, "barley", PlanterType.NORMAL, ItemList.barley, ItemList.barley, "barley", 20, (byte) -1, true, false),
    Corn(10, "corn", PlanterType.NORMAL, ItemList.corn, ItemList.corn, "corn", 40, (byte) -1, false, false),
    Cotton(11, "cotton", PlanterType.NORMAL, ItemList.cottonSeed, ItemList.cotton, "cotton", 7, (byte) -1, false, false),
    Garlic(12, "garlic", PlanterType.NORMAL, ItemList.garlic, ItemList.garlic, "garlic", 70, (byte) -1, false, false),
    Oat(13, "oat", PlanterType.NORMAL, ItemList.oat, ItemList.oat, "oat", 15, (byte) -1, true, false),
    Onion(14, "onion", PlanterType.NORMAL, ItemList.onion, ItemList.onion, "onion", 60, (byte) -1, false, false),
    Potato(15, "potato", PlanterType.NORMAL, ItemList.potato, ItemList.potato, "potato", 4, (byte) -1, false, false),
    Rye(16, "rye", PlanterType.NORMAL, ItemList.rye, ItemList.rye, "rye", 10, (byte) -1, true, false),
    Strawberries(17, "strawberries", PlanterType.NORMAL, ItemList.strawberrySeed, ItemList.strawberries, "strawberries", 60, (byte) -1, false, false),
    Wemp(18, "wemp", PlanterType.NORMAL, ItemList.wempSeed, ItemList.wemp, "wemp", 10, (byte) -1, false, false),
    Wheat(19, "wheat", PlanterType.NORMAL, ItemList.wheat, ItemList.wheat, "wheat", 30, (byte) -1, true, false),
    Tomatoes(20, "tomatoes", PlanterType.NORMAL, ItemList.tomatoSeeds, ItemList.tomato, "tomatoes", 45, (byte) -1, false, false),
    Sugarbeet(21, "sugar beet", PlanterType.NORMAL, ItemList.sugarBeetSeeds, ItemList.sugarBeet, "sugarbeet", 85, (byte) -1, false, false),
    Lettuce(22, "lettuce", PlanterType.NORMAL, ItemList.lettuceSeeds, ItemList.lettuce, "lettuce", 55, (byte) -1, false, false),
    Peapods(23, "peas", PlanterType.NORMAL, ItemList.pea, ItemList.peaPod, "peapods", 65, (byte) -1, false, false),
    Carrots(24, "carrots", PlanterType.NORMAL, ItemList.carrotSeeds, ItemList.carrot, "carrots", 25, (byte) -1, false, false),
    Cucumber(25, "cucumber", PlanterType.NORMAL, ItemList.cucumberSeeds, ItemList.cucumber, "cucumber", 15, (byte) -1, false, false),
    Rice(26, "rice", PlanterType.NORMAL, ItemList.rice, ItemList.rice, "rice", 80, (byte) -1, false, true),
    Reeds(27, "reeds", PlanterType.NORMAL, ItemList.reedSeed, ItemList.reed, "reed", 20, (byte) -1, false, true),
    MagicMushroom(28, "magic mushroom", PlanterType.MAGIC, ItemList.sourceSalt, MiscItems.magicShroomId, "magic", 80, (byte) -1, false, false),
    BirchTree(29, "birch", PlanterType.TREE, ItemList.sprout, -1, "model.tree.birch", 2, (byte) 14, false, false),
    PineTree(30, "pine", PlanterType.TREE, ItemList.sprout, ItemList.pineNuts, "model.tree.pine", 2, (byte) 37, false, false),
    OakTree(31, "oak", PlanterType.TREE, ItemList.sprout, ItemList.acorn, "model.tree.oak", 20, (byte) 38, false, false),
    CedarTree(32, "cedar", PlanterType.TREE, ItemList.sprout, -1, "model.tree.cedar", 5, (byte) 39, false, false),
    WillowTree(33, "willow", PlanterType.TREE, ItemList.sprout, -1, "model.tree.willow", 18, (byte) 40, false, false),
    MapleTree(34, "maple", PlanterType.TREE, ItemList.sprout, ItemList.sapMaple, "model.tree.maple", 4, (byte) 41, false, false),
    AppleTree(35, "apple", PlanterType.TREE, ItemList.sprout, ItemList.appleGreen, "model.tree.apple", 2, (byte) 42, false, false),
    LemonTree(36, "lemon", PlanterType.TREE, ItemList.sprout, ItemList.lemon, "model.tree.lemon", 2, (byte) 43, false, false),
    OliveTree(37, "olive", PlanterType.TREE, ItemList.sprout, ItemList.olive, "model.tree.olive", 2, (byte) 44, false, false),
    CherryTree(38, "cherry", PlanterType.TREE, ItemList.sprout, ItemList.cherries, "model.tree.cherry", 2, (byte) 45, false, false),
    ChestnutTree(39, "chestnut", PlanterType.TREE, ItemList.sprout, ItemList.chestnut, "model.tree.chestnut", 12, (byte) 63, false, false),
    WalnutTree(40, "walnut", PlanterType.TREE, ItemList.sprout, ItemList.walnut, "model.tree.walnut", 15, (byte) 64, false, false),
    FirTree(41, "fir", PlanterType.TREE, ItemList.sprout, -1, "model.tree.fir", 5, (byte) 65, false, false),
    LindenTree(42, "linden", PlanterType.TREE, ItemList.sprout, -1, "model.tree.linden", 12, (byte) 66, false, false),
    OrangeTree(43, "orange", PlanterType.TREE, ItemList.sprout, ItemList.orange, "model.tree.orange", 2, (byte) 88, false, false),
    LavenderBush(44, "lavender", PlanterType.BUSH, ItemList.sprout, ItemList.flowerLavender, "model.bush.lavendel", 4, (byte) 46, false, false),
    RoseBush(45, "rose", PlanterType.BUSH, ItemList.sprout, ItemList.flowerRose, "model.bush.rose", 5, (byte) 47, false, false),
    ThornBush(46, "thorn", PlanterType.BUSH, ItemList.sprout, -1, "model.bush.thorn", 15, (byte) 48, false, false),
    GrapeBushGreen(47, "green grape", PlanterType.BUSH, ItemList.sprout, ItemList.grapesGreen, "model.bush.grape", 5, (byte) 49, false, false, 54),
    CamelliaBush(48, "camellia", PlanterType.BUSH, ItemList.sprout, ItemList.leavesCamellia, "model.bush.camellia", 3, (byte) 50, false, false),
    OleanderBush(49, "oleander", PlanterType.BUSH, ItemList.sprout, ItemList.leavesOleander, "model.bush.oleander", 2, (byte) 51, false, false),
    HazelnutBush(50, "hazelnut", PlanterType.BUSH, ItemList.sprout, ItemList.nutHazel, "model.bush.hazelnut", 2, (byte) 71, false, false),
    RaspberryBush(51, "raspberry", PlanterType.BUSH, ItemList.sprout, ItemList.raspberries, "model.bush.raspberry", 2, (byte) 90, false, false),
    BlueberryBush(52, "blueberry", PlanterType.BUSH, ItemList.sprout, ItemList.blueberry, "model.bush.blueberry", 2, (byte) 91, false, false),
    LingonberryBush(53, "lingonberry", PlanterType.BUSH, ItemList.sprout, ItemList.lingonberry, "model.bush.lingonberry", 2, (byte) 92, false, false),
    GrapeBushBlue(54, "blue grape", PlanterType.BUSH, ItemList.sprout, ItemList.grapesBlue, "model.bush.grape", 5, (byte) 49, false, false, 47);


    static {
        PlantersMod.logInfo("Plantable enum init, magic shroom id = " + MagicMushroom.cropItem);
    }

    public final int number;
    public final String displayName;
    public final PlanterType planterType;
    public final int seedItem;
    public final int cropItem;
    public final String modelName;
    public final float difficulty;
    public final byte material;
    public final boolean needsScythe;
    public final boolean water;
    public final int altVersion;

    Plantable(int number, String displayName, PlanterType planterType, int seedItem, int cropItem, String modelName, float difficulty, byte material, boolean needsScythe, boolean water, int altVersion) {
        this.number = number;
        this.displayName = displayName;
        this.planterType = planterType;
        this.seedItem = seedItem;
        this.cropItem = cropItem;
        this.modelName = modelName + ".";
        this.difficulty = difficulty;
        this.material = material;
        this.needsScythe = needsScythe;
        this.water = water;
        this.altVersion = altVersion;
    }

    Plantable(int number, String displayName, PlanterType planterType, int seedItem, int cropItem, String modelName, float difficulty, byte material, boolean needsScythe, boolean water) {
        this(number, displayName, planterType, seedItem, cropItem, modelName, difficulty, material, needsScythe, water, -1);
    }

    public static Plantable getFromId(int id) {
        for (Plantable value : values())
            if (value.number == id) return value;
        return null;
    }

    public static Plantable findSeed(int id, PlanterType planterType) {
        for (Plantable value : values())
            if (value.seedItem == id && value.planterType == planterType) return value;
        return null;
    }

    public static Plantable getFromMaterial(byte material) {
        for (Plantable value : values())
            if (value.material == material) return value;
        return null;
    }

}
