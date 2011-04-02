package com.zones.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.zones.World;
import com.zones.ZoneBase;
import com.zones.Zones;
import com.zones.ZonesConfig;


/**
 * 
 * @author Meaglin
 *
 */
public class ZonesEntityListener extends EntityListener {

    @SuppressWarnings("unused")
    private Zones zones;

    public ZonesEntityListener(Zones zones) {
        this.zones = zones;
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        Entity defender = event.getEntity();
        Entity attacker = null;
        if(event instanceof EntityDamageByEntityEvent) {
            attacker = ((EntityDamageByEntityEvent)event).getDamager();
        }
        ZoneBase zone = World.getInstance().getActiveZone(defender.getLocation());
        if (defender instanceof Player) {

            if (event.getCause() == DamageCause.FALL && !ZonesConfig.FALL_DAMAGE_ENABLED)
                event.setCancelled(true);

            if (zone == null && !ZonesConfig.HEALTH_ENABLED) {
                event.setCancelled(true);
                return;
            }
            
            if (zone != null && (!zone.allowHealth(((Player)defender)))) {
                event.setCancelled(true);
                return;
            }

        }
        if(attacker != null && zone != null && attacker instanceof Player) {
            Player att = (Player)attacker;
            if(!zone.allowEntityHit(att, defender)) {
                att.sendMessage(ChatColor.RED + "You cannot kill entity's in " + zone.getName() + "!");
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onEntityExplode(EntityExplodeEvent event) {
        ZoneBase zone = World.getInstance().getActiveZone(event.getLocation());
        if (zone == null) {
            if (!ZonesConfig.TNT_ENABLED)
                event.setCancelled(true);
        } else {
            if (!zone.allowDynamite(event.getLocation().getBlock()))
                event.setCancelled(true);
        }

    }

    @Override
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        ZoneBase zone = World.getInstance().getActiveZone(event.getLocation());
        if (zone == null) {
            if (event.getEntity() instanceof Animals && !ZonesConfig.ANIMALS_ENABLED)
                event.setCancelled(true);
            else if (event.getEntity() instanceof Monster && !ZonesConfig.MOBS_ENABLED)
                event.setCancelled(true);
        } else {
            if (!zone.allowSpawn(event.getEntity()))
                event.setCancelled(true);
        }
    }

    public void onEntityCombust(EntityCombustEvent event) {
    }

    public void onExplosionPrime(ExplosionPrimeEvent event) {
    }

    public void onEntityDeath(EntityDeathEvent event) {
    }

    public void onEntityTarget(EntityTargetEvent event) {
    }

}
