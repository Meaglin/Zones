package com.zones.model.types.normal;

import org.bukkit.entity.Player;

import com.zones.accessresolver.interfaces.PlayerFoodResolver;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;

public class NormalPlayerFoodResolver implements PlayerFoodResolver {

    @Override
    public boolean isAllowed(ZoneBase zone, Player player) {
        return zone.getFlag(ZoneVar.FOOD);
    }

}
