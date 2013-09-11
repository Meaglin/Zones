package com.zones.listeners;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.accessresolver.AccessResolver;
import com.zones.accessresolver.interfaces.BlockResolver;
import com.zones.accessresolver.interfaces.EntitySpawnResolver;
import com.zones.accessresolver.interfaces.PlayerAttackEntityResolver;
import com.zones.accessresolver.interfaces.PlayerDamageResolver;
import com.zones.accessresolver.interfaces.PlayerFoodResolver;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;


/**
 * 
 * @author Meaglin
 *
 */
public class ZonesEntityListener implements Listener {

    private Zones plugin;

    public ZonesEntityListener(Zones zones) {
        this.plugin = zones;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        
        Entity defender = event.getEntity();
        WorldManager wm = plugin.getWorldManager(defender.getWorld());
        
        if(event.getCause() == DamageCause.BLOCK_EXPLOSION  || event.getCause() == DamageCause.ENTITY_EXPLOSION) {
            if(!wm.getConfig().EXPLOSION_DAMAGE_ENTITIES) {
                event.setCancelled(true);
                return;
            }
        }
        
        Entity attacker = null;
        if(event instanceof EntityDamageByEntityEvent) {
            attacker = ((EntityDamageByEntityEvent)event).getDamager();
            if(attacker != null && attacker instanceof Projectile) {
                Entity ent = ((Projectile)attacker).getShooter();
                if(ent != null) attacker = ent;
            }
        }

        if(!(defender instanceof Player) && (attacker == null || !(attacker instanceof Player))) return;
        
        ZoneBase zone = wm.getActiveZone(defender.getLocation());
        
        if (zone == null) {
            if(!(defender instanceof Player)) return;
            Player player = (Player) defender;
            if(!wm.getConfig().canReceiveDamage(player, event.getCause())) {
                event.setCancelled(true);
                return;
            }
            if(wm.getConfig().hasGodMode(player)) {
                event.setCancelled(true);
                return;
            }
        } else {            
            if (defender instanceof Player && (!((PlayerDamageResolver)zone.getResolver(AccessResolver.PLAYER_RECEIVE_DAMAGE)).isAllowed(zone, ((Player)defender), event.getCause(), (int) event.getDamage()) 
                || (
                        wm.getConfig().PLAYER_ENFORCE_SPECIFIC_DAMAGE 
                        && !wm.getConfig().canReceiveSpecificDamage(((Player)defender), event.getCause()))
                )) {
                event.setCancelled(true);
                return;
            }
            if(attacker != null && attacker instanceof Player) {
                Player att = (Player)attacker;
                if(!((PlayerAttackEntityResolver)zone.getResolver(AccessResolver.PLAYER_ENTITY_ATTACK)).isAllowed(zone, att, defender, (int) event.getDamage())) {
                    zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_ATTACK_ENTITYS_IN_ZONE, att);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        
        WorldManager wm = plugin.getWorldManager(event.getLocation());
        ZoneBase zone = wm.getActiveZone(event.getLocation());
        if (zone == null) {
            if(event.getEntity() instanceof Creeper) {
                if(!wm.getConfig().ALLOW_CREEPER_TRIGGER) {
                    event.setCancelled(true);
                    return;
                }
            } else {
                if(!wm.getConfig().DYNAMITE_ENABLED) {
                    event.setCancelled(true);
                    return;
                }
            }
        } else {
            if (!((BlockResolver)zone.getResolver(AccessResolver.DYNAMITE)).isAllowed(zone, event.getLocation().getBlock())) {
                event.setCancelled(true);
                return;
            }
        }
        if(wm.getConfig().EXPLOSION_PROTECTED_BLOCKS_ENABLED && wm.getConfig().EXPLOSION_PROTECTED_BLOCKS.size() != 0) {
            Iterator<Block> it = event.blockList().iterator();
            while(it.hasNext()) {
                Block b = it.next();
                if(wm.getConfig().EXPLOSION_PROTECTED_BLOCKS.contains(b.getTypeId())) {
                    it.remove();
                }
            }
         }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        
        WorldManager wm = plugin.getWorldManager(event.getLocation());
        ZoneBase zone = wm.getActiveZone(event.getLocation());
        if (zone == null) {
            if(!wm.getConfig().canSpawn(event.getEntity(), event.getEntityType())){
                event.setCancelled(true);
            }
        } else {
            Entity entity = event.getEntity();
            /*
            if(entity instanceof Animals) {
                if (wm.getConfig().ALLOWED_ANIMALS_ENABLED && !wm.getConfig().ALLOWED_ANIMALS.contains(event.getCreatureType())) {
                    event.setCancelled(true);
                    return;
                }
            } else if(entity instanceof Monster || entity instanceof Flying || entity instanceof Slime) {
                if (wm.getConfig().ALLOWED_MOBS_ENABLED && !wm.getConfig().ALLOWED_MOBS.contains(event.getCreatureType())) {
                    event.setCancelled(true);
                    return;
                }
            }
            */
                
            if (!((EntitySpawnResolver)zone.getResolver(AccessResolver.ENTITY_SPAWN)).isAllowed(zone, entity, event.getEntityType())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityCombust(EntityCombustEvent event) {
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

    @EventHandler(ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        WorldManager wm = plugin.getWorldManager(event.getEntity().getWorld());
        Location loc = event.getEntity().getLocation();
        ZoneBase zone = wm.getActiveZone(loc);

        if(zone!=null) {
            if (!((BlockResolver)zone.getResolver(AccessResolver.DYNAMITE)).isAllowed(zone, loc.getBlock())) {
                event.setCancelled(true);
                return;
            }
        }
        if(event.getEntity() instanceof Creeper) {
            event.setRadius(wm.getConfig().CREEPER_EXPLOSION_RANGE);
        } else if (event.getEntity() instanceof TNTPrimed ) {
            event.setRadius(wm.getConfig().DYNAMITE_RANGE);            
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPaintingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        Block blockPlaced = event.getBlock().getRelative(event.getBlockFace());

        EventUtil.onPlace(plugin, event, player, blockPlaced);
        
    }

    @EventHandler(ignoreCancelled = true)
    public void onPaintingBreak(HangingBreakByEntityEvent event) {
        if(!(event instanceof HangingBreakByEntityEvent)) return;
        Entity entity = event.getRemover();
        if(entity == null) return;
        if(!(entity instanceof Player)) return;
        
        Block block = event.getEntity().getLocation().getBlock();
        Player player = ((Player)entity);

        EventUtil.onBreak(plugin, event, player, block);
        
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();
        if(entity == null || !(entity instanceof Player)) return;
        Player player = (Player)entity;
        WorldManager wm = plugin.getWorldManager(player);
        ZoneBase zone = wm.getActiveZone(player);
        if(zone != null) {
            if(!( ( PlayerFoodResolver ) zone.getResolver(AccessResolver.FOOD)).isAllowed(zone, player) ) {
                if(player.getFoodLevel() < 20) player.setFoodLevel(20);
                if(player.getSaturation() < 3.0F) player.setSaturation(5.0F);
                if(player.getExhaustion() > 3.0F) player.setExhaustion(0.0F);
                event.setCancelled(true);
            }
        } else {
            if(!wm.getConfig().PLAYER_FOOD_ENABLED) {
                if(player.getFoodLevel() < 20) player.setFoodLevel(20);
                if(player.getSaturation() < 3.0F) player.setSaturation(5.0F);
                if(player.getExhaustion() > 3.0F) player.setExhaustion(0.0F);
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEndermanPlace(EntityChangeBlockEvent event) {
        if(!(event.getEntity() instanceof Enderman)) return;
        WorldManager wm = plugin.getWorldManager(event.getEntity().getWorld());
        ZoneBase zone = wm.getActiveZone(event.getBlock());
        if(zone == null) {
            if(!wm.getConfig().ENDER_GRIEFING_ENABLED)
                event.setCancelled(true);
        } else {
            if(!zone.getFlag(ZoneVar.ENDER_GRIEFING))
                event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityInteract(EntityInteractEvent event) {
        if(event.getBlock() == null) return;
        if(event.getBlock().getTypeId() != 60) return;
        WorldManager wm = plugin.getWorldManager(event.getEntity().getWorld());
        ZoneBase zone = wm.getActiveZone(event.getBlock());
        if(zone == null) {
            if(wm.getConfig().CROP_PROTECTION_ENABLED)
                event.setCancelled(true);
        } else {
            if(zone.getFlag(ZoneVar.CROP_PROTECTION))
                event.setCancelled(true);
        }
    }
}
