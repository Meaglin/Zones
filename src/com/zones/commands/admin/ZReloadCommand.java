package com.zones.commands.admin;

import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;

public class ZReloadCommand extends ZoneCommand {

    public ZReloadCommand(Zones plugin) {
        super("zreload", plugin);
        this.setRequiresAdmin(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        getWorldManager().load();
        getZoneManager().load(getPlugin());
        player.sendMessage("Zones Revision " + Zones.Rev + " reloaded.");
        return true;
    }

}
