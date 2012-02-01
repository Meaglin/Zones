package com.zones.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneForm;
import com.zones.model.forms.ZoneCuboid;
import com.zones.model.types.ZoneInherit;
import com.zones.selection.ZoneCreateSelection;
import com.zones.selection.ZoneEditSelection;
import com.zones.selection.ZoneSelection;
import com.zones.util.Log;

public class WorldeditCommands extends CommandsBase {

    
    public WorldeditCommands(Zones plugin) {
        super(plugin);
    }

    @Command(
            name = "zdefine", 
            aliases = { "zd" }, 
            description = "Creates a zone with [name] based on your current worldedit selection.", 
            usage = "/<command> [name]",
            min = 1,
            requiresPlayer = true
    )
    public void define(Player player, String[] params) {
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
        
        if(!ZonesConfig.WORLDEDIT_ENABLED || getPlugin().getWorldEdit() == null) {
            player.sendMessage(ChatColor.RED + "WorldEdit support needs to be enabled!");
            return;
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
        if(!selection.importWorldeditSelection()) {
            player.sendMessage(ChatColor.RED + "Invalid/Missing worldedit Selection");
            return;
        }
        
        
        if(inheritedZone != null){
            ZoneForm form = inheritedZone.getForm();
            if (!form.contains(selection.getSelection())) {
                player.sendMessage(ChatColor.RED + "Your selection is not inside your selected zone, zone cannot be created.");
                return;
            }
        }
        

        if(inheritedZone != null) selection.setClass("ZoneInherit");
        ZoneBase zone = selection.save();
        if(zone != null) {
            player.sendMessage(ChatColor.GREEN + "Zone '" + name + "' saved.");
            Log.info(player.getName() + " created zone " + zone.getName() + "[" + zone.getId() + "]");
        } else {
            player.sendMessage(ChatColor.RED + "Error saving zone.");
        }
    }
    
    @Command(
            name = "zredefine", 
            aliases = { "zrd" }, 
            description = "Changes your current selected zone to the form \nof your current worldedit selection.", 
            requiresPlayer = true,
            requiresSelected = true
    )
    public void redefine(Player player, String[] params) {
        if(!ZonesConfig.WORLDEDIT_ENABLED || getPlugin().getWorldEdit() == null) {
            player.sendMessage(ChatColor.RED + "WorldEdit support needs to be enabled!");
            return;
        }
        
        ZoneBase zone = getSelectedZone(player);
        ZoneSelection selection = new ZoneEditSelection(getPlugin(),player,zone.getName());
        if(!selection.importWorldeditSelection()) {
            player.sendMessage(ChatColor.RED + "Invalid/Missing worldedit Selection");
            return;
        }
        
        if(!canEdit(player, zone, selection)) {
            player.sendMessage(ChatColor.RED + "You're not allowed to edit this zone.");
            return;
        }

        ZoneBase save = selection.save();
        if(save != null) {
            player.sendMessage(ChatColor.GREEN + "Zone '" + save.getName() + "' redefined.");
        } else {
            player.sendMessage(ChatColor.RED + "Error saving zone.");
        }
    }
    
    @Command(
            name = "zexport", 
            aliases = { "zex", "zx" }, 
            description = "Export your current selected zone to your worldedit selection.", 
            requiresPlayer = true,
            requiresSelected = true
    )
    public void exportWE(Player player, String[] params) {
        if(!ZonesConfig.WORLDEDIT_ENABLED || getPlugin().getWorldEdit() == null) {
            player.sendMessage(ChatColor.RED + "WorldEdit support needs to be enabled!");
            return;
        }
        
        ZoneBase zone = getSelectedZone(player);
        ZoneForm form = zone.getForm();
        if(form instanceof ZoneCuboid) {
            Vector pt1 = new Vector(form.getLowX(),form.getLowZ(),form.getLowY());
            Vector pt2 = new Vector(form.getHighX(),form.getHighZ(),form.getHighY());
            CuboidSelection selection = new CuboidSelection(zone.getWorld(), pt1, pt2);
            getPlugin().getWorldEdit().setSelection(player, selection);
            player.sendMessage(ChatColor.GREEN + "Zone " + zone.getName() + " selected as cuboid selection.");
        } else {
            player.sendMessage(ChatColor.RED + "NPoly is not supported yet.");
        }
    }
    
    @Command(
            name = "zimport", 
            aliases = { "zi" , "zimp", "zim" }, 
            description = "Import your worldedit selection into your current \n zone selection.", 
            requiresPlayer = true,
            requiresSelection = true
    )
    public void importWE(Player player, String[] params) {
        if(!ZonesConfig.WORLDEDIT_ENABLED) {
            player.sendMessage(ChatColor.RED + "WorldEdit support is turned off!.");
        } else {
            ZoneSelection selection = getZoneSelection(player);
            if(selection.importWorldeditSelection()) {
                player.sendMessage(ChatColor.YELLOW + "Added your worldedit selection as zone points.");
            } else {
                player.sendMessage(ChatColor.RED + "Invalid/Missing worldedit Selection");
            }
        }
    }
}
