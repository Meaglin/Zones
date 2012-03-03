package com.zones.command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zones.Region;
import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneForm;
import com.zones.model.settings.Serializer;
import com.zones.model.settings.ZoneVar;
import com.zones.model.types.ZoneInherit;

public class GeneralCommands extends CommandsBase {

    public GeneralCommands(Zones plugin) {
        super(plugin);
    }

    @Command(
            name = "zinfo",
            aliases = { "" },
            description = "Show the type,size,etc... of your selected zone.",
            requiresPlayer = true,
            requiresSelected = true
    )
    public void info(Player player, String[] params) {
        ZoneBase b = getSelectedZone(player);
        player.sendMessage(ChatColor.DARK_GREEN + "Zone: " + b.getName() + ChatColor.BLUE + "(" + b.getId() + ")" + ChatColor.WHITE + "[" + b.getAccess(player).toColorCode() + "]" );
        player.sendMessage(ChatColor.AQUA + "World: " + b.getWorld().getName());
        ZoneForm f = b.getForm();
        player.sendMessage(ChatColor.AQUA + "Type: " + getClassName(b.getClass()) + " Form: " + getClassName(f.getClass()));
        player.sendMessage(ChatColor.AQUA + "Size: " + f.getSize() + " (X:" + Math.abs(f.getHighX()-f.getLowX()) + ", Y:" + Math.abs(f.getHighY()-f.getLowY()) + ", Z:" + Math.abs(f.getHighZ()-f.getLowZ()) + ")" );
        player.sendMessage(ChatColor.AQUA + "Location: (X:" + f.getLowX() + "," + f.getHighX() + "; Y:" + f.getLowY() + "," + f.getHighY() + "; Z:" + f.getLowZ() + "," + f.getHighZ() + ")");
        
        String bools = "";
        for(ZoneVar v : ZoneVar.values()) {
            if(v.getType().equals(Boolean.class)) {
                if(b.getSettings().get(v) != null) {
                    bools += " " + (b.getSettings().getBool(v) ? ChatColor.GREEN : ChatColor.RED) + v.getName();
                }
            }
        }
        if(!bools.equals(""))
            player.sendMessage(ChatColor.AQUA + "Booleans:" + bools);
        
        String settings = "";
        for(ZoneVar v : ZoneVar.values()) {
            if(!v.getType().equals(Boolean.class)) {
                if(b.getSettings().get(v) != null) {
                    settings += " " + ChatColor.BLUE + v.getName() + ":" + ChatColor.WHITE + Serializer.unEscape(v.serialize(b.getSettings().get(v)));
                }
            }
        }
        if(!settings.equals("")) 
            player.sendMessage(ChatColor.AQUA + "Settings:" + settings);
        Region min = b.getWorldManager().getRegion(b.getForm().getLowX(),b.getForm().getLowZ());
        Region max = b.getWorldManager().getRegion(b.getForm().getHighX(),b.getForm().getHighZ());
        player.sendMessage(ChatColor.AQUA + "Region: " +  "(X:" + min.getX() + "," + max.getX() + "; Y:" + min.getY() + "," + max.getY() + ")" );
        if(b instanceof ZoneInherit) {
            List<ZoneBase> inherits = ((ZoneInherit)b).getInheritedZones();
            if(inherits.size() > 0) {
                String message = "";
                for(ZoneBase zone : inherits) {
                    message += ", " + zone.getName() + "[" + zone.getId() + "]";
                }
                player.sendMessage(ChatColor.AQUA + "InheritedZones: " + message.substring(2));
            } else {
                player.sendMessage(ChatColor.AQUA + "InheritedZones: None.");
            }
            List<ZoneBase> subs = ((ZoneInherit)b).getSubZones();
            if(subs.size() > 0) {
                String message = "";
                for(ZoneBase zone : subs) {
                    message += ", " + zone.getName() + "[" + zone.getId() + "]";
                }
                player.sendMessage(ChatColor.AQUA + "SubZones: " + message.substring(2));
            } else {
                player.sendMessage(ChatColor.AQUA + "SubZones: None.");
            }
        }
    }
    public static String getClassName(Class<?> c) {
        String className = c.getName();
        int firstChar;
        firstChar = className.lastIndexOf ('.') + 1;
        if ( firstChar > 0 ) {
            className = className.substring ( firstChar );
        }
        return className;
    }
    
