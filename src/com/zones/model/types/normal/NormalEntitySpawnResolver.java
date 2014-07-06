package com.zones.model.types.normal;

import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Slime;

import com.meaglin.json.JSONObject;
import com.zones.accessresolver.interfaces.EntitySpawnResolver;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;

public class NormalEntitySpawnResolver implements EntitySpawnResolver {

    @Override
    public boolean isAllowed(ZoneBase zone, Entity entity, EntityType type) {
        if(entity instanceof Animals || entity instanceof Ambient) {
            if(zone.getFlag(ZoneVar.ANIMALS)) {
                JSONObject settings = zone.getConfig().getJSONObject("settings");
                if(settings.has(ZoneVar.ALLOWED_ANIMALS.getName())
                        && !settings.getJSONArray(ZoneVar.ALLOWED_ANIMALS.getName()).contains(type.name())) {
                    return false;
                }
                return true;
            } 
            return false;
        } else if(entity instanceof Monster || entity instanceof Flying || entity instanceof Slime) {
            if(zone.getFlag(ZoneVar.MOBS)) {
                JSONObject settings = zone.getConfig().getJSONObject("settings");
                if(settings.has(ZoneVar.ALLOWED_MOBS.getName())
                        && !settings.getJSONArray(ZoneVar.ALLOWED_MOBS.getName()).contains(type.name())) {
                    return false;
                }
                return true;
            } 
            return false;
        } 
        return true;
    }
    
}
