package com.zones.accessresolver.interfaces;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.zones.model.ZoneBase;

public interface PlayerHitEntityResolver extends MessagebleResolver {
    public boolean isAllowed(ZoneBase zone, Player attacker, Entity defender, int damage);
}
