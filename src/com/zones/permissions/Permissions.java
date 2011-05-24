package com.zones.permissions;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

public interface Permissions {
    
    public boolean canUse(Player player, String command);
    
    public boolean inGroup(World world, Player player, String group);
    public String getGroup(Player player);
    public boolean isValid(String world, String group);
    
    public List<String> getGroups(Player player);
}
