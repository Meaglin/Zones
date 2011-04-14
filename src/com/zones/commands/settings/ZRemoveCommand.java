package com.zones.commands.settings;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.ZonesDummyZone;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;

/**
 * 
 * @author Meaglin
 *
 */
public class ZRemoveCommand extends ZoneCommand {
    
    public ZRemoveCommand(Zones plugin) {
        super("zremove", plugin);
        this.setRequiresSelected(true);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if(vars.length < 2){
            player.sendMessage(ChatColor.RED + "Usage: /zadd [variable name] [value]");
            return true;
        }
        ZoneVar v = ZSet.vars.get(vars[0].toLowerCase());
        if(v == null) {
            player.sendMessage(ChatColor.RED + "Unknown variable name " + vars[0]);
            player.sendMessage(ChatColor.RED + "Usage: /zadd [variable name] [value]");
            return true;
        }
        
        ZoneBase zone = getSelectedZone(player);
        Object o = zone.getSettings().get(v);
        List<?> list = null;
        if(o != null) {
            list = (List<?>) o;
        } else {
            list = new ArrayList();
        }
        
        if(v.getListType().equals(Integer.class)) {
            List<Integer> toset = (List<Integer>) list;
            Material m = Material.matchMaterial(vars[1]);
            if(m == null) {
                player.sendMessage(ChatColor.RED + "Unknown block " + vars[1] + "!");
                return true;
            }
            if(!toset.contains(m.getId())) {
                player.sendMessage(ChatColor.RED + "Value " + vars[1] + " does not exist within " + vars[0] + "!");
                return true;
            }
            toset.remove(m.getId());
            zone.getSettings().set(v, toset);
        } else if (v.getListType().equals(CreatureType.class)) {
            List<CreatureType> toset = (List<CreatureType>) list;
            CreatureType t = CreatureType.fromName(vars[1].substring(0, 1).toUpperCase() + vars[1].substring(1).toLowerCase());
            if(t == null) {
                player.sendMessage(ChatColor.RED + "Unknown mob type " + vars[1] + "!");
                return true;
            }
            if(!toset.contains(t)) {
                player.sendMessage(ChatColor.RED + "Value " + vars[1] + " does not exist within " + vars[0] + "!");
                return true;
            }
            toset.remove(t);
            zone.getSettings().set(v, toset);
        }
        
        zone.saveSettings();
        player.sendMessage(ChatColor.GREEN + "Variable " + v.getName() + " now changed to " + v.serialize(zone.getSettings().get(v)));
        
        return true;
    }
}
