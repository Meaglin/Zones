package com.zones.commands.create;

import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.commands.ZoneCommand;
import com.zones.selection.ZoneSelection;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Meaglin
 */
public class ZImportCommand extends ZoneCommand {
    
    public ZImportCommand(Zones plugin) {
        super("zimport", plugin);
        this.setRequiresZoneSelection(true);
    }

    @Override
    public void run(Player player, String[] vars) {
        if(!ZonesConfig.WORLDEDIT_ENABLED) {
            player.sendMessage(ChatColor.RED + "WorldEdit support is turned off!.");
        } else {
            ZoneSelection selection = getZoneSelection(player);
            if(selection.importWorldeditSelection()) {
                player.sendMessage(ChatColor.YELLOW + "Added your worldedit selection as zone points.");
            } else {
                player.sendMessage(ChatColor.RED + "Invalid/Missing worldedit Selection");
            }
        }
    }
}