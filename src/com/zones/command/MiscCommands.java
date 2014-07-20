package com.zones.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.settings.ZoneVar;
import com.zones.model.types.ZoneNormal;
import com.zones.model.types.ZonePlot;
import com.zones.test.TestManager;

public class MiscCommands extends CommandsBase {

    public MiscCommands(Zones plugin) {
        super(plugin);
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
            ZoneNormal base = getPlugin().getZoneManager().getZone(id);
            if(base == null) {
                sender.sendMessage(ChatColor.RED + "No zone found with id " + id + ".");
                return;
            }
            getPlugin().getZoneManager().reloadZone(id);
            sender.sendMessage(ChatColor.GREEN + "Zone " + getPlugin().getZoneManager().getZone(id).getName() + " reloaded.");
        }
    }
    
    @Command(
        name = "zrefresh",
        description = "Refreshes the currently selected zone.",
        requiresPlayer = true,
        requiresSelected = true
    )
    public void refresh(Player player, String[] params) {
        ZoneNormal zone = getSelectedZone(player);
        getPlugin().getZoneManager().reloadZone(zone.getId());
        player.sendMessage(ChatColor.GREEN + "Zone reloaded.");
    }
    
    
    @Command(
       name = "ztest",
       description = "The command where i test stuff.",
       requiredPermission = "zones.admin.test"
    )
    public void test(CommandSender sender, String[] params) throws Exception {
        
        TestManager test = new TestManager(getPlugin(), sender);
        test.run();
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
        OfflinePlayer player = getPlugin().matchPlayer(name);
        if(player == null) {
            sender.sendMessage(ChatColor.YELLOW + "Cannot find player " + name);
            return;
        }
        Collection<ZoneNormal> zones = getPlugin().getZoneManager().getAllZones();
        List<ZoneNormal> list = new ArrayList<ZoneNormal>();
        for(ZoneNormal zone : zones) {
            if(!(zone instanceof ZoneNormal)) {
                continue;
            }
            if(zone.isAdminUser(player)) {
                list.add(zone);
            }
        }
        sender.sendMessage(ChatColor.BLUE + name + ChatColor.WHITE + " has " + list.size() + " zones:");
        String message = "";
        for(ZoneNormal zone : list) {
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
        ZoneNormal zone = getPlugin().getWorldManager(player).getActiveZone(player);
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
        ZoneNormal zone = getPlugin().getWorldManager(player).getActiveZone(player);
        if(!(zone instanceof ZonePlot)) {
            zone.sendMarkupMessage(ChatColor.RED + "You cannot unclaim {zname} since it's not a claimable zone.", player);
            return;
        }
        ((ZonePlot)zone).unclaim(player);
    }
    
    @Command(
        name = "zbuy",
        aliases = { "" },
        description = "Buy the zone you're currently standing in.",
        requiresPlayer = true,
        requiredPermission = "zones.buy"
    )
    public void buy(Player player, String[] vars) {
        ZoneNormal zone = getPlugin().getWorldManager(player).getActiveZone(player);
        if(!zone.getFlag(ZoneVar.BUY_ALLOWED)) {
            zone.sendMarkupMessage(ChatColor.RED + "You {zname} is not buyable.", player);
            return;
        }
        Economy eco = getPlugin().getEconomy();
        if(eco == null) {
            player.sendMessage(ChatColor.RED + "Economy not enabled");
            return;
        }
        if(!eco.has(player, zone.getSettings().getDouble(ZoneVar.BUY_PRICE.getName()))) {
            zone.sendMarkupMessage(ChatColor.RED + "You need " + zone.getSettings().getDouble(ZoneVar.BUY_PRICE.getName()) + " to buy {zname}.", player);
            return;
        }
        eco.withdrawPlayer(player, zone.getSettings().getDouble(ZoneVar.BUY_PRICE.getName()));
        zone.setAdmin(player, true);
    }
}
