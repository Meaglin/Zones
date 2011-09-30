package com.zones.accessresolver.interfaces;

import org.bukkit.entity.Player;

import com.zones.model.ZoneBase;

public interface PlayerFoodResolver extends Resolver {
    public boolean isAllowed(ZoneBase zone, Player player);
}
