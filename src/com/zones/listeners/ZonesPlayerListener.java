package com.zones.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.ZonesDummyZone;
import com.zones.model.ZoneBase;


/**
 * 
 * @author Meaglin
 *
 */
public class ZonesPlayerListener extends PlayerListener {

    private Zones plugin;

    public ZonesPlayerListener(Zones plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when a player joins a server
     * 
     * @param event
     *            Relevant event details
     */
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getWorldManager(event.getPlayer()).getRegion(event.getPlayer()).revalidateZones(event.getPlayer());
    }

    /**
     * Called when a player leaves a server
     * 
     * @param event
     *            Relevant event details
     */
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        List<ZoneBase> zones = plugin.getWorldManager(player).getActiveZones(player);
        for(ZoneBase z : zones)
            z.removeCharacter(player,true);
        
        ZonesDummyZone dummy = plugin.getZoneManager().getDummy(player.getEntityId());
        if (dummy != null) {
            dummy.setConfirm("stop");
            dummy.confirm(player);
        }
    }

    /**
     * Called when a player attempts to move location in a world
     * 
     * @param event
     *            Relevant event details
     */
    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        //do nothing when we don't actually change from 1 block to another.
        if(from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
            return;
        
        ZoneBase aZone = plugin.getWorldManager(from).getActiveZone(from);
        ZoneBase bZone = plugin.getWorldManager(to).getActiveZone(to);
        if (bZone != null && !bZone.allowEnter(player, to)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED.toString() + "You can't enter " + bZone.getName() + ".");
            if (aZone != null && !aZone.allowEnter(player, from)) {
                event.setFrom(player.getWorld().getSpawnLocation());
                player.sendMessage(ChatColor.RED.toString() + "You were moved to spawn because you were in an illigal position.");
                plugin.getWorldManager(player.getWorld()).revalidateZones(player, from, player.getWorld().getSpawnLocation());
            } 
            // we don't have to do overall revalidation if the player gets
            // warped back to his previous location.
            return;
        }

        plugin.getWorldManager(from).revalidateZones(player, from, to);
    }

    /**
     * Called when a player attempts to teleport to a new location in a world
     * 
     * @param event
     *            Relevant event details
     */
    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {

        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        ZoneBase aZone = plugin.getWorldManager(from).getActiveZone(from);
        ZoneBase bZone = plugin.getWorldManager(to).getActiveZone(to);
        
        if(aZone != null) {
            if(!aZone.allowTeleport(player, from) && aZone.allowEnter(player, from)) {
                player.sendMessage(ChatColor.RED + "Your area doesn't allow teleporting.");
                event.setCancelled(true);
                return;
            }
        }
        if(bZone != null){
            if(!bZone.allowTeleport(player, to)) {
                player.sendMessage(ChatColor.RED + "You cannot warp into " + bZone.getName() + ", since it is a protected area.");
                event.setCancelled(true);
                return;
            }
        }

        plugin.getWorldManager(from).revalidateZones(player, from, to);
        if(!from.getWorld().equals(to.getWorld()))
            plugin.getWorldManager(to).revalidateZones(player, from, to);
            
    }

    /**
     * Called when a player uses an item
     * 
     * @param event
     *            Relevant event details
     */
    private static List<Integer> items = Arrays.asList(
            Material.FLINT_AND_STEEL.getId(),
            Material.BUCKET.getId(),
            Material.LAVA_BUCKET.getId(),
            Material.WATER_BUCKET.getId(),
            Material.MINECART.getId(),
            Material.BOAT.getId(),
            Material.SEEDS.getId(),
            Material.SIGN.getId(),
            Material.REDSTONE.getId()
            );
    
    private static List<Integer> blocks = Arrays.asList(
            Material.NOTE_BLOCK.getId(),
            93,
            94
            );
    
    private static List<Integer> containers = Arrays.asList(
            Material.FURNACE.getId(),
            Material.BURNING_FURNACE.getId(),
            Material.CHEST.getId(),
            Material.DISPENSER.getId()
            );
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        switch(event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                Block blockPlaced = event.getClickedBlock().getRelative(event.getBlockFace());
                WorldManager wm = plugin.getWorldManager(blockPlaced.getWorld());
                ZoneBase zone = wm.getActiveZone(blockPlaced.getLocation());
                int type = event.getItem().getTypeId();
                int blocktype = event.getClickedBlock().getTypeId();
                Player player = event.getPlayer();
                if (type == Zones.toolType) {
                    ZonesDummyZone dummy = plugin.getZoneManager().getDummy(player.getEntityId());
                    if (dummy != null) {
                        if (dummy.getType() == 1 && dummy.getCoords().size() == 2) {
                            player.sendMessage(ChatColor.RED.toString() + "You can only use 2 points to define a cuboid zone.");
                            return;
                        }
                        int[] p = new int[2];

                        p[0] = WorldManager.toInt(event.getClickedBlock().getX());
                        p[1] = WorldManager.toInt(event.getClickedBlock().getZ());

                        if (event.getClickedBlock().getY() < WorldManager.MAX_Z - Zones.pilonHeight) {
                            for (int i = 1; i <= Zones.pilonHeight; i++) {
                                Block t = player.getWorld().getBlockAt(event.getClickedBlock().getX(), event.getClickedBlock().getY() + i, event.getClickedBlock().getZ());
                                dummy.addDeleteBlock(t);
                                t.setTypeId(Zones.pilonType);
                            }
                        }
                        if (dummy.getCoords().contains(p)) {
                            player.sendMessage(ChatColor.RED.toString() + "Already added this point.");
                        }

                        player.sendMessage(ChatColor.GREEN.toString() + "Added point [" + p[0] + "," + p[1] + "] to the temp zone.");
                        dummy.addCoords(p);
                    }
                }
                
                
                if (zone == null) {
                    if(containers.contains(blocktype) || blocks.contains(blocktype)) {
                        if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getP().permission(player, "zones.build")) {
                            if (blocktype == Material.CHEST.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change chests in this world !");
                            else if (blocktype == Material.FURNACE.getId() || type == Material.BURNING_FURNACE.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change furnaces in this world !");
                            else if (blocktype == Material.DISPENSER.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change dispensers in this world !");
                            else if (blocktype == Material.NOTE_BLOCK.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change blocks in this world !");
                            event.setCancelled(true);
                        }
                    } else if(items.contains(type)) {
                        if(!wm.getConfig().isProtectedPlaceBlock(player, type)) {
                            if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getP().permission(player, "zones.build")) {
                                player.sendMessage(ChatColor.RED + "You cannot place blocks in this world !");
                                event.setCancelled(true);
                            } else {
                                wm.getConfig().logBlockPlace(player, blockPlaced);
                            }
                        } else {
                            event.setCancelled(true);
                        }
                    }
                } else {
                    if(containers.contains(blocktype)) {
                        if(!zone.allowBlockModify(player, event.getClickedBlock())) {
                            if (blocktype == Material.CHEST.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change chests in '" + zone.getName() + "' !");
                            else if (blocktype == Material.FURNACE.getId() || type == Material.BURNING_FURNACE.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change furnaces in '" + zone.getName() + "' !");
                            else if (blocktype == Material.DISPENSER.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change dispensers in '" + zone.getName() + "' !");
                                
                            event.setCancelled(true);
                        }
                    } else if(blocks.contains(blocktype)) {
                        if(!zone.allowBlockHit(player, event.getClickedBlock())) {
                            player.sendMessage(ChatColor.RED + "You cannot change blocks in '" + zone.getName() + "' !");
                            event.setCancelled(true);
                        }
                    } else if (items.contains(type)){
                        if(!wm.getConfig().isProtectedPlaceBlock(player, type)) {
                            if(!zone.allowBlockCreate(player,blockPlaced)) {
                                player.sendMessage(ChatColor.RED + "You cannot place blocks in '" + zone.getName() + "' !");
                                event.setCancelled(true);
                            } else {
                                wm.getConfig().logBlockPlace(player, blockPlaced);
                            }
                        } else {
                            event.setCancelled(true);
                        }
                    }
                }
                break;
            case LEFT_CLICK_BLOCK:
                
                break;
        }
        
    }

    /**
     * 
     * 
     * FUCK U BUKKIT I WILL JUST USE THIS INSTEAD OF THE OVER COMPLICATED CRAP SYSTEM
     * K THX BAI.
     * 
     * 
     */
    /* @Override
    public void onPlayerCommandPreprocess(PlayerChatEvent event) {
        if(ZonesCommandsHandler.onCommand(plugin, event.getPlayer(), event.getMessage().split(" ")))
            event.setCancelled(true);
    } */

    /**
     * Called when a player gets kicked from the server
     * 
     * @param event Relevant event details
     */
    public void onPlayerKick(PlayerKickEvent event) {
    }

    /**
     * Called when a player sends a chat message
     *
     * @param event Relevant event details
     */
    public void onPlayerChat(PlayerChatEvent event) {
    }

    /**
     * Called when a player respawns
     * 
     * @param event Relevant event details
     */
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    }

    /**
     * Called when a player attempts to log in to the server
     *
     * @param event Relevant event details
     */
    public void onPlayerLogin(PlayerLoginEvent event) {
    }

    /**
     * Called when a player throws an egg and it might hatch
     *
     * @param event Relevant event details
     */
    public void onPlayerEggThrow(PlayerEggThrowEvent event) {
    }

    /**
     * Called when a player plays an animation, such as an arm swing
     *
     * @param event Relevant event details
     */
    public void onPlayerAnimation(PlayerAnimationEvent event) {
    }

    /**
     * Called when a player opens an inventory
     *
     * @param event Relevant event details
     */
    public void onInventoryOpen(PlayerInventoryEvent event) {
    }

    /**
     * Called when a player changes their held item
     *
     * @param event Relevant event details
     */
    public void onItemHeldChange(PlayerItemHeldEvent event) {
    }

    /**
     * Called when a player drops an item from their inventory
     *
     * @param event Relevant event details
     */
    public void onPlayerDropItem(PlayerDropItemEvent event) {
    }

    /**
     * Called when a player picks an item up off the ground
     *
     * @param event Relevant event details
     */
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    }

    /**
     * Called when a player toggles sneak mode
     *
     * @param event Relevant event details
     */
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
    }

    /**
     * Called when a player fills a bucket
     * 
     * @param event Relevant event details
     */
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
    }

    /**
     * Called when a player empties a bucket
     * 
     * @param event Relevant event details
     */
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
    }

}
