package net.bdew.planters;

import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.sounds.SoundPlayer;

import java.util.List;

public class Hooks {
    private static long lastPolledPlanters = System.currentTimeMillis();

    public static void addItemLoading(Item item) {
        if (item.getTemplateId() == PlanterItem.id && PlanterItem.needsPolling(item)) {
            PlanterTracker.addPlanter(item);
        }
    }

    public static void pollPlanters() {
        long start = System.currentTimeMillis();

        if (start - lastPolledPlanters < Servers.localServer.getFieldGrowthTime())
            return;

        List<Item> planters = PlanterTracker.getPlanters();
        if (planters.isEmpty()) return;

        for (Item planter : planters) {
            Plantable crop = PlanterItem.getPlantable(planter);
            if (crop == null) {
                PlanterTracker.removePlanter(planter);
                continue;
            }
            int stage = PlanterItem.getGrowthStage(planter);
            int tendPower = PlanterItem.getTendPower(planter);
            int tendCount = PlanterItem.getTendCount(planter);

            if (stage == 5 && PlantersMod.canWilt && Server.rand.nextFloat() < 0.5f) stage = 6;
            if (stage < 5) stage++;

            PlanterItem.updateData(planter, crop, stage, stage >= 5, tendCount, tendPower);

            if (!PlanterItem.needsPolling(planter)) PlanterTracker.removePlanter(planter);

            if (WurmCalendar.isNight())
                SoundPlayer.playSound("sound.ambient.night.crickets", planter.getTileX(), planter.getTileY(), planter.isOnSurface(), 0f);
            else
                SoundPlayer.playSound("sound.birdsong.bird2", planter.getTileX(), planter.getTileY(), planter.isOnSurface(), 0f);
        }

        PlantersMod.logInfo(String.format("Polled %d planters, took %dms, remaining %d", planters.size(), System.currentTimeMillis() - start, PlanterTracker.trackedCount()));
        lastPolledPlanters = start;
    }
}
