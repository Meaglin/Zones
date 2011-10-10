package com.zones.permissions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class BukkitPermissions extends Permissions {

    
    private final Plugin plugin;
    public BukkitPermissions(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean canUse(Player player, String world, String command) {
        if(player.isOp()) return true;
        if(player.hasPermission("*")) return true;
        if(player.hasPermission(command)) return true;
        
        int dotPos = command.lastIndexOf(".");
        while (dotPos > -1) {
            if (player.hasPermission(command.substring(0, dotPos + 1) + "*")) {
                return true;
            }
            dotPos = command.lastIndexOf(".", dotPos - 1);
        }
        
        return false;
    }

    @Override
    public boolean inGroup(Player player, String world, String group) {
        return player.isOp() || canUse(player, world, "group." + group);
    }

    @Override
    public String getGroup(Player player, String world) {
        return (getGroups(player, world).size() == 1  ? getGroups(player, world).get(0) : null);
    }

    @Override
    public List<String> getGroups(Player player, String world) {
        List<String> groupNames = new ArrayList<String>();
        for (PermissionAttachmentInfo permAttach : player.getEffectivePermissions()) {
            String perm = permAttach.getPermission();
            if (!(perm.startsWith("group.") && permAttach.getValue())) {
                continue;
            }
            groupNames.add(perm.substring("group.".length(), perm.length()));
        }
        return groupNames;
    }

    @Override
    public boolean isValid(String world, String group) {
        return group.equals("default");
    }

    @Override
    public void setGroup(Player player, String world, String group) {
        for(String g : getGroups(player, world))
            removePermission(player, world, "group." + g);
        
        addPermission(player, world, "group." + group);
    }

    @Override
    public void addGroup(Player player, String world, String group) {
        addPermission(player, world, "group." + group);
    }

    @Override
    public void removeGroup(Player player, String world, String group) {
        removePermission(player, world, "group." + group);
    }

    @Override
    public void addPermission(Player player, String world, String node) {
        player.addAttachment(plugin, node, true);
    }

    @Override
    public void removePermission(Player player, String world, String node) {
        for(PermissionAttachmentInfo p : player.getEffectivePermissions())
            if(p.getPermission().equals(node))
                player.removeAttachment(p.getAttachment());
    }
    
    @Override
    public String getName() {
        return "GayBukkitPermissions";
    }

    @Override
    public String getPrefix(Player player, String world) {
        return "";
    }

    @Override
    public String getSuffix(Player player, String world) {
        return "";
    }

    @Override
    public String getOption(Player player, String world, String option) {
        return "";
    }

    @Override
    public boolean canUse(String world, String playername, String command) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean inGroup(String world, String playername, String group) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getGroup(String world, String playername) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getGroups(String world, String playername) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGroup(String world, String playername, String group) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addGroup(String world, String playername, String group) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeGroup(String world, String playername, String group) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addPermission(String world, String playername, String node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removePermission(String world, String playername, String node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getPrefix(String world, String playername) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSuffix(String world, String playername) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getOption(String world, String playername, String option) {
        // TODO Auto-generated method stub
        return null;
    }

}
