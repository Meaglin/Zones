package com.zones.commands.create;

import com.sk89q.worldedit.bukkit.selections.Selection;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.ZonesDummyZone;
import com.zones.commands.ZoneCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class ZImportCommand extends ZoneCommand {
    
    public ZImportCommand(Zones plugin) {
        super("zimport", plugin);
        this.setRequiresDummy(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if(!ZonesConfig.WORLDEDIT_ENABLED) {
            player.sendMessage(ChatColor.RED+"WorldEdit support is turned off!.");
        } else {
            ZonesDummyZone dummy = getDummy(player);
            if(dummy.getFormId()!=1)
            {
                player.sendMessage(ChatColor.RED+"Only cuboid selections are supported.");
            } else {
                Selection selection = getPlugin().getWorldEdit().getSelection(player);
                if(selection == null)
                {
                    player.sendMessage(ChatColor.YELLOW+"Can't find your current worldedit selection!");
                } else {
                    player.sendMessage(ChatColor.YELLOW+"Trying to import your current worldedit selection as zone coords.");
                    dummy.setClass("ZoneNormal");
                    dummy.clearCoords();

                    Location min = selection.getMinimumPoint();
                    Location max = selection.getMaximumPoint();
                    dummy.setZ(min.getBlockY(), max.getBlockY());
                    dummy.addCoords(min.getBlockX(), min.getBlockZ());
                    dummy.addCoords(max.getBlockX(), max.getBlockZ());

                    player.sendMessage(ChatColor.YELLOW+"Added your worldedit selection as zone points.");
                }
            }
        }
        return true;
    }
}