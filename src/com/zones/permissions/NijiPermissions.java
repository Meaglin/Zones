package com.zones.permissions;

import java.util.Arrays;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;

public class NijiPermissions implements Permissions {

    private com.nijikokun.bukkit.Permissions.Permissions plugin;
    
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
    public boolean canUse(Player player, String command) {
        return player.isOp() || getHandler().permission(player, command);
    }

    @Override
    public boolean inGroup(World world, Player player, String group) {
        // Enforce default until permissions plugin gets fixed.
        return player.isOp() || group.equalsIgnoreCase("default") || getHandler().inGroup(world.getName(), player.getName(), group);
    }

    @Override
    public String getGroup(Player player) {
        return getHandler().getPrimaryGroup(player.getWorld().getName(), player.getName());
    }

    @Override
    public List<String> getGroups(Player player) {
        return Arrays.asList(getHandler().getGroups(player.getWorld().getName(), player.getName()));
    }

    @Override
    public boolean isValid(String world, String group) {
        return getHandler().getGroupObject(world, group) != null;
    }

}
