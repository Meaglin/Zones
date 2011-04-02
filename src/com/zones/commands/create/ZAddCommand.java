package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.World;
import com.zones.Zones;
import com.zones.ZonesDummyZone;
import com.zones.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZAddCommand extends ZoneCommand{

    public ZAddCommand(Zones plugin) {
        super("zadd", plugin);
        this.setRequiresDummy(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        ZonesDummyZone dummy = getDummy(player);
        if (dummy.getType() == 1 && dummy.getCoords().size() == 2) {
            player.sendMessage(ChatColor.RED.toString() + "You can only use 2 points to define a cuboid zone.");
            return true;
        }
        int[] p = new int[2];
        p[0] = World.toInt(player.getLocation().getX());
        p[1] = World.toInt(player.getLocation().getZ());
        for (int[] point : dummy.getCoords()) {
            if (p[0] == point[0] && p[1] == point[1]) {
                player.sendMessage(ChatColor.YELLOW.toString() + "Already added this point.");
                return true;
            }
        }
        player.sendMessage(ChatColor.GREEN.toString() + "Added point [" + p[0] + "," + p[1] + "] to the temp zone.");
        dummy.addCoords(p);
        return true;
    }

}
