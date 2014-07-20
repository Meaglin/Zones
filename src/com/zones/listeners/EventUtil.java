package com.zones.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.ZonesAccess.Rights;
import com.zones.model.settings.ZoneVar;
import com.zones.model.types.ZoneNormal;
import com.zones.selection.ZoneSelection;
import com.zones.util.BlockUtil;
import com.zones.world.WorldManager;

public class EventUtil {

    public static final void onPlace(Zones plugin, Cancellable event, Player player, Block block) {
        onPlace(plugin, event, player, block, null);
    }
    
    public static final void onPlace(Zones plugin, Cancellable event, Player player, Block block, Material type) {
        WorldManager wm = plugin.getWorldManager(player);
        if(wm.isProtected(player, ZoneVar.PLACE_BLOCKS, type, true)) {
            player.sendMessage(ZonesConfig.BLOCK_IS_PROTECTED);
            event.setCancelled(true);
            return;
        }
        
        if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player, "zones.build")){
            player.sendMessage(ZonesConfig.PLAYER_CANT_BUILD_WORLD);
            event.setCancelled(true);
            return;
        }
        
        ZoneNormal zone = wm.getActiveZone(block);
        if(zone == null){
            if(wm.isProtected(player, ZoneVar.PLACE_BLOCKS, type, false)) {
                player.sendMessage(ZonesConfig.BLOCK_IS_PROTECTED);
                event.setCancelled(true);
                return;
            }
            if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player, "zones.inheritbuild")){
                player.sendMessage(ZonesConfig.PLAYER_CANT_BUILD_WORLD);
                event.setCancelled(true);
                return;
            }
        } else  {
            
            if(!zone.canModify(player, Rights.BUILD)) {
                zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_BUILD_BLOCKS_IN_ZONE, player);
                event.setCancelled(true);
                return;
            }
            type = type == null ? block.getType() : type;
            if(BlockUtil.isContainer(type) && !zone.canModify(player, Rights.MODIFY)) {
                zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_MODIFY_BLOCKS_IN_ZONE, player);
                event.setCancelled(true);
                return;
            }
            if(zone.isProtected(ZoneVar.PLACE_BLOCKS, type) && !zone.canAdministrate(player)) {
                zone.sendMarkupMessage(ZonesConfig.BLOCK_IS_BLACKLISTED_IN_ZONE, player);
                return;
            }
        }
    }
    
    public static final void onHitPlace(Zones plugin, Cancellable event, Player player, Block block, Material type) {
        WorldManager wm = plugin.getWorldManager(player);
        if(wm.isProtected(player, ZoneVar.PLACE_BLOCKS, type, true)) {
            player.sendMessage(ZonesConfig.BLOCK_IS_PROTECTED);
            event.setCancelled(true);
            return; 
        }
        ZoneNormal zone = wm.getActiveZone(block);
        if(zone != null) {
            if(!zone.canModify(player, Rights.HIT)) {
                zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_HIT_BLOCKS_IN_ZONE, player);
                event.setCancelled(true);
                return;
            }
            if(zone.isProtected(ZoneVar.PLACE_BLOCKS, type) && !zone.canAdministrate(player)) {
                zone.sendMarkupMessage(ZonesConfig.BLOCK_IS_BLACKLISTED_IN_ZONE, player);
                return;
            }
        } else {
            if(wm.isProtected(player, ZoneVar.PLACE_BLOCKS, type, false)) {
                player.sendMessage(ZonesConfig.BLOCK_IS_PROTECTED);
                event.setCancelled(true);
                return; 
            }
            
        }
    }
    
    public static final void onEntityCreate(Zones plugin, Cancellable event, Player player, Entity entity) {
        WorldManager wm = plugin.getWorldManager(entity.getWorld());
        if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player,"zones.build")){
            player.sendMessage(ZonesConfig.PLAYER_CANT_BUILD_WORLD);
            event.setCancelled(true);
            return;
        }
        
        ZoneNormal zone = wm.getActiveZone(entity.getLocation());
        if(zone == null) {
            if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player,"zones.inheritbuild")){
                player.sendMessage(ZonesConfig.PLAYER_CANT_BUILD_WORLD);
                event.setCancelled(true);
            }
        } else {
            if(!zone.canModify(player, Rights.BUILD)) {
                zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_BUILD_BLOCKS_IN_ZONE, player);
                event.setCancelled(true);
                return;
            }
        }
    }
    
    public static final void onEntityHit(Zones plugin, Cancellable event, Player player, Entity entity) {
        WorldManager wm = plugin.getWorldManager(entity.getWorld());
        if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player,"zones.build")){
            player.sendMessage(ZonesConfig.PLAYER_CANT_CHANGE_WORLD);
            event.setCancelled(true);
            return;
        }
        
        ZoneNormal zone = wm.getActiveZone(entity.getLocation());
        if(zone == null) {
            if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player,"zones.inheritbuild")){
                player.sendMessage(ZonesConfig.PLAYER_CANT_CHANGE_WORLD);
                event.setCancelled(true);
            }
        } else {
            if(!zone.canModify(player, Rights.HIT)) {
                zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_HIT_ENTITYS_IN_ZONE, player);
                event.setCancelled(true);
                return;
            }
        }
    }

    public static final void onEntityChange(Zones plugin, Cancellable event, Player player, Entity entity) {
        WorldManager wm = plugin.getWorldManager(entity.getWorld());
        if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player,"zones.build")){
            player.sendMessage(ZonesConfig.PLAYER_CANT_CHANGE_WORLD);
            event.setCancelled(true);
            return;
        }
        
        ZoneNormal zone = wm.getActiveZone(entity.getLocation());
        if(zone == null) {
            if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player,"zones.inheritbuild")){
                player.sendMessage(ZonesConfig.PLAYER_CANT_CHANGE_WORLD);
                event.setCancelled(true);
            }
        } else {
            if(!zone.canModify(player, Rights.MODIFY)) {
                zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_MODIFY_BLOCKS_IN_ZONE, player);
                event.setCancelled(true);
                return;
            }
        }
    }
    
    public static final void onModify(Zones plugin, Cancellable event, Player player, Block block, Material type) {
        WorldManager wm = plugin.getWorldManager(player);
        ZoneNormal zone = wm.getActiveZone(block);

        if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player, "zones.build")) {
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
            if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player, "zones.inheritbuild")) {
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
            if(!zone.canModify(player, Rights.MODIFY)) {
                zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_MODIFY_BLOCKS_IN_ZONE, player);
                event.setCancelled(true);
                return;
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    public static final void onBreak(Zones plugin, Cancellable event, Player player, Block block, Material type) {
        WorldManager wm = plugin.getWorldManager(block.getWorld());
        // TODO: magic id number
       if (player.getItemInHand().getTypeId() == ZonesConfig.CREATION_TOOL_TYPE) {
            ZoneSelection selection = plugin.getZoneManager().getSelection(player.getEntityId());
            if (selection != null) {
                selection.onLeftClick(block);
                event.setCancelled(true);
                return;
            }
        }

       if(wm.isProtected(player, ZoneVar.BREAK_BLOCKS, type, true)) {
           event.setCancelled(true);
           return;
       }
       
       if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player, "zones.build")){
           player.sendMessage(ZonesConfig.PLAYER_CANT_DESTROY_WORLD);
           event.setCancelled(true);
           return;
       }
       
        ZoneNormal zone = wm.getActiveZone(block);
        if(zone == null) {
            if(wm.isProtected(player, ZoneVar.BREAK_BLOCKS, type, false)) {
                event.setCancelled(true);
                return;
            }
            if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player, "zones.inheritbuild")){
                player.sendMessage(ZonesConfig.PLAYER_CANT_DESTROY_WORLD);
                event.setCancelled(true);
                return;
            }
        } else {
            if(!zone.canModify(player, Rights.DESTROY)) {
                zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_DESTROY_BLOCKS_IN_ZONE, player);
                event.setCancelled(true);
                return;
            }
            if(BlockUtil.isContainer(type) && !zone.canModify(player, Rights.MODIFY)) {
                zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_DESTROY_CHEST_IN_ZONE, player);
                event.setCancelled(true);
                return;
            }
            if(zone.isProtected(ZoneVar.BREAK_BLOCKS, type) && !zone.canAdministrate(player)) {
                zone.sendMarkupMessage(ZonesConfig.BLOCK_IS_PROTECTED_IN_ZONE, player);
                return;
            }
        }
    }
}
