package com.zones.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneVertice;
import com.zones.model.types.ZoneInherit;
import com.zones.selection.ZoneCreateSelection;
import com.zones.selection.ZoneEditSelection;
import com.zones.selection.ZoneSelection;
import com.zones.selection.ZoneSelection.Confirm;

public class CreateCommands extends CommandsBase {

    public CreateCommands(Zones plugin) {
        super(plugin);
    }

    @Command(
            name = "zcreate", 
            aliases = { "zc" }, 
            description = 
                "Starts zone creation mode\n" +
        		"Use /zimport,/zsetz,/zsetclass,/zsettype and the tool\n" +
        		"to define your zone.\n" +
        		"Use /zsave to save your work or /zstop to RageQuit.", 
            usage = "/<command> [zone name]",
            min = 1,
            requiresPlayer = true
    )
    public void create(Player player, String[] params) {
        ZoneBase inheritedZone = null;
        if(!canUseCommand(player,"zones.create")) {
            if(hasSelected(player)) {
                inheritedZone = getSelectedZone(player);
                if(!(inheritedZone instanceof ZoneInherit)) {
                    player.sendMessage(ChatColor.RED + "This zone doesn't allow subzoning.");
                    return;
                }
            } else {
                player.sendMessage(ChatColor.RED + "You don't have permission to make global zones.");
                return;
            }
        }
        String name = "";
        for (int i = 0; i < params.length; i++)
            name += " " + params[i];

        name = name.substring(1);
        if(name.length() < 4)
        {
            player.sendMessage(ChatColor.RED.toString() + "Too short zone name.");
            return;
        }
        ZoneSelection selection = new ZoneCreateSelection(getPlugin(),player,name);
        selection.setInherited(inheritedZone);
        getPlugin().getZoneManager().addSelection(player.getEntityId(), selection);
        player.sendMessage(ChatColor.YELLOW + "Entering zone creation mode. Zone name: '" + name + "'");
        player.sendMessage(ChatColor.YELLOW + "You can start adding the zone points of this zone by");
        player.sendMessage(ChatColor.RED + "Right clicking blocks with " + Material.getMaterial(ZonesConfig.CREATION_TOOL_TYPE).name().toLowerCase() + "[" + ZonesConfig.CREATION_TOOL_TYPE + "].");
        if(ZonesConfig.WORLDEDIT_ENABLED) player.sendMessage("Or you can import a worldedit selection using /zimport");
    }
    
    
    
    @Command(
            name = "zedit", 
            aliases = { "" }, 
            description = 
            "Loads your selected zone into your edit selection.\n" +
            "Use /zimport,/zsetz,/zsetclass,/zsettype and the tool\n" +
            "to change the zone.\n" +
            "Use /zsave to save your work or /zstop to RageQuit.", 
            requiresPlayer = true,
            requiresSelected = true
    )
    public void edit(Player player, String[] params) {
        ZoneBase z = getSelectedZone(player);
        ZoneEditSelection selection = new ZoneEditSelection(getPlugin(),player,z.getName());
        getPlugin().getZoneManager().addSelection(player.getEntityId(), selection);
        player.sendMessage(ChatColor.GREEN + "Loaded zone " + z.getName() + " into your edit selection.");
    }
    
    @Command(
            name = "zsetz",
            aliases = { "" },
            description = "Sets the [min] and [max] height of \nyour current selection.\nMin and max must be between 0 and 130.",
            usage = "/<command> [min] [max]",
            min = 2,
            max = 2,
            requiresPlayer = true,
            requiresSelection = true
    )
    public void setZ(Player player, String[] params) {
        if (Integer.parseInt(params[0]) < 0 || Integer.parseInt(params[0]) > 260 || Integer.parseInt(params[1]) < 0 || Integer.parseInt(params[1]) > 260) 
            player.sendMessage(ChatColor.YELLOW + "Usage: /zsetz [min Z] [max Z]");
         else 
            getZoneSelection(player).getSelection().setHeight(new ZoneVertice(Integer.parseInt(params[0]),Integer.parseInt(params[1]) ));
    }
    
    @Command(
            name = "zsetclass",
            aliases = { "" },
            description = 
            "Changes the [class] of your selection.\n" +
    		"Possible classes:\n" +
    		"  ZoneNormal - The basic type.\n" +
    		"  ZoneInherit - Allows subzoning and inheritence.\n" +
    		"  ZonePlot - A claimable, subzonable zone.",
            usage = "/<command> [class]",
            min = 1,
            max = 1,
            requiresPlayer = true,
            requiresSelection = true
    )
    public void setClass(Player player, String[] params) {
        getZoneSelection(player).setClass(params[0]);
    }
    
    @Command(
            name = "zsettype",
            aliases = { "" },
            description = "Changes the type of your selection.\n" +
            		"Possible types:\n" +
            		"  Cuboid - The default form.\n" +
            		"  NPoly - Allows polygonal zones.",
            usage = "/<command> [Cuboid|NPoly]",
            min = 1,
            max = 1,
            requiresPlayer = true,
            requiresSelection = true
    )
    public void setType(Player player, String[] params) {
        if(params[0].equalsIgnoreCase("Cuboid"))
            getZoneSelection(player).setForm(params[0]);
        else if(params[0].equalsIgnoreCase("NPoly"))
            getZoneSelection(player).setForm(params[0]);
        else if(params[0].equalsIgnoreCase("Cylinder"))
            getZoneSelection(player).setForm(params[0]);
        else if(params[0].equalsIgnoreCase("Sphere"))
            getZoneSelection(player).setForm(params[0]);
        else
            player.sendMessage(ChatColor.YELLOW + "Usage: /zsettype Cuboid|NPoly - changes zone type.");
    }
    
    @Command(
            name = "zsave",
            aliases = { "" },
            description = "Saves your current selection.\nAutomatically selects the resulting zone.",
            requiresPlayer = true,
            requiresSelection = true
    )
    public void save(Player player, String[] params) {
        ZoneSelection selection = getZoneSelection(player);
        if(!selection.getSelection().isValid()) {
            player.sendMessage(ChatColor.RED + "You don't have a valid selection.");
            return;
        }
        if (selection.getSelection().getHeight().getMax() == 130 && selection.getSelection().getHeight().getMin() == 0)
            player.sendMessage(ChatColor.RED.toString() + "WARNING: default z values not changed!");

        selection.setConfirm(Confirm.SAVE);
        getZoneSelection(player).confirm();
    }
    
    @Command(
            name = "zstop",
            aliases = { "" },
            description = "Deletes your current selection and reverts all modifications.",
            requiresPlayer = true,
            requiresSelection = true
    )
    public void stop(Player player, String[] params) {
        getZoneSelection(player).setConfirm(Confirm.STOP);
        getZoneSelection(player).confirm();
    }
    
}
