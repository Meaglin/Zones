package com.zones.accessresolver.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.zones.model.ZoneBase;

public interface PlayerDamageResolver extends Resolver{
    public boolean isAllowed(ZoneBase zone, Player player, DamageCause type, int damage);
}
