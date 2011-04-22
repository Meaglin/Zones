package com.zones.commands;

import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.ZonesDummyZone;

public class CuiValidateCommand extends ZoneCommand {

    public CuiValidateCommand(Zones plugin) {
        super("worldedit", plugin);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        
        
        if(vars.length < 1) return false;
        if(!vars[0].equals("cui")) return false;
        
        if(hasDummy(player)) {
            ZonesDummyZone z = getDummy(player);
            if(!z.hasCUIEnabled()) {
                z.enableCUI();
                return true;
            }
        }
        return false;
    }

}
