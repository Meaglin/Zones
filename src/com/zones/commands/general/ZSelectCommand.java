package com.zones.commands.general;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;

/**
 * 
 * @author Meaglin
 *
 */
public class ZSelectCommand extends ZoneCommand {

    public ZSelectCommand(Zones plugin) {
        super("zselect", plugin);
    }

    @Override
    public void run(Player player, String[] vars) {
        if(vars.length == 1){
            if(vars[0].equalsIgnoreCase("reset")) {
                getZoneManager().removeSelected(player.getEntityId());
                player.sendMessage(ChatColor.GREEN + "Zone deselected.");
                return;
            }
            List<ZoneBase> zoneslist = getZoneManager().matchZone(player, vars[0]);
            if(zoneslist.size() < 1)
                player.sendMessage(ChatColor.YELLOW + "No zones found with key '" + vars[0] + "'(which you can modify).");
            else if(zoneslist.size() == 1){
                getZoneManager().setSelected(player.getEntityId(), zoneslist.get(0).getId());
                player.sendMessage(ChatColor.GREEN.toString() + "Selected zone '" + zoneslist.get(0).getName() + "' .");
            } else {
                player.sendMessage(ChatColor.YELLOW +  "Too many zones found, please be more specific.");
                String temp = "";
                int delta = Integer.MAX_VALUE;
                ZoneBase closest = null;
                for (ZoneBase zone : zoneslist) {
                    if(closest == null || Math.abs(closest.getName().length()-vars[0].length()) < delta) {
                        closest = zone;
                        delta = Math.abs(closest.getName().length()-vars[0].length());
                    }
                    temp += zone.getName() + "[" + zone.getId() + "]";
                }
                player.sendMessage(ChatColor.DARK_GREEN + "Zones found: " + temp);
                player.sendMessage(ChatColor.GOLD + "Selected closest match '" + closest.getName() +"' .");
                getZoneManager().setSelected(player.getEntityId(), closest.getId());
            }
        }else{
            List<ZoneBase> zoneslist = getWorldManager(player).getAdminZones(player);
            if(zoneslist.size() < 1) {
                player.sendMessage(ChatColor.YELLOW + "No zones found in your current area(which you can modify).");
                player.sendMessage(ChatColor.YELLOW + "Please select a zone by specifying a zone id.");
            } else if(zoneslist.size() == 1){
                getZoneManager().setSelected(player.getEntityId(), zoneslist.get(0).getId());
                player.sendMessage(ChatColor.GREEN + "Selected zone '" + zoneslist.get(0).getName() + "' .");
            } else {
                player.sendMessage(ChatColor.YELLOW +  "Too much zones found, please specify a zone id.(/zselect <id>)");
                String temp = "";
                ZoneBase smallest = null;
                for (ZoneBase zone : zoneslist) {
                    if(smallest == null || zone.getForm().getSize() < smallest.getForm().getSize())
                        smallest = zone;
                    temp += zone.getName() + "[" + zone.getId() + "]";
                }
                player.sendMessage(ChatColor.DARK_GREEN + "Zones found: " + temp);
                player.sendMessage(ChatColor.GOLD + "Selected smallest '" + smallest.getName() +"' .");
                getZoneManager().setSelected(player.getEntityId(), smallest.getId());
            }
        }
    }

}
