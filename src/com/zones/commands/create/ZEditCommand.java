package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.ZonesDummyZone;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;

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
    public boolean run(Player player, String[] vars) {
        ZoneBase z = this.getSelectedZone(player);
        ZonesDummyZone dummy = new ZonesDummyZone(getPlugin(),player,z.getName());
        dummy.loadEdit(z);
        getZoneManager().addDummy(player.getEntityId(), dummy);
        player.sendMessage(ChatColor.GREEN + " Loaded zone " + z.getName() + " into your edit selection.");
        
        return true;
    }
}
