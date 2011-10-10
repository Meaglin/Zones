package com.zones.commands.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import java.util.HashMap;


/**
 * 
 * @author Meaglin
 *
 */
public class ZHelpCommand extends ZoneCommand {

    private HashMap<String, String> commandgrp;

    public ZHelpCommand(Zones plugin) {
        super("zhelp", plugin);

        //Adding the different categories
        commandgrp = new HashMap<String, String>();
        commandgrp.put("admin", " - Commands for user administration of a zone");
        commandgrp.put("create", " - Commands for creation of a zone");
        commandgrp.put("general", " - General commands for zones");
        commandgrp.put("settings", " - Commands for settings in zones");
    }
    private static final int ITEMS_PER_PAGE = 8;

    @Override
    public void run(Player player, String[] vars) {
        List<String> availableCommands = new ArrayList<String>();
        String group = null;

        /*
         * Check if group is entered
         */
        if (vars.length > 0) 
            for (Entry<String, String> tmpgrp : commandgrp.entrySet()) 
                if (vars[0].compareTo(tmpgrp.getKey()) == 0) {
                    group = tmpgrp.getKey();
                }


        /*
         * CASE 1: Help for a specific command
         */
        if (group == null && vars.length > 0 && getCommands().containsKey("/" + vars[0].toLowerCase()) && (getCommands().get("/" + vars[0].toLowerCase())[0] == null || canUseCommand(player, getCommands().get("/" + vars[0].toLowerCase())[0]))) {
            String[] info = getCommands().get("/" + vars[0].toLowerCase())[3].split("\n");
            String command = "/" + vars[0].toLowerCase();

            player.sendMessage(ChatColor.BLUE.toString() + "Description of " + command + " :");
            for (String part : info) {
                player.sendMessage(ChatColor.AQUA.toString() + part);
            }

            return;
        }

        /*
         * CASE 2: Group is entered with or without pagenumbers
         */
        if (group != null) {
            for (Entry<String, String[]> entry : getCommands().entrySet()) {
                if ((entry.getValue()[0] == null || canUseCommand(player, entry.getValue()[0])) && (group.compareTo(entry.getValue()[1]) == 0)) {
                    availableCommands.add(ChatColor.BLUE.toString() + entry.getKey() + " " + ChatColor.WHITE.toString() +entry.getValue()[2]);
                }
            }
            
            int amount = 0;           
            if (vars.length > 1) {
                try {
                    amount = Integer.parseInt(vars[1]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED.toString() + "Not a valid page number.");
                }
                if (amount > 1) {
                    amount = (amount - 1) * ITEMS_PER_PAGE;
                } else {
                    amount = 0;
                }
            }
            player.sendMessage(ChatColor.BLUE.toString() + "--------" + ChatColor.WHITE.toString() + " Zone " + group +" commands (Page " + (vars.length == 2 ? vars[1] : "1") + " of " + (int) Math.ceil((double) availableCommands.size() / (double) ITEMS_PER_PAGE) + ") " + ChatColor.BLUE.toString() + "--------");
            player.sendMessage(ChatColor.BLUE.toString() + "[] = required <> = optional:");
            player.sendMessage(ChatColor.BLUE.toString() + "For more info: /zhelp <command name>");
            for (int i = amount; i < amount + ITEMS_PER_PAGE; i++) {
                if (availableCommands.size() > i) {
                    player.sendMessage(availableCommands.get(i));
                }
            }
            return;
        }
        
        /*
         * CASE 3: there are no options or invalid options, show default menu
         */
        player.sendMessage(ChatColor.BLUE.toString() + "---------------" + ChatColor.WHITE.toString() + " Zone Commands " + ChatColor.BLUE.toString() + "---------------");
        for (Entry<String, String> groups : commandgrp.entrySet()) {
            player.sendMessage(ChatColor.BLUE.toString() + "/zhelp " + groups.getKey() + ChatColor.WHITE.toString() + groups.getValue());
        }

    }
}
