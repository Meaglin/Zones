package com.zones.accessresolver.interfaces;


import com.zones.model.ZoneBase;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public interface EntitySpawnResolver extends Resolver {
    public boolean isAllowed(ZoneBase zone, Entity entity, EntityType type);
}
