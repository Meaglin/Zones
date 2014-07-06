package com.zones.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.world.StructureGrowEvent;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.accessresolver.AccessResolver;
import com.zones.accessresolver.interfaces.PlayerBlockResolver;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;
import com.zones.selection.ZoneSelection;

/**
 * 
 * @author Meaglin
 *
 */
public class ZonesBlockListener implements Listener {

    private Zones plugin;

    public ZonesBlockListener(Zones plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        // TODO: magic id number
        if (player.getItemInHand().getTypeId() == ZonesConfig.CREATION_TOOL_TYPE) {
            ZoneSelection selection = plugin.getZoneManager().getSelection(player.getEntityId());
            if (selection != null) {
                selection.onLeftClick(block);
                event.setCancelled(true);
            }
        }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();

        WorldManager wm = plugin.getWorldManager(blockFrom.getWorld());
        ZoneBase toZone = wm.getActiveZone(blockTo);
        if(toZone == null) {
            if(!wm.getConfig().canFlow(blockFrom, blockTo))
                event.setCancelled(true);
        } else {
            Material mat = blockFrom.getType();
            switch(mat) {
                case WATER:
                case STATIONARY_WATER:
                    if (!toZone.getFlag(ZoneVar.WATER) || wm.getConfig().isFlowProtectedBlock(blockFrom, blockTo)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                case LAVA:
                case STATIONARY_LAVA:
                    if (!toZone.getFlag(ZoneVar.LAVA) || wm.getConfig().isFlowProtectedBlock(blockFrom, blockTo)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                    
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block blockPlaced = event.getBlockPlaced();

        EventUtil.onPlace(plugin, event, player, blockPlaced);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockBurn(BlockBurnEvent event) {
        if(onFire(null, event.getBlock(), IgniteCause.SPREAD))
            event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockIgnite(BlockIgniteEvent event) { 
        if(onFire(event.getPlayer(),event.getBlock(),event.getCause()))
            event.setCancelled(true);
    }
    
    public boolean onFire(Player player, Block block, IgniteCause cause) {
        WorldManager wm = plugin.getWorldManager(block.getWorld());
        ZoneBase zone = wm.getActiveZone(block);
        if(zone == null) {
            if(!wm.getConfig().canBurn(player, block, cause))
                return true;
        } else {
            if(!(wm.getConfig().FIRE_ENFORCE_PROTECTED_BLOCKS && !wm.getConfig().canBurnBlock(block) || 
                    zone.getFlag(ZoneVar.FIRE))) {
                ((PlayerBlockResolver)zone.getResolver(AccessResolver.FIRE)).sendDeniedMessage(zone, player);
                return true;
            }
        }
        return false;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Material mat = event.getBlock().getType();
        switch(mat) {
            case GRAVEL: case SAND: case PORTAL:
                break;
            default:
                return;
        }
        
        WorldManager wm = plugin.getWorldManager(event.getBlock().getWorld());
        if(!wm.testFlag(event.getBlock(), wm.getConfig().PHYSICS_ENABLED, ZoneVar.PHYSICS)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLeavesDecay(LeavesDecayEvent event) {
        WorldManager wm = plugin.getWorldManager(event.getBlock().getWorld());
        
        if(!wm.testFlag(event.getBlock(), wm.getConfig().LEAF_DECAY_ENABLED, ZoneVar.LEAF_DECAY)) {
            event.setCancelled(true);
        }
    }



    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        EventUtil.onBreak(plugin, event, player, block);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockForm(org.bukkit.event.block.BlockFormEvent event) {
        BlockState blockstate = event.getNewState();
        Block block = blockstate.getBlock();
        
        Material mat = blockstate.getType();
        
        switch(mat) {
            case SNOW: case ICE: break;
            default: return;
        }
        
        WorldManager wm = plugin.getWorldManager(block.getWorld());
        
        switch(mat) {
            case SNOW:
                if(!wm.testFlag(block, wm.getConfig().SNOW_FORM_ENABLED, ZoneVar.SNOW_FALL)) {
                    event.setCancelled(true);
                }
                break;
            case ICE:
                if(!wm.testFlag(block, wm.getConfig().ICE_FORM_ENABLED, ZoneVar.ICE_FORM)) {
                    event.setCancelled(true);
                }
                break;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockFade(BlockFadeEvent event) {
        Block block = event.getBlock();
        Material mat = block.getType();
        
        switch(mat) {
            case SNOW: case ICE: break;
            default: return;
        }
        
        WorldManager wm = plugin.getWorldManager(block.getWorld());
        
        switch(mat) {
            case SNOW:
                if(!wm.testFlag(block, wm.getConfig().SNOW_MELT_ENABLED, ZoneVar.SNOW_MELT)) {
                    event.setCancelled(true);
                }
                break;
            case ICE:
                if(!wm.testFlag(block, wm.getConfig().ICE_MELT_ENABLED, ZoneVar.ICE_MELT)) {
                    event.setCancelled(true);
                }
                break;
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockSpread(org.bukkit.event.block.BlockSpreadEvent event) {
        switch(event.getNewState().getType()) {
            case GRASS:
            case BROWN_MUSHROOM: 
            case RED_MUSHROOM:
            case VINE: 
                break;
            default: return;
        }
        Block block = event.getBlock();

        WorldManager wm = plugin.getWorldManager(block.getWorld());
        switch(event.getNewState().getType()) {
            case GRASS:
                if(!wm.testFlag(block, wm.getConfig().GRASS_GROWTH_ENABLED, ZoneVar.GRASS_GROWTH)) {
                    event.setCancelled(true);
                }
                break;
            case BROWN_MUSHROOM: 
            case RED_MUSHROOM:
                if(!wm.testFlag(block, wm.getConfig().MUSHROOM_GROWTH_ENABLED, ZoneVar.MUSHROOM_SPREAD)) {
                    event.setCancelled(true);
                }
                break;
            case VINE:
                if(!wm.testFlag(block, wm.getConfig().VINES_GROWTH_ENABLED, ZoneVar.VINES_GROWTH)) {
                    event.setCancelled(true);
                }
                break;
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onTree(StructureGrowEvent event) {
        Location loc = event.getLocation();
        
        WorldManager wm = plugin.getWorldManager(loc.getWorld());
        if(!wm.testFlag(loc, wm.getConfig().TREE_GROWTH_ENABLED, ZoneVar.TREE_GROWTH)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onSignChange(org.bukkit.event.block.SignChangeEvent event) {
        EventUtil.onPlace(plugin, event, event.getPlayer(), event.getBlock());
    }
}
