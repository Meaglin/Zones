package com.zones.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.ZonesConfig;
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
                if (!toZone.allowWater(blockFrom,blockTo) || wm.getConfig().isFlowProtectedBlock(blockFrom, blockTo))
                    event.setCancelled(true);
            }
    
            if (blockFrom.getTypeId() == 10 || blockFrom.getTypeId() == 11) {
                if (!toZone.allowLava(blockFrom,blockTo) || wm.getConfig().isFlowProtectedBlock(blockFrom, blockTo))
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

        WorldManager wm = plugin.getWorldManager(player);
        if(!wm.getConfig().isProtectedBreakBlock(player, blockPlaced)) {
            ZoneBase zone = wm.getActiveZone(blockPlaced);
            if(zone == null){
                if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().canUse(player,"zones.build")){
                    player.sendMessage(ChatColor.RED + "You cannot build in this world!");
                    event.setBuild(false);
                } else {
                    wm.getConfig().logBlockPlace(player, blockPlaced);
                }
            } else  {
                if (!zone.allowBlockCreate(player, blockPlaced)) {
                    // These messages are now handled in the ZoneClass.
                    //player.sendMessage(ChatColor.RED + "You cannot place blocks in '" + zone.getName() + "' .");
                    event.setBuild(false);
                } else if ((blockPlaced.getTypeId() == 54 || blockPlaced.getTypeId() == 61 || blockPlaced.getTypeId() == 62) && !zone.allowBlockModify(player, blockPlaced)) {
                    player.sendMessage(ChatColor.RED + "You cannot place chests/furnaces in '" + zone.getName() + "' since you don't have modify rights !");
                    event.setBuild(false);
                } else {
                    wm.getConfig().logBlockPlace(player, blockPlaced);
                    event.setBuild(true);
                }
            }
        }
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
            if(!zone.allowFire(player, block) || (wm.getConfig().FIRE_ENFORCE_PROTECTED_BLOCKS && !wm.getConfig().canBurnBlock(block))) {
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
        switch(event.getBlock().getTypeId()) {
            case 12:
            case 13:
            case 90:
                WorldManager wm = plugin.getWorldManager(event.getBlock().getWorld());
                ZoneBase zone = wm.getActiveZone(event.getBlock());
                if(zone == null) {
                    if(!wm.getConfig().PHYSICS_ENABLED)
                        event.setCancelled(true);
                } else {
                    if(!zone.allowPhysics(event.getBlock()))
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
            if(!zone.allowLeafDecay(event.getBlock()))
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

        WorldManager wm = plugin.getWorldManager(block.getWorld());
        if(!wm.getConfig().isProtectedBreakBlock(player, block)) {
            ZoneBase zone = wm.getActiveZone(block);
            if(zone == null) {
                if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().canUse(player, "zones.build")){
                    player.sendMessage(ChatColor.RED + "You cannot destroy blocks in this world!");
                    event.setCancelled(true);
                } else {
                    wm.getConfig().logBlockPlace(player, block);
                }
            } else {
                if (!zone.allowBlockDestroy(player, block)) {
                    // These messages are now handled in the ZoneClass.
                    //player.sendMessage(ChatColor.RED + "You cannot destroy blocks in '" + zone.getName() + "' !");
                    event.setCancelled(true);
                } else if ((block.getTypeId() == 54 || block.getTypeId() == 61 || block.getTypeId() == 62) && !zone.allowBlockModify(player, block)) {
                    player.sendMessage(ChatColor.RED + "You cannot destroy a chest/furnace in '" + zone.getName() + "' since you dont have modify rights!");
                    event.setCancelled(true);
                } else {
                    wm.getConfig().logBlockBreak(player, block);
                }
            }
        }
    }


    /**
     * Will be replaced by onBlockForm in the next RB
     * @param event
     */
    @Override
    public void onSnowForm(SnowFormEvent event) {
        if(event.isCancelled()) return;
        
        Block block = event.getBlock();

        WorldManager wm = plugin.getWorldManager(block.getWorld());
        ZoneBase zone = wm.getActiveZone(block);
        if(zone == null) {
            if(!wm.getConfig().SNOW_FALL_ENABLED)
                event.setCancelled(true);
        } else {
            if(!zone.allowSnowFall(block))
                event.setCancelled(true);
        }
    }
    
    public void onBlockForm(org.bukkit.event.block.BlockFormEvent event) {
        if(event.isCancelled()) return;
        
        BlockState blockstate = event.getNewState();
        Block block = blockstate.getBlock();

        WorldManager wm = plugin.getWorldManager(block.getWorld());
        ZoneBase zone = wm.getActiveZone(block);
        if(zone == null) {
            if(blockstate.getTypeId() == 78 && !wm.getConfig().SNOW_FALL_ENABLED)
                event.setCancelled(true);
            if(blockstate.getTypeId() == 79 && !wm.getConfig().ICE_FORM_ENABLED)
                event.setCancelled(true);
        } else {
            if(blockstate.getTypeId() == 78 && !zone.allowIceForm(block))
                event.setCancelled(true);
            if(blockstate.getTypeId() == 79 && !zone.allowSnowFall(block))
                event.setCancelled(true);
        }
    }

    public void onBlockFade(BlockFadeEvent event) {
        if(event.isCancelled()) return;

        Block block = event.getBlock();

        WorldManager wm = plugin.getWorldManager(block.getWorld());
        ZoneBase zone = wm.getActiveZone(block);
        if(zone == null) {
            if(block.getTypeId() == 78 && !wm.getConfig().SNOW_MELT_ENABLED) {
                event.setCancelled(true);
            }
            if(block.getTypeId() == 79 && !wm.getConfig().ICE_MELT_ENABLED) {
                event.setCancelled(true);
            }
        } else {
            if(block.getTypeId() == 78 && !zone.allowSnowMelt(block)) {
                event.setCancelled(true);
            }
            if(block.getTypeId() == 79 && !zone.allowIceMelt(block)) {
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
            if(!zone.allowMushroomSpread(block))
                event.setCancelled(true);
        }
    }
    
}
