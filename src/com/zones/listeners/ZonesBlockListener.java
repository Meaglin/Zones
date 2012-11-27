package com.zones.listeners;

import org.bukkit.Location;
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
import com.zones.accessresolver.interfaces.BlockFromToResolver;
import com.zones.accessresolver.interfaces.BlockResolver;
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (player.getItemInHand().getTypeId() == ZonesConfig.CREATION_TOOL_TYPE) {
            ZoneSelection selection = plugin.getZoneManager().getSelection(player.getEntityId());
            if (selection != null) {
                selection.onLeftClick(block);
                event.setCancelled(true);
            }
        }

    }

    @EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();

        WorldManager wm = plugin.getWorldManager(blockFrom.getWorld());
        ZoneBase toZone = wm.getActiveZone(blockTo);
        if(toZone == null) {
            if(!wm.getConfig().canFlow(blockFrom, blockTo))
                event.setCancelled(true);
        } else {
            int typeId = blockFrom.getTypeId();
            if (typeId == 8 || typeId == 9 || typeId == 0) {
                if (!((BlockFromToResolver)toZone.getResolver(AccessResolver.WATER_FLOW)).isAllowed(toZone, blockFrom, blockTo) || wm.getConfig().isFlowProtectedBlock(blockFrom, blockTo)) {
                    event.setCancelled(true);
                    return;
                }
            }
    
            if (typeId == 10 || typeId == 11 || typeId == 0) {
                if (!((BlockFromToResolver)toZone.getResolver(AccessResolver.LAVA_FLOW)).isAllowed(toZone, blockFrom, blockTo) || wm.getConfig().isFlowProtectedBlock(blockFrom, blockTo)) {
                    event.setCancelled(true);
                    return;
                }
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
            if(!((PlayerBlockResolver)zone.getResolver(AccessResolver.FIRE)).isAllowed(zone, player, block, -1) || 
                    (wm.getConfig().FIRE_ENFORCE_PROTECTED_BLOCKS && !wm.getConfig().canBurnBlock(block))) {
                ((PlayerBlockResolver)zone.getResolver(AccessResolver.FIRE)).sendDeniedMessage(zone, player);
                return true;
            }
        }
        return false;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if(event.getChangedTypeId() == -1337) {
            WorldManager wm = plugin.getWorldManager(event.getBlock().getWorld());
            ZoneBase zone = wm.getActiveZone(event.getBlock());
            if(zone == null) {
                if(!wm.getConfig().PHYSICS_ENABLED)
                    event.setCancelled(true);
            } else {
                if(!isAllowed(zone,AccessResolver.PHYSICS, event.getBlock()))
                    event.setCancelled(true);
            }
            return;
        }
        int typeId = event.getBlock().getTypeId();
        switch(typeId) {
            case 0:
                
                break;
            case 12:
            case 13:
            case 90:
                WorldManager wm = plugin.getWorldManager(event.getBlock().getWorld());
                ZoneBase zone = wm.getActiveZone(event.getBlock());
                if(zone == null) {
                    if(!wm.getConfig().PHYSICS_ENABLED)
                        event.setCancelled(true);
                } else {
                    if(!isAllowed(zone,AccessResolver.PHYSICS, event.getBlock()))
                        event.setCancelled(true);
                }
                break;
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLeavesDecay(LeavesDecayEvent event) {
        WorldManager wm = plugin.getWorldManager(event.getBlock().getWorld());
        ZoneBase zone = wm.getActiveZone(event.getBlock());
        if(zone == null) {
            if(!wm.getConfig().LEAF_DECAY_ENABLED)
                event.setCancelled(true);
        } else {
            if(!isAllowed(zone,AccessResolver.LEAF_DECAY, event.getBlock()))
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
        if(blockstate.getTypeId() != 78 && blockstate.getTypeId() != 79)
            return;

        Block block = blockstate.getBlock();

        WorldManager wm = plugin.getWorldManager(block.getWorld());
        ZoneBase zone = wm.getActiveZone(block);
        if(zone == null) {
            if(blockstate.getTypeId() == 78 && !wm.getConfig().SNOW_FORM_ENABLED)
                event.setCancelled(true);
            else if(blockstate.getTypeId() == 79 && !wm.getConfig().ICE_FORM_ENABLED)
                event.setCancelled(true);
        } else {
            if(blockstate.getTypeId() == 78 && !isAllowed(zone,AccessResolver.SNOW_FALL, event.getBlock()))
                event.setCancelled(true);
            else if(blockstate.getTypeId() == 79 && !isAllowed(zone,AccessResolver.ICE_FORM, event.getBlock()))
                event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockFade(BlockFadeEvent event) {
        Block block = event.getBlock();
        int typeId = block.getTypeId();
        
        switch(typeId) {
            case 78: case 79: break;
            default: return;
        }
        
        WorldManager wm = plugin.getWorldManager(block.getWorld());
        ZoneBase zone = wm.getActiveZone(block);
        
        if(zone == null) {
            if(typeId == 78 && !wm.getConfig().SNOW_MELT_ENABLED) {
                event.setCancelled(true);
            }
            if(typeId == 79 && !wm.getConfig().ICE_MELT_ENABLED) {
                event.setCancelled(true);
            }
        } else {
            if(typeId == 78 && !isAllowed(zone,AccessResolver.SNOW_MELT, event.getBlock())) {
                event.setCancelled(true);
            }
            if(typeId == 79 && !isAllowed(zone,AccessResolver.ICE_MELT, event.getBlock())) {
                event.setCancelled(true);
            }

        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockSpread(org.bukkit.event.block.BlockSpreadEvent event) {
        switch(event.getNewState().getTypeId()) {
            case 2: case 39: case 40: case 106: break;
            default: return;
        }
        Block block = event.getBlock();

        WorldManager wm = plugin.getWorldManager(block.getWorld());
        ZoneBase zone = wm.getActiveZone(block);
        
        if(zone == null) {
            switch(event.getNewState().getTypeId()) {
                case 2:
                    if(!wm.getConfig().GRASS_GROWTH_ENABLED) event.setCancelled(true);
                    break;
                case 39: case 40:
                    if(!wm.getConfig().MUSHROOM_GROWTH_ENABLED) event.setCancelled(true);
                    break;
                case 106:
                    if(!wm.getConfig().VINES_GROWTH_ENABLED) event.setCancelled(true);
            }
            return;
        } 
        
        switch(event.getNewState().getTypeId()) {
            case 2:
                if(!zone.getFlag(ZoneVar.GRASS_GROWTH)) event.setCancelled(true);
                break;
            case 39: case 40:
                if(!isAllowed(zone,AccessResolver.MUSHROOM_SPREAD, event.getBlock())) event.setCancelled(true);
                break;
            case 106:
                if(!zone.getFlag(ZoneVar.VINES_GROWTH)) event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onTree(StructureGrowEvent event) {
        Location loc = event.getLocation();
        
        WorldManager wm = plugin.getWorldManager(loc.getWorld());
        ZoneBase zone = wm.getActiveZone(loc);
        if(zone == null) {
            if(!wm.getConfig().TREE_GROWTH_ENABLED) event.setCancelled(true);
            return;
        }
        
        if(!zone.getFlag(ZoneVar.TREE_GROWTH)) event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onSignChange(org.bukkit.event.block.SignChangeEvent event) {
        EventUtil.onPlace(plugin, event, event.getPlayer(), event.getBlock());
    }
    
    private static final boolean isAllowed(ZoneBase zone, AccessResolver resolver, Block block) {
        return ((BlockResolver)zone.getResolver(resolver)).isAllowed(zone, block);
    }
    
}
