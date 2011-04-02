package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZSetTypeCommand extends ZoneCommand {

    public ZSetTypeCommand(Zones plugin) {
        super("zsettype", plugin);
        this.setRequiresDummy(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        
        if(vars.length == 1){
            if(vars[0].equals("Cuboid"))
                getDummy(player).setType(vars[0]);
            else if(vars[0].equals("NPoly"))
                getDummy(player).setType(vars[0]);
            else{
                player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsettype Cuboid|NPoly - changes zone type.");
                return true;
            }
            player.sendMessage(ChatColor.GREEN.toString() + "Succesfully changed zone type to '" + vars[0] + "' .");

        }else
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsettype Cuboid|NPoly - changes zone type.");
        
        return true;
    }

}
