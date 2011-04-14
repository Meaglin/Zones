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
public class ZSetUserCommand extends ZoneCommand {

    public ZSetUserCommand(Zones plugin) {
        super("zsetuser", plugin);
        this.setRequiresSelected(true);
        this.setRequiredClass(ZoneNormal.class);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if (vars.length == 2) {
                ZoneNormal zone = (ZoneNormal)getSelectedZone(player);
                ZonesAccess z = new ZonesAccess(vars[1]);

                // TODO : update.
                Player p = getPlugin().getServer().getPlayer(vars[0]);

                if(p != null)
                    vars[0] = p.getName();

                zone.addUser(vars[0], vars[1]);

                

                player.sendMessage(ChatColor.GREEN.toString() + "Succesfully changed access of user " + vars[0] + " of zone '" + zone.getName() + "' to access " + z.textual() + " .");
        } else {
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetuser [user name] b|c|d|e|h|*|- (combination of these) ");
        }
        return true;
    }

}
