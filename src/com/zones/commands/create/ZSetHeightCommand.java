package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.ZonesDummyZone;
import com.zones.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZSetHeightCommand extends ZoneCommand {
    
    public ZSetHeightCommand(Zones plugin) {
        super("zsetheight", plugin);
        this.setRequiresDummy(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        ZonesDummyZone dummy = getDummy(player);
        if (vars.length < 1 || Integer.parseInt(vars[0]) < 1) {
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetheight [height]");
        } else {
            dummy.setZ(dummy.getMin(),WorldManager.toInt(player.getLocation().getY()) + Integer.parseInt(vars[0]) - 1);

            //player.sendMessage(ChatColor.GREEN.toString() + "Max z is now : " + dummy.getMax());
        }
        return true;
    }
}
