package net.bdew.planters.area;

import net.bdew.planters.PlanterItem;
import net.bdew.planters.PlantersMod;
import net.bdew.wurm.betterfarm.api.AreaActionType;
import net.bdew.wurm.betterfarm.api.BetterFarmAPI;

public class BetterFarmHandler {
    private static PlanterActionSow sow;
    private static PlanterActionHarvest harvest, replant;
    private static PlanterActionCultivate cultivate;
    private static PlanterActionTend tend;

    private static void addActions(int id) {
        BetterFarmAPI.INSTANCE.addItemAreaHandler(id, AreaActionType.SOW, sow);
        BetterFarmAPI.INSTANCE.addItemAreaHandler(id, AreaActionType.HARVEST, harvest);
        BetterFarmAPI.INSTANCE.addItemAreaHandler(id, AreaActionType.HARVEST_AND_REPLANT, replant);
        BetterFarmAPI.INSTANCE.addItemAreaHandler(id, AreaActionType.CULTIVATE, cultivate);
        BetterFarmAPI.INSTANCE.addItemAreaHandler(id, AreaActionType.FARM, tend);
    }

    public static void install() {
        if (BetterFarmAPI.INSTANCE == null) {
            PlantersMod.logWarning("Better Farming is loaded but api is not initialized?");
        } else if (BetterFarmAPI.INSTANCE.apiVersion() != 3) {
            PlantersMod.logWarning("Better Farming API version mismatch - skipping");
        } else {
            PlantersMod.logInfo("Adding better farming support");

            sow = new PlanterActionSow();
            harvest = new PlanterActionHarvest(false);
            replant = new PlanterActionHarvest(true);
            tend = new PlanterActionTend();
            cultivate = new PlanterActionCultivate();

            addActions(PlanterItem.woodId);
            addActions(PlanterItem.stoneId);

            if (PlantersMod.magicMushrooms) {
                addActions(PlanterItem.magicWoodId);
                addActions(PlanterItem.magicStoneId);
            }
        }
    }
}
