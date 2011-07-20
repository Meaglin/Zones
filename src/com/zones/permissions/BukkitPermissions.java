package com.zones.permissions;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class BukkitPermissions implements Permissions {

    @Override
    public boolean canUse(Player player, String command) {
        return player.isOp();
    }

    @Override
    public boolean inGroup(World world, Player player, String group) {
        return player.isOp();
    }

    @Override
    public String getGroup(Player player) {
        return null;
    }

    @Override
    public List<String> getGroups(Player player) {
        return null;
    }

    @Override
    public boolean isValid(String world, String group) {
        return group.equals("default");
    }

}
