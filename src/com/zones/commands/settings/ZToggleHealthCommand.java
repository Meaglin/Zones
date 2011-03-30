package com.zones.commands.settings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.ZoneBase;
import com.zones.Zones;
import com.zones.commands.ZoneCommand;

public class ZToggleHealthCommand extends ZoneCommand {

    public ZToggleHealthCommand(Zones plugin) {
        super("ztogglehealth", plugin);
        this.setRequiresSelected(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if(!canUseCommand(player,"zones.toggle.health")) {
            player.sendMessage(ChatColor.RED + "You're not allowed to use this command.");
            return true;
        }
        
        ZoneBase z = getSelectedZone(player);
        if(z.toggleHealth())
            player.sendMessage(ChatColor.GREEN.toString() + "Health is now "+(z.isHealthAllowed() ? "enabled" : "disabled" )+".");
        else
            player.sendMessage(ChatColor.RED.toString() + "Unable to change health flag, please contact a admin.");
        
        return true;
    }

}
