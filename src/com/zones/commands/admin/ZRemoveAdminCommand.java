package com.zones.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.types.ZoneInherit;
import com.zones.model.types.ZoneNormal;

/**
 * 
 * @author Meaglin
 *
 */
public class ZRemoveAdminCommand extends ZoneCommand {

    public ZRemoveAdminCommand(Zones plugin) {
        super("zremoveadmin", plugin);
        this.setRequiresSelected(true);
        this.setRequiredClass(ZoneNormal.class);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if (vars.length == 1) {
            ZoneNormal zone = (ZoneNormal)getSelectedZone(player);
            if(zone instanceof ZoneInherit && !((ZoneInherit)zone).isInheritAdmin(player) ) {
                if(!canUseCommand(player,"zones.admin")) {
                    player.sendMessage(ChatColor.RED + "You're not allowed to remove admin's in this zone.");
                    return true;
                }
            } else if(!canUseCommand(player,"zones.admin")) {
                player.sendMessage(ChatColor.RED + "You're not allowed to remove admin's from zones.");
                return true;
            }
            zone.removeAdmin(vars[0]);
            player.sendMessage(ChatColor.GREEN.toString() + "Succesfully removed player " + vars[0] + " as an admin of zone "  + zone.getName() +  " .");
        } else {
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zremoveadmin [user name]");
        }
        return true;
    }

}
