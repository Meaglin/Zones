package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.Selection;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneVertice;
import com.zones.selection.CuboidSelection;
import com.zones.selection.ZoneEditSelection;
import com.zones.selection.ZoneSelection;

public class ZRedefineCommand extends ZoneCommand {

    public ZRedefineCommand(Zones plugin) {
        super("zredefine", plugin);
        this.setRequiresSelected(true);
        this.setRequiredAccess("zones.create");
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if(!ZonesConfig.WORLDEDIT_ENABLED || getPlugin().getWorldEdit() == null) {
            player.sendMessage(ChatColor.RED + "WorldEdit support needs to be enabled!");
            return true;
        }
        Selection worldeditSelection = getPlugin().getWorldEdit().getSelection(player);
        if(worldeditSelection == null) {
            player.sendMessage(ChatColor.RED + "No WorldEdit selection found.");
            return true;
        }
        if(worldeditSelection.getArea() < 1) {
            player.sendMessage(ChatColor.RED + "Your WorldEdit selection is not a valid selection.");
            return true;
        }
        ZoneBase zone = getSelectedZone(player);
        ZoneVertice point1 = new ZoneVertice(worldeditSelection.getMinimumPoint().getBlockX(), worldeditSelection.getMinimumPoint().getBlockZ());
        ZoneVertice point2 = new ZoneVertice(worldeditSelection.getMaximumPoint().getBlockX(), worldeditSelection.getMaximumPoint().getBlockZ());
        ZoneVertice height = new ZoneVertice(worldeditSelection.getMinimumPoint().getBlockY(), (worldeditSelection.getMaximumPoint().getBlockY() >= 127 ? 130 : worldeditSelection.getMaximumPoint().getBlockY()));

        ZoneSelection selection = new ZoneEditSelection(getPlugin(),player,zone.getName());
        CuboidSelection sel = new CuboidSelection(selection);

        sel.setHeight(height, true);
        sel.setPoint1(point1);
        sel.setPoint2(point2);
        selection.setSelection(sel);
        if(selection.save() != null) {
            player.sendMessage(ChatColor.GREEN + "Zone '" + zone.getName() + "' redefined.");
        } else {
            player.sendMessage(ChatColor.RED + "Error saving zone.");
        }
        return false;
    }

}