package net.bdew.planters;

import com.wurmonline.server.items.ItemList;

public enum Plantable {
    BlackMushroom(1, "black mushroom", ItemList.mushroomBlack, ItemList.mushroomBlack, "mushroom.black", 50, false),
    RedMushroom(2, "red mushroom", ItemList.mushroomRed, ItemList.mushroomRed, "mushroom.red", 40, false),
    BrownMushroom(3, "brown mushroom", ItemList.mushroomBrown, ItemList.mushroomBrown, "mushroom.brown", 30, false),
    BlueMushroom(4, "blue mushroom", ItemList.mushroomBlue, ItemList.mushroomBlue, "mushroom.blue", 20, false),
    YellowMushroom(5, "yellow mushroom", ItemList.mushroomYellow, ItemList.mushroomYellow, "mushroom.yellow", 10, false),
    GreenMushroom(6, "green mushroom", ItemList.mushroomGreen, ItemList.mushroomGreen, "mushroom.green", 5, false),
    Cabbage(7, "cabbage", ItemList.cabbageSeeds, ItemList.cabbage, "cabbage", 35, false),
    Pumpkin(8, "pumpkin", ItemList.pumpkinSeed, ItemList.pumpkin, "pumpkin", 15, false),
    Barley(9, "barley", ItemList.barley, ItemList.barley, "barley", 20, true),
    Corn(10, "corn", ItemList.corn, ItemList.corn, "corn", 40, false),
    Cotton(11, "cotton", ItemList.cottonSeed, ItemList.cotton, "cotton", 7, false),
    Garlic(12, "garlic", ItemList.garlic, ItemList.garlic, "garlic", 70, false),
    Oat(13, "oat", ItemList.oat, ItemList.oat, "oat", 15, true),
    Onion(14, "onion", ItemList.onion, ItemList.onion, "onion", 60, false),
    Potato(15, "potato", ItemList.potato, ItemList.potato, "potato", 4, false),
    Rye(16, "rye", ItemList.rye, ItemList.rye, "rye", 10, true),
    Strawberries(17, "strawberries", ItemList.strawberrySeed, ItemList.strawberries, "strawberries", 60, false),
    Wemp(18, "wemp", ItemList.wempSeed, ItemList.wemp, "wemp", 10, false),
    Wheat(19, "wheat", ItemList.wheat, ItemList.wheat, "wheat", 30, true),
    Tomatoes(20, "tomatoes", ItemList.tomatoSeeds, ItemList.tomato, "tomatoes", 45, false),
    Sugarbeet(21, "sugar beet", ItemList.sugarBeetSeeds, ItemList.sugarBeet, "sugarbeet", 85, false),
    Lettuce(22, "lettuce", ItemList.lettuceSeeds, ItemList.lettuce, "lettuce", 55, false),
    Peapods(23, "peas", ItemList.pea, ItemList.peaPod, "peapods", 65, false),
    Carrots(24, "carrots", ItemList.carrotSeeds, ItemList.carrot, "carrots", 25, false),
    Cucumber(25, "cucumber", ItemList.cucumberSeeds, ItemList.cucumber, "cucumber", 15, false);

    public final int number;
    public final String displayName;
    public final int seedItem;
    public final int cropItem;
    public final String modelName;
    public final float difficulty;
    public final boolean needsScythe;

    Plantable(int number, String displayName, int seedItem, int cropItem, String modelName, float difficulty, boolean needsScythe) {
        this.number = number;
        this.displayName = displayName;
        this.seedItem = seedItem;
        this.cropItem = cropItem;
        this.modelName = modelName + ".";
        this.difficulty = difficulty;
        this.needsScythe = needsScythe;
    }

    public static Plantable getFromId(int id) {
        for (Plantable value : values())
            if (value.number == id) return value;
        return null;
    }

    public static Plantable findSeed(int id) {
        for (Plantable value : values())
            if (value.seedItem == id) return value;
        return null;
    }
}
