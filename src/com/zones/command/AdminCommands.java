package com.zones.command;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.meaglin.json.JSONObject;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;
import com.zones.model.ZonesAccess;
import com.zones.model.types.ZoneInherit;
import com.zones.model.types.ZoneNormal;
import com.zones.util.Log;

public class AdminCommands extends CommandsBase {
    
    public AdminCommands(Zones plugin) {
        super(plugin);
    }
    
    @Command(
        name = "zaddadmin", 
        aliases = { "zaa" }, 
        description = "Adds at least 1 admin to your selected zone.\nAn admin can adjust all of the settings in your zone and has\nfull access to everything inside.", 
        usage = "/zaddadmin [admin1] <admin2> ...",
        min = 1,
        requiresPlayer = true,
        requiresSelected = true,
        requiredType = ZoneNormal.class
    )
    public void addAdmin(Player player, String[] params) {
        ZoneNormal zone = (ZoneNormal)getSelectedZone(player);
        boolean changed = false;
        for(int i = 0;i < params.length;i++) {
            changed |= addAdmin(player, zone, params[i]);
        }
        if(changed) {
            zone.saveSettings();
        }
    }
    private boolean addAdmin(Player owner, ZoneNormal zone, String username) {
        if(username == null || username.trim().equals("")) {
            return false;
        }
        
        OfflinePlayer player = getPlugin().matchPlayer(username);
        if(player == null) {
            owner.sendMessage(ChatColor.YELLOW + "Cannot find player " + username);
            return false;
        }
        zone.setAdmin(player, true);
        owner.sendMessage(ChatColor.GREEN + "Successfully added player " + player.getName() + " as an admin of zone "  + zone.getName() +  " .");
        return true;
    }
    
