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
    public void run(Player player, String[] vars) {
        if (vars.length >= 2) {
                ZoneNormal zone = (ZoneNormal)getSelectedZone(player);
                for(int i = 0;i < floor(vars.length/2);i++) {
                    try {
                        setGroup(player, zone, vars[i*2], vars[i*2 + 1]);
                    } catch(IndexOutOfBoundsException e) {
                        break;
                    }
                }
        } else {
            player.sendMessage(ChatColor.YELLOW + "Usage: /zsetgroup [groupname 1] [access 1] <groupname 2> <access 2>...");
        }
    }

    private int floor(double d) { int rt = (int) d; return rt > d ? rt-1 : rt; }
    
    private void setGroup(Player owner, ZoneNormal zone, String groupname, String access) {
        if(groupname == null || groupname.trim().equals("") || access == null || access.trim().equals(""))
            return;
        

        if (!getPlugin().getPermissions().isValid(zone.getWorld().getName(), groupname)) {
            owner.sendMessage(ChatColor.RED + "Invalid group " + groupname + "!");
            return;
        }
        
        if(!canUseCommand(owner,"zones.admin"))
            access += "e";
        
        ZonesAccess z = new ZonesAccess(access);
        zone.addGroup(groupname,z);
        owner.sendMessage(ChatColor.GREEN + "Succesfully changed access of group '" + groupname + "' of zone '" + zone.getName() + "' to access " + z.textual() + ".");
    }
}
