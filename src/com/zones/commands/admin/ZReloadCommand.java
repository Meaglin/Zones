package com.zones.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;

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
    public void run(Player player, String[] vars) {
        runConsole(player, vars);
    }
    
    @Override
    public void runConsole(CommandSender sender, String[] vars) {

        if(vars.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /zreload config|zones|all|zone <zone id>");
            return;
        }
        String type = vars[0];
        if(type.equalsIgnoreCase("config")) {
            if(!getPlugin().reloadConfig())
                sender.sendMessage("[Zones] Error while reloading config, please contact an server admin.");
            else
                sender.sendMessage("[Zones] Config reloaded.");
        } else if (type.equalsIgnoreCase("zones")) {
            if(!getPlugin().reloadZones())
                sender.sendMessage("[Zones] Error while reloading zones, please contact an server admin.");
            else
                sender.sendMessage("[Zones] Zones reloaded.");
        } else if (type.equalsIgnoreCase("all")) {
            if(!getPlugin().reload())
                sender.sendMessage("[Zones] Error while reloading, please contact an server admin.");
            else
                sender.sendMessage("[Zones ]Revision " + Zones.Rev + " reloaded.");
        } else if (type.equalsIgnoreCase("zone")) {
            if(vars.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /zreload zone [zone id]");
                return;
            }
            int id = -1;
            try { id = Integer.parseInt(vars[1]); } catch(NumberFormatException e) { }
            if(id == -1) {
                sender.sendMessage(ChatColor.RED + "'" + vars[1] + "' is not a valid zone id.");
                return;
            }
            ZoneBase base = getZoneManager().getZone(id);
            if(base == null) {
                sender.sendMessage(ChatColor.RED + "No zone found with id " + id + ".");
                return;
            }
            getZoneManager().reloadZone(id);
            sender.sendMessage(ChatColor.GREEN + "Zone " + getZoneManager().getZone(id).getName() + " reloaded.");
        }
    }

}
