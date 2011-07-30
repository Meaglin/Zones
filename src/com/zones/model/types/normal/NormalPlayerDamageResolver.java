package com.zones.model.types.normal;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.zones.accessresolver.interfaces.PlayerDamageResolver;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;

public class NormalPlayerDamageResolver implements PlayerDamageResolver {

    @Override
    public boolean isAllowed(ZoneBase zone, Player player, DamageCause type, int damage) {
        return zone.getFlag(ZoneVar.HEALTH);
    }

}
