package com.zones.commands.create;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;

public class ZSetClassCommand extends ZoneCommand {

    public ZSetClassCommand(Zones plugin) {
        super("zsetclass", plugin);
        this.setRequiresDummy(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if(vars.length != 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /zsetclass ZoneNormal|ZonePlot|ZoneInherit");
            return true;
        }
        this.getDummy(player).setClass(vars[0]);
        return true;
    }

    

}
