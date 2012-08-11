package com.zones.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.WorldConfig;
import com.zones.model.ZoneBase;
import com.zones.model.types.ZoneNormal;
import com.zones.model.types.ZonePlot;

public class MiscCommands extends CommandsBase {

    public MiscCommands(Zones plugin) {
        super(plugin);
    }
    
    @Command(
        name = "god",
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
        description = "Reloads specified part of the plugin\nWhen using 'zone' the zone with id <zone id> will \nbe reloaded from db.",
        usage = "/<command> [config|zones|all|zone|textiel] <zone id>",
        requiredPermission = "zones.admin",
        requiresPlayer = false,
        min = 1
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
        } else if (type.equalsIgnoreCase("textiel")) {
            if(!ZonesConfig.TEXTURE_MANAGER_ENABLED) {
                sender.sendMessage(ChatColor.RED + "Textiel not enabled.");
                return;
            }
            
            getPlugin().reloadTextiel();
            sender.sendMessage(ChatColor.GREEN + "Textiel reloaded.");
        }
    }
    
    @Command(
        name = "zrefresh",
        description = "Refreshes the currently selected zone.",
        requiresPlayer = true,
        requiresSelected = true
    )
    public void refresh(Player player, String[] params) {
        ZoneBase zone = getSelectedZone(player);
        getPlugin().getZoneManager().reloadZone(zone.getId());
        player.sendMessage(ChatColor.GREEN + "Zone reloaded.");
    }
    
    
    @Command(
       name = "ztest",
       description = "The command where i test stuff.",
       requiresPlayer = true,
       requiredPermission = "zones.admin.test",
       min = 1
    )
    public void test(Player player, String[] params) {
//        String file = FileUtil.readFile(getPlugin().getClass().getResourceAsStream(params[0]));
//        player.sendMessage(file.substring(0, file.length() > 100 ? 100 : file.length()));
//        ZonesConfig.setDatabaseVersion(new File(getPlugin().getDataFolder(), ZonesConfig.ZONES_CONFIG_FILE), Integer.parseInt(params[0]));
    }
    
    @Command(
        name = "zgetzones",
        usage = "/<command> [target]",
        aliases = { "zgz" },
        description = "Displays all the zones where that player is admin in.",
        requiredPermission = "zones.admin",
        min = 1
    )
    public void getzones(CommandSender sender, String[] params) {
        String name = params[0].toLowerCase();
        Player player = getPlugin().getServer().getPlayer(name);
        if(player != null) {
            name = player.getName().toLowerCase();
        }
        Collection<ZoneBase> zones = getPlugin().getZoneManager().getAllZones();
        List<ZoneBase> list = new ArrayList<ZoneBase>();
        for(ZoneBase zone : zones) {
            if(!(zone instanceof ZoneNormal)) continue;
            if(((ZoneNormal)zone).isAdminUser(name)) list.add(zone);
        }
        sender.sendMessage(ChatColor.BLUE + name + ChatColor.WHITE + " has " + list.size() + " zones:");
        String message = "";
        for(ZoneBase zone : list) {
            message += ChatColor.BLUE + zone.getName() + ChatColor.WHITE + "[" + ChatColor.AQUA + zone.getId() + ChatColor.WHITE + "]("+GeneralCommands.getClassName(zone.getClass())+"), ";
        }
        if(message.length() >= 2) {
            message = message.substring(0, message.length() - 2);
            sender.sendMessage(message);
        }
    }
    
    
    @Command(
        name = "zclaim",
        description = "Claim the zone you're currently standing in.",
        requiresPlayer = true,
        requiredPermission = "zones.claim"
    )
    public void claim(Player player, String[] vars) {
        ZoneBase zone = getPlugin().getWorldManager(player).getActiveZone(player);
        if(!(zone instanceof ZonePlot)) {
            zone.sendMarkupMessage(ChatColor.RED + "You cannot claim {zname} since it's not a claimable zone.", player);
            return;
        }
        ((ZonePlot)zone).claim(player);
    }
    
    @Command(
        name = "zunclaim",
        aliases = { "zuc" },
        description = "UnClaim the zone you're currently standing in.",
        requiresPlayer = true,
        requiredPermission = "zones.admin"
    )
    public void unclaim(Player player, String[] vars) {
        ZoneBase zone = getPlugin().getWorldManager(player).getActiveZone(player);
        if(!(zone instanceof ZonePlot)) {
            zone.sendMarkupMessage(ChatColor.RED + "You cannot unclaim {zname} since it's not a claimable zone.", player);
            return;
        }
        ((ZonePlot)zone).unclaim(player);
    }
}
