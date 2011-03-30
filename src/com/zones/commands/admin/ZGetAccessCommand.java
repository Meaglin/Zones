package com.zones.commands.admin;

import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;

public class ZGetAccessCommand extends ZoneCommand {

    
    public ZGetAccessCommand(Zones plugin) {
        super("zgetaccess", plugin);
        this.setRequiresSelected(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        getSelectedZone(player).sendAccess(player);
        return true;
    }

}
