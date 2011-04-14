package com.zones.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZonesAccess;
import com.zones.model.types.ZoneNormal;

/**
 * 
 * @author Meaglin
 *
 */
public class ZSetGroupCommand extends ZoneCommand {

    public ZSetGroupCommand(Zones plugin) {
        super("zsetgroup", plugin);
        this.setRequiresSelected(true);
        this.setRequiredClass(ZoneNormal.class);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if (vars.length == 2) {
                ZoneNormal zone = (ZoneNormal)getSelectedZone(player);
                zone.addGroup(vars[0], vars[1]);
                ZonesAccess newAccess = new ZonesAccess(vars[1]);
                player.sendMessage(ChatColor.GREEN.toString() + "Succesfully changed access of group '" + vars[0] + "' of zone '" + zone.getName() + "' to access " + newAccess.textual() + ".");

        } else {
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetgroup [group name] b|c|d|e|h|*|- (combination of these)");
        }
        return true;
    }

}
