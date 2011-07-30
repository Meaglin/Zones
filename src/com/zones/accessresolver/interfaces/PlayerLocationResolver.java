package com.zones.accessresolver.interfaces;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.zones.model.ZoneBase;

public interface PlayerLocationResolver extends MessagebleResolver {
    public boolean isAllowed(ZoneBase zone, Player player, Location from, Location to);
}
