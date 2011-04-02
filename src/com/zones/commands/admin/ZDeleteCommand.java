package com.zones.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.ZoneBase;
import com.zones.Zones;
import com.zones.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZDeleteCommand extends ZoneCommand {

    public ZDeleteCommand(Zones plugin) {
        super("zdelete", plugin);
        this.setRequiresSelected(true);
        this.setRequiresCreate(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {

        ZoneBase toDelete = getSelectedZone(player);
        if(getZoneManager().delete(toDelete))
            player.sendMessage(ChatColor.GREEN.toString() + "Succesfully deleted zone " + toDelete.getName() + ".");
        else
            player.sendMessage(ChatColor.RED.toString() + "Problems while deleting zone, please contact admin.");
        
        return true;
    }

}
