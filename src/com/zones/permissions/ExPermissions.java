package com.zones.permissions;

import java.util.ArrayList;
import java.util.List;


import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

public class ExPermissions extends Permissions {

    
    private final PermissionManager manager;
    public ExPermissions(PermissionManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean canUse(String world, String playername, String command) {
        return manager.has(playername, command, world);
    }

    @Override
    public boolean inGroup(String world, String playername, String group) {
        return group.equalsIgnoreCase("default") || manager.getUser(playername).inGroup(group, true);
    }

    @Override
    public boolean isValid(String world, String group) {
        PermissionGroup g = manager.getGroup(group);
        if(g == null) return false;
        
        return true;
    }

    @Override
    public String getGroup(String world, String playername) {
        PermissionUser user = manager.getUser(playername);
        if(user == null) return null;
        PermissionGroup[] groups = user.getGroups(world);
        return groups.length > 0 ? groups[0].getName() : null;
    }

    @Override
    public List<String> getGroups(String world, String playername) {
        PermissionUser user = manager.getUser(playername);
        List<String> rt = new ArrayList<String>();
        if(user == null) return rt;
        append(rt, user.getGroups()); // Bah, i guess we have to do it ourselves again....
        return rt;
    }
    
    private List<String> append(List<String> groups, PermissionGroup[] grp) {
        for(PermissionGroup g: grp) {
            groups.add(g.getName());
            append(groups, g.getParentGroups());
        }
        return groups;
    }

    @Override
    public void setGroup(String world, String playername, String group) {
        PermissionUser user = manager.getUser(playername);
        if(user == null) return;
        for(PermissionGroup g : user.getGroups()) {
            user.removeGroup(g);
        }
        if(group == null) return;
        PermissionGroup grp = manager.getGroup(group);
        if(grp == null) return;
        user.addGroup(grp);
    }
    

    @Override
    public void addGroup(String world, String playername, String group) {
        PermissionUser user = manager.getUser(playername);
        if(user == null) return;
        PermissionGroup g = manager.getGroup(group);
        if(g == null) return;
        user.addGroup(g);
    }

    @Override
    public void removeGroup(String world, String playername, String group) {
        PermissionUser user = manager.getUser(playername);
        if(user == null) return;
        user.removeGroup(group, world);
    }

    @Override
    public void addPermission(String world, String playername, String node) {
        PermissionUser user = manager.getUser(playername);
        if(user == null) return;
        user.addPermission(node, world);
    }

    @Override
    public void removePermission(String world, String playername, String node) {
        PermissionUser user = manager.getUser(playername);
        if(user == null) return;
        user.removePermission(node, world);
    }

    @Override
    public String getName() {
        return "PermissionsEx";
    }

    @Override
    public String getPrefix(String world, String playername) {
        PermissionUser user = manager.getUser(playername);
        if(user == null) return "";
        return user.getPrefix(world);
    }

    @Override
    public String getSuffix(String world, String playername) {
        PermissionUser user = manager.getUser(playername);
        if(user == null) return "";
        return user.getSuffix(world);
    }

    @Override
    public String getOption(String world, String playername, String option) {
        PermissionUser user = manager.getUser(playername);
        if(user == null) return "";
        return user.getOption(option, world);
    }

}
