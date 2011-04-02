package com.zones.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.ZonesAccess;
import com.zones.commands.ZoneCommand;
import com.zones.types.ZoneNormal;

/**
 * 
 * @author Meaglin
 *
 */
public class ZSetGroupCommand extends ZoneCommand {

    public ZSetGroupCommand(Zones plugin) {
        super("zsetgroup", plugin);
        this.setRequiresSelected(true);
        this.setRequiresZoneNormal(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if (vars.length == 2) {
                ZoneNormal zone = getSelectedNormalZone(player);
                zone.addGroup(vars[0], vars[1]);
                ZonesAccess newAccess = new ZonesAccess(vars[1]);
                player.sendMessage(ChatColor.GREEN.toString() + "Succesfully changed access of group '" + vars[0] + "' of zone '" + zone.getName() + "' to access " + newAccess.textual() + ".");

        } else {
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetgroup [group name] b|c|d|e|h|*|- (combination of these)");
        }
        return true;
    }

}
