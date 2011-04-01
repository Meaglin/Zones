package com.zones.commands.settings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.ZoneBase;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.commands.ZoneCommand;

public class ZToggleAnimalsCommand extends ZoneCommand {

    public ZToggleAnimalsCommand(Zones plugin) {
        super("ztoggleanimals", plugin);
        this.setRequiresSelected(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if(!canUseCommand(player,"zones.toggle.animals")) {
            player.sendMessage(ChatColor.RED + "You're not allowed to use this command.");
            return true;
        }
        
        ZoneBase z = getSelectedZone(player);
        if(z.setSetting(ZonesConfig.SPAWN_ANIMALS_NAME, !z.getSettings().getBool(ZonesConfig.SPAWN_ANIMALS_NAME, ZonesConfig.ANIMALS_ENABLED)))
            player.sendMessage(ChatColor.GREEN.toString() + "Animal spawning is now "+(z.getSettings().getBool(ZonesConfig.SPAWN_ANIMALS_NAME, ZonesConfig.ANIMALS_ENABLED) ? "enabled" : "disabled" )+".");
        else
            player.sendMessage(ChatColor.RED.toString() + "Unable to change animals flag, please contact a admin.");
        
        return true;
    }
}
