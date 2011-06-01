package com.zones.listeners;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SnowFormEvent;

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
     * Called when a player right clicks a block
     * 
     * @param event
     *            Relevant event details
     *
    @Override
    public void onBlockRightClick(BlockRightClickEvent event) {
        int itemInHand = event.getItemInHand().getTypeId();
        Block blockClicked = event.getBlock();
        Player player = event.getPlayer();
        if (itemInHand == Zones.toolType) {
            ZonesDummyZone dummy = ZoneManager.getInstance().getDummy(player.getName());
            if (dummy != null) {
                if (dummy.getType() == 1 && dummy.getCoords().size() == 2) {
                    player.sendMessage(ChatColor.RED.toString() + "You can only use 2 points to define a cuboid zone.");
                    return;
                }
                int[] p = new int[2];

                p[0] = World.toInt(blockClicked.getX());
                p[1] = World.toInt(blockClicked.getZ());

                if (blockClicked.getY() < World.MAX_Z - Zones.pilonHeight) {
                    for (int i = 1; i <= Zones.pilonHeight; i++) {
                        Block t = player.getWorld().getBlockAt(blockClicked.getX(), blockClicked.getY() + i, blockClicked.getZ());
                        dummy.addDeleteBlock(t);
                        t.setTypeId(Zones.pilonType);
                    }
                }
                if (dummy.getCoords().contains(p)) {
                    player.sendMessage(ChatColor.RED.toString() + "Already added this point.");
                    return;
                }

                player.sendMessage(ChatColor.GREEN.toString() + "Added point [" + p[0] + "," + p[1] + "] to the temp zone.");
                dummy.addCoords(p);
            }
        }
    }

    /**
     * Called when a block gets ignited
     * 
     * @param event
     *            Relevant event details
     */
    public void onBlockIgnite(BlockIgniteEvent event) { 
        if(event.isCancelled()) return;
        
        WorldManager wm = plugin.getWorldManager(event.getBlock().getWorld());
        ZoneBase zone = wm.getActiveZone(event.getBlock());
        if(zone == null) {
            if(!wm.getConfig().canBurn(event.getPlayer(), event.getBlock(), event.getCause()))
                event.setCancelled(true);
        } else {
            if(!zone.allowFire(event.getPlayer(), event.getBlock()) || (wm.getConfig().FIRE_ENFORCE_PROTECTED_BLOCKS && !wm.getConfig().canBurnBlock(event.getBlock()))) {
                event.setCancelled(true);
            }
        }
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
     * Called when a block is destroyed from burning
     * 
     * @param event
     *            Relevant event details
     */
    public void onBlockBurn(BlockBurnEvent event) {
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
                    player.sendMessage(ChatColor.RED + "You're not allowed to destroy blocks in this world!");
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
    
    public void onIceForm(org.bukkit.event.block.IceFormEvent event) {
        if(event.isCancelled()) return;
        
        Block block = event.getBlock();

        WorldManager wm = plugin.getWorldManager(block.getWorld());
        ZoneBase zone = wm.getActiveZone(block);
        if(zone == null) {
            if(!wm.getConfig().ICE_FORM_ENABLED)
                event.setCancelled(true);
        } else {
            if(!zone.allowIceForm(block))
                event.setCancelled(true);
        }
    }
}
