package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.ZonesDummyZone;
import com.zones.commands.ZoneCommand;

public class ZSaveCommand extends ZoneCommand {
    
    public ZSaveCommand(Zones plugin) {
        super("zsave", plugin);
        this.setRequiresDummy(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        ZonesDummyZone dummy = getDummy(player);
        if (dummy.getType() == 1 && dummy.getCoords().size() != 2) {
            player.sendMessage(ChatColor.RED.toString() + "Not enough coordinates set for this zone type, you need 2.");
            return true;
        }else if(dummy.getType() == 2 && dummy.getCoords().size() < 3){
            player.sendMessage(ChatColor.RED.toString() + "Not enough coordinates set for this zone type, you need atleast 3.");
            return true;
        }
        if (dummy.getMax() == 130 && dummy.getMin() == 0)
            player.sendMessage(ChatColor.RED.toString() + "WARNING: default z values not changed!");

        player.sendMessage(ChatColor.YELLOW.toString() + "If you are sure you want to save this zone do /zconfirm");

        dummy.setConfirm("save");
        return true;
    }
}
