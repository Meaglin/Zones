package com.zones.commands.settings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;

public class ZSetNameCommand extends ZoneCommand {

    public ZSetNameCommand(Zones plugin) {
        super("zsetname", plugin);
        this.setRequiresSelected(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if(vars.length < 1)
            player.sendMessage(ChatColor.YELLOW.toString() + "Usage: /zsetname [zone name]");
        else {
            String name = "";
            for (int i = 0; i < vars.length; i++)
                name += " " + vars[i];

            name = name.substring(1);

            if(name.length() < 4)
                player.sendMessage(ChatColor.RED.toString() + "Too short zone name.");
            else if(name.length() > 40)
                player.sendMessage(ChatColor.RED.toString() + "Too long zone name.");
            else if(getSelectedZone(player).setName(name))
                player.sendMessage(ChatColor.GREEN.toString() + "Succesfully changed zone name to " + name + ".");
            else
                player.sendMessage(ChatColor.RED.toString() + "Unable to change zone name, please contact a admin.");

        }
        return true;
    }

}
