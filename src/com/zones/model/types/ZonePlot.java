package com.zones.model.types;

import org.bukkit.entity.Player;

import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;

/**
 * 
 * @author Meaglin
 *
 */
public class ZonePlot extends ZoneInherit {
    
    public boolean claim(Player player) {
        if(adminusers.size() != 0) {
            sendMarkupMessage(ZonesConfig.ZONE_ALREADY_CLAIMED, player);
            return false;
        }
        for(ZoneBase b : getInheritedZones()) {
            if(b instanceof ZoneInherit) {
                for(ZoneBase sub : ((ZoneInherit)b).getSubZones()) {
                    if(!sub.equals(this) && sub instanceof ZonePlot && ((ZoneInherit)sub).isAdminUser(player)) {
                        b.sendMarkupMessage(ZonesConfig.PLAYER_ALREADY_CLAIMED_ZONE, player);
                        return false;
                    }
                }
            }
        }
        
        doClaim(player);
        return true;
    }
    
    protected void doClaim(Player player) {
        addAdmin(player.getName());
        sendMarkupMessage(ZonesConfig.PLAYER_CLAIMES_ZONES, player);
    }

    public void unclaim(Player player) {
        this.adminusers.clear();
        this.updateRights();
        sendMarkupMessage("Zone {zname} unclaimed.", player);
    }
}
