package com.zones.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZReloadCommand extends ZoneCommand {

    public ZReloadCommand(Zones plugin) {
        super("zreload", plugin);
        this.setRequiresAdmin(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {

        if(vars.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /zreload config|zones|all");
        }
        String type = vars[0];
        if(type.equalsIgnoreCase("config")) {
            if(!getPlugin().reloadConfig())
                player.sendMessage("[Zones]Error while reloading config, please contact an server admin.");
            else
                player.sendMessage("[Zones]Config reloaded.");
        } else if (type.equalsIgnoreCase("zones")) {
            if(!getPlugin().reloadZones())
                player.sendMessage("[Zones]Error while reloading zones, please contact an server admin.");
            else
                player.sendMessage("[Zones]Zones reloaded.");
        } else if (type.equalsIgnoreCase("all")) {
            if(!getPlugin().reload())
                player.sendMessage("[Zones]Error while reloading, please contact an server admin.");
            else
                player.sendMessage("[Zones]Revision " + Zones.Rev + " reloaded.");
        }
        		


        
        return true;
    }

}
