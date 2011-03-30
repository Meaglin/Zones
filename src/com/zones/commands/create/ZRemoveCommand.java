package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.World;
import com.zones.Zones;
import com.zones.ZonesDummyZone;
import com.zones.commands.ZoneCommand;

public class ZRemoveCommand extends ZoneCommand {
    
    public ZRemoveCommand(Zones plugin) {
        super("zremove", plugin);
        this.setRequiresDummy(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        ZonesDummyZone dummy = getDummy(player);
        int[] p = new int[2];
        p[0] = World.toInt(player.getLocation().getX());
        p[1] = World.toInt(player.getLocation().getZ());
        for (int[] point : dummy.getCoords()) {
            if (p[0] == point[0] && p[1] == point[1]) {
                dummy.remove(point);
                player.sendMessage(ChatColor.GREEN.toString() + "Removed point[" + p[0] + "," + p[1] + "]  from temp zone.");
                return true;
            }
        }
        player.sendMessage(ChatColor.RED.toString() + "Couldn't find point in zone so nothing could be removed");
        return true;
    }
}
