package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.Selection;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneForm;
import com.zones.model.ZoneVertice;
import com.zones.model.types.ZoneInherit;
import com.zones.selection.CuboidSelection;
import com.zones.selection.ZoneCreateSelection;
import com.zones.selection.ZoneSelection;
import com.zones.util.Log;

public class ZDefineCommand extends ZoneCommand {

    public ZDefineCommand(Zones plugin) {
        super("zdefine", plugin);
    }

    @Override
    public void run(Player player, String[] vars) {
        
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
        if(vars.length < 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /zdefine [zone name]");
            return;
        }
        String name = "";
        for (int i = 0; i < vars.length; i++)
            name += " " + vars[i];

        name = name.substring(1);
        if(name.length() < 4)
        {
            player.sendMessage(ChatColor.RED.toString() + "Too short zone name.");
            return;
        }
        Selection worldeditSelection = getPlugin().getWorldEdit().getSelection(player);
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
        
        ZoneSelection selection = new ZoneCreateSelection(getPlugin(),player,name);
        CuboidSelection sel = new CuboidSelection(selection);

        sel.setHeight(height, true);
        sel.setPoint1(point1);
        sel.setPoint2(point2);
        selection.setSelection(sel);
        if(inheritedZone != null) selection.setClass("ZoneInherit");
        ZoneBase zone = selection.save();
        if(zone != null) {
            player.sendMessage(ChatColor.GREEN + "Zone '" + name + "' saved.");
            Log.info(player.getName() + " created zone " + zone.getName() + "[" + zone.getId() + "]");
        } else {
            player.sendMessage(ChatColor.RED + "Error saving zone.");
        }
    }

}
