package com.zones.model.types;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.meaglin.json.JSONObject;
import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;

/**
 * 
 * @author Meaglin
 *
 */
public class ZonePlot extends ZoneInherit {
    
    public boolean claim(Player player) {
        if(admins.size() != 0) {
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
        setAdmin(player, true);
        sendMarkupMessage(ZonesConfig.PLAYER_CLAIMES_ZONES, player);
    }

    public void unclaim(Player player) {
        JSONObject userlist = getConfig().getJSONObject("users");
        for(UUID uuid : admins) {
            userlist.remove(uuid.toString());
        }
        this.admins.clear();
        this.updateRights();
        sendMarkupMessage("Zone {zname} unclaimed.", player);
    }
}
