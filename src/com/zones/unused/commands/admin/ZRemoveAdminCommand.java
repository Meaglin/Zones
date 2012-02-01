package com.zones.unused.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.types.ZoneInherit;
import com.zones.model.types.ZoneNormal;
import com.zones.unused.commands.ZoneCommand;

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
    public void run(Player player, String[] vars) {
        if (vars.length >= 1) {
            ZoneNormal zone = (ZoneNormal)getSelectedZone(player);
            if(zone instanceof ZoneInherit && !((ZoneInherit)zone).isInheritAdmin(player) ) {
                if(!canUseCommand(player,"zones.admin")) {
                    player.sendMessage(ChatColor.RED + "You're not allowed to remove admin's in this zone.");
                    return;
                }
            } else if(!canUseCommand(player,"zones.admin")) {
                player.sendMessage(ChatColor.RED + "You're not allowed to remove admin's from zones.");
                return;
            }
            for(int i = 0;i < vars.length;i++) {
                removeAdmin(player, zone, vars[i]);
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "Usage: /zremoveadmin [user 1] <user 2> <user 3>...");
        }
    }
    
    private void removeAdmin(Player owner, ZoneNormal zone, String username) {
        if(username == null || username.trim().equals(""))
            return;
        
        // This is fine since it finds the closest match.
        Player p = getPlugin().getServer().getPlayer(username);

        if(p != null)
            username = p.getName();
        
        zone.removeAdmin(username);
        owner.sendMessage(ChatColor.GREEN + "Succesfully removed player " + username + " as an admin of zone "  + zone.getName() +  " .");
    }


}
