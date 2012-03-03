package com.zones.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;

public class CommandMap {
    private Map<String, Method> aliases = new HashMap<String, Method>();
    private Map<Method, Object> owners = new HashMap<Method, Object>();
    
    private Zones plugin;
    
    private org.bukkit.command.CommandMap bukkitMap;
    private Constructor<PluginCommand> con;
    
    private Map<String, List<Command>> helpCategories;
    private Map<String, Command> commands;
    
    public CommandMap(Zones plugin) {
        this.plugin = plugin;
        load();
    }
    
    private void load() {
        bukkitIWantToRegisterCommandsDynamicly();
        helpCategories = new HashMap<String, List<Command>>();
        commands = new HashMap<String, Command>();
        
        register(new AdminCommands(plugin));
        register(new CreateCommands(plugin));
        register(new GeneralCommands(plugin));
        register(new MiscCommands(plugin));
        register(new SettingsCommands(plugin));
        
        if(ZonesConfig.WORLDEDIT_ENABLED) register(new WorldeditCommands(plugin));
    }
    
    public void register(Object object) {
        for(Method method : object.getClass().getMethods()) {
            if(method.isAnnotationPresent(Command.class)) {
                Command annotation = method.getAnnotation(Command.class);
                for(String alias : annotation.aliases()) {
                    aliases.put(alias, method);
                }
                aliases.put(annotation.name(), method);
                owners.put(method, object);
                
                registerCommandWithBukkitBecauseIFuckingWantTo(annotation);
                registerCommand(getClassName(object.getClass()).replace("Commands", ""), annotation);
            }
        }
    }
    
    private void registerCommand(String category, Command command) {
        category = category.toLowerCase();
        List<Command> cat = helpCategories.get(category);
        if(cat == null) {
            cat = new ArrayList<Command>();
            helpCategories.put(category, cat);
        }
        cat.add(command);
        commands.put(command.name().toLowerCase(), command);
    }
    
    public Map<String, List<Command>> getHelpCategories() {
        return helpCategories;
    }
    
    public Map<String, Command> getCommands() {
        return commands;
    }

    public boolean run(CommandSender sender, org.bukkit.command.Command command, String alias, String[] params) {
        Method method = aliases.get(command.getName());
        if(method != null) {
            Object owner = owners.get(method);
            Command ann = method.getAnnotation(Command.class);
            if(ann == null || owner == null) {
                Zones.log.info("[Zones] command " + command.getName() + " is mssing annotation or owner!");
                return false;
            }
            
            if((ann.requiresPlayer() || ann.requiresSelected() || ann.requiresSelection() || !ann.requiredType().equals(ZoneBase.class)) && !(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Where do you think you're going?");
                return true;
            }
            if(sender instanceof Player) {
                Player player = (Player)sender;
                if(ann.requiresSelected() && plugin.getZoneManager().getSelectedZone(player.getEntityId()) == null) {
                    sender.sendMessage(ChatColor.RED + "Please select a zone first with /zselect !");
                    return true;
                }
                if(ann.requiresSelection() && plugin.getZoneManager().getSelection(player.getEntityId()) == null) {
                    sender.sendMessage(ChatColor.RED + "Please create a selection first with /zcreate or /zedit !");
                    return true;
                }
                if(!ann.requiredPermission().equals("") && !plugin.getPermissions().canUse(player, ann.requiredPermission())) {
                    sender.sendMessage(ChatColor.RED + "You're not allowed to use this command.");
                    return true;
                }
                if(!ann.requiredType().equals(ZoneBase.class) && 
                    (plugin.getZoneManager().getSelected(player.getEntityId()) == 0 || 
                     !ann.requiredType().isAssignableFrom(plugin.getZoneManager().getSelectedZone(player.getEntityId()).getClass())
                )){
                    sender.sendMessage(ChatColor.RED + "You cannot use this command on your currently selected zone.");
                    return true;
                }
            }
            if(ann.min() > params.length || (ann.max() != -1 && params.length > ann.max())) {
                sender.sendMessage(ChatColor.RED + "Usage: " + ann.usage().replace("<command>", alias));
                return true;
            }
            
            try {
                method.invoke(owner, sender, params);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "An internal error occurred while attempting to perform this command");
                e.printStackTrace();
            }
            return true;
        }
        
        return false;
    }
    
    public void bukkitIWantToRegisterCommandsDynamicly() {
        SimplePluginManager pm = (SimplePluginManager) plugin.getServer().getPluginManager();
        Field map;
        try {
            map = SimplePluginManager.class.getDeclaredField("commandMap");
            map.setAccessible(true);
            bukkitMap = (org.bukkit.command.CommandMap) map.get(pm);
            con = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            con.setAccessible(true);
        } catch (Exception e) {
            Zones.log.info("[Zones] Error registering commands with bukkit!");
            e.printStackTrace();
        }
    }
    
    private void registerCommandWithBukkitBecauseIFuckingWantTo(Command command) {
        if(bukkitMap != null) {
            try {
                PluginCommand cmd = con.newInstance(command.name(), plugin);
                cmd.setUsage(command.usage());
                cmd.setDescription(command.description());
                cmd.setAliases(Arrays.asList(command.aliases()));
                
                cmd.setExecutor(plugin);
                
                bukkitMap.register(plugin.getDescription().getName(), cmd);
            } catch (Exception e) {
                Zones.log.info("[Zones] Error registering command " + command.name() + " with bukkit!");
                e.printStackTrace();
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
}
