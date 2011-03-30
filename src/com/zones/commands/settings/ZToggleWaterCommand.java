package com.zones.commands.settings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.ZoneBase;
import com.zones.Zones;
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
        if(z.toggleWater())
            player.sendMessage(ChatColor.GREEN.toString() + "Water is now "+(z.isWaterAllowed() ? "allowed" : "blocked" )+".");
        else
            player.sendMessage(ChatColor.RED.toString() + "Unable to change water flag, please contact a admin.");
        
        return true;
    }
}
