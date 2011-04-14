package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.ZonesDummyZone;
import com.zones.commands.ZoneCommand;

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
            getZoneManager().addDummy(player.getEntityId(), new ZonesDummyZone(getPlugin(),player.getWorld(),name));
            player.sendMessage("Entering zone creation mode. Zone name: '" + name + "'");
            player.sendMessage("You can start adding the zone points of this zone by");
            player.sendMessage(ChatColor.RED + " right clicking blocks with " + Material.getMaterial(ZonesConfig.CREATION_TOOL_TYPE).name().toLowerCase() + "[" + ZonesConfig.CREATION_TOOL_TYPE + "].");
            if(ZonesConfig.WORLDEDIT_ENABLED) player.sendMessage("Or you can import a worldedit selection using /zimport");
        }
        return true;
    }

}
