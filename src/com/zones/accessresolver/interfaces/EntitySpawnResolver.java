package com.zones.accessresolver.interfaces;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.zones.model.ZoneBase;

public interface EntitySpawnResolver extends Resolver {
    public boolean isAllowed(ZoneBase zone, Entity entity, EntityType type);
}
