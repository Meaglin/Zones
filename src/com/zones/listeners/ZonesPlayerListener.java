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
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;
import com.zones.selection.ZoneSelection;
import com.zones.selection.ZoneSelection.Confirm;


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
        for(WorldManager wm : plugin.getWorlds())
            if(wm.getConfig().GOD_MODE_ENABLED)
                wm.getConfig().setGodMode(event.getPlayer(), wm.getConfig().GOD_MODE_AUTOMATIC);
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
            z.removePlayer(player,true);
        
        ZoneSelection selection = plugin.getZoneManager().getSelection(player.getEntityId());
        if (selection != null) {
            selection.setConfirm(Confirm.STOP);
            selection.confirm();
        }
        for(WorldManager wm : plugin.getWorlds())
            if(wm.getConfig().GOD_MODE_ENABLED)
                wm.getConfig().setGodMode(event.getPlayer(), true); // remove from list.
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
                player.sendMessage(ChatColor.RED + "You can't enter " + bZone.getName() + ".");
                /*
                 * In principle this should only occur when someone's access to a zone gets revoked when still inside the zone.
                 * This prevents players getting stuck ;).
                 */
                if (aZone != null && !aZone.allowEnter(player, from)) {
                    event.setFrom(wm.getWorld().getSpawnLocation());
                    player.sendMessage(ChatColor.RED + "You were moved to spawn because you were in an illigal position.");
                    wm.revalidateZones(player, from, player.getWorld().getSpawnLocation());
                } 
                player.teleport(event.getFrom());
                event.setCancelled(true);
                return;
            } else if (wm.getConfig().BORDER_ENABLED && wm.getConfig().BORDER_ENFORCE) {
                if(wm.getConfig().isOutsideBorder(to) && (!wm.getConfig().BORDER_OVERRIDE_ENABLED || !plugin.getPermissions().canUse(player, "zones.override.border"))) {
                    if(wm.getConfig().isOutsideBorder(from)) {
                        event.setFrom(wm.getWorld().getSpawnLocation());
                        player.sendMessage(ChatColor.RED + "You were moved to spawn because you were in an illigal position.");
                        wm.revalidateZones(player, from, wm.getWorld().getSpawnLocation());
                        return;
                    }
                    player.sendMessage(ChatColor.RED + "You have reached the border.");
                    player.teleport(event.getFrom());
                    event.setCancelled(true);
                    return;
                }
            }
        } else if(wm.getConfig().BORDER_ENABLED) {
            if(wm.getConfig().isOutsideBorder(to) && (!wm.getConfig().BORDER_OVERRIDE_ENABLED || !plugin.getPermissions().canUse(player, "zones.override.border"))) {
                if(wm.getConfig().isOutsideBorder(from)) {

                    event.setFrom(wm.getWorld().getSpawnLocation());
                    player.teleport(event.getFrom());
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED.toString() + "You were moved to spawn because you were in an illigal position.");
                    wm.revalidateZones(player, from, wm.getWorld().getSpawnLocation());
                    return;
                }
                player.sendMessage(ChatColor.RED + "You have reached the border.");
                player.teleport(event.getFrom());
                event.setCancelled(true);
                return;
            }
        }

        wm.revalidateZones(player, from, to);
    }

    public void onPlayerPortal(PlayerPortalEvent event) {
        onPlayerTeleport(event);
    }
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if(event.isCancelled()) return;
        
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        WorldManager wmfrom = plugin.getWorldManager(from);
        WorldManager wmto = plugin.getWorldManager(to);
        ZoneBase aZone = wmfrom.getActiveZone(from);
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
                if(wmto.getConfig().isOutsideBorder(to) && (!wmto.getConfig().BORDER_OVERRIDE_ENABLED || !plugin.getPermissions().canUse(player, "zones.override.border"))) {
                    player.sendMessage(ChatColor.RED + "You cannot warp outside the border.");
                    event.setCancelled(true);
                    return;
                }
            }
        } else if(wmto.getConfig().BORDER_ENABLED) {
            if(wmto.getConfig().isOutsideBorder(to) && (!wmto.getConfig().BORDER_OVERRIDE_ENABLED || !plugin.getPermissions().canUse(player, "zones.override.border"))) {
                player.sendMessage(ChatColor.RED + "You cannot warp outside the border.");
                event.setCancelled(true);
                return;
            }
        }

        if(from.getWorld() != to.getWorld())
            wmfrom.revalidatOutZones(player, from);
        wmto.revalidateZones(player, from, to);
            
    }

    /**
     * Called when a player uses an item
     * 
     * @param event
     *            Relevant event details
     */
    /*
     * Some might say that referencing to the items like this might not be efficient
     * however the actual materials are only requested when the array gets initialized
     * (this happens when it gets called for the first time) and after that its
     * just a list of Integers in the memory.
     */
    private static final List<Integer> placeItems = Arrays.asList(
            Material.FLINT_AND_STEEL.getId(),
            Material.LAVA_BUCKET.getId(),
            Material.WATER_BUCKET.getId(),
            Material.MINECART.getId(),
            Material.BOAT.getId(),
            Material.SEEDS.getId(),
            Material.SIGN.getId(),
            Material.REDSTONE.getId(),
            Material.INK_SACK.getId(),
            Material.PAINTING.getId(),
            Material.WOOD_DOOR.getId(),
            Material.IRON_DOOR.getId()
            );
    
    private static final List<Integer> destroyItems = Arrays.asList(
            Material.BUCKET.getId()
            );
    
    private static final List<Integer> placeBlocks = Arrays.asList(
            Material.DIODE_BLOCK_OFF.getId(),
            Material.DIODE_BLOCK_ON.getId()
            );
    
    private static final List<Integer> hitBlocks = Arrays.asList(
            Material.CAKE_BLOCK.getId(),
            Material.LEVER.getId(),
            Material.STONE_PLATE.getId(),
            Material.WOOD_PLATE.getId(),
            Material.STONE_BUTTON.getId(),
            Material.WOODEN_DOOR.getId()
            );
    
    private static final List<Integer> modifyBlocks = Arrays.asList(
            Material.NOTE_BLOCK.getId(),
            Material.FURNACE.getId(),
            Material.BURNING_FURNACE.getId(),
            Material.CHEST.getId(),
            Material.DISPENSER.getId()
            );
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        
        /*
        System.out.println( event.getAction().name() + 
                "\t i:" + event.getItem().getTypeId() + 
                "\t b:" + event.getClickedBlock().getTypeId() + 
                "\t p:" + event.getClickedBlock().getRelative(event.getBlockFace()).getTypeId());
        */
        
        
        if(event.isCancelled()) return;

        Player player = event.getPlayer();
        int blockType = (event.getClickedBlock() != null ? event.getClickedBlock().getTypeId() : 0);
        int toolType = (event.getItem() != null ? event.getItem().getTypeId() : 0);
        
        /*
         * Using a huge ass if(...) would have been possible too however this seems more elegant and prolly is a little bit faster
         * (however this speed difference would be very hard to determine :P )
         */
        switch(event.getAction()) {
            case RIGHT_CLICK_BLOCK:
            case LEFT_CLICK_BLOCK:
            case PHYSICAL:
                if(hitBlocks.contains(blockType)) {
                    // Allow people to play a note block, shouldn't be protected imho.
                   // if(event.getAction() == Action.LEFT_CLICK_BLOCK && blockType == 25) break;
                    
                    WorldManager wm = plugin.getWorldManager(player.getWorld());
                    ZoneBase zone = wm.getActiveZone(event.getClickedBlock());
                    if(zone == null) {
                        if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().canUse(player, "zones.build")) {
                            player.sendMessage(ChatColor.RED + "You cannot change blocks in this world !");
                            event.setCancelled(true);
                            return;
                        }
                    } else {
                        if(!zone.allowBlockHit(player, event.getClickedBlock())) {
                            player.sendMessage(ChatColor.RED + "You cannot change blocks in '" + zone.getName() + "' !");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                break;
        }
        
        switch(event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                
                if (toolType == ZonesConfig.CREATION_TOOL_TYPE) {
                    ZoneSelection selection = plugin.getZoneManager().getSelection(player.getEntityId());
                    if (selection != null) {
                        if(event.getClickedBlock() != null) {
                            Block block = event.getClickedBlock();
                            selection.onRightClick(block);
                            event.setCancelled(true);
                            return;
                        }
                    } else {
                        if(event.getClickedBlock() != null) {
                            Block block = event.getClickedBlock();
                            WorldManager wm = plugin.getWorldManager(player.getWorld());
                            List<ZoneBase> zones = wm.getActiveZones(block.getX(), block.getZ(), block.getY());
                            if(zones.size() > 0) {
                                player.sendMessage(ChatColor.DARK_GREEN + "Permission:" + wm.getActiveZone(block).getAccess(player).toColorCode() + ", zones found:");
                                String str = "";
                                for(ZoneBase zone : zones) {
                                    str += "," + zone.getName() + "[" + zone.getId() + "]";
                                }
                                player.sendMessage(ChatColor.AQUA + str.substring(1));
                            } else {
                                player.sendMessage(ChatColor.GREEN + "No zones found.");
                            }
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                
                
                if(modifyBlocks.contains(blockType)) {
                    WorldManager wm = plugin.getWorldManager(player.getWorld());
                    ZoneBase zone = wm.getActiveZone(event.getClickedBlock());
                    if(zone == null) {
                        if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().canUse(player, "zones.build")) {
                            if (blockType == Material.CHEST.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change chests in this world!");
                            else if (blockType == Material.FURNACE.getId() || blockType == Material.BURNING_FURNACE.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change furnaces in this world!");
                            else if (blockType == Material.DISPENSER.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change dispensers in this world!");
                            else if (blockType == Material.NOTE_BLOCK.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change note blocks in this world!");
                            
                            event.setCancelled(true);
                            return;
                        }
                    } else {
                        if(!zone.allowBlockModify(player, event.getClickedBlock())) {
                            if (blockType == Material.CHEST.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change chests in '" + zone.getName() + "' !");
                            else if (blockType == Material.FURNACE.getId() || blockType == Material.BURNING_FURNACE.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change furnaces in '" + zone.getName() + "' !");
                            else if (blockType == Material.DISPENSER.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change dispensers in '" + zone.getName() + "' !");
                            else if (blockType == Material.NOTE_BLOCK.getId())
                                player.sendMessage(ChatColor.RED + "You cannot change note blocks in '" + zone.getName() + "' !");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                
                if(placeItems.contains(toolType) || placeBlocks.contains(blockType)) {
                    WorldManager wm = plugin.getWorldManager(player.getWorld());
                    if(!wm.getConfig().isProtectedPlaceBlock(player, toolType)) {
                        Block block = event.getClickedBlock().getRelative(event.getBlockFace());
                        ZoneBase zone = wm.getActiveZone(block);
                        if(zone == null) {                      
                            if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().canUse(player, "zones.build")) {
                                player.sendMessage(ChatColor.RED + "You cannot build in this world!");
                                event.setCancelled(true);
                                return;
                            } else {
                                wm.getConfig().logBlockPlace(player, block,event.getItem());
                            }
                        } else {
                            if(!zone.allowBlockCreate(player,block,event.getItem())) {
                                event.setCancelled(true);
                                return;
                            } else {
                                wm.getConfig().logBlockPlace(player, block,event.getItem());                                
                            }
                        }
                    } else {
                        event.setCancelled(true);
                        return;
                    }
                }
                
                else if(destroyItems.contains(toolType)) {
                    WorldManager wm = plugin.getWorldManager(player.getWorld());
                    Block block = event.getClickedBlock().getRelative(event.getBlockFace());
                    if(!wm.getConfig().isProtectedBreakBlock(player, block)) {
                        ZoneBase zone = wm.getActiveZone(block);
                        if(zone == null) {        
                            if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().canUse(player, "zones.build")) {
                                player.sendMessage(ChatColor.RED + "You cannot destroy blocks in this world!");
                                event.setCancelled(true);
                                return;
                            } else {
                                wm.getConfig().logBlockBreak(player, block);
                            }
                        } else {
                            if(!zone.allowBlockDestroy(player, block)) {
                                event.setCancelled(true);
                                return;
                            } else {
                                wm.getConfig().logBlockBreak(player, block);                             
                            }
                        }
                    } else {
                        event.setCancelled(true);
                        return;
                    }
                }
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
        ZoneBase z = plugin.getWorldManager(event.getPlayer()).getActiveZone(event.getPlayer());
        if(z != null) {
            Location loc = z.getSpawnLocation(event.getPlayer());
            loc.setY(loc.getY()+2);
            event.setRespawnLocation(loc);
        }
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
        if(event.isCancelled()) return;
        
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
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
                } else {
                    wm.getConfig().logBlockBreak(player, block);
                }
            }
        }
    }

    /**
     * Called when a player empties a bucket
     * 
     * @param event Relevant event details
     */
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if(event.isCancelled()) return;
        
        Player player = event.getPlayer();
        Block blockPlaced = event.getBlockClicked().getRelative(event.getBlockFace());

        WorldManager wm = plugin.getWorldManager(player);
        if(!wm.getConfig().isProtectedBreakBlock(player, blockPlaced)) {
            ZoneBase zone = wm.getActiveZone(blockPlaced);
            if(zone == null){
                if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().canUse(player,"zones.build")){
                    player.sendMessage(ChatColor.RED + "You cannot build in this world!");
                    event.setCancelled(true);
                } else {
                    wm.getConfig().logBlockPlace(player, blockPlaced);
                }
            } else  {
                if (!zone.allowBlockCreate(player, blockPlaced)) {
                    // These messages are now handled in the ZoneClass.
                    //player.sendMessage(ChatColor.RED + "You cannot place blocks in '" + zone.getName() + "' .");
                    event.setCancelled(true);
                } else {
                    wm.getConfig().logBlockPlace(player, blockPlaced);
                }
            }
        }
    }
}
