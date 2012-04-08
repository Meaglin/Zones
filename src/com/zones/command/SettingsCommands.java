package com.zones.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneSettings;
import com.zones.model.settings.ZoneVar;
import com.zones.util.Point;

public class SettingsCommands extends CommandsBase {

    private final HashMap<String,Object[]> toggles = new HashMap<String, Object[]>();
    private final Map<String, ZoneVar> lists = new HashMap<String, ZoneVar>();
    private final Map<String, ZoneVar> vars = new HashMap<String, ZoneVar>();
    
    public SettingsCommands(Zones plugin) {
        super(plugin);
        toggles.put("dynamite" ,        new Object[] { "zones.toggle.tnt", "Dynamite", ZoneVar.DYNAMITE } );
        toggles.put("tnt",              new Object[] { "zones.toggle.tnt", "Tnt", ZoneVar.DYNAMITE } );
        toggles.put("food",             new Object[] { "zones.toggle.food", "Food", ZoneVar.FOOD });
        toggles.put("health",           new Object[] { "zones.toggle.health", "Health", ZoneVar.HEALTH } );
        toggles.put("lava" ,            new Object[] { "zones.toggle.lava", "Lava Flow", ZoneVar.LAVA } );
        toggles.put("water" ,           new Object[] { "zones.toggle.water", "Water Flow", ZoneVar.WATER } );
        toggles.put("mobs" ,            new Object[] { "zones.toggle.mobs", "Mobs Spawning", ZoneVar.SPAWN_MOBS } );
        toggles.put("animals" ,         new Object[] { "zones.toggle.animals", "Animals Spawning", ZoneVar.SPAWN_ANIMALS } );
        toggles.put("leafdecay",        new Object[] { "zones.toggle.leafdecay", "Leaf Decay", ZoneVar.LEAF_DECAY });
        toggles.put("teleport",         new Object[] { "zones.toggle.teleport", "Teleporting", ZoneVar.TELEPORT });
        toggles.put("lighter",          new Object[] { "zones.toggle.lighter", "Flint & Steel", ZoneVar.LIGHTER });
        toggles.put("fire",             new Object[] { "zones.toggle.fire", "Fire", ZoneVar.FIRE });
        toggles.put("snowfall",         new Object[] { "zones.toggle.snowfall", "SnowFall", ZoneVar.SNOW_FALL });
        toggles.put("iceform",          new Object[] { "zones.toggle.iceform", "IceForm", ZoneVar.ICE_FORM });
        toggles.put("icemelt",          new Object[] { "zones.toggle.icemelt", "IceMelt", ZoneVar.ICE_MELT });
        toggles.put("snowmelt",         new Object[] { "zones.toggle.snowmelt", "SnowMelt", ZoneVar.SNOW_MELT });
        toggles.put("mushroomspread",   new Object[] { "zones.toggle.mushroomspread", "MushroomSpread", ZoneVar.MUSHROOM_SPREAD});
        toggles.put("vinespread",       new Object[] { "zones.toggle.vinespread", "VineSpread", ZoneVar.VINES_SPREAD});
        toggles.put("physics",          new Object[] { "zones.toggle.physics", "Physics", ZoneVar.PHYSICS });
        toggles.put("notify",           new Object[] { "zones.toggle.notify", "Enters/Leaves Notify's", ZoneVar.NOTIFY });
        toggles.put("crop",             new Object[] { "zones.toggle.crop", "Crop protection", ZoneVar.CROPS_PROTECTED });
        toggles.put("enderman",         new Object[] { "zones.toggle.enderman", "Enderman Grief", ZoneVar.ALLOW_ENDER_GRIEF });
        toggles.put("inheritgroups",    new Object[] { "zones.toggle.inheritgroup", "Inherit Group", ZoneVar.INHERIT_GROUP });
        
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
        vars.put("texturepack",  ZoneVar.TEXTURE_PACK);
        vars.putAll(lists);
    }

