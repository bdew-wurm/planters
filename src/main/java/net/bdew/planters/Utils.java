package net.bdew.planters;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.villages.Villages;

import java.util.function.Predicate;

public class Utils {
    public static boolean checkRoleAllows(Creature performer, Item target, Predicate<VillageRole> check) {
        if (performer.getPower() >= 2) return true;
        Village village = Villages.getVillage(target.getTileX(), target.getTileY(), target.isOnSurface());
        if (village == null) return true;
        VillageRole role = village.getRoleFor(performer);
        return role != null && check.test(role);
    }
}
