package com.zones.unused.commands.god;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.WorldConfig;
import com.zones.unused.commands.ZoneCommand;

public class UnGodCommand extends ZoneCommand {

    public UnGodCommand(Zones plugin) {
        super("ungod", plugin);
        this.setRequiredAccess("zones.god");
    }

    @Override
    public void run(Player player, String[] vars) {
        WorldConfig config = this.getWorldManager(player).getConfig();
        if(!config.GOD_MODE_ENABLED) {
            player.sendMessage(ChatColor.RED + "Godmode is not available here.");
            return;
        }
        config.setGodMode(player, false);
        player.sendMessage(ChatColor.GREEN + "Godmode is now disabled.");
    }

}
