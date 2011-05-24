package com.zones.commands.general;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;

public class ZWhoCommand extends ZoneCommand {

    public ZWhoCommand(Zones plugin) {
        super("zwho", plugin);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        
        
        if(hasSelected(player)) {
            sendZone(player,getSelectedZone(player), null);
        } else {      
            Set<ZoneBase> sorted = new TreeSet<ZoneBase>(new Comparator<ZoneBase>() {
                @Override
                public int compare(ZoneBase o1, ZoneBase o2) {
                    if(o1.getForm().getSize() > o2.getForm().getSize())
                        return 1;
                    else if(o2.getForm().getSize() > o1.getForm().getSize())
                        return -1;
                    else
                        return 0;
                }
                
            });
            sorted.addAll(getWorldManager(player).getActiveZones(player));
            if(sorted.size() > 0) {
                Set<String> usedNames = new HashSet<String>();
                usedNames.add(player.getName());
                for(ZoneBase zone : sorted){
                    sendZone(player, zone, usedNames);
                }
            } else {
                player.sendMessage(ChatColor.GREEN + "No zones found.");
            }
        }
        
        return true;
    }

    
    private void sendZone(Player player, ZoneBase zone, Set<String> usedNames) {
        String msg = "";
        for(Player insidePlayer : zone.getCharactersInside().values()) {
            if(usedNames == null || !usedNames.contains(insidePlayer.getName())) {
                msg += ", " + insidePlayer.getDisplayName();
                if(usedNames != null) usedNames.add(insidePlayer.getName());
            }
        }
        if(!msg.equals("")) {
            player.sendMessage(ChatColor.DARK_GREEN + "Players inside zone " + ChatColor.GREEN + zone.getName() + ChatColor.DARK_GREEN + ":");
            player.sendMessage(msg.substring(2));
        } else {
            player.sendMessage(ChatColor.DARK_GREEN + "Players inside zone " + zone.getName() + ":");
            player.sendMessage("None.");
        }
    }
}