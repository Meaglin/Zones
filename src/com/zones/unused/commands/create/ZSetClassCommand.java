package com.zones.unused.commands.create;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.unused.commands.ZoneCommand;

public class ZSetClassCommand extends ZoneCommand {

    public ZSetClassCommand(Zones plugin) {
        super("zsetclass", plugin);
        this.setRequiresZoneSelection(true);
    }

    @Override
    public void run(Player player, String[] vars) {
        if(vars.length != 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /zsetclass ZoneNormal|ZonePlot|ZoneInherit");
            return;
        }
        getZoneSelection(player).setClass(vars[0]);
    }

    

}
