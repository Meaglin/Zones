package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneVertice;
import com.zones.selection.ZoneSelection;

/**
 * 
 * @author Meaglin
 *
 */
public class ZSetHeightCommand extends ZoneCommand {
    
    public ZSetHeightCommand(Zones plugin) {
        super("zsetheight", plugin);
        this.setRequiresZoneSelection(true);
    }

    @Override
    public void run(Player player, String[] vars) {
        ZoneSelection selection = getZoneSelection(player);
        if (vars.length < 1 || Integer.parseInt(vars[0]) < 1) {
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetheight [height]");
        } else {
            ZoneVertice height = selection.getSelection().getHeight();
            selection.getSelection().setHeight(new ZoneVertice(height.getMin(), WorldManager.toInt(player.getLocation().getY()) + Integer.parseInt(vars[0]) - 1));
        }
    }
}
