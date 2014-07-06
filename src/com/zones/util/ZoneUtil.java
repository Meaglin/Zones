package com.zones.util;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.accessresolver.AccessResolver;
import com.zones.accessresolver.interfaces.PlayerBlockResolver;
import com.zones.accessresolver.interfaces.PlayerDamageResolver;
import com.zones.accessresolver.interfaces.PlayerHitEntityResolver;
import com.zones.accessresolver.interfaces.PlayerLocationResolver;
import com.zones.model.ZoneBase;

public class ZoneUtil {
    private Zones plugin;
    
    public ZoneUtil(Zones plugin) {
        this.plugin = plugin;
    }
    
    public ZoneBase getActiveZone(Location loc) {
        return plugin.getWorldManager(loc).getActiveZone(loc);
    }
    
    public List<ZoneBase> getActiveZones(Location loc) {
        return plugin.getWorldManager(loc).getActiveZones(loc);
    }
    
    public boolean canAdministrate(Player player, Block block) {
        ZoneBase zone = plugin.getWorldManager(block.getWorld()).getActiveZone(block);
        if(zone != null && !zone.canAdministrate(player))
            return false;
        return true;
    }
    
    public boolean canAdministrate(Player player) {
        return canAdministrate(player, player.getLocation());
    }
    
    public boolean canAdministrate(Player player, Location loc) {
        ZoneBase zone = plugin.getWorldManager(loc).getActiveZone(loc);
        if(zone != null && !zone.canAdministrate(player))
            return false;
        return true;
    }
    
    public boolean canBuild(Player player, Block block) {
        WorldManager wm = plugin.getWorldManager(player);
        Material type = block.getType();
        if(wm.getConfig().isProtectedPlaceBlock(player, type, false))
            return false;
        
        if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(), player.getName(),"zones.build")){
            return false;
        }
        
        ZoneBase zone = wm.getActiveZone(block);
        if(zone == null){
            if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(), player.getName(),"zones.inheritbuild")){
                return false;
            }
        } else  {
            if(!((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_CREATE)).isAllowed(zone, player, block, type)) {
                return false;
            }

            if(BlockUtil.isContainer(type) && !((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_MODIFY)).isAllowed(zone, player, block, type)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean canModify(Player player, Block block) {
        WorldManager wm = plugin.getWorldManager(player);
        ZoneBase zone = wm.getActiveZone(block);
        if(zone == null) {
            if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(), player.getName(), "zones.inheritbuild")) {
                return false;
            }
        } else {
            if(!((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_MODIFY)).isAllowed(zone, player, block, null)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean canDestroy(Player player, Block block) {
        WorldManager wm = plugin.getWorldManager(block.getWorld());
        Material type = block.getType();
        if(wm.getConfig().isProtectedBreakBlock(player, block, false))
            return false;

        if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(), player.getName(),"zones.build")){
            return false;
        }
        
        ZoneBase zone = wm.getActiveZone(block);
        if(zone == null) {
            if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(), player.getName(), "zones.inheritbuild")){
                return false;
            }
        } else {
            if(!((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_DESTROY)).isAllowed(zone, player, block, null)) {
                return false;
            }
            if(BlockUtil.isContainer(type) && !((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_MODIFY)).isAllowed(zone, player, block, type)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean canHit(Player player, Block block) {
        WorldManager wm = plugin.getWorldManager(player.getWorld());
        ZoneBase zone = wm.getActiveZone(block);
        
        if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(), player.getName(),"zones.build")){
            return false;
        }
        
        if(zone == null) {
            if(wm.getConfig().LIMIT_BUILD_BY_FLAG && !plugin.getPermissions().has(wm.getWorldName(), player.getName(), "zones.inheritbuild")) {
                return false;
            }
        } else {
            if(!((PlayerBlockResolver)zone.getResolver(AccessResolver.PLAYER_BLOCK_HIT)).isAllowed(zone, player, block, block.getType())) {
                return false;
            }
        }
        return true;
    }
    
    public boolean canHit(Player attacker, Entity defender) {
        WorldManager wm = plugin.getWorldManager(defender.getWorld());
        ZoneBase zone = wm.getActiveZone(defender.getLocation());
        Player player = (defender instanceof Player ? (Player)defender : null);
        if (zone == null) {
            if(player != null) {
                if(!wm.getConfig().canReceiveDamage(player, null)) {
                    return false;
                }
                if(wm.getConfig().hasGodMode(player)) {
                    return false;
                }
            }
        } else {
            if (player != null) {                
                if (!((PlayerDamageResolver)zone.getResolver(AccessResolver.PLAYER_RECEIVE_DAMAGE)).isAllowed(zone, player, null, 0) || (wm.getConfig().PLAYER_ENFORCE_SPECIFIC_DAMAGE && !wm.getConfig().canReceiveSpecificDamage(player, null))) {
                    return false;
                }
            }
            if(attacker != null && attacker instanceof Player) {
                Player att = attacker;
                if(!((PlayerHitEntityResolver)zone.getResolver(AccessResolver.PLAYER_ENTITY_HIT)).isAllowed(zone, att, defender, 0)) {
                    return false;
                }
            }            
        }
        
        return true;
    }
    
    public boolean canEnter(Player player, Location loc) {
        WorldManager wm = plugin.getWorldManager(loc);
        ZoneBase zone = wm.getActiveZone(loc);
        if(zone != null) {
            if(!((PlayerLocationResolver)zone.getResolver(AccessResolver.PLAYER_ENTER)).isAllowed(zone, player, player.getLocation(), loc)) {
                return false;
            } else if (wm.getConfig().BORDER_ENABLED && wm.getConfig().BORDER_ENFORCE) {
                if(wm.getConfig().isOutsideBorder(loc) && (!wm.getConfig().BORDER_OVERRIDE_ENABLED || !plugin.getPermissions().has(wm.getWorldName(), player.getName(), "zones.override.border"))) {
                    return false;
                }
            }
        } else {
            if(wm.getConfig().BORDER_ENABLED) {
                if(wm.getConfig().isOutsideBorder(loc) && (!wm.getConfig().BORDER_OVERRIDE_ENABLED || !plugin.getPermissions().has(wm.getWorldName(), player.getName(), "zones.override.border"))) {
                    return false;
                }
            }
        }
        return true;
    }
}
