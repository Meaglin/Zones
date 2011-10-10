package com.zones.permissions;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.nijiko.permissions.Entry;
import com.nijiko.permissions.Group;
import com.nijiko.permissions.PermissionHandler;
import com.nijiko.permissions.User;

public class NijiPermissions extends Permissions {

    private final com.nijikokun.bukkit.Permissions.Permissions plugin;
    
    public NijiPermissions(com.nijikokun.bukkit.Permissions.Permissions plugin) {
        this.plugin = plugin;
    }
    
    public com.nijikokun.bukkit.Permissions.Permissions getPlugin() {
        return plugin;
    }
    
    public PermissionHandler getHandler() {
        return getPlugin().getHandler();
    }
    
    @Override
    public boolean canUse(String world, String playername, String command) {
        return getHandler().permission(world, playername, command);
    }

    @Override
    public boolean inGroup(String world, String playername, String group) {
        // Enforce default until permissions plugin gets fixed.
        return group.equalsIgnoreCase("default") || getHandler().inGroup(world, playername, group);
    }

    @Override
    public String getGroup(String world, String playername) {
        return getHandler().getPrimaryGroup(world, playername);
    }

    @Override
    /*
     * I seriously couldn't find a more efficient way.
     * /sadface
     */
    public List<String> getGroups(String world, String playername) {
        User u = getHandler().getUserObject(world, playername);
        List<String> groups = new ArrayList<String>();
        if(u == null) return groups;
        LinkedHashSet<Entry> ancestors = u.getAncestors();
        if(ancestors == null) return groups;
        for(Entry e : ancestors)
            if(e.getWorld().equals(world))
                groups.add(e.getName().toLowerCase());
        
        return groups;
        //return getHandler().getGroups(player.getWorld().getName(), player.getName());
    }

    @Override
    public boolean isValid(String world, String group) {
        return getHandler().getGroupObject(world, group) != null;
    }

    @Override
    public void setGroup(String world, String playername, String group) {
        try {
            User user = getHandler().safeGetUser(world, playername);
            Group grp = getHandler().getGroupObject(world, group);
            for(Entry e : user.getParents())
                if(e instanceof Group)
                    user.removeParent((Group)e);
            if(grp == null) return;
            user.addParent(grp);
        
        } catch(Exception e) { }
    }
        

    @Override
    public void addGroup(String world, String playername, String group) {
        try {
            User user = getHandler().safeGetUser(world, playername);
            Group grp = getHandler().getGroupObject(world, group);
            if(grp == null) return;
            user.addParent(grp);
        } catch(Exception e) { }
    }

    @Override
    public void removeGroup(String world, String playername, String group) {
        try {
            User user = getHandler().safeGetUser(world, playername);
            Group grp = getHandler().getGroupObject(world, group);
            if(grp == null) return;
            user.removeParent(grp);
        } catch(Exception e) { }
    }

    @Override
    public void addPermission(String world, String playername, String node) {
        try {
            User user = getHandler().safeGetUser(world, playername);
            user.addPermission(node);
        } catch(Exception e) { }
    }

    @Override
    public void removePermission(String world, String playername, String node) {
        try {
            User user = getHandler().safeGetUser(world, playername);
            user.removePermission(node);
        } catch(Exception e) { }
    }

    @Override
    public String getName() {
        return "NijiPermissions";
    }

    @Override
    public String getPrefix(String world, String playername) {
        return getHandler().getUserPrefix(world, playername);
    }

    @Override
    public String getSuffix(String world, String playername) {
        return getHandler().getUserSuffix(world, playername);
    }

    @Override
    public String getOption(String world, String playername, String option) {
        return getHandler().getInfoString(world, playername, option, false);
    }

}
