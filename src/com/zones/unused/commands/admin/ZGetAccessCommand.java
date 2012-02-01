package com.zones.unused.commands.admin;

import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.types.ZoneNormal;
import com.zones.unused.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZGetAccessCommand extends ZoneCommand {

    
    public ZGetAccessCommand(Zones plugin) {
        super("zgetaccess", plugin);
        this.setRequiresSelected(true);
        this.setRequiredClass(ZoneNormal.class);
    }

    @Override
    public void run(Player player, String[] vars) {
       ((ZoneNormal)getSelectedZone(player)).sendAccess(player);
    }

}
