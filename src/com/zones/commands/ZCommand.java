package com.zones.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.Selection;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneForm;
import com.zones.model.ZoneVertice;
import com.zones.model.ZonesAccess;
import com.zones.model.types.ZoneInherit;
import com.zones.model.types.ZoneNormal;
import com.zones.selection.CuboidSelection;
import com.zones.selection.ZoneCreateSelection;
import com.zones.selection.ZoneEditSelection;
import com.zones.selection.ZoneSelection;
import com.zones.util.Log;

public class ZCommand extends ZoneCommand {

    public enum Action {
        
        DEFINE("<admin1> <admin2> ...",0,false, "D") {
            @Override
            public void onCommand(ZoneCommand command, ZoneBase unused, CommandSender sender, String[] arguments) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You need to be ingame to make zones!");
                    return;
                }
                Player player = (Player)sender;
                ZoneBase inheritedZone = null;
                if(!command.canUseCommand(sender,"zones.create")) {
                    if(command.hasSelected(player)) {
                        inheritedZone = command.getSelectedZone(player);
                        if(!(inheritedZone instanceof ZoneInherit)) {
                            sender.sendMessage(ChatColor.RED + "This zone doesn't allow subzoning.");
                            return;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to make global zones.");
                        return;
                    }
                }
                
                if(!ZonesConfig.WORLDEDIT_ENABLED || command.getPlugin().getWorldEdit() == null) {
                    player.sendMessage(ChatColor.RED + "WorldEdit support needs to be enabled!");
                    return;
                }
                if(arguments.length < 1) {
                    player.sendMessage(ChatColor.YELLOW + "Usage: /zdefine [zone name]");
                    return ;
                }
                String name = arguments[0];
                if(name.length() < 4)
                {
                    player.sendMessage(ChatColor.RED.toString() + "Too short zone name.");
                    return;
                }
                Selection worldeditSelection = command.getPlugin().getWorldEdit().getSelection(player);
                if(worldeditSelection == null) {
                    player.sendMessage(ChatColor.RED + "No WorldEdit selection found.");
                    return;
                }
                if(worldeditSelection.getArea() < 1) {
                    player.sendMessage(ChatColor.RED + "Your WorldEdit selection is not a valid selection.");
                    return;
                }
                ZoneVertice point1 = new ZoneVertice(worldeditSelection.getMinimumPoint().getBlockX(), worldeditSelection.getMinimumPoint().getBlockZ());
                ZoneVertice point2 = new ZoneVertice(worldeditSelection.getMaximumPoint().getBlockX(), worldeditSelection.getMaximumPoint().getBlockZ());
                ZoneVertice height = new ZoneVertice(worldeditSelection.getMinimumPoint().getBlockY(), (worldeditSelection.getMaximumPoint().getBlockY() >= 127 ? 130 : worldeditSelection.getMaximumPoint().getBlockY()));
                if(inheritedZone != null){
                    ZoneForm form = inheritedZone.getForm();
                    if (    form.getLowZ() > height.getMin() || 
                            form.getHighZ() < height.getMax() ||
                            !form.isInsideZone(point1.getX(), point1.getY()) ||
                            !form.isInsideZone(point2.getX(), point2.getY()) ) {
                        player.sendMessage(ChatColor.RED + "Your selection is not inside your selected zone, zone cannot be created.");
                        return;
                    }
                }
                
                ZoneSelection selection = new ZoneCreateSelection(command.getPlugin(),player,name);
                CuboidSelection sel = new CuboidSelection(selection);

                sel.setHeight(height, true);
                sel.setPoint1(point1);
                sel.setPoint2(point2);
                selection.setSelection(sel);
                ZoneBase zone = selection.save();
                if(zone != null) {
                    player.sendMessage(ChatColor.GREEN + "Zone '" + name + "' saved.");
                    Log.info(player.getName() + " created zone " + zone.getName() + "[" + zone.getId() + "]");
                } else {
                    player.sendMessage(ChatColor.RED + "Error saving zone.");
                }
            }
        },
        REDEFINE("", "RD") {
            @Override
            public void onCommand(ZoneCommand command, ZoneBase zone, CommandSender sender, String[] arguments) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You need to be ingame to make zones!");
                    return;
                }
                Player player = (Player)sender;
                
                if(!command.canUseCommand(player, "zones.create")) {
                    player.sendMessage(ChatColor.RED + "You're not allowed to use this command.");
                    return;
                }
                
                if(!ZonesConfig.WORLDEDIT_ENABLED || command.getPlugin().getWorldEdit() == null) {
                    player.sendMessage(ChatColor.RED + "WorldEdit support needs to be enabled!");
                    return;
                }
                Selection worldeditSelection = command.getPlugin().getWorldEdit().getSelection(player);
                if(worldeditSelection == null) {
                    player.sendMessage(ChatColor.RED + "No WorldEdit selection found.");
                    return;
                }
                if(worldeditSelection.getArea() < 1) {
                    player.sendMessage(ChatColor.RED + "Your WorldEdit selection is not a valid selection.");
                    return;
                }
                ZoneVertice point1 = new ZoneVertice(worldeditSelection.getMinimumPoint().getBlockX(), worldeditSelection.getMinimumPoint().getBlockZ());
                ZoneVertice point2 = new ZoneVertice(worldeditSelection.getMaximumPoint().getBlockX(), worldeditSelection.getMaximumPoint().getBlockZ());
                ZoneVertice height = new ZoneVertice(worldeditSelection.getMinimumPoint().getBlockY(), (worldeditSelection.getMaximumPoint().getBlockY() >= 127 ? 130 : worldeditSelection.getMaximumPoint().getBlockY()));

