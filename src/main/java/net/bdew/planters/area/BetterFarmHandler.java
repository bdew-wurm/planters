package net.bdew.planters.area;

import net.bdew.planters.PlanterItem;
import net.bdew.planters.PlantersMod;
import net.bdew.wurm.betterfarm.api.AreaActionType;
import net.bdew.wurm.betterfarm.api.BetterFarmAPI;

public class BetterFarmHandler {
    public static void install() {
        if (BetterFarmAPI.INSTANCE == null) {
            PlantersMod.logWarning("Better Farming is loaded but api is not initialized?");
        } else if (BetterFarmAPI.INSTANCE.apiVersion() != 3) {
            PlantersMod.logWarning("Better Farming API version mismatch - skipping");
        } else {
            PlantersMod.logInfo("Adding better farming support");

            PlanterActionSow sow = new PlanterActionSow();
            PlanterActionHarvest harvest = new PlanterActionHarvest(false);
            PlanterActionHarvest replant = new PlanterActionHarvest(true);
            PlanterActionTend tend = new PlanterActionTend();
            PlanterActionCultivate cultivate = new PlanterActionCultivate();

            PlanterItem.allTemplateIds().forEach(tpl -> {
                BetterFarmAPI.INSTANCE.addItemAreaHandler(tpl, AreaActionType.SOW, sow);
                BetterFarmAPI.INSTANCE.addItemAreaHandler(tpl, AreaActionType.HARVEST, harvest);
                BetterFarmAPI.INSTANCE.addItemAreaHandler(tpl, AreaActionType.HARVEST_AND_REPLANT, replant);
                BetterFarmAPI.INSTANCE.addItemAreaHandler(tpl, AreaActionType.CULTIVATE, cultivate);
                BetterFarmAPI.INSTANCE.addItemAreaHandler(tpl, AreaActionType.FARM, tend);
            });
        }
    }
}
