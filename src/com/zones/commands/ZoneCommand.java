package com.zones.commands;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.zones.WorldManager;
import com.zones.ZoneManager;
import com.zones.Zones;
import com.zones.commands.create.ZConfirmCommand;
import com.zones.model.ZoneBase;
import com.zones.selection.ZoneSelection;

/**
 * 
 * @author Meaglin
 *
 */
public abstract class ZoneCommand extends Command {

    private Zones plugin;
    private boolean requiresSelected;
    private boolean requiresDummy;
    private boolean requiresAdmin;
    private Class<? extends ZoneBase> requiredClass = null;
    private String requiredAccess = null;
    
    public ZoneCommand(String name, Zones plugin) {
        super(name);
        this.plugin = plugin;
    }
    
    protected Zones getPlugin() {
        return plugin;
    }
    
    protected boolean canUseCommand(CommandSender sender, String command) {
        return (sender instanceof Player ? canUseCommand((Player)sender, command) : true);
    }
    
    protected boolean canUseCommand(Player p, String command) {
        return getPlugin().getPermissions().canUse(p,p.getWorld().getName(), command);
    }
    
    protected WorldManager getWorldManager(Player p) {
        return plugin.getWorldManager(p.getWorld());
    }
    
    protected ZoneManager getZoneManager() {
        return plugin.getZoneManager();
    }
    
    protected boolean requiresSelected() {
        return requiresSelected;
    }
    
    protected void setRequiresSelected(boolean b){
        requiresSelected = b;
    }
    
    protected void setRequiresAdmin(boolean requiresAdmin) {
        this.requiresAdmin = requiresAdmin;
    }

    protected void setRequiresDummy(boolean requiresDummy) {
        this.requiresDummy = requiresDummy;
    }
    
    protected void setRequiredAccess(String access) {
        requiredAccess = access;
    }
    
    protected boolean requiresAccess() {
        return requiredAccess != null;
    }

    protected boolean requiresDummy() {
        return requiresDummy;
    }

    protected boolean requiresAdmin() {
        return requiresAdmin;
    }

    protected boolean hasDummy(Player p) {
        return getDummy(p) != null;
    }
    
    protected ZoneSelection getDummy(Player p) {
        return getZoneManager().getSelection(p.getEntityId());
    }
    
    protected boolean hasSelected(Player p) {
        return getSelectedZone(p) != null;
    }
    
    protected int getSelected(Player p) {
        return getZoneManager().getSelected(p.getEntityId());
    }
    
    protected ZoneBase getSelectedZone(Player p) {
        return getZoneManager().getSelectedZone(p.getEntityId());
    }
    
    @Override
    public boolean execute(CommandSender sender, String alias, String[] vars) {
        if(sender instanceof Player) {
            Player p = (Player)sender;
            if(requiresSelected() && !hasSelected(p)) {
                p.sendMessage(ChatColor.RED + "Please select a zone first with /zselect.");
            } else if(requiresAdmin() && !canUseCommand(p,"zones.admin")) {
                p.sendMessage(ChatColor.RED + "You need to be a ServerZoneAdmin to be allowed to do this.");
            } else if(requiresDummy() && !hasDummy(p)){
                if(!canUseCommand(p,"zones.create")) {
                    p.sendMessage(ChatColor.RED + "You're not allowed to create zones.");
                } else {
                    p.sendMessage(ChatColor.RED + "Please create a dummy zone first with:");
                    p.sendMessage(ChatColor.RED + "/zcreate [zone name]");
                }
            } else if(hasRequiredClass() && (!hasSelected(p) || !(requiredClass.isAssignableFrom(getSelectedZone(p).getClass())))){
                p.sendMessage(ChatColor.RED + "This zone doesn't allow this type of command.");
            } else if(requiresAccess() && !canUseCommand(p,requiredAccess)) {
                p.sendMessage(ChatColor.RED + "You don't have the required permissions to use this command.");
            } else {
                if(requiresDummy() && !(this instanceof ZConfirmCommand)){
                    getDummy(p).setConfirm(null);
                }
                run((Player)sender,vars);
            }
            
            return true;
        } else if(sender instanceof ConsoleCommandSender){
            runConsole(sender, vars);
            return true;
        } else {
            return false;
        }
    }
    
