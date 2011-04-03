package com.zones.commands.general;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.ZoneBase;
import com.zones.Zones;
import com.zones.commands.ZoneCommand;

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
    public boolean run(Player player, String[] vars) {
        if(vars.length == 1){
            ZoneBase zone = getZoneManager().getZone(Integer.parseInt(vars[0]));
            if (zone == null)
                player.sendMessage(ChatColor.YELLOW.toString() + "No zone found with id : " + Integer.parseInt(vars[0]));
            else if (!zone.canAdministrate(player))
                player.sendMessage(ChatColor.RED.toString() + "You don't have rights to administrate this zone.");
            else {
                getZoneManager().setSelected(player.getName(), zone.getId());
                player.sendMessage(ChatColor.GREEN.toString() + "Selected zone '" + zone.getName() + "' .");
            }
        }else{
            List<ZoneBase> zoneslist = getWorldManager().getAdminZones(player);
            if(zoneslist.size() < 1)
                player.sendMessage(ChatColor.YELLOW.toString() + "No zones found in your current area(which you can modify).");
            else if(zoneslist.size() == 1){
                getZoneManager().setSelected(player.getName(), zoneslist.get(0).getId());
                player.sendMessage(ChatColor.GREEN.toString() + "Selected zone '" + zoneslist.get(0).getName() + "' .");
            } else {
                player.sendMessage(ChatColor.YELLOW.toString() +  "Too much zones found, please specify a zone id.(/zselect <id>)");
                String temp = "";
                for (ZoneBase zone : zoneslist)
                    temp += zone.getName() + "[" + zone.getId() + "]";
                player.sendMessage("Zones found: " + temp);
            }
        }
        return true;
    }

}