    @Command(
            name = "zselect",
            aliases = { "zs" },
            description = "Select a zone to modify.\nReset your selected zone with 'reset'.",
            usage = "/<command> <zone id|zone name|reset>",
            requiresPlayer = true
    )
    public void select(Player player, String[] params) {
        if(params.length == 1){
            if(params[0].equalsIgnoreCase("reset")) {
                getPlugin().getZoneManager().removeSelected(player.getEntityId());
                player.sendMessage(ChatColor.GREEN + "Zone deselected.");
                return;
            }
            List<ZoneBase> zoneslist = getPlugin().getZoneManager().matchZone(player, params[0]);
            if(zoneslist.size() < 1)
                player.sendMessage(ChatColor.YELLOW + "No zones found with key '" + params[0] + "'(which you can modify).");
            else if(zoneslist.size() == 1){
                getPlugin().getZoneManager().setSelected(player.getEntityId(), zoneslist.get(0).getId());
                player.sendMessage(ChatColor.GREEN.toString() + "Selected zone '" + zoneslist.get(0).getName() + "' .");
            } else {
                player.sendMessage(ChatColor.YELLOW +  "Too many zones found, please be more specific.");
                String temp = "";
                int delta = Integer.MAX_VALUE;
                ZoneBase closest = null;
                for (ZoneBase zone : zoneslist) {
                    if(closest == null || Math.abs(closest.getName().length()-params[0].length()) < delta) {
                        closest = zone;
                        delta = Math.abs(closest.getName().length()-params[0].length());
                    }
                    temp += zone.getName() + "[" + zone.getId() + "]";
                }
                player.sendMessage(ChatColor.DARK_GREEN + "Zones found: " + temp);
                player.sendMessage(ChatColor.GOLD + "Selected closest match '" + closest.getName() +"' .");
                getPlugin().getZoneManager().setSelected(player.getEntityId(), closest.getId());
            }
        }else{
            List<ZoneBase> zoneslist = getPlugin().getWorldManager(player).getAdminZones(player);
            if(zoneslist.size() < 1) {
                player.sendMessage(ChatColor.YELLOW + "No zones found in your current area (which you can modify).");
                player.sendMessage(ChatColor.YELLOW + "Please select a zone by specifying a zone id.");
            } else if(zoneslist.size() == 1){
                getPlugin().getZoneManager().setSelected(player.getEntityId(), zoneslist.get(0).getId());
                player.sendMessage(ChatColor.GREEN + "Selected zone '" + zoneslist.get(0).getName() + "' .");
            } else {
                String temp = "";
                ZoneBase smallest = null;
                for (ZoneBase zone : zoneslist) {
                    if(smallest == null || zone.getForm().getSize() < smallest.getForm().getSize())
                        smallest = zone;
                    temp += "," + zone.getName() + "[" + zone.getId() + "]";
                }
                if(!temp.equals("")) temp = temp.substring(1);
                player.sendMessage(ChatColor.DARK_GREEN + "Zones found: " + temp);
                player.sendMessage(ChatColor.GOLD + "Selected smallest '" + smallest.getName() +"' .");
                getPlugin().getZoneManager().setSelected(player.getEntityId(), smallest.getId());
            }
        }
    }
    
    @Command(
            name = "zwho",
            aliases = { "" },
            description = "Shows all the players in the zone you're standing in.\nShows all players in selected zone when you have\n a zone selected.",
            requiresPlayer = true
    )
    public void who(Player player, String[] params) {
        if(hasSelected(player)) {
            sendZone(player,getSelectedZone(player), null);
        } else {      
            Set<ZoneBase> sorted = new TreeSet<ZoneBase>(new Comparator<ZoneBase>() {
                @Override
                public int compare(ZoneBase o1, ZoneBase o2) {
                    if(o1.getForm().getSize() > o2.getForm().getSize())
                        return 1;
                    else if(o2.getForm().getSize() > o1.getForm().getSize())
                        return -1;
                    else
                        return 0;
                }
                
            });
            sorted.addAll(getPlugin().getWorldManager(player).getActiveZones(player));
            if(sorted.size() > 0) {
                Set<String> usedNames = new HashSet<String>();
                for(ZoneBase zone : sorted){
                    sendZone(player, zone, usedNames);
                }
            } else {
                player.sendMessage(ChatColor.GREEN + "No zones found.");
            }
        }
    }
    
    private void sendZone(Player player, ZoneBase zone, Set<String> usedNames) {
        String msg = "";
        for(Player insidePlayer : zone.getPlayersInside()) {
            if(player.getEntityId() != insidePlayer.getEntityId() && (usedNames == null || !usedNames.contains(insidePlayer.getName()))) {
                msg += ", " + insidePlayer.getDisplayName();
                if(usedNames != null) usedNames.add(insidePlayer.getName());
            }
        }
        if(!msg.equals("")) {
            player.sendMessage(ChatColor.DARK_GREEN + "Players inside zone " + ChatColor.GREEN + zone.getName() + ChatColor.DARK_GREEN + ":");
            player.sendMessage(msg.substring(2));
        } else {
            player.sendMessage(ChatColor.DARK_GREEN + "Players inside zone " + zone.getName() + ":");
            player.sendMessage("None.");
        }
    }
    
    @Command(
            name = "zregioninfo",
            aliases = { "" },
            description = "Shows region info, mostly for debug purposes.",
            requiresPlayer = true
    )
    public void regioninfo(Player player, String[] params) {
        Region r = getPlugin().getWorldManager(player).getRegion(player);
        player.sendMessage(ChatColor.GREEN + "Region[X: " + r.getX() + ", Y: " + r.getY() + "] Zone count: " + r.getZones().size() + ".");
        player.sendMessage(ChatColor.GREEN + "Calculated region: [" + (WorldManager.toInt(player.getLocation().getX()) >> WorldManager.SHIFT_SIZE) + "," + (WorldManager.toInt(player.getLocation().getZ()) >> WorldManager.SHIFT_SIZE) +  "].");
    }
    
