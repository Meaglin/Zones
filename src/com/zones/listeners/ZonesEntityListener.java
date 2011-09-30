package com.zones.listeners;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.accessresolver.AccessResolver;
import com.zones.accessresolver.interfaces.BlockResolver;
import com.zones.accessresolver.interfaces.EntitySpawnResolver;
import com.zones.accessresolver.interfaces.PlayerDamageResolver;
import com.zones.accessresolver.interfaces.PlayerFoodResolver;
import com.zones.accessresolver.interfaces.PlayerHitEntityResolver;
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
        Player player = (defender instanceof Player ? (Player)defender : null);
        if (zone == null) {
            if(player != null) {
                if(!wm.getConfig().canReceiveDamage(player, event.getCause())) {
                    event.setCancelled(true);
                    return;
                }
                if(wm.getConfig().hasGodMode(player)) {
                    event.setCancelled(true);
                    return;
                }
            } else if(event.getCause() == DamageCause.BLOCK_EXPLOSION) {
                if(!wm.getConfig().EXPLOSION_DAMAGE_ENTITIES) {
                    event.setCancelled(true);
                    return;
                }
            }
        } else {
            if (player != null) {                
                if (!((PlayerDamageResolver)zone.getResolver(AccessResolver.PLAYER_RECEIVE_DAMAGE)).isAllowed(zone, player, event.getCause(), event.getDamage()) || (wm.getConfig().PLAYER_ENFORCE_SPECIFIC_DAMAGE && !wm.getConfig().canReceiveSpecificDamage(player, event.getCause()))) {
                    event.setCancelled(true);
                    return;
                }
            }
            if(attacker != null && attacker instanceof Player) {
                Player att = (Player)attacker;
                if(!((PlayerHitEntityResolver)zone.getResolver(AccessResolver.PLAYER_ENTITY_HIT)).isAllowed(zone, att, defender, event.getDamage())) {
                    att.sendMessage(ChatColor.RED + "You cannot kill entities in " + zone.getName() + "!");
                    event.setCancelled(true);
                    return;
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
            if (!((BlockResolver)zone.getResolver(AccessResolver.DYNAMITE)).isAllowed(zone, event.getLocation().getBlock()))
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
            if (!((EntitySpawnResolver)zone.getResolver(AccessResolver.ENTITY_SPAWN)).isAllowed(zone, event.getEntity(), event.getCreatureType())){
                event.setCancelled(true);
            }
        }
    }

    public void onEntityCombust(EntityCombustEvent event) {
        if(event.isCancelled()) return;
        Entity entity = event.getEntity();
        if(entity == null || !(entity instanceof Player)) return;
        Player player = (Player)entity;
        
        WorldManager wm = plugin.getWorldManager(player);
        ZoneBase zone = wm.getActiveZone(player);
        if(zone != null ) {
            if (!((PlayerDamageResolver)zone.getResolver(AccessResolver.PLAYER_RECEIVE_DAMAGE)).isAllowed(zone, player, DamageCause.FIRE, 0) || (wm.getConfig().PLAYER_ENFORCE_SPECIFIC_DAMAGE && !wm.getConfig().canReceiveSpecificDamage(player, DamageCause.FIRE))) {
                event.setCancelled(true);
                return;
            }
        } else {
            if(!wm.getConfig().canReceiveDamage(player, DamageCause.FIRE)) {
                event.setCancelled(true);
                return;
            }
            if(wm.getConfig().hasGodMode(player)) {
                event.setCancelled(true);
                return;
            }
        }
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
    
    public void onPaintingPlace(PaintingPlaceEvent event) {
        if(event.isCancelled()) return;
        
        Player player = event.getPlayer();
        Block blockPlaced = event.getBlock().getRelative(event.getBlockFace());

        EventUtil.onPlace(plugin, event, player, blockPlaced);
        
    }

    public void onPaintingBreak(PaintingBreakEvent event) {
        if(event.isCancelled()) return;
        if(!(event instanceof PaintingBreakByEntityEvent)) return;
        Entity entity = ((PaintingBreakByEntityEvent)event).getRemover();
        if(entity == null) return;
        if(!(entity instanceof Player)) return;
        
        Block block = event.getPainting().getLocation().getBlock();
        Player player = ((Player)entity);

        EventUtil.onBreak(plugin, event, player, block);
        
    }
    
    /**
     * Called when a human entity's food level changes
     *
     * @param event Relevant event details
     */
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(event.isCancelled()) return;
        Entity entity = event.getEntity();
        if(entity == null || !(entity instanceof Player)) return;
        Player player = (Player)entity;
        WorldManager wm = plugin.getWorldManager(player);
        ZoneBase zone = wm.getActiveZone(player);
        if(zone != null) {
            if(!( ( PlayerFoodResolver ) zone.getResolver(AccessResolver.FOOD)).isAllowed(zone, player) ) {
                event.setCancelled(true);
            }
        } else {
            if(!wm.getConfig().PLAYER_FOOD_ENABLED)
                event.setCancelled(true);
        }
    }

}
