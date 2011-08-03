package com.zones.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Region;
import com.zones.WorldManager;
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
    public void run(Player player, String[] vars) {
        Region r = getWorldManager(player).getRegion(player);
        player.sendMessage(ChatColor.GREEN + "Region[" + r.getX() + "," + r.getY() + "] Zone count: " + r.getZones().size() + ".");
        player.sendMessage(ChatColor.GREEN + "Calculated region: [" + (WorldManager.toInt(player.getLocation().getX()) >> WorldManager.SHIFT_SIZE) + "," + (WorldManager.toInt(player.getLocation().getZ()) >> WorldManager.SHIFT_SIZE) +  "].");
    }

}
