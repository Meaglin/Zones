package com.zones.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.util.Log;

/**
 * 
 * @author Meaglin
 *
 */
public class ZDeleteCommand extends ZoneCommand {

    public ZDeleteCommand(Zones plugin) {
        super("zdelete", plugin);
        this.setRequiresSelected(true);
        this.setRequiresAdmin(true);
    }

    @Override
    public void run(Player player, String[] vars) {

        ZoneBase toDelete = getSelectedZone(player);
        if(getZoneManager().delete(toDelete)) {
            player.sendMessage(ChatColor.GREEN.toString() + "Succesfully deleted zone " + toDelete.getName() + ".");
            Log.info(player.getName() + " delete zone " + toDelete.getName() + "[" + toDelete.getId() + "].");
        } else
            player.sendMessage(ChatColor.RED.toString() + "Problems while deleting zone, please contact admin.");
    }

}
