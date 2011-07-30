package com.zones.accessresolver.interfaces;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;

import com.zones.model.ZoneBase;

public interface EntitySpawnResolver extends Resolver {
    public boolean isAllowed(ZoneBase zone, Entity entity, CreatureType type);
}
