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
public class ZToggleDynamiteCommand extends ZoneCommand {

    public ZToggleDynamiteCommand(Zones plugin) {
        super("ztoggledynamite", plugin);
        this.setRequiresSelected(true);
        this.setRequiredAccess("zones.toggle.tnt");
    }

    @Override
    public void run(Player player, String[] vars) {
        
        ZoneBase z = getSelectedZone(player);
        if(z.setSetting(ZoneVar.DYNAMITE, !z.getSettings().getBool(ZoneVar.DYNAMITE, z.getWorldManager().getConfig().ALLOW_TNT_TRIGGER)))
            player.sendMessage(ChatColor.GREEN + "Dynamite is now "+(z.getSettings().getBool(ZoneVar.DYNAMITE, z.getWorldManager().getConfig().ALLOW_TNT_TRIGGER) ? "enabled" : "disabled" )+".");
        else
            player.sendMessage(ChatColor.RED + "Unable to change dynamite flag, please contact a admin.");
    }
}
