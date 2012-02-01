package com.zones.unused.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.ZoneVertice;
import com.zones.unused.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZSetzCommand extends ZoneCommand {
    
    public ZSetzCommand(Zones plugin) {
        super("zsetz", plugin);
        this.setRequiresZoneSelection(true);
    }

    @Override
    public void run(Player player, String[] vars) {
        if (vars.length < 2 || Integer.parseInt(vars[0]) < 0 || Integer.parseInt(vars[0]) > 130 || Integer.parseInt(vars[1]) < 0 || Integer.parseInt(vars[1]) > 130) 
            player.sendMessage(ChatColor.YELLOW + "Usage: /zsetz [min Z] [max Z]");
         else 
            getZoneSelection(player).getSelection().setHeight(new ZoneVertice(Integer.parseInt(vars[0]),Integer.parseInt(vars[1]) ));
        
    }
}
