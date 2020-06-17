package net.bdew.planters;

import com.wurmonline.server.items.ItemList;

public enum Plantable {
    BlackMushroom(1, "black mushroom", PlanterType.NORMAL, ItemList.mushroomBlack, ItemList.mushroomBlack, "mushroom.black", 50, false, false),
    RedMushroom(2, "red mushroom", PlanterType.NORMAL, ItemList.mushroomRed, ItemList.mushroomRed, "mushroom.red", 40, false, false),
    BrownMushroom(3, "brown mushroom", PlanterType.NORMAL, ItemList.mushroomBrown, ItemList.mushroomBrown, "mushroom.brown", 30, false, false),
    BlueMushroom(4, "blue mushroom", PlanterType.NORMAL, ItemList.mushroomBlue, ItemList.mushroomBlue, "mushroom.blue", 20, false, false),
    YellowMushroom(5, "yellow mushroom", PlanterType.NORMAL, ItemList.mushroomYellow, ItemList.mushroomYellow, "mushroom.yellow", 10, false, false),
    GreenMushroom(6, "green mushroom", PlanterType.NORMAL, ItemList.mushroomGreen, ItemList.mushroomGreen, "mushroom.green", 5, false, false),
    Cabbage(7, "cabbage", PlanterType.NORMAL, ItemList.cabbageSeeds, ItemList.cabbage, "cabbage", 35, false, false),
    Pumpkin(8, "pumpkin", PlanterType.NORMAL, ItemList.pumpkinSeed, ItemList.pumpkin, "pumpkin", 15, false, false),
    Barley(9, "barley", PlanterType.NORMAL, ItemList.barley, ItemList.barley, "barley", 20, true, false),
    Corn(10, "corn", PlanterType.NORMAL, ItemList.corn, ItemList.corn, "corn", 40, false, false),
    Cotton(11, "cotton", PlanterType.NORMAL, ItemList.cottonSeed, ItemList.cotton, "cotton", 7, false, false),
    Garlic(12, "garlic", PlanterType.NORMAL, ItemList.garlic, ItemList.garlic, "garlic", 70, false, false),
    Oat(13, "oat", PlanterType.NORMAL, ItemList.oat, ItemList.oat, "oat", 15, true, false),
    Onion(14, "onion", PlanterType.NORMAL, ItemList.onion, ItemList.onion, "onion", 60, false, false),
    Potato(15, "potato", PlanterType.NORMAL, ItemList.potato, ItemList.potato, "potato", 4, false, false),
    Rye(16, "rye", PlanterType.NORMAL, ItemList.rye, ItemList.rye, "rye", 10, true, false),
    Strawberries(17, "strawberries", PlanterType.NORMAL, ItemList.strawberrySeed, ItemList.strawberries, "strawberries", 60, false, false),
    Wemp(18, "wemp", PlanterType.NORMAL, ItemList.wempSeed, ItemList.wemp, "wemp", 10, false, false),
    Wheat(19, "wheat", PlanterType.NORMAL, ItemList.wheat, ItemList.wheat, "wheat", 30, true, false),
    Tomatoes(20, "tomatoes", PlanterType.NORMAL, ItemList.tomatoSeeds, ItemList.tomato, "tomatoes", 45, false, false),
    Sugarbeet(21, "sugar beet", PlanterType.NORMAL, ItemList.sugarBeetSeeds, ItemList.sugarBeet, "sugarbeet", 85, false, false),
    Lettuce(22, "lettuce", PlanterType.NORMAL, ItemList.lettuceSeeds, ItemList.lettuce, "lettuce", 55, false, false),
    Peapods(23, "peas", PlanterType.NORMAL, ItemList.pea, ItemList.peaPod, "peapods", 65, false, false),
    Carrots(24, "carrots", PlanterType.NORMAL, ItemList.carrotSeeds, ItemList.carrot, "carrots", 25, false, false),
    Cucumber(25, "cucumber", PlanterType.NORMAL, ItemList.cucumberSeeds, ItemList.cucumber, "cucumber", 15, false, false),
    Rice(26, "rice", PlanterType.NORMAL, ItemList.rice, ItemList.rice, "rice", 80, false, true),
    Reeds(27, "reeds", PlanterType.NORMAL, ItemList.reedSeed, ItemList.reed, "reed", 20, false, true),
    MagicMushroom(28, "magic mushroom", PlanterType.MAGIC, ItemList.sourceSalt, ItemList.source, "magic", 80, false, false);

    public final int number;
    public final String displayName;
    public final PlanterType planterType;
    public final int seedItem;
    public final int cropItem;
    public final String modelName;
    public final float difficulty;
    public final boolean needsScythe;
    public final boolean water;

    Plantable(int number, String displayName, PlanterType planterType, int seedItem, int cropItem, String modelName, float difficulty, boolean needsScythe, boolean water) {
        this.number = number;
        this.displayName = displayName;
        this.planterType = planterType;
        this.seedItem = seedItem;
        this.cropItem = cropItem;
        this.modelName = modelName + ".";
        this.difficulty = difficulty;
        this.needsScythe = needsScythe;
        this.water = water;
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
}
