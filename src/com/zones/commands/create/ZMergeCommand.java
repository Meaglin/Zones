package com.zones.commands.create;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.ZonesDummyZone;
import com.zones.ZonesDummyZone.Confirm;
import com.zones.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZMergeCommand extends ZoneCommand {
    
    public ZMergeCommand(Zones plugin) {
        super("zmerge", plugin);
        this.setRequiresDummy(true);
        this.setRequiresSelected(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        ZonesDummyZone dummy = getDummy(player);
        if (dummy.getFormId() == 1 && dummy.getCoords().size() != 2) {
            player.sendMessage(ChatColor.RED.toString() + "Not enough coordinates set for this zone type, you need 2.");
            return true;
        }else if(dummy.getFormId() == 2 && dummy.getCoords().size() < 3){
            player.sendMessage(ChatColor.RED.toString() + "Not enough coordinates set for this zone type, you need atleast 3.");
            return true;
        }
        if (dummy.getMax() == 130 && dummy.getMin() == 0)
            player.sendMessage(ChatColor.RED.toString() + "WARNING: default z values not changed!");

        player.sendMessage(ChatColor.YELLOW.toString() + "If you are sure you want to save this zone do /zconfirm");

        dummy.setConfirm(Confirm.MERGE);
        
        return true;
    }
}