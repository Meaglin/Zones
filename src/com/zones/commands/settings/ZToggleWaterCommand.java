package com.zones.commands.settings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.ZoneBase;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.commands.ZoneCommand;

public class ZToggleWaterCommand extends ZoneCommand {

    public ZToggleWaterCommand(Zones plugin) {
        super("ztogglewater", plugin);
        this.setRequiresSelected(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if(!canUseCommand(player,"zones.toggle.mobs")) {
            player.sendMessage(ChatColor.RED + "You're not allowed to use this command.");
            return true;
        }
        ZoneBase z = getSelectedZone(player);
        if(z.setSetting(ZonesConfig.WATER_ENABLED_NAME, !z.getSettings().getBool(ZonesConfig.WATER_ENABLED_NAME, true)))
            player.sendMessage(ChatColor.GREEN.toString() + "Water is now "+(z.getSettings().getBool(ZonesConfig.WATER_ENABLED_NAME, true) ? "allowed" : "blocked" )+".");
        else
            player.sendMessage(ChatColor.RED.toString() + "Unable to change water flag, please contact a admin.");
        
        return true;
    }
}
