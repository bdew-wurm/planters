package net.bdew.planters.area;

import net.bdew.planters.PlanterItem;
import net.bdew.planters.PlantersMod;
import net.bdew.wurm.betterfarm.api.AreaActionType;
import net.bdew.wurm.betterfarm.api.BetterFarmAPI;

public class BetterFarmHandler {
    private static PlanterActionSow sow;

    private static void addActions(int id) {
        BetterFarmAPI.INSTANCE.addItemAreaHandler(id, AreaActionType.SOW, sow);
    }

    public static void install() {
        if (BetterFarmAPI.INSTANCE == null) {
            PlantersMod.logWarning("Better Farming is loaded but api is not initialized?");
        } else if (BetterFarmAPI.INSTANCE.apiVersion() != 2) {
            PlantersMod.logWarning("Better Farming API version mismatch - skipping");
        } else {
            PlantersMod.logInfo("Adding better farming support");
            sow = new PlanterActionSow();
            addActions(PlanterItem.woodId);
            addActions(PlanterItem.stoneId);
            if (PlantersMod.magicMushrooms) {
                addActions(PlanterItem.magicWoodId);
                addActions(PlanterItem.magicStoneId);
            }
        }
    }
}
