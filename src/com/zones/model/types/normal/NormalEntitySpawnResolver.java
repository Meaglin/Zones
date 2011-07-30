package com.zones.model.types.normal;

import java.util.List;

import org.bukkit.entity.Animals;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Slime;

import com.zones.accessresolver.interfaces.EntitySpawnResolver;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;

public class NormalEntitySpawnResolver implements EntitySpawnResolver {

    @Override
    public boolean isAllowed(ZoneBase zone, Entity entity, CreatureType type) {
        if(entity instanceof Animals) {
            if(zone.getSettings().getBool(ZoneVar.SPAWN_ANIMALS, zone.getWorldManager().getConfig().ANIMAL_SPAWNING_ENABLED)) {
                List<?> list = zone.getSettings().getList(ZoneVar.ANIMALS);
                if(list != null && !list.contains(type))
                    return false;
                else
                    return true;
            } else {
                return false;
            }
        } else if(entity instanceof Monster || entity instanceof Flying || entity instanceof Slime) {
            if(zone.getSettings().getBool(ZoneVar.SPAWN_MOBS, zone.getWorldManager().getConfig().MOB_SPAWNING_ENABLED)) {
                List<?> list = zone.getSettings().getList(ZoneVar.MOBS);
                if(list != null && !list.contains(type))
                    return false;
                else
                    return true;
            } else {
                return false;
            }       
        } else
            return true;
    }
    
}