    public abstract void run(Player player, String[] vars) ;
    public void runConsole(CommandSender sender, String[] vars) {
        sender.sendMessage(ChatColor.RED + "This command doesn't support console usage.");
    }
    
    public static final Map<String, String[]> commands;
    static {
        commands = new LinkedHashMap<String,String[]>();
        commands.put("/zcreate", new String[] {
            null,
            "create",
            "[zone name] - starts zone creation.",
            "Starts Zone creation mode in which you define \n the area/height and type of the zone."
        });

        commands.put("/zhelp",new String[] {
            null,
            "general",
            "<cmd> - shows <cmd> page/command from the zone help.",
            "Shows <cmd> page or command description from the \n zone help file."
        });

        commands.put("/zsetheight",new String[] {
                null,
                "create",
             "[height] - sets maxz to current z + [height].",
            "Sets the height of your selection to your \n current z position + [height]"
        });

        commands.put("/zsetdepth",new String[] {
                null,
                "create",
             "[depth] - sets minz to current z - [depth].",
            "Sets the depth of your current selection \n to your current z position - [depth]"
        });

        commands.put("/zsave",new String[] {
            null,
            "create",
            "- saves the selection after confirmation.",
            "Initiates saving of the zone you were creating \n you will need to confirm this with \n /zconfirm to make it actually save the zone."
        });

        commands.put("/zconfirm",new String[] {
            null,
            "create",
            "- confirms confirmations.",
            "Confirms the last action that needs confirmation \n needed when /zsave,/zmerge or /zstop is used."
        });

        commands.put("/zsetz",new String[] {
            null,
            "create",
            "[minz] [maxz] - sets minz, maxz, range [0-127].",
            "Sets the depth and height of the zone according to \n [minz] and [maxz] limited by the max \n and min height of the map [0-127]."
        });

        commands.put("/zstop",new String[] {
            null,
            "create",
            "- stops creation and deletes selection (asks confirmation).",
            "Stops the creation of the current zone and deletes \n all relative data this needs to be confirmed \n with /zconfirm though."
        });
            //ttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttT
        commands.put("/zsetuser",new String[] {
            null,
            "admin",
            "[username 1] [access1] <username 2> <accces 2> ....",
            "Sets the access of usernames to what is specified\n "
            + "b = Build(placing blocks),\n"
            + "c = Modify(accessing chest/furnaces/note blocks),\n "
            + "d = Destroy(destroying blocks),\n"
            + "e = Enter(entering your zone),\n"
            + "h = Hit(killing mobs,minecarts or boats/modify redstone),\n"
            + "* = full access(all of the above) and - = remove all access. \n"
            + "Example: /zsetuser Meaglin bde this will give meaglin access \n"
            + " to build,destroy and walk around in your zone but not to \n"
            + "access your chests."
        });

        commands.put("/zsetgroup",new String[] {
            null,
            "admin",
            "[groupname 1] [access 1] <groupname 2> <access 2> ...",
            "Sets the access of groupnames to what is specified \n "
            + "Possible group names: beunhaas, default, builder and vip \n"
            + "b = Build(placing blocks),\n"
            + "c = Modify(accessing chest/furnaces/note blocks),\n "
            + "d = Destroy(destroying blocks),\n"
            + "e = Enter(entering your zone), \n"
            + "h = Hit(killing mobs,minecarts or boats/modify redstone),\n"
            + "* = full access(all of the above) and - = remove all access. \n"
            + "Example: /zsetuser default bde this will give all users access \n"
            + " to build,destroy and walk around in your zone but not to \n"
            + "access your chests."
        });

        commands.put("/zaddadmin",new String[] {
            null,
            "admin",
            "[user 1] <user 2> <user 3>...",
            "Adds usernames as admin to your zone which gives \n"
            + "specified users rights to everything in your zone. \n"
            + "(Giving permissions, changing flags, etc..)"
        });

        commands.put("/zremoveadmin",new String[] {
            null,
            "admin",
            "[user 1] <user 2> <user 3>...",
            "Removes usernames as admin from the zone." +
            "Note: This can only be used if your an ZonesAdmin or\n" +
            "if you're an admin in a super zone."
        });

        commands.put("/zselect",new String[] {
            null,
            "general",
            "<zone id>",
            "Selects a zone so you can modify the rights of the zone and \n"
            + "or modify other properties of the zone."
        });

        commands.put("/zsettype",new String[] {
            null,
            "create",
            "Cuboid|NPoly - changes selection form.",
            "changes the form of your selection to \na square(cuboid) or polygon(NPoly)."
        });

        commands.put("/zregioninfo",new String[] {
            "zones.info",
            "general",
            " returns region info.",
            "Return the region x and y index and the amount of zones in \n"
            + " the region"
        });

        commands.put("/zgetaccess",new String[] {
            null,
            "admin",
            "- sends a access list of the selected zone.",
            "Sends you a list of all the access given in your currently \n"
            + "selected zone."
        });

        commands.put("/zdelete",new String[] {
            "zones.admin",
            "admin",
            "- deletes selected zone.",
            "Deletes the currently selected zone, No confirmation!"
        });

        commands.put("/zsetname",new String[] {
            null,
            "settings",
            "[zone name] - changes zone name.",
            "Changes your current selected zones name to [zone name] \n"
            + "(note: [zone name] is allowed to have spaces)."
        });
        commands.put("/ztogglehealth", new String[] {
            "zones.toggle.health",
            "settings",
            "Enables or disables health in the selected zone.",
            "Nothing extra here."
        });
        commands.put("/ztoggledynamite", new String[] {
            "zones.toggle.tnt",
            "settings",
            "Enables or disables dynamite in the selected zone.",
            "Nothing extra here."
        });
        commands.put("/ztogglelava", new String[] {
            "zones.toggle.lava",
            "settings",
            "Prevents or allowes lava flow into the zone.",
            "Nothing extra here."
        });
        commands.put("/ztogglewater", new String[] {
            "zones.toggle.water",
            "settings",
            "Prevents or allowes water flow into the zone..",
            "Nothing extra here."
        });
        commands.put("/ztogglemobs", new String[] {
                "zones.toggle.mobs",
                "settings",
                "Enables or disables mobs spawning inside the zone.",
                "Nothing extra here."
        });
        commands.put("/ztoggleanimals", new String[] {
                "zones.toggle.animals",
                "settings",
                "Enables or disables animals spawning inside the zone.",
                "Nothing extra here."
        });
        commands.put("/zedit", new String[] {
            "zones.create",
            "create",
            "see extended help.",
            "loads the current selected zone into your \n"
            + "edit selection so that it can be editten.\n"
            + "and merged back into an active zone.\n" +
            		"This is used to adjust the area of a zone."
        });
        commands.put("/zreload", new String[] {
                "zones.admin",
                "admin",
                "config|zones|all - reloads specified part of zones.",
                "Config - reloads all the config files \n (main config file + each worlds config file).\n" +
                "Zones - reloads all the zones and resets all dummy&selected zones." +
                "All - reloads the whole plugin."
        });
        commands.put("/ztoggle", new String[] {
                "zones.toggle",
                "settings",
                "[variable name] - toggles variable name.",
                "[variable name] - toggles variable name, options: \n"
                + "lava|water - Toggles lava/water flow into the zone. \n"
                + "mobs|animals - Toggles mobs/animal spawning in the zone.\n"
                + "health - Enables/Disables Health in the zone.\n"
                + "tnt - Enables/Disables tnt explosions in the zone.\n"
                + "leafdecay - Enables/Disables leave decay in the zone.\n"
                + "teleport - Enables/Disables teleporting in/out of the zone.\n"
                + "fire - Enables/Disables fire in the zone.\n"
                + "snowfall|iceform - Enables/Disables snowfall|iceform.\n"
                + "physics - Enables/Disables physics in the zone.\n" 
                + "notify - Toggles enter/leave notifications in the zone.\n"
                + "crop - Toggles Crop Protection."
        });
        
        commands.put("/zadd", new String[] {
            "zones.settings.add",
            "settings",
            "- [variable name] [value] add value to variable.",
            "Adds the [value] to the list [variable name] \n." +
            "List of variables :\n" +
            "protectedplace - blocks which cannot be placed.\n" +
            "protectedbreak - blocks which cannot be destroyed.\n" +
            "allowedanimals - list of animals that can spawn.\n" +
            "allowedmobs - list of mobs that can spawn."
        });

        commands.put("/zremove", new String[] {
            "zones.settings.remove",
            "settings",
            "- [variable name] [value] remove value from variable.",
            "removes the [value] from the list [variable name] \n." +
            "List of variables :\n" +
            "protectedplace - blocks which cannot be placed.\n" +
            "protectedbreak - blocks which cannot be destroyed.\n" +
            "allowedanimals - list of animals that can spawn.\n" +
            "allowedmobs - list of mobs that can spawn."
        });
        
        commands.put("/zset", new String[] {
            "zones.settings.set",
            "settings",
            "- [variable name] [value] changes variable to value.",
            "Defines [variables name]'s value as [value]. Variables:\n" +
            "protectedplace - [L] blocks which cannot be placed.\n" +
            "protectedbreak - [L] blocks which cannot be destroyed.\n" +
            "allowedanimals - [L] list of animals that can spawn.\n" +
            "allowedmobs - [L] list of mobs that can spawn.\n" +
            "entermessage - The message you see when you enter a zone.\n" +
            "leavemessage - The message you see when you leave a zone.\n" +
            "{zname} - zone name,{pname} - playername,{access} - access\n" +
            "and ^ - colors, Can be used to make the message dynamic.\n" +
            "Disable enter/leave messages by settings them to \"NONE\".\n" +
            "spawnlocation - change the respawn location within the zone.\n" +
            "[L]List variables requires comma seperated input: <val1>,<val2>"
        });
        
        commands.put("/zaccess" , new String[] {
             null,
             "admin",
             "Gives basic explenation about access tags.",
             "Use the command damnit :<."
        });
        
        commands.put("/zinfo", new String[] {
            null,
            "general",
            "Gives basic info about the selected zone.",
            "Displays zone size,min-max coordinates \n" +
            "and all set settings."
        });
        
        commands.put("/zsetclass", new String[] {
               null,
               "create",
               "[class] - changes selection class to [class].",
               "Beh"
        });
        
        commands.put("/zdefine", new String[] {
                null,
                "create",
                "[zone naam] - defines a zone.",
                "based on your word edit selection a zone \n" +
                "is defind with name [zone name]."
        });
        
        commands.put("/zredefine", new String[] {
                "zones.create",
                "create",
                "- changes the form of your selected zone.",
                "Changes the form of your current selected\n" +
                "zone to your world edit selection."
        });
        
        commands.put("/zexport", new String[] {
                null,
                "create",
                "- see description./zhelp /zexport",
                "Exports the selected zone to your\n" +
                "world edit selection."
        });
        
        commands.put("/zwho", new String[] { 
           null,
           "general",
           "- displays list of players in zone.",
           "Displays a list of players in the zones\n" +
           "at your current location or when you have\n" +
           "a zone selected it will display a list of\n" +
           "players in that zone."
        });
            
    }


    protected static Map<String, String[]> getCommands() { return commands; }

    public void setRequiredClass(Class<?  extends ZoneBase> requiredClass) {
        this.requiredClass = requiredClass;
    }

    public boolean hasRequiredClass() {
        return requiredClass != null;
    }

}
