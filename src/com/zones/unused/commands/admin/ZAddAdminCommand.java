package com.zones.unused.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.types.ZoneNormal;
import com.zones.unused.commands.ZoneCommand;

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
    public void run(Player player, String[] vars) {
        if (vars.length >= 1) {
            ZoneNormal zone = (ZoneNormal)getSelectedZone(player);
            for(int i = 0;i < vars.length;i++) {
                addAdmin(player, zone, vars[i]);
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "Usage: /zaddadmin [user 1] <user 2> <user 3>...");
        }
    }
    
    private void addAdmin(Player owner, ZoneNormal zone, String username) {
        if(username == null || username.trim().equals(""))
            return;
        
        // This is fine since it finds the closest match.
        Player p = getPlugin().getServer().getPlayer(username);

        if(p != null)
            username = p.getName();
        
        zone.addAdmin(username);
        owner.sendMessage(ChatColor.GREEN + "Succesfully added player " + username + " as an admin of zone "  + zone.getName() +  " .");
    }

}
