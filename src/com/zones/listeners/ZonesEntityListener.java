package com.zones.listeners;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Flying;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
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
import org.bukkit.projectiles.ProjectileSource;

import com.meaglin.json.JSONObject;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;
import com.zones.model.ZonesAccess.Rights;
import com.zones.model.settings.ZoneVar;
import com.zones.model.types.ZoneNormal;
import com.zones.world.WorldManager;


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

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        
        Entity defender = event.getEntity();
        WorldManager wm = plugin.getWorldManager(defender.getWorld());
        
        if(event.getCause() == DamageCause.BLOCK_EXPLOSION  || event.getCause() == DamageCause.ENTITY_EXPLOSION) {
            if(!wm.testFlag(defender.getLocation(), ZoneVar.EXPLOSION_PROTECT_ENTITIES)) {
                event.setCancelled(true);
                return;
            }
        }
        
        Entity attacker = null;
        if(event instanceof EntityDamageByEntityEvent) {
            attacker = ((EntityDamageByEntityEvent)event).getDamager();
            if(attacker != null && attacker instanceof Projectile) {
                ProjectileSource source = ((Projectile)attacker).getShooter();
                if(source != null && source instanceof LivingEntity) {
                    attacker = (Entity) source;
                }
            }
        }

        if(attacker != null && attacker instanceof Player) {
            ZoneNormal zone = wm.getActiveZone(defender.getLocation());
            if(zone != null && !zone.canModify(((Player) attacker), Rights.ATTACK)) {
                zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_ATTACK_ENTITYS_IN_ZONE, ((Player) attacker));
                event.setCancelled(true);
                return;
            }
        }
        if(!(defender instanceof Player)) {
            return;
        }
        
        ZoneVar sub = null;
        switch(event.getCause()) {
            case CONTACT:
                sub = ZoneVar.PLAYER_CONTACT_DAMAGE;
                break;
            case ENTITY_ATTACK:
                sub = ZoneVar.PLAYER_ENTITY_DAMAGE;
                break;
            case SUFFOCATION:
                sub = ZoneVar.PLAYER_SUFFOCATION_DAMAGE;
                break;
            case FALL:
                sub = ZoneVar.PLAYER_FALL_DAMAGE;
                break;
            case FIRE:
                sub = ZoneVar.PLAYER_FIRE_DAMAGE;
                break;
            case FIRE_TICK:
                sub = ZoneVar.PLAYER_BURN_DAMAGE;
                break;
            case LAVA:
                sub = ZoneVar.PLAYER_LAVA_DAMAGE;
                break;
            case DROWNING:
                sub = ZoneVar.PLAYER_DROWNING_DAMAGE;
                break;
            case BLOCK_EXPLOSION:
                sub = ZoneVar.PLAYER_TNT_DAMAGE;
                break;
            case ENTITY_EXPLOSION:
                sub = ZoneVar.PLAYER_CREEPER_DAMAGE;
                break;
            case VOID:
                sub = ZoneVar.PLAYER_VOID_DAMAGE;
                break;
        }
        if((sub == null && !wm.testFlag(defender.getLocation(), ZoneVar.HEALTH)) ||
                !wm.canReceiveDamage((Player) defender, sub)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        
        WorldManager wm = plugin.getWorldManager(event.getLocation());
        if(event.getEntity() instanceof Creeper) {
            if(!wm.testFlag(event.getLocation(), ZoneVar.CREEPER_EXPLOSION)) {
                event.setCancelled(true);
            }
        } else {
            if(!wm.testFlag(event.getLocation(), ZoneVar.DYNAMITE)) {
                event.setCancelled(true);
            }
        }
        JSONObject blocks = wm.getConfig().getSetting(ZoneVar.EXPLOSION_PROTECTED_BLOCKS);
        ZoneBase zone = wm.getActiveZone(event.getLocation());
        if(blocks.getBoolean("enabled") && (blocks.getBoolean("enforce") || zone == null || !zone.getFlag(ZoneVar.EXPLOSION_PROTECTED_BLOCKS))) {
            if(blocks.getJSONArray("value").size() == 0) {
                return;
            }
            Iterator<Block> it = event.blockList().iterator();
            while(it.hasNext()) {
                Block b = it.next();
                if(blocks.getJSONArray("value").contains(b.getType().name())) {
                    it.remove();
                }
            }
        } else if(zone != null && !zone.getFlag(ZoneVar.EXPLOSION_PROTECTED_BLOCKS)) {
            if(zone.getSettings().getJSONArray(ZoneVar.EXPLOSION_PROTECTED_BLOCKS.getName()).size() == 0) {
                return;
            }
            Iterator<Block> it = event.blockList().iterator();
            while(it.hasNext()) {
                Block b = it.next();
                if(zone.getSettings().getJSONArray(ZoneVar.EXPLOSION_PROTECTED_BLOCKS.getName()).contains(b.getType().name())) {
                    it.remove();
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        
        WorldManager wm = plugin.getWorldManager(event.getLocation());
        Entity entity = event.getEntity();
        Location loc = entity.getLocation();
        if(entity instanceof Animals || entity instanceof Ambient) {
            if(!wm.canSpawn(loc, ZoneVar.ANIMALS, ZoneVar.ALLOWED_ANIMALS, entity.getType())) {
                event.setCancelled(true);
            }
        } else if(entity instanceof Monster || entity instanceof Flying || entity instanceof Slime) {
            if(!wm.canSpawn(loc, ZoneVar.MOBS, ZoneVar.ALLOWED_MOBS, entity.getType())) {
                event.setCancelled(true);
            }
        } 
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityCombust(EntityCombustEvent event) {
        Entity entity = event.getEntity();
        if(entity == null || !(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        
        WorldManager wm = plugin.getWorldManager(player);
        if(!wm.canReceiveDamage(player, ZoneVar.PLAYER_FIRE_DAMAGE)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        WorldManager wm = plugin.getWorldManager(event.getEntity().getWorld());
        Location loc = event.getEntity().getLocation();
        if(event.getEntity() instanceof Creeper) {
            if(!wm.testFlag(loc, ZoneVar.CREEPER_EXPLOSION)) {
                event.setCancelled(true);
            }
        } else if (event.getEntity() instanceof TNTPrimed ) {
            if(!wm.testFlag(loc, ZoneVar.DYNAMITE)) {
                event.setCancelled(true);
            }        
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        Block blockPlaced = event.getBlock().getRelative(event.getBlockFace());

        EventUtil.onPlace(plugin, event, player, blockPlaced);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if(!(event instanceof HangingBreakByEntityEvent)) {
            return;
        }
        Entity entity = event.getRemover();
        if(entity == null) return;
        if(!(entity instanceof Player)) {
            return;
        }
        
        Block block = event.getEntity().getLocation().getBlock();
        Player player = ((Player)entity);

        EventUtil.onBreak(plugin, event, player, block, Material.PAINTING);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();
        if(entity == null || !(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        WorldManager wm = plugin.getWorldManager(player);
        if(!wm.testFlag(player.getLocation(), ZoneVar.FOOD)) {
            if(player.getFoodLevel() < 20) player.setFoodLevel(20);
            if(player.getSaturation() < 3.0F) player.setSaturation(5.0F);
            if(player.getExhaustion() > 3.0F) player.setExhaustion(0.0F);
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEndermanPlace(EntityChangeBlockEvent event) {
        if(!(event.getEntity() instanceof Enderman)) {
            return;
        }
        WorldManager wm = plugin.getWorldManager(event.getEntity().getWorld());
        if(!wm.testFlag(event.getBlock(), ZoneVar.ENDER_GRIEFING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityInteract(EntityInteractEvent event) {
        if(event.getBlock() == null) {
            return;
        }
        if(event.getBlock().getType() != Material.SOIL) {
            return;
        }
        WorldManager wm = plugin.getWorldManager(event.getEntity().getWorld());
        if(!wm.testFlag(event.getBlock(), ZoneVar.CROP_PROTECTION)) {
            event.setCancelled(true);
        }
    }
}
