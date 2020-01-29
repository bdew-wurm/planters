package net.bdew.planters;

import com.wurmonline.server.Items;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.items.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlanterTracker {
    private static Set<Long> planterIds = new HashSet<>();

    public static void addPlanter(Item planter) {
        planterIds.add(planter.getWurmId());
    }

    public static void removePlanter(Item planter) {
        planterIds.remove(planter.getWurmId());
    }

    public static int trackedCount() {
        return planterIds.size();
    }

    public static List<Item> getPlanters() {
        ArrayList<Item> res = new ArrayList<>();
        HashSet<Long> toClear = new HashSet<>();
        for (long id : planterIds) {
            try {
                Item item = Items.getItem(id);
                if (PlanterItem.needsPolling(item))
                    res.add(item);
                else
                    toClear.add(id);
            } catch (NoSuchItemException e) {
                toClear.add(id);
            }
        }
        if (!toClear.isEmpty()) planterIds.removeAll(toClear);
        return res;
    }
}
