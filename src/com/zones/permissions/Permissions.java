package com.zones.permissions;

import java.util.List;

import org.bukkit.entity.Player;

public abstract class Permissions {
    
    public abstract boolean canUse(String world, String playername, String command);
    public boolean canUse(Player player, String command) { return player.isOp() || canUse(player, player.getWorld().getName(), command); }
    public boolean canUse(Player player, String world, String command) { return player.isOp() || canUse(world, player.getName(), command); }
    
    
    public abstract boolean isValid(String world, String group);
    
    public boolean inGroup(Player player, String group) { return player.isOp() ||inGroup(player, player.getWorld().getName(), group); }
    public boolean inGroup(Player player, String world, String group) { return player.isOp() || inGroup(world, player.getName(), group); }

    public abstract boolean inGroup(String world, String playername, String group);
    
    public abstract String getGroup(String world, String playername);
    public abstract List<String> getGroups(String world, String playername);
    
    public String getGroup(Player player) { return getGroup(player, player.getWorld().getName() ); }
    public String getGroup(Player player, String world) { return getGroup(world , player.getName()); }
    public List<String> getGroups(Player player) { return getGroups(player, player.getWorld().getName()); }
    public List<String> getGroups(Player player, String world) { return getGroups(world , player.getName()); }
    
    public abstract void setGroup(String world, String playername, String group);
    public abstract void addGroup(String world, String playername, String group);
    public abstract void removeGroup(String world, String playername, String group);
    
    public void setGroup(Player player, String group) { setGroup(player, player.getWorld().getName(), group); }
    public void addGroup(Player player, String group) { addGroup(player, player.getWorld().getName(), group); }
    public void removeGroup(Player player, String group) { removeGroup(player, player.getWorld().getName(), group); }
    
    public void setGroup(Player player, String world, String group) { setGroup(world , player.getName(), group); }
    public void addGroup(Player player, String world, String group) { addGroup(world , player.getName(), group); }
    public void removeGroup(Player player, String world, String group) { removeGroup(world , player.getName(), group); }
    
    public abstract void addPermission(String world, String playername, String node);
    public abstract void removePermission(String world, String playername, String node);
    
    public void addPermission(Player player, String node) {addPermission(player,player.getWorld().getName(), node);}
    public void addPermission(Player player, String world, String node) {addPermission(world, player.getName(), node);}
    public void removePermission(Player player, String node) {removePermission(player, player.getWorld().getName(), node);}
    public void removePermission(Player player, String world, String node) {removePermission(world, player.getName(), node);}
    
    public abstract String getPrefix(String world, String playername);
    public abstract String getSuffix(String world, String playername);
    public abstract String getOption(String world, String playername, String option);
    
    public String getPrefix(Player player) { return getPrefix(player, player.getWorld().getName()); }
    public String getPrefix(Player player, String world) { return getPrefix(world, player.getName()); }
    public String getSuffix(Player player) { return getSuffix(player, player.getWorld().getName()); }
    public String getSuffix(Player player, String world) { return getSuffix(world, player.getName()); }
    public String getOption(Player player, String option) { return getOption(player, player.getWorld().getName(), option); }
    public String getOption(Player player, String world, String option) { return getOption(world, player.getName(), option); }
    
    
    public abstract String getName();
}
