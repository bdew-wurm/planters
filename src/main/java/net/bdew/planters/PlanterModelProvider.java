package net.bdew.planters;

import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.shared.util.MaterialUtilities;
import org.gotti.wurmunlimited.modsupport.items.ModelNameProvider;

public class PlanterModelProvider implements ModelNameProvider {
    private static boolean isWinter() {
        return GmCommands.forceWinter.orElse(WurmCalendar.isWinter());
    }

    @Override
    public String getModelName(Item item) {
        StringBuilder sb = new StringBuilder(item.getTemplate().getModelName());
        Plantable plant = PlanterItem.getPlantable(item);
        PlanterType type = PlanterItem.getPlanterType(item.getTemplateId());

        if (type != PlanterType.TREE && type != PlanterType.BUSH) {
            if (plant != null) {
                sb.append(plant.modelName);

                int growth = PlanterItem.getGrowthStage(item);
                if (growth < 5) {
                    sb.append("young.");
                    if (!PlanterItem.isTended(item))
                        sb.append("untended.");
                } else {
                    sb.append("ripe.");
                    if (growth > 5) {
                        sb.append("wilted.");
                    }
                }
            } else sb.append("dirt.");
        }

        if (PlanterItem.isInfected(item))
            sb.append("infected.");
        else if (item.getDamage() >= 50f)
            sb.append("decayed.");

        if (isWinter() && item.isOnSurface()) {
            VolaTile vt = Zones.getTileOrNull(item.getTilePos(), true);
            if (vt != null) {
                Structure st = vt.getStructure();
                if (st == null || st.isTypeBridge())
                    sb.append("winter.");
            }
        }

        sb.append(MaterialUtilities.getMaterialString(item.getMaterial()));

        return sb.toString();

    }
}
