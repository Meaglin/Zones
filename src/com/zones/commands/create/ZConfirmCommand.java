package com.zones.commands.create;

import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;

/**
 * 
 * @author Meaglin
 *
 */
public class ZConfirmCommand extends ZoneCommand {
    
    public ZConfirmCommand(Zones plugin) {
        super("zconfirm", plugin);
        this.setRequiresDummy(true);
    }

    @Override
    public void run(Player player, String[] vars) {
        getDummy(player).confirm();
    }
}
