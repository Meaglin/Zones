package com.zones.commands.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;

public class ZHelpCommand extends ZoneCommand {

    public ZHelpCommand(Zones plugin) {
        super("zhelp", plugin);
    }

    private static final int ITEMS_PER_PAGE = 7;
    @Override
    public boolean run(Player player, String[] vars) {
        List<String> availableCommands = new ArrayList<String>();

        for (Entry<String, String[]> entry : getCommands().entrySet())
            if (entry.getValue()[0] == null || canUseCommand(player, entry.getValue()[0]))
                availableCommands.add(entry.getKey() + " " + entry.getValue()[1]);

        int amount = 0;
        boolean isCommand = false;
        if (vars.length > 0) {
            try {
                amount = Integer.parseInt(vars[0]);
            } catch (NumberFormatException ex) {

                if(getCommands().containsKey("/" + vars[0].toLowerCase()) && (getCommands().get("/" + vars[0].toLowerCase())[0] == null || canUseCommand(player, getCommands().get("/" + vars[0].toLowerCase())[0])))
                    isCommand = true;
                else
                    player.sendMessage(ChatColor.RED.toString() + "Not a valid page number.");
            }
            if (amount > 1)
                amount = (amount - 1) * ITEMS_PER_PAGE;
            else
                amount = 0;
        }
        if(isCommand){
            String[] info = getCommands().get("/" + vars[0].toLowerCase())[2].split("\n");
            String command = "/" + vars[0].toLowerCase();

            player.sendMessage(ChatColor.BLUE.toString() + "Description of " + command + " :");
            for(String part : info)
                player.sendMessage(ChatColor.AQUA.toString() + part);

            return true;
        }

        player.sendMessage(ChatColor.BLUE.toString() + "Available commands (Page " + (vars.length == 1 ? vars[0] : "1") + " of " + (int) Math.ceil((double) availableCommands.size() / (double) ITEMS_PER_PAGE) + ") [] = required <> = optional:");
        player.sendMessage(ChatColor.BLUE.toString() + "For more info: /zhelp <command name>");
        for (int i = amount; i < amount + ITEMS_PER_PAGE; i++)
            if (availableCommands.size() > i)
                player.sendMessage(ChatColor.RED.toString() + availableCommands.get(i));
        
        return true;
    }

}
