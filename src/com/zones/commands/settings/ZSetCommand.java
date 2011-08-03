package com.zones.commands.settings;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneSettings;
import com.zones.model.ZoneVertice;
import com.zones.model.settings.ZoneVar;

public class ZSetCommand extends ZoneCommand {

    
    public static final Map<String, ZoneVar> lists = new HashMap<String, ZoneVar>();
    public static final Map<String, ZoneVar> vars = new HashMap<String, ZoneVar>();
    static {
        lists.put("place", ZoneVar.PLACE_BLOCKS);
        lists.put("protectedplace", ZoneVar.PLACE_BLOCKS);
        lists.put("break", ZoneVar.BREAK_BLOCKS);
        lists.put("protectedbreak", ZoneVar.BREAK_BLOCKS);
        lists.put("allowedanimals", ZoneVar.ANIMALS);
        lists.put("animals", ZoneVar.ANIMALS);
        lists.put("mobs", ZoneVar.MOBS);
        lists.put("allowedmobs" , ZoneVar.MOBS);
        
        vars.put("entermessage", ZoneVar.ENTER_MESSAGE);
        vars.put("leavemessage", ZoneVar.LEAVE_MESSAGE);
        vars.put("spawnlocation",  ZoneVar.SPAWN_LOCATION);
        vars.putAll(lists);
    }

    public ZSetCommand(Zones plugin) {
        super("zset", plugin);
        this.setRequiresSelected(true);
        this.setRequiredAccess("zones.settings.set");
    }

    @Override
    public void run(Player player, String[] vars) {
        
        if(vars.length < 1){
            player.sendMessage(ChatColor.RED + "Usage: /zset [variable name] <value>");
            return;
        }

        ZoneVar v = ZSetCommand.vars.get(vars[0].toLowerCase());
        if(v == null) {
            player.sendMessage(ChatColor.RED + "Unknown variable name " + vars[0]);
            player.sendMessage(ChatColor.RED + "Usage: /zset [variable name] <value>");
            return;
        }
        
        ZoneBase zone = getSelectedZone(player);
        if(vars[0].trim().equalsIgnoreCase("reset")) {
            zone.getSettings().set(v, null);
            zone.saveSettings();
            player.sendMessage(ChatColor.GREEN + "Variable " + v.getName() + " has now been reset to default.");
            return;
        }
        /**
         * Some variables require special treatment.
         */
        switch(v) {
            case SPAWN_LOCATION:
                zone.getSettings().set(v, new ZoneVertice(player.getLocation().getBlockX(),player.getLocation().getBlockZ()));
                zone.saveSettings();
                player.sendMessage(ChatColor.GREEN + "The respawn location of your zone is now changed to your current location");
                break;
            default:
                if(vars.length < 2){
                    player.sendMessage(ChatColor.RED + "Usage: /zset [variable name] <value>");
                    return;
                }  
                String name = "";
                for (int i = 1; i < vars.length; i++)
                    name += " " + vars[i];
                
                name = name.trim();

                zone.getSettings().set(v, v.unSerialize(name));
                zone.saveSettings();
                player.sendMessage(ChatColor.GREEN + "Variable " + v.getName() + " now changed to " + ZoneSettings.unEscape(v.serialize(zone.getSettings().get(v))));
                break;
        }
    }

}