    @Command(
            name = "zaccess",
            aliases = { "" },
            description = "Displays info about all the access 'tags'(BCDEH)"
    )
    public void access(CommandSender sender, String[] params) {
        sender.sendMessage(ChatColor.GREEN + "Zone Access tags explained:");
        sender.sendMessage(ChatColor.GREEN + "b = Build(placing blocks),");
        sender.sendMessage(ChatColor.GREEN + "c = Chest Access(accessing chest/furnaces/note blocks),");
        sender.sendMessage(ChatColor.GREEN + "d = Destroy(destroying blocks),");
        sender.sendMessage(ChatColor.GREEN + "e = Enter(entering zone), ");
        sender.sendMessage(ChatColor.GREEN + "h = Hit(killing mobs,minecarts or boats/modify redstone).");
    }
    
    @Command(
            name = "zhelp",
            aliases = { "" },
            description = "Shows help about the specified <page|command|category> ",
            usage = "/<command> <page|command|category> "
    )
    public void help(CommandSender sender, String[] params) {
        CommandMap cmds = getPlugin().getCommandMap();
        int ITEMS_PER_PAGE = 8;
        
        List<String> availableCommands = new ArrayList<String>();
        String group = null;

        /*
         * Check if group is entered
         */
        if (params.length > 0) 
            for (String category : cmds.getHelpCategories().keySet()) 
                if (params[0].compareToIgnoreCase(category) == 0) {
                    group = category;
                }

        /*
         * CASE 1: Group is entered with or without pagenumbers
         */
        if (group != null) {
            group = group.toLowerCase();
            for (Command cmd : cmds.getHelpCategories().get(group)) {
                if ((cmd.requiredPermission().equals("") || canUseCommand(sender, cmd.requiredPermission())) ) {
                    availableCommands.add(ChatColor.BLUE + cmd.usage().replace("<command>", cmd.name()));
                }
            }
            
            int amount = 0;           
            if (params.length > 1) {
                try {
                    amount = Integer.parseInt(params[1]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.RED + "Not a valid page number.");
                }
                if (amount > 1) {
                    amount = (amount - 1) * ITEMS_PER_PAGE;
                } else {
                    amount = 0;
                }
            }
            sender.sendMessage(ChatColor.BLUE + "--------" + ChatColor.WHITE + " Zone " + group +" commands (Page " + (params.length == 2 ? params[1] : "1") + " of " + (int) Math.ceil((double) availableCommands.size() / (double) ITEMS_PER_PAGE) + ") " + ChatColor.BLUE + "--------");
            sender.sendMessage(ChatColor.BLUE + "[] = required <> = optional:");
            sender.sendMessage(ChatColor.BLUE + "For more info: /zhelp <command name>");
            for (int i = amount; i < amount + ITEMS_PER_PAGE; i++) {
                if (availableCommands.size() > i) {
                    sender.sendMessage(availableCommands.get(i));
                }
            }
            return;
        }
        
        /*
         * CASE 2: Help for a specific command
         */
        if (params.length > 0) {
            String cmd = params[0].toLowerCase();
            if(cmd.length() > 0 && cmd.charAt(0) == '/') cmd = cmd.substring(1);
            if(cmd.length() > 0 && cmd.charAt(0) != 'z') cmd = "z" + cmd;
            
            if(cmds.getCommands().containsKey(cmd) && (cmds.getCommands().get(cmd).requiredPermission().equals("") || canUseCommand(sender, cmds.getCommands().get(cmd).requiredPermission()))) {
        
                sender.sendMessage(ChatColor.BLUE + "Description of /" + cmd + " :");
                for (String part : cmds.getCommands().get(cmd).description().split("\n")) {
                    sender.sendMessage(ChatColor.AQUA + part);
                }
        
                return;
            }
        }
        
        /*
         * CASE 3: there are no options or invalid options, show default menu
         */
        sender.sendMessage(ChatColor.BLUE + "---------------" + ChatColor.WHITE + " Zone Commands " + ChatColor.BLUE + "---------------");
        for (String category : cmds.getHelpCategories().keySet()) {
            sender.sendMessage(ChatColor.BLUE + "/zhelp " + category + ChatColor.WHITE + " - " + category + " commands.");
        }
    }

    @Command(
            name = "zabout",
            aliases = { "" },
            description = "Shows current zone version and total count of zones."
    )
    public void about(CommandSender sender, String[] params) {
        sender.sendMessage(ChatColor.GOLD + "Zones Area Protection plugin by Meaglin.");
        sender.sendMessage(ChatColor.GREEN + "Bukkit version: " + getPlugin().getDescription().getVersion());
        sender.sendMessage(ChatColor.GREEN + "Revision: " + Zones.Rev);
        sender.sendMessage(ChatColor.GREEN + "Loaded zones count: " + getPlugin().getZoneManager().getZoneCount());
    }
}