    @Command(
            name = "zset",
            aliases = { "" },
            description = 
            "Defines [variables name]'s value as [value]. Variables:\n" +
            "place - [L] blocks which cannot be placed.\n" +
            "break - [L] blocks which cannot be destroyed.\n" +
            "animals - [L] list of animals that can spawn.\n" +
            "mobs - [L] list of mobs that can spawn.\n" +
            "entermessage - The message you see when you enter a zone.\n" +
            "leavemessage - The message you see when you leave a zone.\n" +
            "{zname} - zone name,{pname} - playername,{access} - access\n" +
            "and ^ - colors, Can be used to make the message dynamic.\n" +
            "Disable enter/leave messages by settings them to \"NONE\".\n" +
            "spawnlocation - change the respawn location within the zone.\n" +
            "[L]List variables requires comma separated input: <val1>,<val2>",
            usage = "/<command> [variable name] [value]",
            min = 1,
            requiresPlayer = true,
            requiresSelected = true
    )
    public void set(Player player, String[] params) {
        ZoneVar v = vars.get(params[0].toLowerCase());
        if(v == null) {
            player.sendMessage(ChatColor.RED + "Unknown variable name " + params[0]);
            player.sendMessage(ChatColor.RED + "Usage: /zset [variable name] [value]");
            return;
        }
        
        ZoneBase zone = getSelectedZone(player);
        if(params.length > 1 && params[1].trim().equalsIgnoreCase("reset")) {
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
                zone.getSettings().set(v, new Point(player.getLocation().getBlockX(),player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
                zone.saveSettings();
                player.sendMessage(ChatColor.GREEN + "The respawn location of your zone is now changed to your current location");
                break;
            default:
                if(params.length < 2){
                    player.sendMessage(ChatColor.RED + "Usage: /zset [variable name] <value>");
                    return;
                }  
                String name = "";
                for (int i = 1; i < params.length; i++)
                    name += " " + params[i];
                
                name = name.trim();

                zone.getSettings().set(v, v.unSerialize(name));
                zone.saveSettings();
                player.sendMessage(ChatColor.GREEN + "Variable " + v.getName() + " now changed to " + ZoneSettings.unEscape(v.serialize(zone.getSettings().get(v))));
                break;
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Command(
            name = "zadd",
            aliases = { "za" },
            description = 
            "Adds the [value] to the list [variable name] \n." +
            "List of variables :\n" +
            "place - blocks which cannot be placed.\n" +
            "break - blocks which cannot be destroyed.\n" +
            "animals - list of animals that can spawn.\n" +
            "mobs - list of mobs that can spawn.",
            usage = "/<command> [variable name] [value]",
            min = 1,
            requiresPlayer = true,
            requiresSelected = true
    )
    public void add(Player player, String[] params) {
        ZoneVar v = lists.get(params[0].toLowerCase());
        if(v == null) {
            player.sendMessage(ChatColor.RED + "Unknown variable name " + params[0]);
            player.sendMessage(ChatColor.RED + "Usage: /zadd [variable name] [value]");
            return;
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
            Material m = Material.matchMaterial(params[1]);
            if(m == null) {
                player.sendMessage(ChatColor.RED + "Unknown block " + params[1] + "!");
                return;
            }
            toset.add(m.getId());
            zone.getSettings().set(v, toset);
        } else if (v.getListType().equals(EntityType.class)) {
            List<EntityType> toset = (List<EntityType>) list;
            EntityType t = EntityType.fromName(params[1].substring(0, 1).toUpperCase() + params[1].substring(1).toLowerCase());
            if(t == null) {
                player.sendMessage(ChatColor.RED + "Unknown mob type " + params[1] + "!");
                return;
            }
            toset.add(t);
            zone.getSettings().set(v, toset);
        }
        
        zone.saveSettings();
        player.sendMessage(ChatColor.GREEN + "Variable " + v.getName() + " now changed to " + v.serialize(zone.getSettings().get(v)));
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Command(
            name = "zremove",
            aliases = { "zr" },
            description = 
            "Removes the [value] from the list [variable name] \n." +
            "List of variables :\n" +
            "place - blocks which cannot be placed.\n" +
            "break - blocks which cannot be destroyed.\n" +
            "animals - list of animals that can spawn.\n" +
            "mobs - list of mobs that can spawn.",
            usage = "/<command> [variable name] [value]",
            min = 2,
            requiresPlayer = true,
            requiresSelected = true
    )
    public void remove(Player player, String[] params) {
        ZoneVar v = lists.get(params[0].toLowerCase());
        if(v == null) {
            player.sendMessage(ChatColor.RED + "Unknown variable name " + params[0]);
            player.sendMessage(ChatColor.RED + "Usage: /zadd [variable name] [value]");
            return;
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
            Material m = Material.matchMaterial(params[1]);
            if(m == null) {
                player.sendMessage(ChatColor.RED + "Unknown block " + params[1] + "!");
                return;
            }
            if(!toset.contains(m.getId())) {
                player.sendMessage(ChatColor.RED + "Value " + params[1] + " does not exist within " + params[0] + "!");
                return;
            }
            toset.remove((Object)m.getId());
            zone.getSettings().set(v, toset);
        } else if (v.getListType().equals(EntityType.class)) {
            List<EntityType> toset = (List<EntityType>) list;
            EntityType t = EntityType.fromName(params[1].substring(0, 1).toUpperCase() + params[1].substring(1).toLowerCase());
            if(t == null) {
                player.sendMessage(ChatColor.RED + "Unknown mob type " + params[1] + "!");
                return;
            }
            if(!toset.contains(t)) {
                player.sendMessage(ChatColor.RED + "Value " + params[1] + " does not exist within " + params[0] + "!");
                return;
            }
            toset.remove(t);
            zone.getSettings().set(v, toset);
        }
        
        zone.saveSettings();
        player.sendMessage(ChatColor.GREEN + "Variable " + v.getName() + " now changed to " + v.serialize(zone.getSettings().get(v)));
    }
    
    @Command(
            name = "ztoggle",
            aliases = { "zt" },
            description = 
                "toggles [variable name], options: \n" +
                "lava|water - Toggles lava/water flow into the zone. \n" +
                "mobs|animals - Toggles mobs/animal spawning in the zone.\n" +
                "health - Enables/Disables Health in the zone.\n" +
                "tnt - Enables/Disables tnt explosions in the zone.\n" +
                "leafdecay - Enables/Disables leave decay in the zone.\n" +
                "teleport - Enables/Disables teleporting in/out of the zone.\n" +
                "fire - Enables/Disables fire in the zone.\n" +
                "snowfall|iceform - Enables/Disables snowfall|iceform.\n" +
                "physics - Enables/Disables physics in the zone.\n"  +
                "notify - Toggles enter/leave notifications in the zone.\n" +
                "crop - Toggles Crop Protection.",
            usage = "/<command> [variable name]",
            min = 1,
            max = 1,
            requiresPlayer = true,
            requiresSelected = true
    )
    public void toggle(Player player, String[] params) {
        if(!toggles.containsKey(params[0].toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Invalid variable name.");
            player.sendMessage(ChatColor.YELLOW + "Usage: /ztoggle [tnt|health|lava|water|mobs|animals|leafdecay|fire|lighter|teleport|snowfall|iceform|mushroomspread|icemelt|snowmelt|notify] ");
            return;
        }
        Object[] variable = toggles.get(params[0].toLowerCase());
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
    
    @Command(
            name = "zsetname",
            aliases = { "zsn" },
            description = "Changes to name of the currently selected zone.\nZone name needs to be between 4 and 40 characters long.",
            usage = "/<command> [zone name]",
            min = 1,
            requiresPlayer = true,
            requiresSelected = true
    )
    public void setName(Player player, String[] params) {
        String name = "";
        for (int i = 0; i < params.length; i++)
            name += " " + params[i];

        name = name.substring(1);

        if(name.length() < 4)
            player.sendMessage(ChatColor.RED + "Too short zone name.");
        else if(name.length() > 40)
            player.sendMessage(ChatColor.RED + "Too long zone name.");
        else if(getSelectedZone(player).setName(name))
            player.sendMessage(ChatColor.GREEN + "Successfully changed zone name to " + name + ".");
        else
            player.sendMessage(ChatColor.RED + "Unable to change zone name, please contact a admin.");
    }
}
