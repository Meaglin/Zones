package com.zones.commands.settings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.ZoneBase;
import com.zones.Zones;
import com.zones.commands.ZoneCommand;

public class ZToggleDynamiteCommand extends ZoneCommand {

    public ZToggleDynamiteCommand(Zones plugin) {
        super("ztoggledynamite", plugin);
        this.setRequiresSelected(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if(!canUseCommand(player,"zones.toggle.tnt")) {
            player.sendMessage(ChatColor.RED + "You're not allowed to use this command.");
            return true;
        }
        
        ZoneBase z = getSelectedZone(player);
        if(z.toggleDynamite())
            player.sendMessage(ChatColor.GREEN + "Dynamite is now "+(z.isDynamiteAllowed() ? "enabled" : "disabled" )+".");
        else
            player.sendMessage(ChatColor.RED + "Unable to change dynamite flag, please contact a admin.");
        
        return true;
    }
}
