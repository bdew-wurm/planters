package net.bdew.planters;

import com.wurmonline.server.items.ItemList;

public enum Plantable {
    BlackMushroom(1, "black mushroom", ItemList.mushroomBlack, ItemList.mushroomBlack, "mushroom.black", 50),
    RedMushroom(2, "red mushroom", ItemList.mushroomRed, ItemList.mushroomRed, "mushroom.red", 40),
    BrownMushroom(3, "brown mushroom", ItemList.mushroomBrown, ItemList.mushroomBrown, "mushroom.brown", 30),
    BlueMushroom(4, "blue mushroom", ItemList.mushroomBlue, ItemList.mushroomBlue, "mushroom.blue", 20),
    YellowMushroom(5, "yellow mushroom", ItemList.mushroomYellow, ItemList.mushroomYellow, "mushroom.yellow", 10),
    GreenMushroom(6, "green mushroom", ItemList.mushroomGreen, ItemList.mushroomGreen, "mushroom.green", 5),
    Cabbage(7, "cabbage", ItemList.cabbageSeeds, ItemList.cabbage, "cabbage", 35),
    Pumpkin(8, "pumpkin", ItemList.pumpkinSeed, ItemList.pumpkin, "pumpkin", 15),
    Barley(9, "barley", ItemList.barley, ItemList.barley, "barley", 20),
    Corn(10, "corn", ItemList.corn, ItemList.corn, "corn", 40),
    Cotton(11, "cotton", ItemList.cottonSeed, ItemList.cotton, "cotton", 7),
    Garlic(12, "garlic", ItemList.garlic, ItemList.garlic, "garlic", 70),
    Oat(13, "oat", ItemList.oat, ItemList.oat, "oat", 15),
    Onion(14, "onion", ItemList.onion, ItemList.onion, "onion", 60),
    Potato(15, "potato", ItemList.potato, ItemList.potato, "potato", 4),
    Rye(16, "rye", ItemList.rye, ItemList.rye, "rye", 10),
    Strawberries(17, "strawberries", ItemList.strawberrySeed, ItemList.strawberries, "strawberries", 60),
    Wemp(18, "wemp", ItemList.wempSeed, ItemList.wemp, "wemp", 10),
    Wheat(19, "wheat", ItemList.wheat, ItemList.wheat, "wheat", 30),
    Tomatoes(20, "tomatoes", ItemList.tomatoSeeds, ItemList.tomato, "tomatoes", 45),
    Sugarbeet(21, "sugar beet", ItemList.sugarBeetSeeds, ItemList.sugarBeet, "sugarbeet", 85),
    Lettuce(22, "lettuce", ItemList.lettuceSeeds, ItemList.lettuce, "lettuce", 55),
    Peapods(23, "peas", ItemList.pea, ItemList.peaPod, "peapods", 65),
    Carrots(24, "carrots", ItemList.carrotSeeds, ItemList.carrot, "carrots", 25),
    Cucumber(25, "cucumber", ItemList.cucumberSeeds, ItemList.cucumber, "cucumber", 15);

    public final int number;
    public final String displayName;
    public final int seedItem;
    public final int cropItem;
    public final String modelName;
    public final float difficulty;

    Plantable(int number, String displayName, int seedItem, int cropItem, String modelName, float difficulty) {
        this.number = number;
        this.displayName = displayName;
        this.seedItem = seedItem;
        this.cropItem = cropItem;
        this.modelName = modelName + ".";
        this.difficulty = difficulty;
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
