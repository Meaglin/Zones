package com.zones.model.types.normal;

import java.util.List;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Slime;

import com.zones.accessresolver.interfaces.EntitySpawnResolver;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;
import org.bukkit.entity.*;

public class NormalEntitySpawnResolver implements EntitySpawnResolver {

    @Override
    public boolean isAllowed(ZoneBase zone, Entity entity, EntityType type) {
        if(entity instanceof Animals) {
            if(zone.getFlag(ZoneVar.SPAWN_ANIMALS)) {
                Object obj = zone.getSetting(ZoneVar.ANIMALS);
                if(obj != null) {
                    if(!((List<?>)obj).contains(type))
                        return false;
                }
                return true;
            } 
            return false;
        } else if(entity instanceof Monster || entity instanceof Flying || entity instanceof Slime) {
            if(zone.getFlag(ZoneVar.SPAWN_MOBS)) {
                Object obj = zone.getSetting(ZoneVar.MOBS);
                if(obj != null) {
                    if(!((List<?>)obj).contains(type))
                        return false;
                }
                return true;
            } 
            return false;
        } 
        return true;
    }
    
}