                ZoneSelection selection = new ZoneEditSelection(command.getPlugin(),player,zone.getName());
                CuboidSelection sel = new CuboidSelection(selection);

                sel.setHeight(height, true);
                sel.setPoint1(point1);
                sel.setPoint2(point2);
                selection.setSelection(sel);
                ZoneBase save = selection.save();
                if(save != null) {
                    player.sendMessage(ChatColor.GREEN + "Zone '" + save.getName() + "' redefined.");
                    Log.info(player.getName() + " resized zone " + save.getName() + "[" + save.getId() + "]");
                } else {
                    player.sendMessage(ChatColor.RED + "Error saving zone.");
                }
            }
        },
        ADDUSER("[user1] <user2> ...",1,"AU") {
            @Override
            public void onCommand(ZoneCommand command, ZoneBase zone, CommandSender sender, String[] arguments) {
                
                if(!(zone instanceof ZoneNormal)) {
                    sender.sendMessage(ChatColor.RED + "You're zone doesn't allow this command.");
                    return;
                }
                for(int i = 0;i < arguments.length;i++) {
                    String username = arguments[i];
                    
                    if(username == null || username.trim().equals(""))
                        continue;
                
                    ZonesAccess z = new ZonesAccess("*");
                    // This is fine since it finds the closest match.
                    Player p = command.getPlugin().getServer().getPlayer(username);

                    if(p != null)
                        username = p.getName();
                    ((ZoneNormal)zone).addUser(username,z);

                    sender.sendMessage(ChatColor.GREEN.toString() + "Succesfully changed access of user " + username + " of zone '" + zone.getName() + "' to access " + z.textual() + " .");
                }
            }
        },
        REMOVEUSER("[user1] <user2> ...",1,"RU") {
            @Override
            public void onCommand(ZoneCommand command, ZoneBase zone, CommandSender sender, String[] arguments) {
                if(!(zone instanceof ZoneNormal)) {
                    sender.sendMessage(ChatColor.RED + "You're zone doesn't allow this command.");
                    return;
                }
                for(int i = 0;i < arguments.length;i++) {
                    String username = arguments[i];
                    
                    if(username == null || username.trim().equals(""))
                        continue;
                    
                    ZonesAccess z = new ZonesAccess("-");
                    // This is fine since it finds the closest match.
                    Player p = command.getPlugin().getServer().getPlayer(username);

                    if(p != null)
                        username = p.getName();
                    ((ZoneNormal)zone).addUser(username,z);

                    sender.sendMessage(ChatColor.GREEN.toString() + "Succesfully changed access of user " + username + " of zone '" + zone.getName() + "' to access " + z.textual() + " .");
                }
            }
        },
        ADDADMIN("[admin1] <admin2> ...",1,"AA") {
            @Override
            public void onCommand(ZoneCommand command, ZoneBase zone, CommandSender sender, String[] arguments) {
                if(!(zone instanceof ZoneNormal)) {
                    sender.sendMessage(ChatColor.RED + "You're zone doesn't allow this command.");
                    return;
                }
                for(int i = 0;i < arguments.length;i++) {
                    String username = arguments[i];
                    if(username == null || username.trim().equals(""))
                        continue;;
                    
                    // This is fine since it finds the closest match.
                    Player p = command.getPlugin().getServer().getPlayer(username);
                    
                    if(p != null)
                        username = p.getName();
                    
                    ((ZoneNormal)zone).addAdmin(username);
                    sender.sendMessage(ChatColor.GREEN + "Succesfully added player " + username + " as an admin of zone "  + zone.getName() +  " .");
                }
            }
        },
        REMOVEADMIN("[admin1] <admin2> ...",1,"RA") {
            @Override
            public void onCommand(ZoneCommand command, ZoneBase zone, CommandSender sender, String[] arguments) {
                if(!(zone instanceof ZoneNormal)) {
                    sender.sendMessage(ChatColor.RED + "You're zone doesn't allow this command.");
                    return;
                }
                
                if(zone instanceof ZoneInherit && sender instanceof Player  && !((ZoneInherit)zone).isInheritAdmin((Player)sender)) {
                    if(!command.canUseCommand(sender,"zones.admin")) {
                        sender.sendMessage(ChatColor.RED + "You're not allowed to remove admin's in this zone.");
                        return;
                    }
                } else if(!command.canUseCommand(sender,"zones.admin")) {
                    sender.sendMessage(ChatColor.RED + "You're not allowed to remove admin's from zones.");
                    return;
                }
                
                for(int i = 0;i < arguments.length;i++) {
                    String username = arguments[i];
                    if(username == null || username.trim().equals(""))
                        continue;;
                
                    // This is fine since it finds the closest match.
                    Player p = command.getPlugin().getServer().getPlayer(username);
    
                    if(p != null)
                        username = p.getName();
                    
                    ((ZoneNormal)zone).removeAdmin(username);
                    sender.sendMessage(ChatColor.GREEN + "Succesfully removed player " + username + " as an admin of zone "  + zone.getName() +  " .");
                }
            }
        },
        DELETE("confirm",1,"del") {

            @Override
            public void onCommand(ZoneCommand command, ZoneBase zone, CommandSender sender, String[] arguments) {
                
                if(!command.canUseCommand(sender, "zones.admin")) {
                    sender.sendMessage(ChatColor.RED + "You're not allowed to use this command.");
                    return;
                }
                
                if(command.getZoneManager().delete(zone)) {
                    sender.sendMessage(ChatColor.GREEN.toString() + "Succesfully deleted zone " + zone.getName() + ".");
                    Log.info(sender.toString() + " delete zone " + zone.getName() + "[" + zone.getId() + "].");
                } else
                    sender.sendMessage(ChatColor.RED.toString() + "Problems while deleting zone, please contact admin.");
            }
            
        };
        
        private final String[] aliases;
        private final String usage;
        private final int requiredcount;
        private final boolean zonerequired;
        private Action(String usage, String... aliases) {
            this(usage, 0, aliases);
        }
        private Action(String usage,int requiredcount, String... aliases) {
            this(usage, requiredcount, true, aliases);
        }
        private Action(String usage, int requiredcount, boolean zonerequired, String... aliases) {
            if(aliases == null)
                aliases = new String[] { name().toLowerCase() };
            else 
                aliases[aliases.length] = name().toLowerCase();
            this.aliases = aliases;
            this.usage = usage;
            this.requiredcount = requiredcount;
            this.zonerequired = zonerequired;
        }
        public abstract void onCommand(ZoneCommand command, ZoneBase zone, CommandSender sender, String[] arguments); 
        public String[] getAliases() {
            return aliases;
        }
        public String getUsage() {
            return "/z " + name().toLowerCase() + " [zone name] " + usage;
        }
        public int getRequiredCount() {
            return requiredcount;
        }
        public boolean zoneRequired() {
            return zonerequired;
        }
        
        public static Action fromName(String name) {
            return map.get(name);
        }
        private static final Map<String, Action> map;
        static {
            map = new HashMap<String, Action>();
            for(Action a : values()) {
                for(String s : a.getAliases())
                    map.put(s, a);
            }
        }
        
    }
    
    public ZCommand(Zones plugin) {
        super("z", plugin);
        this.setUsage("/z define|redefine|adduser|removeuser|addadmin|removeadmin|delete [zone name] ...");
    }
    
    @Override
    public void run(Player sender, String[] vars) {
        run(sender, vars);
    }
    
    @Override
    public void runConsole(CommandSender sender, String[] vars) {
        run(sender, vars);
    }
    
    public void run(CommandSender sender, String[] vars) {
        if(vars.length < 1) {
            sender.sendMessage(ChatColor.RED + getUsage());
            return;
        }
        String actionname = vars[0];
        Action action = Action.fromName(actionname);
        if(action == null) {
            sender.sendMessage(ChatColor.RED + "Invalid action specified.");
            sender.sendMessage(ChatColor.RED + getUsage());
            return;
        }
        if(action.requiredcount + 2 < vars.length) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments.");
            sender.sendMessage(ChatColor.RED + action.getUsage());
            return;
        }
        
        if(vars.length < 2) {
            sender.sendMessage(ChatColor.RED + "No zone name specified.");
            sender.sendMessage(ChatColor.RED + getUsage());            
            return;
        }
        String zonename = vars[1];
        ZoneBase zone = null;
        if(action.zoneRequired()) {
            List<ZoneBase> zoneslist = (sender instanceof Player ? getZoneManager().matchZone(((Player)sender), zonename) : getZoneManager().matchZone(zonename));
            if(zoneslist.size() < 1) {
                sender.sendMessage(ChatColor.YELLOW + "No zones found with key '" + zonename + "'(which you can modify).");
                return;
            } else if(zoneslist.size() > 1) {
                sender.sendMessage(ChatColor.YELLOW +  "Too many zones found, please be more specific.");
                String temp = "";
                for (ZoneBase z : zoneslist) {
                    temp += z.getName() + "[" + z.getId() + "]";
                }
                sender.sendMessage(ChatColor.DARK_GREEN + "Zones found: " + temp);
                return;
            }
            zone = zoneslist.get(0);
            sender.sendMessage(ChatColor.GREEN + "Using zone '" + zone.getName() + "' .");
        }
        String[] arguments = Arrays.copyOfRange(vars, action.zoneRequired() ? 2 : 1, vars.length);
        action.onCommand(this, zone, sender, arguments);
    }

}
