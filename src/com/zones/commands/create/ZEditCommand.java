package com.zones.commands.create;

import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;

public class ZEditCommand extends ZoneCommand {
    
    public ZEditCommand(Zones plugin) {
        super("zedit", plugin);
        this.setRequiresDummy(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        
        return true;
    }
}
