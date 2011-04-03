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
public class ZSetDepthCommand extends ZoneCommand {
    
    public ZSetDepthCommand(Zones plugin)  {
        super("zsetdepth", plugin);
        this.setRequiresDummy(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        ZonesDummyZone dummy = getDummy(player);
        if (vars.length < 1 || Integer.parseInt(vars[0]) < 0) {
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetdepth [depth]");
        } else {
            dummy.setZ(WorldManager.toInt(player.getLocation().getY()) - Integer.parseInt(vars[0]),dummy.getMax());

            player.sendMessage(ChatColor.GREEN.toString() + "Min z is now : " + dummy.getMin());
        }
        return true;
    }
}
