package com.zones.commands.settings;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.commands.ZoneCommand;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneSettings;
import com.zones.model.settings.ZoneVar;

/**
 * 
 * @author Meaglin
 *
 */
public class ZToggleCommand extends ZoneCommand {

    private final HashMap<String,Object[]> variables = new HashMap<String, Object[]>();
    
    
    public ZToggleCommand(Zones plugin) {
        super("ztoggle", plugin);
        this.setRequiresSelected(true);
        variables.put("dynamite" , new Object[] { 
                "zones.toggle.tnt",
                "Dynamite",
                ZoneVar.DYNAMITE
        } );
        variables.put("tnt" , new Object[] { 
                "zones.toggle.tnt",
                "Tnt",
                ZoneVar.DYNAMITE
        } );
        variables.put("health" , new Object[] { 
                "zones.toggle.health",
                "Health",
                ZoneVar.HEALTH
        } );
        variables.put("lava" , new Object[] { 
                "zones.toggle.lava",
                "Lava Flow",
                ZoneVar.LAVA
        } );
        variables.put("water" , new Object[] { 
                "zones.toggle.water",
                ZoneVar.WATER
        } );
        variables.put("mobs" , new Object[] { 
                "zones.toggle.mobs",
                "Mobs Spawning",
                ZoneVar.SPAWN_MOBS
        } );
        variables.put("animals" , new Object[] { 
                "zones.toggle.animals",
                "Animals Spawning",
                ZoneVar.SPAWN_ANIMALS
        } );
        variables.put("leafdecay", new Object[] {
               "zones.toggle.leafdecay",
               "Leaf Decay",
               ZoneVar.LEAF_DECAY
        });
        variables.put("teleport", new Object[] {
               "zones.toggle.teleport",
               "Teleporting",
               ZoneVar.TELEPORT
        });
        variables.put("fire", new Object[] {
                "zones.toggle.fire",
                "Fire",
                ZoneVar.FIRE
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
        Object[] variable = variables.get(vars[0].toLowerCase());
        if(!canUseCommand(player,((String)variable[0]))) {
            player.sendMessage(ChatColor.RED + "You're not allowed to change this variable.");
        } else {
            ZoneBase zone = getSelectedZone(player);
            ZoneSettings settings = zone.getSettings();
            if(zone.setSetting(((ZoneVar)variable[2]), !settings.getBool(((ZoneVar)variable[2]),getDefault(vars[0],zone)))) {
                player.sendMessage(ChatColor.GREEN + ((String)variable[1]) + " is now " + (settings.getBool(((ZoneVar)variable[2]),getDefault(vars[0],zone)) ?  "allowed" : "blocked")+ " in this zone!");
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
