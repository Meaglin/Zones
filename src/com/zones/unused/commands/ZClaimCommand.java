package com.zones.unused.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.ZoneBase;
import com.zones.model.types.ZonePlot;

public class ZClaimCommand extends ZoneCommand {

    public ZClaimCommand(Zones plugin) {
        super("zclaim", plugin);
    }

    @Override
    public void run(Player player, String[] vars) {
        ZoneBase zone = getWorldManager(player).getActiveZone(player);
        if(!(zone instanceof ZonePlot)) {
            zone.sendMarkupMessage(ChatColor.RED + "You cannot claim {zname} since it's not a claimable zone.", player);
            return;
        }
        ((ZonePlot)zone).claim(player);
    }
    
}
