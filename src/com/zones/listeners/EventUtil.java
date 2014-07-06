package com.zones.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.accessresolver.AccessResolver;
import com.zones.accessresolver.interfaces.PlayerBlockResolver;
import com.zones.model.ZoneBase;
import com.zones.model.ZonesAccess.Rights;
import com.zones.model.types.ZoneNormal;
import com.zones.selection.ZoneSelection;
import com.zones.util.BlockUtil;

public class EventUtil {

    public static final void onPlace(Zones plugin, Cancellable event, Player player, Block block) {
        onPlace(plugin, event, player, block, null);
    }
    
    public static final void onPlace(Zones plugin, Cancellable event, Player player, Block block, Material type) {
        WorldManager wm = plugin.getWorldManager(player);
        if(!wm.getConfig().isProtectedPlaceBlock(player, type, true)) {
            ZoneBase zone = wm.getActiveZone(block);
            
            if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(),player.getName(),"zones.build")){
                player.sendMessage(ZonesConfig.PLAYER_CANT_BUILD_WORLD);
                event.setCancelled(true);
                return;
            }
            
            if(zone == null){
                if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(),player.getName(),"zones.inheritbuild")){
                    player.sendMessage(ZonesConfig.PLAYER_CANT_BUILD_WORLD);
                    event.setCancelled(true);
                } else {
                    wm.getConfig().logBlockPlace(player, block);
                }
            } else  {
                
                if(!((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_CREATE)).isAllowed(zone, player, block, type)) {
                    ((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_CREATE)).sendDeniedMessage(zone, player);
                    event.setCancelled(true);
                    return;
                }
                type = type == null ? block.getType() : type;
                if(BlockUtil.isContainer(type) && !((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_MODIFY)).isAllowed(zone, player, block, type)) {
                    zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_PLACE_CHEST_IN_ZONE, player);
                    event.setCancelled(true);
                    return;
                }
                
                wm.getConfig().logBlockPlace(player, block);
            }
        } else {
            event.setCancelled(true);
        }
    }
    
    public static final void onHitPlace(Zones plugin, Cancellable event, Player player, Block block, Material type) {
        WorldManager wm = plugin.getWorldManager(player);
        if(!wm.getConfig().isProtectedPlaceBlock(player, type, true)) {
            ZoneBase zone = wm.getActiveZone(block);
            if(zone != null) {
                if(!((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_HIT)).isAllowed(zone, player, block, type)) {
                    ((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_HIT)).sendDeniedMessage(zone, player);
                    event.setCancelled(true);
                    return;
                }
            }
        } else {
            event.setCancelled(true);
        }
    }
    
    public static final void onEntityCreate(Zones plugin, Cancellable event, Player player, Entity entity) {
        WorldManager wm = plugin.getWorldManager(entity.getWorld());
        if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(),player.getName(),"zones.build")){
            player.sendMessage(ZonesConfig.PLAYER_CANT_BUILD_WORLD);
            event.setCancelled(true);
            return;
        }
        
        ZoneBase zone = wm.getActiveZone(entity.getLocation());
        if(zone == null) {
            if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(),player.getName(),"zones.inheritbuild")){
                player.sendMessage(ZonesConfig.PLAYER_CANT_BUILD_WORLD);
                event.setCancelled(true);
            }
        } else {
            if(!((ZoneNormal)zone).canModify(player, Rights.BUILD)) {
                zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_BUILD_BLOCKS_IN_ZONE, player);
                event.setCancelled(true);
                return;
            }
        }
    }
    
    public static final void onEntityHit(Zones plugin, Cancellable event, Player player, Entity entity) {
        WorldManager wm = plugin.getWorldManager(entity.getWorld());
        if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(),player.getName(),"zones.build")){
            player.sendMessage(ZonesConfig.PLAYER_CANT_CHANGE_WORLD);
            event.setCancelled(true);
            return;
        }
        
        ZoneBase zone = wm.getActiveZone(entity.getLocation());
        if(zone == null) {
            if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(),player.getName(),"zones.inheritbuild")){
                player.sendMessage(ZonesConfig.PLAYER_CANT_CHANGE_WORLD);
                event.setCancelled(true);
            }
        } else {
            if(!((ZoneNormal)zone).canModify(player, Rights.HIT)) {
                zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_HIT_ENTITYS_IN_ZONE, player);
                event.setCancelled(true);
                return;
            }
        }
    }

    public static final void onEntityChange(Zones plugin, Cancellable event, Player player, Entity entity) {
        WorldManager wm = plugin.getWorldManager(entity.getWorld());
        if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(),player.getName(),"zones.build")){
            player.sendMessage(ZonesConfig.PLAYER_CANT_CHANGE_WORLD);
            event.setCancelled(true);
            return;
        }
        
        ZoneBase zone = wm.getActiveZone(entity.getLocation());
        if(zone == null) {
            if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(),player.getName(),"zones.inheritbuild")){
                player.sendMessage(ZonesConfig.PLAYER_CANT_CHANGE_WORLD);
                event.setCancelled(true);
            }
        } else {
            if(!((ZoneNormal)zone).canModify(player, Rights.MODIFY)) {
                zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_MODIFY_BLOCKS_IN_ZONE, player);
                event.setCancelled(true);
                return;
            }
        }
    }
    
    public static final void onModify(Zones plugin, Cancellable event, Player player, Block block, Material type) {
        WorldManager wm = plugin.getWorldManager(player);
        ZoneBase zone = wm.getActiveZone(block);

        if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(),player.getName(), "zones.build")) {
            switch(type) {
                case CHEST: case TRAPPED_CHEST:
                    player.sendMessage(ChatColor.RED + "You cannot change chests in this world!");
                    break;
                case FURNACE: case BURNING_FURNACE:
                    player.sendMessage(ChatColor.RED + "You cannot change furnaces in this world!");
                    break;
                case DISPENSER:
                    player.sendMessage(ChatColor.RED + "You cannot change dispensers in this world!");
                    break;
                case DROPPER:
                    player.sendMessage(ChatColor.RED + "You cannot change droppers in this world!");
                    break;
                case NOTE_BLOCK:
                    player.sendMessage(ChatColor.RED + "You cannot change note blocks in this world!");
                    break;
            }
            event.setCancelled(true);
            return;
        }

        if(zone == null) {
            if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(),player.getName(), "zones.inheritbuild")) {
                switch(type) {
                    case CHEST: case TRAPPED_CHEST:
                        player.sendMessage(ChatColor.RED + "You cannot change chests in this world!");
                        break;
                    case FURNACE: case BURNING_FURNACE:
                        player.sendMessage(ChatColor.RED + "You cannot change furnaces in this world!");
                        break;
                    case DISPENSER:
                        player.sendMessage(ChatColor.RED + "You cannot change dispensers in this world!");
                        break;
                    case DROPPER:
                        player.sendMessage(ChatColor.RED + "You cannot change droppers in this world!");
                        break;
                    case NOTE_BLOCK:
                        player.sendMessage(ChatColor.RED + "You cannot change note blocks in this world!");
                        break;
                }
                event.setCancelled(true);
                return;
            }
        } else {
            if(!((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_MODIFY)).isAllowed(zone, player, block, null)) {
                ((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_MODIFY)).sendDeniedMessage(zone, player);
                event.setCancelled(true);
                return;
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    public static final void onBreak(Zones plugin, Cancellable event, Player player, Block block) {
        WorldManager wm = plugin.getWorldManager(block.getWorld());
        if(!wm.getConfig().isProtectedBreakBlock(player, block)) {
            // TODO: magic id number
           if (player.getItemInHand().getTypeId() == ZonesConfig.CREATION_TOOL_TYPE) {
                ZoneSelection selection = plugin.getZoneManager().getSelection(player.getEntityId());
                if (selection != null) {
                    selection.onLeftClick(block);
                    event.setCancelled(true);
                    return;
                }
            }
           
           if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(),player.getName(), "zones.build")){
               player.sendMessage(ZonesConfig.PLAYER_CANT_DESTROY_WORLD);
               event.setCancelled(true);
               return;
           }
           
           
            ZoneBase zone = wm.getActiveZone(block);
            if(zone == null) {
                if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(),player.getName(), "zones.inheritbuild")){
                    player.sendMessage(ZonesConfig.PLAYER_CANT_DESTROY_WORLD);
                    event.setCancelled(true);
                } else {
                    wm.getConfig().logBlockBreak(player, block);
                }
            } else {
                if(!((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_DESTROY)).isAllowed(zone, player, block, null)) {
                    ((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_DESTROY)).sendDeniedMessage(zone, player);
                    event.setCancelled(true);
                    return;
                }
                Material type = block.getType();
                if(BlockUtil.isContainer(type) && !((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_MODIFY)).isAllowed(zone, player, block, type)) {
                    zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_PLACE_CHEST_IN_ZONE, player);
                    event.setCancelled(true);
                    return;
                }
                wm.getConfig().logBlockBreak(player, block);
            }
        } else {
            event.setCancelled(true);
        }
    }
}
