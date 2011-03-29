package com.zones.listeners;

import java.util.Arrays;
import java.util.List;

import com.zones.World;
import com.zones.ZoneManager;
import com.zones.ZoneBase;
import com.zones.Zones;
import com.zones.ZonesAccess;
import com.zones.ZonesConfig;
import com.zones.ZonesDummyZone;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockInteractEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.event.block.LeavesDecayEvent;

/**
 * 
 * @author Meaglin
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

        if (player.getItemInHand().getTypeId() == Zones.toolType) {
            ZonesDummyZone dummy = ZoneManager.getInstance().getDummy(player.getName());
            if (dummy != null) {
                if (dummy.containsDeleteBlock(block)) {
                    int[] p = new int[2];
                    p[0] = block.getX();
                    p[1] = block.getZ();
                    dummy.removeCoords(p);
                    dummy.fix(block.getX(), block.getZ());
                    player.sendMessage(ChatColor.GREEN.toString() + "Removed point [" + p[0] + "," + p[1] + "] from temp zone.");

                } else {
                    player.sendMessage(ChatColor.RED.toString() + "Couldn't find point in zone so nothing could be removed");
                }
            }
        }

    }

    /**
     * Called when we try to place a block, to see if we can build it
     */
    public void onBlockCanBuild(BlockCanBuildEvent event) {

    }

    /**
     * Called when a block flows (water/lava)
     * 
     * @param event
     *            Relevant event details
     */
    @Override
    public void onBlockFlow(BlockFromToEvent event) {

        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();

        if (blockFrom.getTypeId() == 8 || blockFrom.getTypeId() == 9) {

            ZoneBase fromZone = World.getInstance().getActiveZone(blockFrom.getLocation());
            ZoneBase toZone = World.getInstance().getActiveZone(blockTo.getLocation());

            if (toZone != null && (fromZone == null || fromZone.getId() != toZone.getId()) && !toZone.allowWater(blockTo))
                event.setCancelled(true);
        }

        if (blockFrom.getTypeId() == 10 || blockFrom.getTypeId() == 11) {

            ZoneBase fromZone = World.getInstance().getActiveZone(blockFrom.getLocation());
            ZoneBase toZone = World.getInstance().getActiveZone(blockTo.getLocation());

            if (toZone != null && (fromZone == null || fromZone.getId() != toZone.getId()) && !toZone.allowLava(blockTo))
                event.setCancelled(true);
        }

    }

    /**
     * Called when a block gets ignited
     * 
     * @param event
     *            Relevant event details
     */
    public void onBlockIgnite(BlockIgniteEvent event) {

    }

    /**
     * Called when block physics occurs
     * 
     * @param event
     *            Relevant event details
     */
    public void onBlockPhysics(BlockPhysicsEvent event) {
    }

    /**
     * Called when a player places a block
     * 
     * @param event
     *            Relevant event details
     */
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block blockPlaced = event.getBlockPlaced();

        ZoneBase zone = World.getInstance().getActiveZone(blockPlaced.getLocation());
        if (zone != null && !zone.canModify(player, ZonesAccess.Rights.BUILD)) {
            player.sendMessage(ChatColor.RED.toString() + "You cannot place blocks in '" + zone.getName() + "' !");
            event.setBuild(false);
        } else if (zone != null && (blockPlaced.getTypeId() == 54 || blockPlaced.getTypeId() == 61 || blockPlaced.getTypeId() == 62) && !zone.canModify(player, ZonesAccess.Rights.MODIFY)) {
            player.sendMessage(ChatColor.RED.toString() + "You cannot place chests/furnaces in '" + zone.getName() + "' since you don't have modify rights !");
            event.setBuild(false);
        } else if (!ZonesConfig.LIMIT_BY_BUILD_ENABLED || plugin.getP().permission(player, "zones.build") || (zone != null && zone.canModify(player, ZonesAccess.Rights.BUILD)))
            return;
        else {
            player.sendMessage(ChatColor.RED.toString() + "You cannot build in the world.");
            event.setBuild(false);
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

        Block block = event.getBlock();
        Player player = event.getPlayer();

        
        ZoneBase zone = World.getInstance().getActiveZone(block.getLocation());
        if (zone != null && !zone.canModify(player, ZonesAccess.Rights.DESTROY)) {
            player.sendMessage(ChatColor.RED.toString() + "You cannot destroy blocks in '" + zone.getName() + "' !");
            event.setCancelled(true);
        } else if (zone != null && (block.getTypeId() == 54 || block.getTypeId() == 61 || block.getTypeId() == 62) && !zone.canModify(player, ZonesAccess.Rights.MODIFY)) {

            player.sendMessage(ChatColor.RED.toString() + "You cannot destroy a chest/furnace in '" + zone.getName() + "' since you dont have modify rights!");

            event.setCancelled(true);
        } else if (!ZonesConfig.LIMIT_BY_BUILD_ENABLED || plugin.getP().permission(player, "zones.build") || (zone != null && zone.canModify(player, ZonesAccess.Rights.DESTROY)))
            return;
        else {
            player.sendMessage(ChatColor.RED.toString() + "You cannot destroy in the world.");
            event.setCancelled(true);
        }

    }
}
