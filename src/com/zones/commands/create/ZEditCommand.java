package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.selection.ZoneEditSelection;

/**
 * 
 * @author Meaglin
 *
 */
public class ZEditCommand extends ZoneCommand {
    
    public ZEditCommand(Zones plugin) {
        super("zedit", plugin);
        this.setRequiresSelected(true);
    }

    @Override
    public void run(Player player, String[] vars) {
        ZoneBase z = getSelectedZone(player);
        ZoneEditSelection selection = new ZoneEditSelection(getPlugin(),player,z.getName());
        getZoneManager().addSelection(player.getEntityId(), selection);
        player.sendMessage(ChatColor.GREEN + "Loaded zone " + z.getName() + " into your edit selection.");
    }
}
