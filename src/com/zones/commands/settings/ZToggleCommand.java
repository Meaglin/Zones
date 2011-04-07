package com.zones.commands.settings;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.util.Settings;

/**
 * 
 * @author Meaglin
 *
 */
public class ZToggleCommand extends ZoneCommand {

    private final HashMap<String,String[]> variables = new HashMap<String, String[]>();
    
    
    public ZToggleCommand(Zones plugin) {
        super("ztoggle", plugin);
        this.setRequiresSelected(true);
        variables.put("dynamite" , new String[] { 
                "zones.toggle.tnt",
                "Dynamite",
                ZonesConfig.DYNAMITE_ENABLED_NAME
        } );
        variables.put("tnt" , new String[] { 
                "zones.toggle.tnt",
                "Tnt",
                ZonesConfig.DYNAMITE_ENABLED_NAME
        } );
        variables.put("health" , new String[] { 
                "zones.toggle.health",
                "Health",
                ZonesConfig.HEALTH_ENABLED_NAME
        } );
        variables.put("lava" , new String[] { 
                "zones.toggle.lava",
                "Lava Flow",
                ZonesConfig.LAVA_ENABLED_NAME,
                Boolean.TRUE.toString()
        } );
        variables.put("water" , new String[] { 
                "zones.toggle.water",
                "Water Flow",
                ZonesConfig.WATER_ENABLED_NAME,
                Boolean.TRUE.toString()
        } );
        variables.put("mobs" , new String[] { 
                "zones.toggle.mobs",
                "Mobs Spawning",
                ZonesConfig.SPAWN_MOBS_NAME
        } );
        variables.put("animals" , new String[] { 
                "zones.toggle.animals",
                "Animals Spawning",
                ZonesConfig.SPAWN_ANIMALS_NAME
        } );
        variables.put("leafdecay", new String[] {
               "zones.toggle.leafdecay",
               "Leaf Decay",
               ZonesConfig.LEAF_DECAY_ENABLED_NAME
        });
        variables.put("teleport", new String[] {
               "zones.toggle.teleport",
               "Teleporting",
               ZonesConfig.ALLOW_TELEPORT_NAME ,
               Boolean.TRUE.toString()
        });
        variables.put("fire", new String[] {
                "zones.toggle.fire",
                "Fire",
                ZonesConfig.ALLOW_FIRE_NAME
        });
    }

    @Override
    public boolean run(Player player, String[] vars) {

        if(vars.length < 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /ztoggle [tnt|health|lava|water|mobs|animals|leafdecay|fire|teleport] ");
            return true;
        }
        
        if(!variables.containsKey(vars[0].toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Invalid variable name.");
            player.sendMessage(ChatColor.YELLOW + "Usage: /ztoggle [tnt|health|lava|water|mobs|animals|leafdecay|fire|teleport] ");
            return true;
        }
        String[] variable = variables.get(vars[0].toLowerCase());
        if(!canUseCommand(player,variable[0])) {
            player.sendMessage(ChatColor.RED + "You're not allowed to change this variable.");
        } else {
            ZoneBase zone = getSelectedZone(player);
            Settings settings = zone.getSettings();
            if(zone.setSetting(variable[2], !settings.getBool(variable[2],getDefault(vars[0],zone)))) {
                player.sendMessage(ChatColor.GREEN + variable[1] + " is now " + (settings.getBool(variable[2],getDefault(vars[0],zone)) ?  "allowed" : "blocked")+ " in this zone!");
            } else {
                player.sendMessage(ChatColor.RED + "Error changing variable, contact an admin.");
            }
        }
        
        return false;
    }
    private static boolean getDefault(String name,ZoneBase zone) {
        if(name.equalsIgnoreCase("tnt") || name.equalsIgnoreCase("dynamite"))
            return zone.getWorldManager().getConfig().ALLOW_TNT_TRIGGER;
        else if(name.equalsIgnoreCase("health"))
            return zone.getWorldManager().getConfig().PLAYER_HEALTH_ENABLED;
        else if(name.equalsIgnoreCase("lava"))
            return true;
        else if(name.equalsIgnoreCase("water"))
            return true;
        else if(name.equalsIgnoreCase("teleport"))
            return true;
        else if(name.equalsIgnoreCase("fire"))
            return zone.getWorldManager().getConfig().FIRE_ENABLED;
        else if(name.equalsIgnoreCase("leafdecay"))
            return zone.getWorldManager().getConfig().LEAF_DECAY_ENABLED;
        else 
            return false;
    }

}
