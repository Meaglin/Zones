package com.zones.unused.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.ZonesAccess;
import com.zones.model.types.ZoneNormal;
import com.zones.unused.commands.ZoneCommand;

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
    public void run(Player player, String[] vars) {
        if (vars.length >= 2) {
                ZoneNormal zone = (ZoneNormal)getSelectedZone(player);
                for(int i = 0;i < floor(vars.length/2);i++) {
                    try {
                        setUser(player, zone, vars[i*2], vars[i*2 + 1]);
                    } catch(IndexOutOfBoundsException e) {
                        break;
                    }
                }
        } else {
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetuser [username 1] [access1] <username 2> <accces 2>...");
        }
    }
    
    private int floor(double d) { int rt = (int) d; return rt > d ? rt-1 : rt; }
    
    private void setUser(Player owner, ZoneNormal zone, String username, String access) {
        if(username == null || username.trim().equals("") || access == null || access.trim().equals(""))
            return;
        
        ZonesAccess z = new ZonesAccess(access);
        // This is fine since it finds the closest match.
        Player p = getPlugin().getServer().getPlayer(username);

        if(p != null)
            username = p.getName();
        zone.addUser(username,z);

        owner.sendMessage(ChatColor.GREEN.toString() + "Succesfully changed access of user " + username + " of zone '" + zone.getName() + "' to access " + z.textual() + " .");
    }

}
