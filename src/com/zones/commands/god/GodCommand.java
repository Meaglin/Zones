package com.zones.commands.god;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.WorldConfig;

public class GodCommand extends ZoneCommand {

    public GodCommand(Zones plugin) {
        super("god_mask", plugin);
        this.setRequiredAccess("zones.god");
    }

    @Override
    public void run(Player player, String[] vars) {
        WorldConfig config = this.getWorldManager(player).getConfig();
        if(!config.GOD_MODE_ENABLED) {
            player.sendMessage(ChatColor.RED + "Godmode is not availeble here.");
            return;
        }
        config.setGodMode(player, !config.hasGodMode(player));
        player.sendMessage(ChatColor.GREEN + "Godmode is now " + (config.hasGodMode(player) ? "enabled" : "disabled") + ".");
    }

}
