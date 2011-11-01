package com.zones.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.accessresolver.AccessResolver;
import com.zones.accessresolver.interfaces.BlockFromToResolver;
import com.zones.accessresolver.interfaces.BlockResolver;
import com.zones.accessresolver.interfaces.PlayerBlockResolver;
import com.zones.model.ZoneBase;
import com.zones.selection.ZoneSelection;

/**
 * 
 * @author Meaglin
 *
 */
public class ZonesBlockListener extends BlockListener {

    private Zones plugin;

    public ZonesBlockListener(Zones plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when a block is damaged (or broken)
     * 
     * @param event
     *            Relevant event details
     */
    @Override
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

    /**
     * Called when a block flows (water/lava)
     * 
     * @param event
     *            Relevant event details
     */
    @Override
    public void onBlockFromTo(BlockFromToEvent event) {
        if(event.isCancelled()) return;
        
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();


        WorldManager wm = plugin.getWorldManager(blockFrom.getWorld());
        ZoneBase toZone = wm.getActiveZone(blockTo);
        if(toZone == null) {
            if(!wm.getConfig().canFlow(blockFrom, blockTo))
                event.setCancelled(true);
        } else {
            if (blockFrom.getTypeId() == 8 || blockFrom.getTypeId() == 9) {
                if (!((BlockFromToResolver)toZone.getResolver(AccessResolver.WATER_FLOW)).isAllowed(toZone, blockFrom, blockTo) || wm.getConfig().isFlowProtectedBlock(blockFrom, blockTo))
                    event.setCancelled(true);
            }
    
            if (blockFrom.getTypeId() == 10 || blockFrom.getTypeId() == 11) {
                if (!((BlockFromToResolver)toZone.getResolver(AccessResolver.LAVA_FLOW)).isAllowed(toZone, blockFrom, blockTo) || wm.getConfig().isFlowProtectedBlock(blockFrom, blockTo))
                    event.setCancelled(true);
            }
        }
    }

    /**
     * Called when a player places a block
     * 
     * @param event
     *            Relevant event details
     */
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.isCancelled()) return;
        
        Player player = event.getPlayer();
        Block blockPlaced = event.getBlockPlaced();

        EventUtil.onPlace(plugin, event, player, blockPlaced);
    }

    /**
     * Called when a block is destroyed from burning
     * 
     * @param event
     *            Relevant event details
     */
    public void onBlockBurn(BlockBurnEvent event) {
        if(event.isCancelled())return;
        
        if(onFire(null, event.getBlock(), IgniteCause.SPREAD))
            event.setCancelled(true);
    }
    
    /**
     * Called when a block gets ignited
     * 
     * @param event
     *            Relevant event details
     */
    public void onBlockIgnite(BlockIgniteEvent event) { 
        if(event.isCancelled())return;
        
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

    /**
     * Called when we try to place a block, to see if we can build it
     */
    public void onBlockCanBuild(BlockCanBuildEvent event) {

    }
    
    /**
     * Called when block physics occurs
     * 
     * @param event
     *            Relevant event details
     */
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if(event.isCancelled()) return;
        int typeId = event.getBlock().getTypeId();
        switch(typeId) {
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
    
    /**
     * Called when redstone changes From: the source of the redstone change To:
     * The redstone dust that changed
     * 
     * @param event
     *            Relevant event details
     */
    public void onBlockRedstoneChange(BlockFromToEvent event) {
    }

    /**
     * Called when leaves are decaying naturally
     * 
     * @param event
     *            Relevant event details
     */
    public void onLeavesDecay(LeavesDecayEvent event) {
        if(event.isCancelled()) return;
        
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



    /**
     * Called when a block is destroyed by a player.
     * 
     * @param event
     *            Relevant event details
     */
    
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled()) return;
        
        Block block = event.getBlock();
        Player player = event.getPlayer();

        EventUtil.onBreak(plugin, event, player, block);
    }
    
    public void onBlockForm(org.bukkit.event.block.BlockFormEvent event) {
        if(event.isCancelled()) return;
        BlockState blockstate = event.getNewState();
        if(blockstate.getTypeId() != 78 && blockstate.getTypeId() != 79)
            return;

        Block block = blockstate.getBlock();

        WorldManager wm = plugin.getWorldManager(block.getWorld());
        ZoneBase zone = wm.getActiveZone(block);
        if(zone == null) {
            if(blockstate.getTypeId() == 78 && !wm.getConfig().SNOW_FALL_ENABLED)
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

    public void onBlockFade(BlockFadeEvent event) {
        if(event.isCancelled()) return;

        
        Block block = event.getBlock();
        int typeId = block.getTypeId();
        if(typeId != 78 && typeId != 79)
            return;
        
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
    
    public void onBlockSpread(org.bukkit.event.block.BlockSpreadEvent event) {
        if(event.isCancelled()) return;
        
        Block block = event.getBlock();

        WorldManager wm = plugin.getWorldManager(block.getWorld());
        ZoneBase zone = wm.getActiveZone(block);
        if(zone == null) {
            if(!wm.getConfig().MUSHROOM_SPREAD_ENABLED)
                event.setCancelled(true);
        } else {
            if(!isAllowed(zone,AccessResolver.MUSHROOM_SPREAD, event.getBlock()))
                event.setCancelled(true);
        }
    }
    
    public void onSignChange(org.bukkit.event.block.SignChangeEvent event) {
        if(event.isCancelled()) return;

        EventUtil.onPlace(plugin, event, event.getPlayer(), event.getBlock());
    }
    
    private static final boolean isAllowed(ZoneBase zone, AccessResolver resolver, Block block) {
        return ((BlockResolver)zone.getResolver(resolver)).isAllowed(zone, block);
    }
    
}
