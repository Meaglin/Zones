package com.zones.commands.admin;

import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.types.ZoneNormal;

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
    public boolean run(Player player, String[] vars) {
       ((ZoneNormal)getSelectedZone(player)).sendAccess(player);
        return true;
    }

}
