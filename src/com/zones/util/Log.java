package com.zones.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;

public class Log {
    
    public static void info(String message) {
        Zones.log.info("[Zones] " + message);
    }
    
    public static void info(Player player, String message) {
        if(Zones.debugEnabled) {
            info(message);
            if(player != null)player.sendMessage(ChatColor.DARK_BLUE + "[Zones] " + message);
        }
    }
}
