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
        if(z.setSetting(ZoneVar.SPAWN_MOBS, !z.getSettings().getBool(ZoneVar.SPAWN_MOBS, z.getWorldManager().getConfig().MOB_SPAWNING_ENABLED)))
            player.sendMessage(ChatColor.GREEN.toString() + "Mobs spawning is now "+(z.getSettings().getBool(ZoneVar.SPAWN_MOBS, z.getWorldManager().getConfig().MOB_SPAWNING_ENABLED) ? "enabled" : "disabled" )+".");
        else
            player.sendMessage(ChatColor.RED.toString() + "Unable to change mobs flag, please contact a admin.");
        
        return true;
    }
}
