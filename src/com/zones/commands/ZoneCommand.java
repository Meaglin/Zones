package com.zones.commands;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zones.WorldManager;
import com.zones.ZoneManager;
import com.zones.Zones;
import com.zones.ZonesDummyZone;
import com.zones.commands.create.ZConfirmCommand;
import com.zones.model.ZoneBase;

/**
 * 
 * @author Meaglin
 *
 */
public abstract class ZoneCommand extends Command {

    private Zones plugin;
    private boolean requiresSelected;
    private boolean requiresDummy;
    private boolean requiresCreate;
    private boolean requiresAdmin;
    private Class<? extends ZoneBase> requiredClass = null;
    private String requiredAccess = null;
    
    public ZoneCommand(String name, Zones plugin) {
        super(name);
        this.plugin = plugin;
    }

    public abstract boolean run(Player player,String[] vars) ;
    
    protected Zones getPlugin() {
        return plugin;
    }
    
    protected boolean canUseCommand(Player p, String command) {
        return getPlugin().getP().has(p, command);
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
    
    protected void setRequiresCreate(boolean requiresCreate) {
        this.requiresCreate = requiresCreate;
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

    protected boolean requiresCreate() {
        return requiresCreate;
    }

    protected boolean hasDummy(Player p) {
        return getDummy(p) != null;
    }
    
    protected ZonesDummyZone getDummy(Player p) {
        return getZoneManager().getDummy(p.getEntityId());
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
                p.sendMessage(ChatColor.RED.toString() + "Please select a zone first with /zselect.");
            } else if(requiresCreate() && !canUseCommand(p,"zones.create")) {
                p.sendMessage(ChatColor.RED + "You're not allowed to create zones.");
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
        } else
            return false;
    }
    
    public static final Map<String, String[]> commands;
    static {
        commands = new LinkedHashMap<String,String[]>();
        commands.put("/zcreate", new String[] {
            "zones.create",
            "[zone name] - starts zone creation in a new zone.",

            "Starts Zone creation mode in which you can set the \n zones perimiter and type and height en depth."
        });

        commands.put("/zsetplot", new String[] {
            "zones.create",
            "- set height and depth to according to plot specs.",
            "Changes the zone you are making to a plot type \n with the related height and depth of the zone relative to \n your z position."
        });

        commands.put("/zhelp",new String[] {
            null,
            "<cmd> - shows <cmd> page/command from the zone help.",
            "Shows <cmd> page or command description from the \n zone help file."
        });

        commands.put("/zsetheight",new String[] {
            "zones.create",
             "[height] - sets maxz to current z + [height].",
            "Sets the zone you are creating height to your \n current z position + [height]"
        });

        commands.put("/zsetdepth",new String[] {
            "zones.create",
             "[depth] - sets minz to current z - [depth].",
            "Sets the depth of the zone you are creating \n to your current z position - [depth]"
        });

        commands.put("/zsave",new String[] {
            "zones.create",
            "- saves the temp zone after confirmation.",
            "Initiates saving of the zone you were creating \n you will need to confirm this with \n /zconfirm to make it actually save the zone. \n THIS CANNOT BE USED WHEN EDITTING A ZONE USE /zmerge!!!! "
        });

        commands.put("/zconfirm",new String[] {
            "zones.create",
            "- confirms confirmations.",
            "Confirms the last action that needs confirmation \n needed when /zsave or /zstop is used."
        });

        commands.put("/zsetz",new String[] {
            "zones.create",
            "[minz] [maxz] - sets minz, maxz, range [0-127].",
            "Sets the depth and height of the zone according to \n [minz] and [maxz] limited by the max \n and min height of the map [0-127]."
        });

        commands.put("/zstop",new String[] {
            "zones.create",
            "- stop creation and delete zone (asks confirmation).",
            "Stops the creation of the current zone and deletes \n all relative data this needs to be confirmed \n with /zconfirm though."
        });
            //ttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttTttttT
        commands.put("/zsetuser",new String[] {
            null,
            "[user name] b|m|d|e|h|*|- (combination of these)",
            "Sets the access of [user name] to what is specified\n "
            + "b = Build(placing blocks),\n"
            + "c = Modify(accessing chest/furnaces),\n "
            + "d = Destroy(destroying blocks),\n"
            + "e = Enter(entering your zone),\n"
            + "h = Hit(killing mobs/destroying minecarts or boats),\n"
            + "* = full access(all of the above) and - = remove all access. \n"
            + "Example: /zsetuser Meaglin bde this will give meaglin access \n"
            + " to build,destroy and walk around in your zone but not to \n"
            + "access your chests."
        });

        commands.put("/zsetgroup",new String[] {
            null,
            "[group name] b|m|d|e|h|*|- (combination of these)",
            "Sets the access of [group name] to what is specified \n "
            + "Possible group names: beunhaas, default, builder and vip \n"
            + "b = Build(placing blocks),\n"
            + "c = Modify(accessing chest/furnaces),\n "
            + "d = Destroy(destroying blocks),\n"
            + "e = Enter(entering your zone), \n"
            + "h = Hit(killing mobs/destroying minecarts or boats),\n"
            + "* = full access(all of the above) and - = remove all access. \n"
            + "Example: /zsetuser default bde this will give all users access \n"
            + " to build,destroy and walk around in your zone but not to \n"
            + "access your chests."

        });

        commands.put("/zaddadmin",new String[] {
            null,
            "[user name]",
            "Adds [user name] as admin to your zone which gives \n"
            + "[user name] rights to build,modify,destroy,enter your zone \n"
            + "and to give other people rights to do so . (access to \n"
            + "/zsetuser and /zsetgroup in your zone)"
        });

        commands.put("/zremoveadmin",new String[] {
            "zones.admin",
            "[user name]",
            "Removes [user name] as an admin from the zone."
        });

        commands.put("/zselect",new String[] {
            null,
            "<zone id>",
            "Selects a zone so you can modify the rights of the zone and \n"
            + "or modify other properties of the zone."
        });

        commands.put("/zsettype",new String[] {
            "zones.create",
            "Cuboid|NPoly - changes zone type.",
            "changes the zone type to a square(cuboid) or polygon(NPoly)."
        });

        commands.put("/zregioninfo",new String[] {
            "zones.info",
            " returns region info.",
            "Return the region x and y index and the amount of zones in \n"
            + " the region"
        });

        commands.put("/zgetaccess",new String[] {
            null,
            "- sends a access list of the selected zone.",
            "Sends you a list of all the access given in your currently \n"
            + "selected zone."
        });

        commands.put("/zdelete",new String[] {
            "zones.create",
            "- deletes selected zone.",
            "Deletes the currently selected zone, No confirmation!"
        });

        commands.put("/zsetname",new String[] {
            null,
            "[zone name] - changes zone name.",
            "Changes your current selected zones name to [zone name] \n"
            + "(note: [zone name] is allowed to have spaces)."
        });
        commands.put("/ztogglehealth", new String[] {
            "zones.toggle.health",
            "Enables or disables health in the selected zone.",
            "Nothing extra here."
        });
        commands.put("/ztoggledynamite", new String[] {
            "zones.toggle.tnt",
            "Enables or disables dynamite in the selected zone.",
            "Nothing extra here."
        });
        commands.put("/ztogglelava", new String[] {
            "zones.toggle.lava",
            "Prevents or allowes lava flow into the zone.",
            "Nothing extra here."
        });
        commands.put("/ztogglewater", new String[] {
            "zones.toggle.water",
            "Prevents or allowes water flow into the zone..",
            "Nothing extra here."
        });
        commands.put("/ztogglemobs", new String[] {
                "zones.toggle.mobs",
                "Enables or disables mobs spawning inside the zone.",
                "Nothing extra here."
            });
        commands.put("/ztoggleanimals", new String[] {
                "zones.toggle.animals",
                "Enables or disables animals spawning inside the zone.",
                "Nothing extra here."
            });
        commands.put("/zedit", new String[] {
            "zones.create",
            "see extended help.",
            "loads the current selected zone into your \n"
            + "edit selection so that it can be editten.\n"
            + "and merged back into an active zone.\n" +
            		"This is used to adjust the area of a zone."
        });
        commands.put("/zmerge", new String[] {
            "zones.create",
            "Merges your current edit selection with your selected\n",
            "zone replacing the area of the zone with your selection."
        });
        commands.put("/zreload", new String[] {
                "zones.admin",
                "config|zones|all - reloads specified part of zones.",
                "Config - reloads all the config files \n (main config file + each worlds config file).\n" +
                "Zones - reloads all the zones and resets all dummy&selected zones." +
                "All - reloads the whole plugin."
        });
        commands.put("/ztoggle", new String[] {
                "zones.toggle",
                "[variable name] - toggles variable name.",
                "[variable name] - toggles variable name, options: \n"
                + "lava|water - Toggles lava/water flow into the zone. \n"
                + "mobs|animals - Toggles mobs/animal spawning in the zone.\n"
                + "health - Enables/Disables Health in the zone.\n"
                + "tnt - Enables/Disables tnt explosions in the zone.\n"
                + "leafdecay - Enables/Disables leave decay in the zone.\n"
                + "teleport - Enables/Disables teleporting in/out of the zone.\n"
                + "fire - Enables/Disables fire in the zone."
                
        } );
        
        commands.put("/zadd", new String[] {
            "zones.settings.add",
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
            "- [variable name] [value] changes variable to value.",
            "Defines [variables name]'s value as [value].\n" +
            "List of variables :\n" +
            "protectedplace - [L] blocks which cannot be placed.\n" +
            "protectedbreak - [L] blocks which cannot be destroyed.\n" +
            "allowedanimals - [L] list of animals that can spawn.\n" +
            "allowedmobs - [L] list of mobs that can spawn.\n" +
            "entermessage - The message you see when you enter a zone.\n" +
            "leavemessage - The message you see when you leave a zone.\n" +
            "{zname} - zone name,{pname} - player name,{access} - access\n" +
            "Can be used to make enter/leave messages dynamic.\n" +
            "spawnlocation - change the respawn location within the zone.\n" +
            "List variables[L] requires comma seperated input: <val1>,<val2>"
        });
        
        commands.put("/zaccess" , new String[] {
             null,
             "Gives basic explenation about access tags.",
             "Use the command damnit :<."
        } );
        
        commands.put("/zinfo", new String[] {
            null,
            "Gives basic info about the selected zone.",
            "Displays zone size,min-max coordinates \n" +
            "and all set settings."
        } );
            
    }


    protected static Map<String, String[]> getCommands() { return commands; }

    public void setRequiredClass(Class<?  extends ZoneBase> requiredClass) {
        this.requiredClass = requiredClass;
    }

    public boolean hasRequiredClass() {
        return requiredClass != null;
    }

}
