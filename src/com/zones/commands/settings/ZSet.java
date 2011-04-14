package com.zones.commands.settings;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;

public class ZSet extends ZoneCommand {

    
    public static final Map<String, ZoneVar> vars = new HashMap<String, ZoneVar>();
    
    static {
        vars.put("place", ZoneVar.PLACE_BLOCKS);
        vars.put("protectedplace", ZoneVar.PLACE_BLOCKS);
        vars.put("break", ZoneVar.BREAK_BLOCKS);
        vars.put("protectedbreak", ZoneVar.BREAK_BLOCKS);
        vars.put("allowedanimals", ZoneVar.ANIMALS);
        vars.put("animals", ZoneVar.ANIMALS);
        vars.put("mobs", ZoneVar.MOBS);
        vars.put("allowsmobs" , ZoneVar.MOBS);
    }

    public ZSet(Zones plugin) {
        super("/zset", plugin);
        this.setRequiresSelected(true);
        
    }

    @Override
    public boolean run(Player player, String[] vars) {
        
        if(vars.length < 2){
            player.sendMessage(ChatColor.RED + "Usage: /zset [variable name] [value]");
            return true;
        }
        ZoneVar v = ZSet.vars.get(vars[0].toLowerCase());
        if(v == null) {
            player.sendMessage(ChatColor.RED + "Unknown variable name " + vars[0]);
            player.sendMessage(ChatColor.RED + "Usage: /zset [variable name] [value]");
            return true;
        }
        
        ZoneBase zone = getSelectedZone(player);
        zone.getSettings().set(v, v.unSerialize(vars[1]));
        zone.saveSettings();
        player.sendMessage(ChatColor.GREEN + "Variable " + v.getName() + " now changed to " + v.serialize(zone.getSettings().get(v)));
        return true;
    }

}
