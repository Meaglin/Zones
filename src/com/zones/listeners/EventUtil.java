package com.zones.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.accessresolver.AccessResolver;
import com.zones.accessresolver.interfaces.PlayerBlockResolver;
import com.zones.model.ZoneBase;
import com.zones.selection.ZoneSelection;

public class EventUtil {

    public static final void onPlace(Zones plugin, Cancellable event, Player player, Block block) {
        onPlace(plugin, event, player, block, -1);
    }
    public static final void onPlace(Zones plugin, Cancellable event, Player player, Block block, int typeId) {
        WorldManager wm = plugin.getWorldManager(player);
        if(!wm.getConfig().isProtectedBreakBlock(player, block)) {
            ZoneBase zone = wm.getActiveZone(block);
            if(zone == null){
                if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().canUse(player,wm.getWorldName(),"zones.build")){
                    player.sendMessage(ZonesConfig.PLAYER_CANT_BUILD_WORLD);
                    event.setCancelled(true);
                } else {
                    wm.getConfig().logBlockPlace(player, block);
                }
            } else  {
                
                if(!((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_CREATE)).isAllowed(zone, player, block, typeId)) {
                    ((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_CREATE)).sendDeniedMessage(zone, player);
                    event.setCancelled(true);
                    return;
                }
                typeId = typeId == -1 ? block.getTypeId() : typeId;
                if((typeId == Material.FURNACE.getId() || typeId == Material.CHEST.getId() || typeId == Material.DISPENSER.getId()) &&
                        !((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_MODIFY)).isAllowed(zone, player, block, typeId)) {
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
    
    public static final void onModify(Zones plugin, Cancellable event, Player player, Block block, int blockType) {
        WorldManager wm = plugin.getWorldManager(player);
        ZoneBase zone = wm.getActiveZone(block);
        if(zone == null) {
            if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().canUse(player,wm.getWorldName(), "zones.build")) {
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
            if(!((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_MODIFY)).isAllowed(zone, player, block, -1)) {
                ((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_MODIFY)).sendDeniedMessage(zone, player);
                event.setCancelled(true);
                return;
            }
        }
    }
    
    public static final void onBreak(Zones plugin, Cancellable event, Player player, Block block) {
        WorldManager wm = plugin.getWorldManager(block.getWorld());
        if(!wm.getConfig().isProtectedBreakBlock(player, block)) {
           if (player.getItemInHand().getTypeId() == ZonesConfig.CREATION_TOOL_TYPE) {
                ZoneSelection selection = plugin.getZoneManager().getSelection(player.getEntityId());
                if (selection != null) {
                    selection.onLeftClick(block);
                    event.setCancelled(true);
                    return;
                }
            }
            ZoneBase zone = wm.getActiveZone(block);
            if(zone == null) {
                if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().canUse(player,wm.getWorldName(), "zones.build")){
                    player.sendMessage(ZonesConfig.PLAYER_CANT_DESTROY_WORLD);
                    event.setCancelled(true);
                } else {
                    wm.getConfig().logBlockBreak(player, block);
                }
            } else {
                if(!((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_DESTROY)).isAllowed(zone, player, block, -1)) {
                    ((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_DESTROY)).sendDeniedMessage(zone, player);
                    event.setCancelled(true);
                    return;
                }
                int typeId = block.getTypeId();
                if((typeId == Material.FURNACE.getId() || typeId == Material.CHEST.getId() || typeId == Material.DISPENSER.getId()) &&
                        !((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_MODIFY)).isAllowed(zone, player, block, typeId)) {
                    zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_DESTROY_CHEST_IN_ZONE, player);
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
