package com.zones.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.meaglin.json.JSONArray;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.ZonesAccess.Rights;
import com.zones.model.settings.ZoneVar;
import com.zones.model.types.ZoneNormal;
import com.zones.selection.ZoneSelection;
import com.zones.selection.ZoneSelection.Confirm;
import com.zones.world.WorldManager;


/**
 * 
 * @author Meaglin
 *
 */
public class ZonesPlayerListener implements Listener {

    private Zones plugin;

    public ZonesPlayerListener(Zones plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        WorldManager wm  = plugin.getWorldManager(event.getPlayer());
        ZoneNormal zone = wm.getActiveZone(event.getPlayer());
        if(zone == null) {
           if(wm.getConfig().isEnabled(ZoneVar.ALLOWED_COMMANDS)) {
               JSONArray arr = wm.getConfig().getSetting(ZoneVar.ALLOWED_COMMANDS).getJSONArray("value");
               boolean contains = false;
               for(Object o : arr) {
                   if(event.getMessage().toLowerCase().startsWith((String) o)) {
                       contains = true;
                       break;
                   }
               }
               if(!contains
                       && !plugin.hasPermission(event.getPlayer(), "zones.override.command")) {
                   event.getPlayer().sendMessage(ZonesConfig.PLAYER_CANT_USE_COMMAND_IN_WORLD);
                   event.setCancelled(true);
                   return;
               }
           }
           if(wm.getConfig().isEnabled(ZoneVar.DENIED_COMMANDS)) {
               JSONArray arr = wm.getConfig().getSetting(ZoneVar.DENIED_COMMANDS).getJSONArray("value");
               for(Object o : arr) {
                   if(event.getMessage().toLowerCase().startsWith((String) o)
                           && !plugin.hasPermission(event.getPlayer(), "zones.override.command")) {
                       event.getPlayer().sendMessage(ZonesConfig.PLAYER_CANT_USE_COMMAND_IN_WORLD);
                       event.setCancelled(true);
                       return;
                   }
               }
           }
        } else {
            if(wm.getConfig().isEnabled(ZoneVar.ALLOWED_COMMANDS) && wm.getConfig().isEnforced(ZoneVar.ALLOWED_COMMANDS)) {
                JSONArray arr = wm.getConfig().getSetting(ZoneVar.ALLOWED_COMMANDS).getJSONArray("value");
                boolean contains = false;
                for(Object o : arr) {
                    if(event.getMessage().toLowerCase().startsWith((String) o)) {
                        contains = true;
                        break;
                    }
                }
                if(!contains
                        && !plugin.hasPermission(event.getPlayer(), "zones.override.command")) {
                    event.getPlayer().sendMessage(ZonesConfig.PLAYER_CANT_USE_COMMAND_IN_WORLD);
                    event.setCancelled(true);
                    return;
                }
            }
            if(wm.getConfig().isEnabled(ZoneVar.DENIED_COMMANDS) && wm.getConfig().isEnforced(ZoneVar.DENIED_COMMANDS)) {
                JSONArray arr = wm.getConfig().getSetting(ZoneVar.DENIED_COMMANDS).getJSONArray("value");
                for(Object o : arr) {
                    if(event.getMessage().toLowerCase().startsWith((String) o)
                            && !plugin.hasPermission(event.getPlayer(), "zones.override.command")) {
                        event.getPlayer().sendMessage(ZonesConfig.PLAYER_CANT_USE_COMMAND_IN_WORLD);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            if(zone.getFlag(ZoneVar.ALLOWED_COMMANDS)) {
                JSONArray arr = zone.getSettings().getJSONArray(ZoneVar.ALLOWED_COMMANDS.getName());
                boolean contains = false;
                for(Object o : arr) {
                    if(event.getMessage().toLowerCase().startsWith((String) o)) {
                        contains = true;
                        break;
                    }
                }
                if(!contains
                        && !plugin.hasPermission(event.getPlayer(), "zones.override.command")) {
                    zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_USE_COMMAND_IN_ZONE, event.getPlayer());
                    event.setCancelled(true);
                    return;
                }
            }
            if(zone.getFlag(ZoneVar.DENIED_COMMANDS)) {
                JSONArray arr = zone.getSettings().getJSONArray(ZoneVar.DENIED_COMMANDS.getName());
                for(Object o : arr) {
                    if(event.getMessage().toLowerCase().startsWith((String) o)
                            && !plugin.hasPermission(event.getPlayer(), "zones.override.command")) {
                        zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_USE_COMMAND_IN_ZONE, event.getPlayer());
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getWorldManager(event.getPlayer()).revalidateZones(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        List<ZoneNormal> zones = plugin.getWorldManager(player).getActiveZones(player);
        for(ZoneNormal z : zones) {
            z.removePlayer(player,true);
        }
        
        ZoneSelection selection = plugin.getZoneManager().getSelection(player.getEntityId());
        if (selection != null) {
            selection.setConfirm(Confirm.STOP);
            selection.confirm();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        /*
         * We only revalidate when we change actually change from 1 block to another.
         * and since bukkits "check" is allot smaller we have to do this properly ourselves, sadly.
         */
        if(WorldManager.toInt(from.getX()) == WorldManager.toInt(to.getX()) && WorldManager.toInt(from.getY()) == WorldManager.toInt(to.getY()) && WorldManager.toInt(from.getZ()) == WorldManager.toInt(to.getZ()))
            return;
        /*
         * For the heck of it al:
         * if you're wondering why we use the same world manager for both aZone and bZone it's because as far as i know you cant MOVE to another world
         * and always get teleported.
         */
        WorldManager wm = plugin.getWorldManager(from);
        ZoneNormal aZone = wm.getActiveZone(from);
        ZoneNormal bZone = wm.getActiveZone(to);
        
        if (bZone != null) {
            if(!bZone.canModify(player, Rights.ENTER)) {
                bZone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_ENTER_INTO_ZONE, player);
                /*
                 * In principle this should only occur when someones access to a zone gets revoked when still inside the zone.
                 * This prevents players getting stuck ;).
                 */
                if (aZone != null && !aZone.canModify(player, Rights.ENTER)) {
                    player.sendMessage(ZonesConfig.PLAYER_ILLIGAL_POSITION);
                    player.teleport(from.getWorld().getSpawnLocation());
                    event.setCancelled(false);
                    return;
                } 
                player.teleport(from);
                event.setCancelled(false);
                return;
            } else if (wm.getFlag(ZoneVar.BORDER) 
                    && wm.getConfig().isEnforced(ZoneVar.BORDER)
                    && wm.getConfig().isOutsideBorder(to)
                    && (!wm.getFlag(ZoneVar.BORDER_EXCEMPT_ADMIN) || plugin.hasPermission(player, "zones.override.border"))
                ) {
                if(wm.getConfig().isOutsideBorder(from)) {
                    player.sendMessage(ZonesConfig.PLAYER_ILLIGAL_POSITION);
                    player.teleport(from.getWorld().getSpawnLocation());
                    event.setCancelled(false);
                    return;
                }
                player.sendMessage(ZonesConfig.PLAYER_REACHED_BORDER);
                player.teleport(from);
                event.setCancelled(false);
                return;
            }
        } else if(wm.getFlag(ZoneVar.BORDER) 
                && wm.getConfig().isOutsideBorder(to)
                && (!wm.getFlag(ZoneVar.BORDER_EXCEMPT_ADMIN) || plugin.hasPermission(player, "zones.override.border"))
            ) {
            if(wm.getConfig().isOutsideBorder(from)
                && (
                        wm.getConfig().isEnforced(ZoneVar.BORDER) ||
                        aZone == null ||
                        aZone.canModify(player, Rights.ENTER)
            )) {
                player.sendMessage(ZonesConfig.PLAYER_ILLIGAL_POSITION);
                player.teleport(from.getWorld().getSpawnLocation());
                event.setCancelled(false);
                return;
            }
            player.sendMessage(ZonesConfig.PLAYER_REACHED_BORDER);
            player.teleport(from);
            event.setCancelled(false);
            return;
        }
        
        wm.revalidateZones(player, from, to);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if(event.getTo() == null) {
            return;
        }
        if(event.getTo().getWorld() == null) {
            return;
        }
        onPlayerTeleport(event);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        WorldManager wmfrom = plugin.getWorldManager(from);
        WorldManager wmto = plugin.getWorldManager(to);
        ZoneNormal aZone = wmfrom.getActiveZone(from);
        ZoneNormal bZone = wmto.getActiveZone(to);
        
        if(aZone != null) {
            if(!aZone.getFlag(ZoneVar.TELEPORT) && !aZone.canAdministrate(player)) {
                aZone.sendMarkupMessage(ZonesConfig.TELEPORT_INTO_ZONE_DISABLED, player);
                event.setCancelled(true);
                return;
            }
        }
        if(bZone != null) {
            if(!bZone.getFlag(ZoneVar.TELEPORT) && !bZone.canAdministrate(player)) {
                bZone.sendMarkupMessage(ZonesConfig.TELEPORT_INTO_ZONE_DISABLED, player);
                event.setCancelled(true);
                return;
            }
            if(!bZone.canModify(player, Rights.ENTER)) {
                bZone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_ENTER_INTO_ZONE, player);
                event.setCancelled(false);
                return;
            }
            if (wmto.getFlag(ZoneVar.BORDER) 
                    && wmto.getConfig().isEnforced(ZoneVar.BORDER)
                    && wmto.getConfig().isOutsideBorder(to)
                    && (!wmto.getFlag(ZoneVar.BORDER_EXCEMPT_ADMIN) || plugin.hasPermission(wmto.getWorldName(), player, "zones.override.border"))
                ) {
                player.sendMessage(ZonesConfig.PLAYER_CANT_WARP_OUTSIDE_BORDER);
                player.teleport(from);
                event.setCancelled(false);
                return;
            }
        } else if (wmto.getFlag(ZoneVar.BORDER) 
                && wmto.getConfig().isOutsideBorder(to)
                && (!wmto.getFlag(ZoneVar.BORDER_EXCEMPT_ADMIN) || plugin.hasPermission(wmto.getWorldName(), player, "zones.override.border"))
            ) {
            player.sendMessage(ZonesConfig.PLAYER_CANT_WARP_OUTSIDE_BORDER);
            player.teleport(from);
            event.setCancelled(false);
            return;
        }

        if(from.getWorld() != to.getWorld()) {
            wmfrom.revalidateOutZones(player, from);
        }
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
    private static final List<Material> placeItems = Arrays.asList(
            Material.FLINT_AND_STEEL,
            Material.LAVA_BUCKET,
            Material.WATER_BUCKET,
            Material.SEEDS,
            Material.SIGN,
            Material.REDSTONE,
            Material.INK_SACK,
            Material.PAINTING,
            Material.WOOD_DOOR,
            Material.IRON_DOOR,
            Material.TNT,
            Material.REDSTONE_TORCH_ON,
            Material.PUMPKIN,
            Material.ITEM_FRAME
        );

    private static final List<Material> placeHitItems = Arrays.asList(
            Material.MINECART,
            Material.STORAGE_MINECART,
            Material.BOAT,
            Material.POWERED_MINECART
        );

    private static final List<Material> destroyItems = Arrays.asList(
            Material.BUCKET
        );

    private static final List<Material> placeBlocks = Arrays.asList(
            Material.DIODE_BLOCK_OFF,
            Material.DIODE_BLOCK_ON
        );

    private static final List<Material> hitBlocks = Arrays.asList(
            Material.CAKE_BLOCK,
            Material.LEVER,
            Material.STONE_PLATE,
            Material.WOOD_PLATE,
            Material.STONE_BUTTON,
            Material.WOODEN_DOOR,
            Material.TRAP_DOOR,
            Material.FENCE_GATE,
            Material.TRIPWIRE,
            Material.TRIPWIRE_HOOK,
            Material.WOOD_BUTTON
        );

    private static final List<Material> modifyBlocks = Arrays.asList(
            Material.NOTE_BLOCK,
            Material.JUKEBOX,
            Material.FURNACE,
            Material.BURNING_FURNACE,
            Material.CHEST,
            Material.DISPENSER,
            Material.BREWING_STAND,
            Material.BEACON,
            Material.COMMAND,
            Material.ANVIL,
            Material.TRAPPED_CHEST,
            Material.DROPPER,
            Material.HOPPER
        );
    
    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        
        /*
        System.out.println( event.getAction().name() + 
                "\t i:" + event.getItem().getTypeId() + 
                "\t b:" + event.getClickedBlock().getTypeId() + 
                "\t p:" + event.getClickedBlock().getRelative(event.getBlockFace()).getTypeId());
        */
        

        Player player = event.getPlayer();
        Material blockType = (event.getClickedBlock() != null ? event.getClickedBlock().getType() : null);
        Material toolType = (event.getItem() != null ? event.getItem().getType() : null);
        
        /*
         * Using a huge ass if(...) would have been possible too however this seems more elegant and prolly is a little bit faster
         * (however this speed difference would be very hard to determine :P )
         */
        switch(event.getAction()) {
            case RIGHT_CLICK_BLOCK:
            case LEFT_CLICK_BLOCK:
            case PHYSICAL:
                if(!hitBlocks.contains(blockType)) {
                    break;
                }
                // Allow people to play a note block, shouldn't be protected imho.
                // if(event.getAction() == Action.LEFT_CLICK_BLOCK && blockType == 25) break;

                EventUtil.onHitPlace(plugin, event, player, event.getClickedBlock(), blockType);
                if(event.isCancelled()) {
                    return;
                }
        }
        
        switch(event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                
                if (toolType != null && toolType.getId() == ZonesConfig.CREATION_TOOL_TYPE) {
                    ZoneSelection selection = plugin.getZoneManager().getSelection(player.getEntityId());
                    if (selection != null) {
                        if(event.getClickedBlock() != null) {
                            Block block = event.getClickedBlock();
                            selection.onRightClick(block);
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                
                
                if(modifyBlocks.contains(blockType)) 
                    EventUtil.onModify(plugin, event, player, event.getClickedBlock(), blockType);
                else if(placeItems.contains(toolType)) 
                    EventUtil.onPlace(plugin, event, player, event.getClickedBlock().getRelative(event.getBlockFace()), toolType);
                else if(placeHitItems.contains(toolType)) 
                    EventUtil.onHitPlace(plugin, event, player, event.getClickedBlock().getRelative(event.getBlockFace()), toolType);
                else if(placeBlocks.contains(blockType)) 
                    EventUtil.onPlace(plugin, event, player, event.getClickedBlock(), blockType);
                else if(destroyItems.contains(toolType)) {
                    Block bl = event.getClickedBlock().getRelative(event.getBlockFace());
                    EventUtil.onBreak(plugin, event, player, bl, bl.getType());
                } else if((blockType == Material.DIRT || blockType == Material.GRASS) && (
                        toolType == Material.WOOD_HOE ||
                        toolType == Material.STONE_HOE ||
                        toolType == Material.IRON_HOE ||
                        toolType == Material.GOLD_HOE ||
                        toolType == Material.DIAMOND_HOE
                )) {
                    EventUtil.onPlace(plugin, event, player, event.getClickedBlock(), blockType);
                }
                
                
                break;
        }
        if(!event.isCancelled() && event.getAction() == Action.PHYSICAL &&
                event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.SOIL) {
            WorldManager wm = plugin.getWorldManager(player.getWorld());
            if(wm.testFlag(event.getClickedBlock(), ZoneVar.CROP_PROTECTION)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ZoneNormal z = plugin.getWorldManager(event.getPlayer()).getActiveZone(event.getPlayer());
        if(z != null) {
            Location loc = z.getSpawnLocation(event.getPlayer());
            if(loc != null) {
                loc.setY(loc.getY()+1);
                event.setRespawnLocation(loc);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ZoneNormal zone = plugin.getWorldManager(event.getItemDrop().getWorld()).getActiveZone(event.getItemDrop().getLocation());
        if(zone != null && !zone.canModify(event.getPlayer(), Rights.HIT)) {
            zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_PICKUP_ITEMS_IN_ZONE, event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if(event.isCancelled()) return;
        ZoneNormal zone = plugin.getWorldManager(event.getItem().getWorld()).getActiveZone(event.getItem().getLocation());
        if(zone != null && !zone.canModify(event.getPlayer(), Rights.HIT)) {
            zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_DROP_ITEMS_IN_ZONE, event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if(event.isCancelled()) {
            return;
        }
        
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        Player player = event.getPlayer();
        EventUtil.onBreak(plugin, event, player, block, Material.WATER);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block blockPlaced = event.getBlockClicked().getRelative(event.getBlockFace());
        EventUtil.onPlace(plugin, event, player, blockPlaced, event.getItemStack().getType());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity target = event.getRightClicked();
        if(target == null) {
            return;
        }
        
        // Don't allow leashing without hit rights.
        if(player.getItemInHand() != null && 
                player.getItemInHand().getType() == Material.LEASH &&
                target instanceof Animals) {
            EventUtil.onEntityHit(plugin, event, player, target);
        }
        
        if (target instanceof StorageMinecart || target instanceof HopperMinecart) {
            EventUtil.onEntityChange(plugin, event, player, target);
        } else if (target instanceof Horse) {
            if (player.isSneaking()) {
                EventUtil.onEntityChange(plugin, event, player, target);
            } else {
                EventUtil.onEntityHit(plugin, event, player, target);
            }
        } else if (target instanceof ItemFrame) {
            ItemFrame frame = (ItemFrame) target;
            if(frame.getItem() == null && player.getItemInHand() != null) {
                EventUtil.onEntityCreate(plugin, event, player, target);
            } else {
                EventUtil.onEntityHit(plugin, event, player, target);
            }
        } else if (target instanceof LeashHitch) {
            EventUtil.onEntityHit(plugin, event, player, target);
        } else if (target instanceof PoweredMinecart && player.getItemInHand() != null && player.getItemInHand().getType() == Material.COAL) {
            EventUtil.onEntityHit(plugin, event, player, target);
        }
    }
    
    @EventHandler(ignoreCancelled = true) 
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if(holder.equals(player)) {
            return;
        }
        if(holder instanceof Entity) {
            Entity entity = (Entity) holder;
            EventUtil.onEntityChange(plugin, event, player, entity);
        } else if (holder instanceof BlockState) {
            BlockState state = (BlockState) holder;
            EventUtil.onModify(plugin, event, player, state.getBlock(), state.getType());
        }
        
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onShearSheep(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        Entity target = event.getEntity();
        
        ZoneNormal zone = plugin.getWorldManager(target.getWorld()).getActiveZone(target.getLocation());
        if(zone != null && !zone.canModify(player, Rights.HIT)){
            zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_SHEAR_IN_ZONE, player);
            event.setCancelled(true);
            return;
        }
    }
}
