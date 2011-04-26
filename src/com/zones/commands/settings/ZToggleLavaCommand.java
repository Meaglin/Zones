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
public class ZToggleLavaCommand extends ZoneCommand {

    public ZToggleLavaCommand(Zones plugin) {
        super("ztogglelava", plugin);
        this.setRequiresSelected(true);
        this.setRequiredAccess("zones.toggle.lava");
    }

    @Override
    public boolean run(Player player, String[] vars) {
        ZoneBase z = getSelectedZone(player);
        if(z.setSetting(ZoneVar.LAVA, !z.getSettings().getBool(ZoneVar.LAVA, true)))
            player.sendMessage(ChatColor.GREEN.toString() + "Lava is now "+(z.getSettings().getBool(ZoneVar.LAVA, true) ? "allowed" : "blocked" )+".");
        else
            player.sendMessage(ChatColor.RED.toString() + "Unable to change lava flag, please contact a admin.");
        
        return true;
    }
}