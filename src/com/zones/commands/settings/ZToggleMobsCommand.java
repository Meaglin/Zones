package com.zones.commands.settings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.ZoneBase;
import com.zones.Zones;
import com.zones.commands.ZoneCommand;

public class ZToggleMobsCommand extends ZoneCommand {

    public ZToggleMobsCommand(Zones plugin) {
        super("ztoggleanimals", plugin);
        this.setRequiresSelected(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if(!canUseCommand(player,"zones.toggle.mobs")) {
            player.sendMessage(ChatColor.RED + "You're not allowed to use this command.");
            return true;
        }
        ZoneBase z = getSelectedZone(player);
        if(z.toggleMobs())
            player.sendMessage(ChatColor.GREEN.toString() + "Mobs spawning is now "+(z.isMobsAllowed() ? "enabled" : "disabled" )+".");
        else
            player.sendMessage(ChatColor.RED.toString() + "Unable to change mobs flag, please contact a admin.");
        
        return true;
    }
}
