package com.zones.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;

public class ZAboutCommand extends ZoneCommand {

    public ZAboutCommand(Zones plugin) {
        super("zabout", plugin);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        player.sendMessage(ChatColor.GOLD + "Zones Area Protection plugin by Meaglin.");
        player.sendMessage(ChatColor.GREEN + "Bukkit version: " + getPlugin().getDescription().getVersion());
        player.sendMessage(ChatColor.GREEN + "Revision: " + Zones.Rev);
        return true;
    }

}
