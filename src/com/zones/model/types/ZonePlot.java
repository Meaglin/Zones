package com.zones.model.types;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.model.ZoneBase;

/**
 * 
 * @author Meaglin
 *
 */
public class ZonePlot extends ZoneInherit {
    
    public boolean claim(Player player) {
        if(adminusers.size() != 0) {
            sendMarkupMessage(ChatColor.RED + "Zone {zname} is already claimed.", player);
            return false;
        }
        for(ZoneBase b : getInheritedZones()) {
            if(b instanceof ZoneInherit) {
                for(ZoneBase sub : ((ZoneInherit)b).getSubZones()) {
                    if(!sub.equals(this) && sub instanceof ZonePlot && ((ZoneInherit)sub).isAdminUser(player)) {
                        b.sendMarkupMessage(ChatColor.RED + "You have already claimed a zone in {zname}!", player);
                        return false;
                    }
                }
            }
        }
        
        addAdmin(player.getName());
        sendMarkupMessage(ChatColor.GREEN + "You are now the proud owner of {zname}!", player);
        return true;
    }
}
