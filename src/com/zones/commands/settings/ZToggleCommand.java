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
        variables.put("food", new Object[] {
            "zones.toggle.food",
            "Food",
            ZoneVar.FOOD
        });
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
            "Water Flow",
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
        variables.put("lighter", new Object[] {
           "zones.toggle.lighter",
           "Flint & Steel",
           ZoneVar.LIGHTER
        });
        variables.put("fire", new Object[] {
            "zones.toggle.fire",
            "Fire",
            ZoneVar.FIRE
        });
        variables.put("snowfall", new Object[] {
           "zones.toggle.snowfall",
           "SnowFall",
           ZoneVar.SNOW_FALL
        });
        variables.put("iceform", new Object[] {
            "zones.toggle.iceform",
            "IceForm",
            ZoneVar.ICE_FORM
         });
        variables.put("icemelt", new Object[] {
           "zones.toggle.icemelt",
           "IceMelt",
           ZoneVar.ICE_MELT
        });
        variables.put("snowmelt", new Object[] {
           "zones.toggle.snowmelt",
           "SnowMelt",
           ZoneVar.SNOW_MELT
        });
        variables.put("mushroomspread", new Object[] {
            "zones.toggle.mushroomspread",
            "MushroomSpread",
            ZoneVar.MUSHROOM_SPREAD
         });
        variables.put("physics", new Object[] {
           "zones.toggle.physics",
           "Physics",
           ZoneVar.PHYSICS
        });
        variables.put("notify", new Object[] {
            "zones.toggle.notify",
            "Enters/Leaves Notify's",
            ZoneVar.NOTIFY
        });
        variables.put("crop", new Object[] {
                "zones.toggle.crop",
                "Crop protection",
                ZoneVar.CROPS_PROTECTED
        });
        variables.put("enderman", new Object[] {
                "zones.toggle.enderman",
                "Enderman Grief",
                ZoneVar.ALLOW_ENDER_GRIEF
        });
        variables.put("inheritgroups", new Object[] {
           "zones.toggle.inheritgroup",
           "Inherit Group",
           ZoneVar.INHERIT_GROUP
        });
    }

    @Override
    public void run(Player player, String[] vars) {

        if(vars.length < 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /ztoggle [tnt|health|lava|water|mobs|animals|leafdecay|fire|lighter|teleport|snowfall|iceform|mushroomspread|icemelt|snowmelt|notify] ");
            return;
        }
        
        if(!variables.containsKey(vars[0].toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Invalid variable name.");
            player.sendMessage(ChatColor.YELLOW + "Usage: /ztoggle [tnt|health|lava|water|mobs|animals|leafdecay|fire|lighter|teleport|snowfall|iceform|mushroomspread|icemelt|snowmelt|notify] ");
            return;
        }
        Object[] variable = variables.get(vars[0].toLowerCase());
        if(!canUseCommand(player,((String)variable[0]))) {
            player.sendMessage(ChatColor.RED + "You're not allowed to change this variable.");
        } else {
            ZoneBase zone = getSelectedZone(player);
            ZoneSettings settings = zone.getSettings();
            ZoneVar var = ((ZoneVar)variable[2]);
            if(zone.setSetting(var, !settings.getBool(var,(Boolean)var.getDefault(zone)))) {
                player.sendMessage(ChatColor.GREEN + ((String)variable[1]) + " is now " + (settings.getBool(var,(Boolean)var.getDefault(zone)) ?  "allowed" : "blocked")+ " in this zone!");
            } else {
                player.sendMessage(ChatColor.RED + "Error changing variable, contact an admin.");
            }
        }
    }

}
