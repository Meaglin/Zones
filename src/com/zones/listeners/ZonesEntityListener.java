package com.zones.listeners;

import com.zones.World;
import com.zones.ZoneType;
import com.zones.Zones;
import com.zones.ZonesConfig;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimedEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * 
 * @author Meaglin
 */
public class ZonesEntityListener extends EntityListener {

    private Zones zones;

    public ZonesEntityListener(Zones zones) {
        this.zones = zones;
    }

    public void onEntityDamage(EntityDamageEvent event) {
        Entity defender = event.getEntity();
        if (defender instanceof Player) {

            if (event.getCause() == DamageCause.FALL && !ZonesConfig.FALL_DAMAGE_ENABLED)
                event.setCancelled(true);

            ZoneType zone = World.getInstance().getActiveZone((Player) defender);
            if (zone == null && !ZonesConfig.HEALTH_ENABLED)
                event.setCancelled(true);

            if (zone != null && (!zone.allowHealth()))
                event.setCancelled(true);

        }
    }

    public void onEntityExplode(EntityExplodeEvent event) {
        Location loc = event.getLocation();
        ZoneType zone = World.getInstance().getActiveZone(loc.getX(), loc.getZ(), loc.getY());
        if (zone == null) {
            if (!ZonesConfig.TNT_ENABLED)
                event.setCancelled(true);
        } else {
            if (!zone.allowDynamite(event.getLocation().getBlock()))
                event.setCancelled(true);
        }

    }

    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Location loc = event.getLocation();
        ZoneType zone = World.getInstance().getActiveZone(loc.getX(), loc.getZ(), loc.getY());
        if (zone == null) {
            if (event.getEntity() instanceof Animals && !ZonesConfig.ANIMALS_ENABLED)
                event.setCancelled(true);
            else if (event.getEntity() instanceof Monster && !ZonesConfig.MOBS_ENABLED)
                event.setCancelled(true);
        } else {
            if (event.getEntity() instanceof Animals && !zone.isAnimalsAllowed())
                event.setCancelled(true);
            else if (event.getEntity() instanceof Monster && !zone.isAnimalsAllowed())
                event.setCancelled(true);
        }
    }

    public void onEntityCombust(EntityCombustEvent event) {
    }

    public void onExplosionPrimed(ExplosionPrimedEvent event) {
    }

    public void onEntityDeath(EntityDeathEvent event) {
    }

    public void onEntityTarget(EntityTargetEvent event) {
    }

}
