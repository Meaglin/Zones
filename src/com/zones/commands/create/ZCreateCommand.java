package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.ZonesDummyZone;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.model.types.ZoneInherit;

/**
 * 
 * @author Meaglin
 *
 */
public class ZCreateCommand extends ZoneCommand {

    public ZCreateCommand(Zones plugin) {
        super("zcreate", plugin);
        this.setRequiresCreate(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        
        ZoneBase inheritedZone = null;
        if(!canUseCommand(player,"zones.create")) {
            if(hasSelected(player)) {
                inheritedZone = getSelectedZone(player);
                if(!(inheritedZone instanceof ZoneInherit)) {
                    player.sendMessage(ChatColor.RED + "This zone doesn't allow subzoning.");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.RED + "You don't have permission to make global zones.");
                return true;
            }
        }
        
        if (vars.length < 1) {
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zcreate [zone name]");
        } else {
            String name = "";
            for (int i = 0; i < vars.length; i++)
                name += " " + vars[i];

            name = name.substring(1);
            if(name.length() < 4)
            {
                player.sendMessage(ChatColor.RED.toString() + "Too short zone name.");
                return true;
            }
            ZonesDummyZone dummy = new ZonesDummyZone(getPlugin(),player,name);
            dummy.setInherited(inheritedZone);
            getZoneManager().addDummy(player.getEntityId(), dummy);
            player.sendMessage("Entering zone creation mode. Zone name: '" + name + "'");
            player.sendMessage("You can start adding the zone points of this zone by");
            player.sendMessage(ChatColor.RED + "Right clicking blocks with " + Material.getMaterial(ZonesConfig.CREATION_TOOL_TYPE).name().toLowerCase() + "[" + ZonesConfig.CREATION_TOOL_TYPE + "].");
            if(ZonesConfig.WORLDEDIT_ENABLED) player.sendMessage("Or you can import a worldedit selection using /zimport");
        }
        return true;
    }

}
