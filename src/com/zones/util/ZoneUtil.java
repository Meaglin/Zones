package com.zones.util;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.ZonesAccess.Rights;
import com.zones.model.settings.ZoneVar;
import com.zones.model.types.ZoneNormal;
import com.zones.world.WorldManager;

public class ZoneUtil {
    private Zones plugin;
    
    public ZoneUtil(Zones plugin) {
        this.plugin = plugin;
    }
    
    public ZoneNormal getActiveZone(Location loc) {
        return plugin.getWorldManager(loc).getActiveZone(loc);
    }
    
    public List<ZoneNormal> getActiveZones(Location loc) {
        return plugin.getWorldManager(loc).getActiveZones(loc);
    }
    
    public boolean canAdministrate(Player player, Block block) {
        ZoneNormal zone = plugin.getWorldManager(block.getWorld()).getActiveZone(block);
        if(zone != null && !zone.canAdministrate(player))
            return false;
        return true;
    }
    
    public boolean canAdministrate(Player player) {
        return canAdministrate(player, player.getLocation());
    }
    
    public boolean canAdministrate(Player player, Location loc) {
        ZoneNormal zone = plugin.getWorldManager(loc).getActiveZone(loc);
        if(zone != null && !zone.canAdministrate(player))
            return false;
        return true;
    }
    
    public boolean canBuild(Player player, Block block) {
        WorldManager wm = plugin.getWorldManager(player);
        Material type = block.getType();
        if(wm.isProtected(player, ZoneVar.PLACE_BLOCKS, type, true)) {
            return false;
        }
        
        if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player, "zones.build")){
            return false;
        }
        
        ZoneNormal zone = wm.getActiveZone(block);
        if(zone == null){
            if(wm.isProtected(player, ZoneVar.PLACE_BLOCKS, type, false)) {
                return false;
            }
            if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player, "zones.inheritbuild")){
                return false;
            }
        } else  {
            
            if(!zone.canModify(player, Rights.BUILD)) {
                return false;
            }
            type = type == null ? block.getType() : type;
            if(BlockUtil.isContainer(type) && !zone.canModify(player, Rights.MODIFY)) {
                return false;
            }
            if(zone.isProtected(ZoneVar.PLACE_BLOCKS, type) && !zone.canAdministrate(player)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean canModify(Player player, Block block) {
        WorldManager wm = plugin.getWorldManager(player);
        if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player,"zones.build")){
            return false;
        }
        
        ZoneNormal zone = wm.getActiveZone(block);
        if(zone == null) {
            if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player,"zones.inheritbuild")){
                return false;
            }
        } else {
            if(!zone.canModify(player, Rights.MODIFY)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean canDestroy(Player player, Block block) {
        WorldManager wm = plugin.getWorldManager(block.getWorld());
        Material type = block.getType();
        
        if(wm.isProtected(player, ZoneVar.BREAK_BLOCKS, type, true)) {
            return false;
        }
        
        if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player, "zones.build")){
            return false;
        }
        
         ZoneNormal zone = wm.getActiveZone(block);
         if(zone == null) {
             if(wm.isProtected(player, ZoneVar.BREAK_BLOCKS, type, false)) {
                 return false;
             }
             if(wm.getFlag(ZoneVar.BUILD_PERMISSION_REQUIRED) && !plugin.hasPermission(wm.getWorldName(), player, "zones.inheritbuild")){
                 return false;
             }
         } else {
             if(!zone.canModify(player, Rights.DESTROY)) {
                 return false;
             }
             if(BlockUtil.isContainer(type) && !zone.canModify(player, Rights.MODIFY)) {
                 return false;
             }
             if(zone.isProtected(ZoneVar.BREAK_BLOCKS, type) && !zone.canAdministrate(player)) {
                 return false;
             }
         }
        return true;
    }
    
    public boolean canHit(Player player, Block block) {
        WorldManager wm = plugin.getWorldManager(player.getWorld());
        Material type = block.getType();
        
        if(wm.isProtected(player, ZoneVar.PLACE_BLOCKS, type, true)) {
            return false; 
        }
        ZoneNormal zone = wm.getActiveZone(block);
        if(zone != null) {
            if(!zone.canModify(player, Rights.HIT)) {
                return false;
            }
            if(zone.isProtected(ZoneVar.PLACE_BLOCKS, type) && !zone.canAdministrate(player)) {
                return false;
            }
        } else {
            if(wm.isProtected(player, ZoneVar.PLACE_BLOCKS, type, false)) {
                return false;
            }
            
        }
        return true;
    }
    
    // TODO: redo this.
//    public boolean canHit(Player attacker, Entity defender) {
//        WorldManager wm = plugin.getWorldManager(defender.getWorld());
//        ZoneNormal zone = wm.getActiveZone(defender.getLocation());
//        Player player = (defender instanceof Player ? (Player)defender : null);
//        if (zone == null) {
//            if(player != null) {
//                if(!wm.getConfig().canReceiveDamage(player, null)) {
//                    return false;
//                }
//                if(wm.getConfig().hasGodMode(player)) {
//                    return false;
//                }
//            }
//        } else {
//            if (player != null) {                
//                if (!((PlayerDamageResolver)zone.getResolver(AccessResolver.PLAYER_RECEIVE_DAMAGE)).isAllowed(zone, player, null, 0) || (wm.getConfig().PLAYER_ENFORCE_SPECIFIC_DAMAGE && !wm.getConfig().canReceiveSpecificDamage(player, null))) {
//                    return false;
//                }
//            }
//            if(attacker != null && attacker instanceof Player) {
//                Player att = attacker;
//                if(!((PlayerHitEntityResolver)zone.getResolver(AccessResolver.PLAYER_ENTITY_HIT)).isAllowed(zone, att, defender, 0)) {
//                    return false;
//                }
//            }            
//        }
//        
//        return true;
//    }
    
    public boolean canEnter(Player player, Location loc) {
        WorldManager wm = plugin.getWorldManager(loc);
        ZoneNormal zone = wm.getActiveZone(loc);
        if(zone != null) {
            if(!zone.canModify(player, Rights.ENTER)) {
                return false;
            }
        }
        return true;
    }
}
