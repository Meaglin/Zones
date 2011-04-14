package com.zones.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
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

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.model.ZoneBase;


/**
 * 
 * @author Meaglin
 *
 */
public class ZonesEntityListener extends EntityListener {

    private Zones plugin;

    public ZonesEntityListener(Zones zones) {
        this.plugin = zones;
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.isCancelled()) return;
        
        Entity defender = event.getEntity();
        Entity attacker = null;
        if(event instanceof EntityDamageByEntityEvent) {
            attacker = ((EntityDamageByEntityEvent)event).getDamager();
        }
        WorldManager wm = plugin.getWorldManager(defender.getWorld());
        ZoneBase zone = wm.getActiveZone(defender.getLocation());
        
        if (zone == null) {
            if(defender instanceof Player) {
                Player p  = (Player)defender;
                if(!wm.getConfig().canReceiveDamage(p, event.getCause())) {
                    event.setCancelled(true);
                }
            } else if(event.getCause() == DamageCause.BLOCK_EXPLOSION) {
                if(!wm.getConfig().EXPLOSION_DAMAGE_ENTITIES) {
                    event.setCancelled(true);
                    return;
                }
            }
        } else {
            if (defender instanceof Player) {                
                if (!zone.allowHealth(((Player)defender)) || (wm.getConfig().PLAYER_ENFORCE_SPECIFIC_DAMAGE && !wm.getConfig().canReceiveSpecificDamage((Player)defender, event.getCause()))) {
                    event.setCancelled(true);
                    return;
                }
            }
            if(attacker != null && attacker instanceof Player) {
                Player att = (Player)attacker;
                if(!zone.allowEntityHit(att, defender)) {
                    att.sendMessage(ChatColor.RED + "You cannot kill entity's in " + zone.getName() + "!");
                    event.setCancelled(true);
                }
            }            
        }
    }

    @Override
    public void onEntityExplode(EntityExplodeEvent event) {
        if(event.isCancelled()) return;
        
        WorldManager wm = plugin.getWorldManager(event.getLocation());
        ZoneBase zone = wm.getActiveZone(event.getLocation());
        if (zone == null) {
            if(event.getEntity() instanceof Creeper) {
                if(!wm.getConfig().ALLOW_CREEPER_TRIGGER) {
                    event.setCancelled(true);
                }
            } else {
                if(!wm.getConfig().ALLOW_TNT_TRIGGER) {
                    event.setCancelled(true);
                }
            }
        } else {
            if (!zone.allowDynamite(event.getLocation().getBlock()))
                event.setCancelled(true);
        }

    }

    @Override
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(event.isCancelled()) return;
        
        WorldManager wm = plugin.getWorldManager(event.getLocation());
        ZoneBase zone = wm.getActiveZone(event.getLocation());
        if (zone == null) {
            if(!wm.getConfig().canSpawn(event.getEntity(), event.getCreatureType())){
                event.setCancelled(true);
            }
        } else {
            if (!zone.allowSpawn(event.getEntity(),event.getCreatureType())){
                event.setCancelled(true);
            }
        }
    }

    public void onEntityCombust(EntityCombustEvent event) {
    }

    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if(event.isCancelled()) return;
        
        WorldManager wm = plugin.getWorldManager(event.getEntity().getWorld());
        if(event.getEntity() instanceof Creeper) {
            event.setRadius(wm.getConfig().CREEPER_EXPLOSION_RANGE);
        } else {
            event.setRadius(wm.getConfig().TNT_RANGE);            
        }
    }

    public void onEntityDeath(EntityDeathEvent event) {
    }

    public void onEntityTarget(EntityTargetEvent event) {
    }

}
