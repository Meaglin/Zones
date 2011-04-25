package com.zones.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;

public class ZAccessCommand extends ZoneCommand {

    public ZAccessCommand(Zones plugin) {
        super("zaccess", plugin);
    }

    @Override
    public boolean run(Player player, String[] vars) {

        player.sendMessage(ChatColor.GREEN + "Zone Access tags explained:");
        player.sendMessage(ChatColor.GREEN + "b = Build(placing blocks),");
        player.sendMessage(ChatColor.GREEN + "c = Chest Access(accessing chest/furnaces),");
        player.sendMessage(ChatColor.GREEN + "d = Destroy(destroying blocks),");
        player.sendMessage(ChatColor.GREEN + "e = Enter(entering zone), ");
        player.sendMessage(ChatColor.GREEN + "h = Hit(killing mobs/destroying minecarts or boats).");
        return true;
    }

}
