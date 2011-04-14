package com.zones.commands.settings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;

/**
 * 
 * @author Meaglin
 *
 */
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
        if(z.setSetting(ZoneVar.WATER, !z.getSettings().getBool(ZoneVar.WATER, true)))
            player.sendMessage(ChatColor.GREEN.toString() + "Water is now "+(z.getSettings().getBool(ZoneVar.WATER, true) ? "allowed" : "blocked" )+".");
        else
            player.sendMessage(ChatColor.RED.toString() + "Unable to change water flag, please contact a admin.");
        
        return true;
    }
}
