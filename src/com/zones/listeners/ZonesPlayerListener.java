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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
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
import com.zones.ZonesConfig;
import com.zones.ZonesDummyZone;
import com.zones.ZonesDummyZone.Confirm;
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
            dummy.setConfirm(Confirm.STOP);
            dummy.confirm();
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
        if(event.isCancelled()) return;
        
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        /*
         * We only revalidate when we change actually change from 1 block to another.
         * and since bukkits "check" is allot smaller we have to do this properly ourselves, sadly.
         */
        if(from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
            return;
        /*
         * For the heck of it al:
         * if you're wondering why we use the same world manager for both aZone and bZone it's because as far as i know you cant MOVE to another world
         * and always get teleported.
         */
        WorldManager wm = plugin.getWorldManager(from);
        ZoneBase aZone = wm.getActiveZone(from);
        ZoneBase bZone = wm.getActiveZone(to);
        
        if (bZone != null) {
            if(!bZone.allowEnter(player, to)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED.toString() + "You can't enter " + bZone.getName() + ".");
                /*
                 * In principle this should only occur when someone's access to a zone gets revoked when still inside the zone.
                 * This prevents players getting stuck ;).
                 */
                if (aZone != null && !aZone.allowEnter(player, from)) {
                    event.setFrom(wm.getWorld().getSpawnLocation());
                    player.sendMessage(ChatColor.RED.toString() + "You were moved to spawn because you were in an illigal position.");
                    wm.revalidateZones(player, from, player.getWorld().getSpawnLocation());
                } 
                return;
            } else if (wm.getConfig().BORDER_ENABLED && wm.getConfig().BORDER_ENFORCE && !plugin.getP().permission(player, "zones.override.border")) {
                if(wm.getConfig().isOutsideBorder(to)) {
                    if(wm.getConfig().isOutsideBorder(from)) {
                        event.setCancelled(true);
                        event.setFrom(wm.getWorld().getSpawnLocation());
                        player.sendMessage(ChatColor.RED.toString() + "You were moved to spawn because you were in an illigal position.");
                        wm.revalidateZones(player, from, wm.getWorld().getSpawnLocation());
                        return;
                    }
                    player.sendMessage(ChatColor.RED + "You have reached the border.");
                    event.setCancelled(true);
                    return;
                }
            }
        } else if(wm.getConfig().BORDER_ENABLED && !plugin.getP().permission(player, "zones.override.border")) {
            if(wm.getConfig().isOutsideBorder(to)) {
                if(wm.getConfig().isOutsideBorder(from)) {
                    event.setCancelled(true);
                    event.setFrom(wm.getWorld().getSpawnLocation());
                    player.sendMessage(ChatColor.RED.toString() + "You were moved to spawn because you were in an illigal position.");
                    wm.revalidateZones(player, from, wm.getWorld().getSpawnLocation());
                    return;
                }
                player.sendMessage(ChatColor.RED + "You have reached the border.");
                event.setCancelled(true);
                return;
            }
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
        if(event.isCancelled()) return;
        
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        WorldManager wmto = plugin.getWorldManager(to);
        ZoneBase aZone = plugin.getWorldManager(from).getActiveZone(from);
        ZoneBase bZone = wmto.getActiveZone(to);
        
        if(aZone != null) {
            if(!aZone.allowTeleport(player, from) && aZone.allowEnter(player, from)) {
                player.sendMessage(ChatColor.RED + "Your area doesn't allow teleporting.");
                event.setCancelled(true);
                return;
            }
        }
        if(bZone != null){
            if(!bZone.allowTeleport(player, to)) {
                if(bZone.allowEnter(player, to)) {
                    player.sendMessage(ChatColor.RED + "You cannot warp into " + bZone.getName() + " because it has teleporting disabled.");                    
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot warp into " + bZone.getName() + ", since it is a protected area.");                    
                }
                event.setCancelled(true);
                return;
            } else if (wmto.getConfig().BORDER_ENABLED && wmto.getConfig().BORDER_ENFORCE) {
                if(wmto.getConfig().isOutsideBorder(to) && !plugin.getP().permission(player, "zones.override.border")) {
                    player.sendMessage(ChatColor.RED + "You cannot warp outside the border.");
                    event.setCancelled(true);
                    return;
                }
            }
        } else if(wmto.getConfig().BORDER_ENABLED) {
            if(wmto.getConfig().isOutsideBorder(to) && !plugin.getP().permission(player, "zones.override.border")) {
                player.sendMessage(ChatColor.RED + "You cannot warp outside the border.");
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
            Material.DIODE_BLOCK_OFF.getId(),
            Material.DIODE_BLOCK_ON.getId()
            );
    
    private static List<Integer> containers = Arrays.asList(
            Material.FURNACE.getId(),
            Material.BURNING_FURNACE.getId(),
            Material.CHEST.getId(),
            Material.DISPENSER.getId()
            );
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.isCancelled()) return;
        
        switch(event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                Block blockPlaced = event.getClickedBlock().getRelative(event.getBlockFace());
                WorldManager wm = plugin.getWorldManager(blockPlaced.getWorld());
                ZoneBase zone = wm.getActiveZone(blockPlaced.getLocation());
                int type = event.getItem().getTypeId();
                int blocktype = event.getClickedBlock().getTypeId();
                Player player = event.getPlayer();
                if (type == ZonesConfig.CREATION_TOOL_TYPE) {
                    ZonesDummyZone dummy = plugin.getZoneManager().getDummy(player.getEntityId());
                    if (dummy != null) {
                        if (dummy.getFormId() == 1 && dummy.getCoords().size() == 2) {
                            player.sendMessage(ChatColor.RED.toString() + "You can only use 2 points to define a cuboid zone.");
                            return;
                        }

                        if (dummy.containsCoords(event.getClickedBlock().getX(), event.getClickedBlock().getZ())) {
                            player.sendMessage(ChatColor.RED.toString() + "Already added this point.");
                        } else {
                            if (event.getClickedBlock().getY() < WorldManager.MAX_Z - ZonesConfig.CREATION_PILON_HEIGHT) {
                                for (int i = 1; i <= ZonesConfig.CREATION_PILON_HEIGHT; i++) {
                                    Block t = player.getWorld().getBlockAt(event.getClickedBlock().getX(), event.getClickedBlock().getY() + i, event.getClickedBlock().getZ());
                                    dummy.addDeleteBlock(t);
                                    t.setTypeId(ZonesConfig.CREATION_PILON_TYPE);
                                }
                            }
                            player.sendMessage(ChatColor.GREEN.toString() + "Added point [" + event.getClickedBlock().getX() + "," + event.getClickedBlock().getZ() + "] to the temp zone.");
                            dummy.addCoords(event.getClickedBlock().getX(), event.getClickedBlock().getZ());
                        }
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
     * 
     */
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(event.getMessage().toLowerCase().startsWith("/worldedit")) {
            plugin.onCommand(event.getPlayer(), null, "worldedit", Arrays.copyOfRange(event.getMessage().split(" "), 1, event.getMessage().split(" ").length));
        }
    }

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
