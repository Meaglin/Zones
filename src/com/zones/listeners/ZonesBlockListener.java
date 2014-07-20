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

import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.settings.ZoneVar;
import com.zones.selection.ZoneSelection;
import com.zones.world.WorldManager;

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
        Material mat = blockFrom.getType();
        switch(mat) {
            case WATER:
            case STATIONARY_WATER:
                if(!wm.testFlag(blockTo, ZoneVar.WATER) || wm.isProtected(blockTo, ZoneVar.WATER_PROTECTED_BLOCKS, blockTo.getType())) {
                    event.setCancelled(true);
                    return;
                }
                break;
            case LAVA:
            case STATIONARY_LAVA:
                if(!wm.testFlag(blockTo, ZoneVar.LAVA) || wm.isProtected(blockTo, ZoneVar.LAVA_PROTECTED_BLOCKS, blockTo.getType())) {
                    event.setCancelled(true);
                    return;
                }
                break;
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
        if(onFire(null, event.getBlock(), IgniteCause.SPREAD)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockIgnite(BlockIgniteEvent event) { 
        if(onFire(event.getPlayer(),event.getBlock(),event.getCause())) {
            event.setCancelled(true);
        }
    }
    
    public boolean onFire(Player player, Block block, IgniteCause cause) {
        WorldManager wm = plugin.getWorldManager(block.getWorld());
        if(player == null) {
            if(!wm.testFlag(block, ZoneVar.FIRE) || wm.isProtected(block, ZoneVar.FIRE_PROTECTED_BLOCKS, block.getType())) {
                return true;
            }
        } else {
            if(!wm.testFlag(block, ZoneVar.LIGHTER) || wm.isProtected(block, ZoneVar.FIRE_PROTECTED_BLOCKS, block.getType())) {
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
        if(!wm.testFlag(event.getBlock(), ZoneVar.PHYSICS)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLeavesDecay(LeavesDecayEvent event) {
        WorldManager wm = plugin.getWorldManager(event.getBlock().getWorld());
        
        if(!wm.testFlag(event.getBlock(), ZoneVar.LEAF_DECAY)) {
            event.setCancelled(true);
        }
    }



    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        EventUtil.onBreak(plugin, event, player, block, block.getType());
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
                if(!wm.testFlag(event.getBlock(), ZoneVar.SNOW_FALL)) {
                    event.setCancelled(true);
                }
                break;
            case ICE:
                if(!wm.testFlag(event.getBlock(), ZoneVar.ICE_FORM)) {
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
                if(!wm.testFlag(event.getBlock(), ZoneVar.SNOW_MELT)) {
                    event.setCancelled(true);
                }
                break;
            case ICE:
                if(!wm.testFlag(event.getBlock(), ZoneVar.ICE_MELT)) {
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
                if(!wm.testFlag(event.getBlock(), ZoneVar.GRASS_GROWTH)) {
                    event.setCancelled(true);
                }
                break;
            case BROWN_MUSHROOM: 
            case RED_MUSHROOM:
                if(!wm.testFlag(event.getBlock(), ZoneVar.MUSHROOM_SPREAD)) {
                    event.setCancelled(true);
                }
                break;
            case VINE:
                if(!wm.testFlag(event.getBlock(), ZoneVar.VINES_GROWTH)) {
                    event.setCancelled(true);
                }
                break;
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onTree(StructureGrowEvent event) {
        Location loc = event.getLocation();
        
        WorldManager wm = plugin.getWorldManager(loc.getWorld());
        if(!wm.testFlag(loc.getBlock(), ZoneVar.TREE_GROWTH)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onSignChange(org.bukkit.event.block.SignChangeEvent event) {
        EventUtil.onPlace(plugin, event, event.getPlayer(), event.getBlock());
    }
}
