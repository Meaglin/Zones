package com.zones.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.types.ZoneNormal;

/**
 * 
 * @author Meaglin
 *
 */
public class ZAddAdminCommand extends ZoneCommand {

    public ZAddAdminCommand(Zones plugin) {
        super("zaddadmin", plugin);
        this.setRequiresSelected(true);
        this.setRequiredClass(ZoneNormal.class);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if (vars.length == 1) {
            ZoneNormal zone = (ZoneNormal)getSelectedZone(player);

            Player p = getPlugin().getServer().getPlayer(vars[0]);

            if(p != null)
                vars[0] = p.getName();

            zone.addAdmin(vars[0]);

            player.sendMessage(ChatColor.GREEN.toString() + "Succesfully added player " + vars[0] + " as an admin of zone "  + zone.getName() +  " .");
        
        } else {
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zaddadmim [user name]");
        }
        return true;
    }

}