    @Command(
        name = "zremoveadmin", 
        aliases = { "zra", "zdeleteadmin", "zdeladmin" }, 
        description = "Removes an admin from the zone.\nNote: only superowning entities can use this commands\nBeing serveradmins or inherited zone admins.", 
        usage = "/<command> [admin1] <admin2> ...",
        min = 1,
        requiresPlayer = true,
        requiresSelected = true,
        requiredType = ZoneNormal.class
    )
    public void removeAdmin(Player player, String[] params) {
        ZoneNormal zone = (ZoneNormal)getSelectedZone(player);
        if(zone instanceof ZoneInherit) {
            if(!((ZoneInherit)zone).isInheritAdmin(player)) {
                player.sendMessage(ChatColor.RED + "You're not allowed to remove admins in this zone.");
                return;
            }
        } else if(!canUseCommand(player,"zones.admin")) {
            player.sendMessage(ChatColor.RED + "You're not allowed to remove admins from zones.");
            return;
        }
        boolean changed = false;
        for(int i = 0;i < params.length;i++) {
            changed |= removeAdmin(player, zone, params[i]);
        }
        if(changed) {
            zone.saveSettings();
        }
    }
    private boolean removeAdmin(Player owner, ZoneNormal zone, String username) {
        if(username == null || username.trim().equals("")) {
            return false;
        }

        JSONObject user = zone.matchUser(username);
        if(user == null || !user.getBoolean("admin")) {
            owner.sendMessage(ChatColor.YELLOW + "Cannot find existing admin with name " + username);
            return false;
        }
        
        zone.removeAdmin(user);
        owner.sendMessage(ChatColor.GREEN + "Successfully removed player " + user.getString("name") + " as an admin of zone "  + zone.getName() +  " .");
        return true;
    }
    
    
    @Command(
        name = "zsetuser", 
        aliases = { "zsu" }, 
        description = 
        "Sets the access of usernames to what is specified\n " +
        "a = Attack(Attacking entity's)," +
        "b = Build(placing blocks),\n" +
        "c = Chest Access(accessing chest/furnaces/note blocks),\n " +
        "d = Destroy(destroying blocks),\n" +
        "e = Enter(entering your zone),\n" +
        "h = Hit(modify/trigger redstone, pickup items),\n" +
        "* = full access(all of the above) " +
        "- = remove all access. \n" +
        "Example: /zsetuser Meaglin bde this will give meaglin access \n" +
        " to build,destroy and walk around in your zone but not to \n" +
        "access your chests.",
        usage = "/<command> [username 1] [access1] <username 2> <accces 2> ...",
        min = 2,
        requiresPlayer = true,
        requiresSelected = true,
        requiredType = ZoneNormal.class
    )
    public void setUser(Player player, String[] params) {
        ZoneNormal zone = (ZoneNormal)getSelectedZone(player);
        boolean changed = false;
        for(int i = 0;i <= floor(params.length/2);i++) {
            try {
                changed |= setUser(player, zone, params[i*2], params[i*2 + 1]);
            } catch(IndexOutOfBoundsException e) {
                break;
            }
        }
        if(changed) {
            zone.saveSettings();
        }
    }
    private int floor(double d) { int rt = (int) d; return rt > d ? rt-1 : rt; }
    private boolean setUser(Player owner, ZoneNormal zone, String username, String access) {
        if (username == null 
                || username.trim().equals("") 
                || access == null 
                || access.trim().equals("")) {
            return false;
        }
        
        OfflinePlayer player = getPlugin().matchPlayer(username);
        if(player == null) {
            owner.sendMessage(ChatColor.YELLOW + "Cannot find player " + username);
            return false;
        }
        if(player instanceof Player && zone.isInsideZone((Player) player)) {
            ZonesAccess acc = zone.getAccess(player);
            if(!acc.equals(ZonesAccess.factory(access))) {
                ((Player) player).sendMessage("Your access in " + zone.getName() + " has been changed from [" + acc.toColorCode() + "] to [" + ZonesAccess.factory(access).toColorCode() + "].");
            }
        }
        ZonesAccess z = zone.setUser(player, access);
        owner.sendMessage(ChatColor.GREEN + "Successfully changed access of user " + username + " of zone '" + zone.getName() + "' to access " + z.textual() + " .");
        return true;
    }
    
    
    @Command(
        name = "zsetgroup", 
        aliases = { "zsg" }, 
        description = 
        "Sets the access of groupnames to what is specified \n " +
        "a = Attack(Attacking entity's)," +
        "b = Build(placing blocks),\n" +
        "c = Chest Access(accessing chest/furnaces/note blocks),\n " +
        "d = Destroy(destroying blocks),\n" +
        "e = Enter(entering your zone),\n" +
        "h = Hit(modify/trigger redstone, pickup items),\n" +
        "* = full access(all of the above) " +
        "- = remove all access. \n" +
        "Example: /zsetuser default bde this will give all users\n" +
        "access to build,destroy and walk around in your zone \n" +
        "but not to access your chests.\n" +
        "Note: Only severadmins can remove 'e' from default group",
        usage = "/<command> [groupname 1] [access 1] <groupname 2> <access 2> ...",
        min = 2,
        requiresPlayer = true,
        requiresSelected = true,
        requiredType = ZoneNormal.class
    )
    public void setGroup(Player player, String[] params) {
        ZoneNormal zone = (ZoneNormal)getSelectedZone(player);
        boolean changed = false;
        for(int i = 0;i < floor(params.length/2);i++) {
            try {
                changed |= setGroup(player, zone, params[i*2], params[i*2 + 1]);
            } catch(IndexOutOfBoundsException e) {
                break;
            }
        }
        if(changed) {
            zone.saveSettings();
        }
    }
    private boolean setGroup(Player owner, ZoneNormal zone, String groupname, String access) {
        if(groupname == null 
                || groupname.trim().equals("") 
                || access == null 
                || access.trim().equals("")) {
            return false;
        }
        

        boolean valid = false;
        for(String group : getPlugin().getPermissions().getGroups()) {
            if(group.equalsIgnoreCase(groupname)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            owner.sendMessage(ChatColor.RED + "Invalid group " + groupname + "!");
            return false;
        }
        
        if(groupname.equalsIgnoreCase(ZonesConfig.DEFAULT_GROUP) && !canUseCommand(owner,"zones.admin")) {
            access += "e";
        }
        
        ZonesAccess z = zone.setGroup(groupname, access);
        owner.sendMessage(ChatColor.GREEN + "Successfully changed access of group '" + groupname + "' of zone '" + zone.getName() + "' to access " + z.textual() + ".");
        return true;
    }
    
    @Command(
        name = "zgetaccess", 
        aliases = { "zga" }, 
        description = "Prints a list of all zoneadmins/users/groups that have\n access in your zone.", 
        requiresPlayer = true,
        requiresSelected = true,
        requiredType = ZoneNormal.class
    )
    public void getAccess(Player player, String[] params) {
        ((ZoneNormal)getSelectedZone(player)).sendAccess(player);
    }
 
    @Command(
            name = "zdelete",
            aliases = { "" },
            description = "Deletes your selected zones.\nWARNING: this is NOT revertible!\nNote: only superowning entities can use this commands\nBeing serveradmins or inherited zone admins.",
            requiresPlayer = true,
            requiresSelected = true
    )
    public void delete(Player player, String[] params) {
        ZoneBase toDelete = getSelectedZone(player);
        if(toDelete instanceof ZoneInherit) {
            if(!((ZoneInherit)toDelete).isInheritAdmin(player)) {
                toDelete.sendMarkupMessage(ChatColor.RED + "You do not have the required permission to delete {zname}.", player);
                return;
            }
        } else if(!canUseCommand(player, "zones.admin")){
            toDelete.sendMarkupMessage(ChatColor.RED + "You do not have the required permission to delete {zname}.", player);
            return;
        }
        if(getPlugin().getZoneManager().delete(toDelete)) {
            player.sendMessage(ChatColor.GREEN + "Successfully deleted zone " + toDelete.getName() + ".");
            Log.info(player.getName() + " delete zone " + toDelete.getName() + "[" + toDelete.getId() + "].");
        } else
            player.sendMessage(ChatColor.RED + "Problems while deleting zone, please contact admin.");
    }
    
}
