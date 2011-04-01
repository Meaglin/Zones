package com.zones.commands.settings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.ZoneBase;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.commands.ZoneCommand;

public class ZToggleLavaCommand extends ZoneCommand {

    public ZToggleLavaCommand(Zones plugin) {
        super("ztogglelava", plugin);
        this.setRequiresSelected(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if(!canUseCommand(player,"zones.toggle.lava")) {
            player.sendMessage(ChatColor.RED + "You're not allowed to use this command.");
            return true;
        }
        ZoneBase z = getSelectedZone(player);
        if(z.setSetting(ZonesConfig.LAVA_ENABLED_NAME, !z.getSettings().getBool(ZonesConfig.LAVA_ENABLED_NAME, true)))
            player.sendMessage(ChatColor.GREEN.toString() + "Lava is now "+(z.getSettings().getBool(ZonesConfig.LAVA_ENABLED_NAME, true) ? "allowed" : "blocked" )+".");
        else
            player.sendMessage(ChatColor.RED.toString() + "Unable to change lava flag, please contact a admin.");
        
        return true;
    }
}
