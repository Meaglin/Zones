package com.zones.unused.commands.settings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;
import com.zones.unused.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZToggleHealthCommand extends ZoneCommand {

    public ZToggleHealthCommand(Zones plugin) {
        super("ztogglehealth", plugin);
        this.setRequiresSelected(true);
        this.setRequiredAccess("zones.toggle.health");
    }

    @Override
    public void run(Player player, String[] vars) {
        
        ZoneBase z = getSelectedZone(player);
        if(z.setSetting(ZoneVar.HEALTH, !z.getSettings().getBool(ZoneVar.HEALTH, z.getWorldManager().getConfig().PLAYER_HEALTH_ENABLED)))
            player.sendMessage(ChatColor.GREEN.toString() + "Health is now "+(z.getSettings().getBool(ZoneVar.HEALTH, z.getWorldManager().getConfig().PLAYER_HEALTH_ENABLED) ? "enabled" : "disabled" )+".");
        else
            player.sendMessage(ChatColor.RED.toString() + "Unable to change health flag, please contact a admin.");
    }

}