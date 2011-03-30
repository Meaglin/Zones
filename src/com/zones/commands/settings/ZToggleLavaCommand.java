package com.zones.commands.settings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.ZoneBase;
import com.zones.Zones;
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
        if(z.toggleLava())
            player.sendMessage(ChatColor.GREEN.toString() + "Lava is now "+(z.isLavaAllowed() ? "allowed" : "blocked" )+".");
        else
            player.sendMessage(ChatColor.RED.toString() + "Unable to change lava flag, please contact a admin.");
        
        return true;
    }
}
