package com.zones.unused.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.model.ZoneVertice;
import com.zones.selection.ZoneSelection;
import com.zones.unused.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZSetDepthCommand extends ZoneCommand {
    
    public ZSetDepthCommand(Zones plugin)  {
        super("zsetdepth", plugin);
        this.setRequiresZoneSelection(true);
    }

    @Override
    public void run(Player player, String[] vars) {
        ZoneSelection selection = getZoneSelection(player);
        if (vars.length < 1 || Integer.parseInt(vars[0]) < 0) {
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetdepth [depth]");
        } else {
            ZoneVertice height = selection.getSelection().getHeight();
            selection.getSelection().setHeight(new ZoneVertice(WorldManager.toInt(player.getLocation().getY()) - Integer.parseInt(vars[0]), height.getMax()));
        }
    }
}
