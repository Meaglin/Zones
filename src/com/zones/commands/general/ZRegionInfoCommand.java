package com.zones.commands.general;

import org.bukkit.entity.Player;

import com.zones.Region;
import com.zones.Zones;
import com.zones.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZRegionInfoCommand extends ZoneCommand {

    public ZRegionInfoCommand(Zones plugin) {
        super("zregioninfo", plugin);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        Region r = getWorldManager(player).getRegion(player);
        player.sendMessage("Region[" + r.getX() + "," + r.getY() + "] Zone count: " + r.getZones().size() + ".");
        return true;
    }

}
