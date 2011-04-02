package com.zones.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.types.ZoneNormal;

/**
 * 
 * @author Meaglin
 *
 */
public class ZRemoveAdminCommand extends ZoneCommand {

    public ZRemoveAdminCommand(Zones plugin) {
        super("zremoveadmin", plugin);
        this.setRequiresSelected(true);
        this.setRequiresAdmin(true);
        this.setRequiresZoneNormal(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if (vars.length == 1) {
            ZoneNormal zone = getSelectedNormalZone(player);
            zone.removeAdmin(vars[0]);
            player.sendMessage(ChatColor.GREEN.toString() + "Succesfully removed player " + vars[0] + " as an admin of zone "  + zone.getName() +  " .");
        } else {
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zaddadmim [user name]");
        }
        return true;
    }

}
