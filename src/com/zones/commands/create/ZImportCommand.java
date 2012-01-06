package com.zones.commands.create;

import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.commands.ZoneCommand;
import com.zones.selection.ZoneSelection;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Meaglin
 */
public class ZImportCommand extends ZoneCommand {
    
    public ZImportCommand(Zones plugin) {
        super("zimport", plugin);
        this.setRequiresZoneSelection(true);
    }

    @Override
    public void run(Player player, String[] vars) {
        if(!ZonesConfig.WORLDEDIT_ENABLED) {
            player.sendMessage(ChatColor.RED + "WorldEdit support is turned off!.");
        } else {
            ZoneSelection selection = getZoneSelection(player);
            if(selection.importWorldeditSelection()) {
                player.sendMessage(ChatColor.YELLOW + "Added your worldedit selection as zone points.");
            } else {
                player.sendMessage(ChatColor.RED + "Invalid/Missing worldedit Selection");
            }
            /*
            if(!(selection.getSelection() instanceof CuboidSelection))
            {
                player.sendMessage(ChatColor.RED+"Only cuboid selections are supported.");
            } else {
                Selection worldeditSelection = getPlugin().getWorldEdit().getSelection(player);
                if(worldeditSelection == null)
                {
                    player.sendMessage(ChatColor.YELLOW+"Can't find your current worldedit selection!");
                } else {
                    player.sendMessage(ChatColor.YELLOW+"Trying to import your current worldedit selection as zone coords.");
                    //selection.setClass("ZoneNormal");
                    CuboidSelection sel = new CuboidSelection(selection);

                    Location min = worldeditSelection.getMinimumPoint();
                    Location max = worldeditSelection.getMaximumPoint();
                    sel.setHeight(new ZoneVertice(min.getBlockY(), (max.getBlockY() >= 127 ? 130 : max.getBlockY())));
                    sel.setPoint1(new ZoneVertice(min.getBlockX(), min.getBlockZ()));
                    sel.setPoint2(new ZoneVertice(max.getBlockX(), max.getBlockZ()));
                    selection.setSelection(sel);
                    
                    player.sendMessage(ChatColor.YELLOW+"Added your worldedit selection as zone points.");
                }
            }
            */
        }
    }
}