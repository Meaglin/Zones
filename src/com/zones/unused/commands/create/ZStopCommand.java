package com.zones.unused.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.selection.ZoneSelection.Confirm;
import com.zones.unused.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZStopCommand extends ZoneCommand {
    
    public ZStopCommand(Zones plugin) {
        super("zstop", plugin);
        this.setRequiresZoneSelection(true);
    }

    @Override
    public void run(Player player, String[] vars) {
        player.sendMessage(ChatColor.YELLOW.toString() + "Delete the zone? If yes do /zconfirm");
        getZoneSelection(player).setConfirm(Confirm.STOP);
    }
}
