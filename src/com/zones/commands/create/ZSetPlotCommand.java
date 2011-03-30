package com.zones.commands.create;

import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;

public class ZSetPlotCommand extends ZoneCommand {
    
    public ZSetPlotCommand(Zones plugin) {
        super("zsetplot", plugin);
        this.setRequiresDummy(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        getDummy(player).makePlot(player);
        return true;
    }
}
