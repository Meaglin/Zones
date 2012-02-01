package com.zones.unused.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.unused.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZSetTypeCommand extends ZoneCommand {

    public ZSetTypeCommand(Zones plugin) {
        super("zsettype", plugin);
        this.setRequiresZoneSelection(true);
    }

    @Override
    public void run(Player player, String[] vars) {
        
        if(vars.length == 1) {
            if(vars[0].equalsIgnoreCase("Cuboid"))
                getZoneSelection(player).setForm(vars[0]);
            else if(vars[0].equalsIgnoreCase("NPoly"))
                getZoneSelection(player).setForm(vars[0]);
            else
                player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsettype Cuboid|NPoly - changes zone type.");
            
        } else
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsettype Cuboid|NPoly - changes zone type.");
    }

}
