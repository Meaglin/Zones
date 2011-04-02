package com.zones.commands.create;

import com.sk89q.worldedit.bukkit.selections.Selection;
import com.zones.Zones;
import com.zones.ZonesDummyZone;
import com.zones.commands.ZoneCommand;
import com.zones.types.ZoneNormal;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class ZImportCommand extends ZoneCommand {

    private Zones p;
    
    public ZImportCommand(Zones plugin) {
        super("zimport", plugin);
        this.setRequiresDummy(true);
        p = plugin;
    }

    @Override
    public boolean run(Player player, String[] vars) {
        ZonesDummyZone dummy = getDummy(player);
        if(dummy.getType()!=1)
        {
            player.sendMessage(ChatColor.RED+"Only cuboid selections are supported.");
        } else {
            Selection selection = p.getWorldEdit().getSelection(player);
            if(selection == null)
            {
                player.sendMessage(ChatColor.YELLOW+"Can't find your current worldedit selection!");
            } else {
                player.sendMessage(ChatColor.YELLOW+"Trying to import your current worldedit selection as zone coords.");
                dummy.setClass(player, "ZoneNormal");
                for(int[] c : dummy.getCoords()) // remove the current selection!
                {
                    dummy.remove(c);
                }
                Location min = selection.getMinimumPoint();
                Location max = selection.getMaximumPoint();
                dummy.setZ(min.getBlockY(), max.getBlockY());
                int[] coords = new int[] { min.getBlockX(), min.getBlockZ() };
                dummy.addCoords(coords);
                      coords = new int[] { max.getBlockX(), max.getBlockZ() };
                dummy.addCoords(coords);

                player.sendMessage(ChatColor.YELLOW+"Added your worldedit selection as zone points.");
            }
        }
        return true;
    }
}
