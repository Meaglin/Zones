package com.zones.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.WorldConfig;
import com.zones.model.ZoneBase;

public class MiscCommands extends CommandsBase {

    public MiscCommands(Zones plugin) {
        super(plugin);
    }
    
    @Command(
            name = "god",
            aliases = { "" },
            description = "Toggles you're godmode.",
            requiresPlayer = true,
            requiredPermission = "zones.god"
    )
    public void god(Player player, String[] params) {
        WorldConfig config = getPlugin().getWorldManager(player).getConfig();
        if(!config.GOD_MODE_ENABLED) {
            player.sendMessage(ChatColor.RED + "Godmode is not available here.");
            return;
        }
        config.setGodMode(player, !config.hasGodMode(player));
        player.sendMessage(ChatColor.GREEN + "Godmode is now " + (config.hasGodMode(player) ? "enabled" : "disabled") + ".");
    }
    
    @Command(
            name = "ungod",
            aliases = { "" },
            description = "Disables you're godmode.",
            requiresPlayer = true,
            requiredPermission = "zones.god"
    )
    public void ungod(Player player, String[] params) {
        WorldConfig config = getPlugin().getWorldManager(player).getConfig();
        if(!config.GOD_MODE_ENABLED) {
            player.sendMessage(ChatColor.RED + "Godmode is not available here.");
            return;
        }
        config.setGodMode(player, false);
        player.sendMessage(ChatColor.GREEN + "Godmode is now disabled.");
    }
    
    @Command(
            name = "zreload",
            aliases = { "" },
            description = "Reloads specified part of the plugin\nWhen using 'zone' the zone with id <zone id> will \nbe reloaded from db.",
            usage = "/<command> [config|zones|all|zone] <zone id>",
            min = 1,
            requiredPermission = "zones.admin"
    )
    public void reload(CommandSender sender, String[] params) {
        String type = params[0];
        if(type.equalsIgnoreCase("config")) {
            if(!getPlugin().reloadZonesConfig())
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
            if(params.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /zreload zone [zone id]");
                return;
            }
            int id = -1;
            try { id = Integer.parseInt(params[1]); } catch(NumberFormatException e) { }
            if(id == -1) {
                sender.sendMessage(ChatColor.RED + "'" + params[1] + "' is not a valid zone id.");
                return;
            }
            ZoneBase base = getPlugin().getZoneManager().getZone(id);
            if(base == null) {
                sender.sendMessage(ChatColor.RED + "No zone found with id " + id + ".");
                return;
            }
            getPlugin().getZoneManager().reloadZone(id);
            sender.sendMessage(ChatColor.GREEN + "Zone " + getPlugin().getZoneManager().getZone(id).getName() + " reloaded.");
        }
    }
    /*
     * @Command(
                name = "",
                aliases = { "" },
                description = "",
                usage = "",
                requiresPlayer = true
        )
        public void (Player player, String[] params) {
            
        }
     */
    
    @Command(
            name = "zrefresh",
            aliases = { "" },
            description = "Refreshes the currently selected zone.",
            requiresPlayer = true,
            requiresSelected = true
    )
    public void refresh(Player player, String[] params) {
        ZoneBase zone = getSelectedZone(player);
        getPlugin().getZoneManager().reloadZone(zone.getId());
        player.sendMessage(ChatColor.GREEN + "Zone reloaded.");
    }
}
