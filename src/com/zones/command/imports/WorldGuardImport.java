package com.zones.command.imports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import com.meaglin.json.JSONArray;
import com.meaglin.json.JSONObject;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Location;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.zones.Zones;
import com.zones.model.settings.ZoneVar;
import com.zones.persistence.Vertice;
import com.zones.persistence.Zone;

public class WorldGuardImport {
    public static List<Zone> importAll(Zones plugin) {
        List<Zone> zones = new ArrayList<>();
        WorldGuardPlugin wg = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        if(wg == null) {
            return zones;
        }
        for(World w : plugin.getServer().getWorlds()) {
            RegionManager m = wg.getRegionManager(w);
            for(Entry<String, ProtectedRegion> e : m.getRegions().entrySet()) {
                Zone z = new Zone();
                ProtectedRegion r = e.getValue();
                if(r instanceof ProtectedCuboidRegion) {
                    Vertice v = new Vertice();
                    v.setVertexorder(0);
                    v.setX(r.getMinimumPoint().getBlockX());
                    v.setZ(r.getMinimumPoint().getBlockZ());
                    z.addVertice(v);
                    v = new Vertice();
                    v.setVertexorder(1);
                    v.setX(r.getMaximumPoint().getBlockX());
                    v.setZ(r.getMaximumPoint().getBlockZ());
                    z.addVertice(v);
                } else if(r instanceof ProtectedPolygonalRegion) {
                    int order = 0;
                    for(BlockVector2D p : ((ProtectedPolygonalRegion) r).getPoints()) {
                        Vertice v = new Vertice();
                        v.setVertexorder(order);
                        v.setX(p.getBlockX());
                        v.setZ(p.getBlockZ());
                        z.addVertice(v);
                        order++;
                    }
                }
                z.setMinY(r.getMinimumPoint().getBlockY());
                z.setMaxY(r.getMaximumPoint().getBlockY());
                JSONObject userlist = z.getConfig().getJSONObject("users");
                JSONObject grouplist = z.getConfig().getJSONObject("groups");
                for(String admin : r.getOwners().getPlayers()) {
                    OfflinePlayer p = plugin.getOfflinePlayer(admin);
                    if(p == null) {
                        // Log?
                        continue;
                    }
                    JSONObject user = new JSONObject();
                    user.put("admin", true);
                    user.put("name", p.getName());
                    user.put("access", "*");
                    user.put("uuid", p.getUniqueId().toString());
                    userlist.put(p.getUniqueId().toString(), user);
                }
                for(String group : r.getOwners().getGroups()) {
                    JSONObject user = new JSONObject();
                    user.put("name", group);
                    user.put("access", "*");
                    grouplist.put(group, user);
                }
                String defaultAccess = "*";
                for(String admin : r.getMembers().getPlayers()) {
                    OfflinePlayer p = plugin.getOfflinePlayer(admin);
                    if(p == null) {
                        // Log?
                        continue;
                    }
                    JSONObject user = new JSONObject();
                    user.put("admin", false);
                    user.put("name", p.getName());
                    user.put("access", defaultAccess);
                    user.put("uuid", p.getUniqueId().toString());
                    userlist.put(p.getUniqueId().toString(), user);
                }
                for(String group : r.getMembers().getGroups()) {
                    JSONObject user = new JSONObject();
                    user.put("name", group);
                    user.put("access", defaultAccess);
                    grouplist.put(group, user);
                }
                parseFlags(z, r);
                zones.add(z);
            }
        }
        return zones;
    }
    
    private static void parseFlags(Zone z, ProtectedRegion r) {
        JSONObject settings = z.getConfig().getJSONObject("settings");
        for (Entry<Flag<?>, Object> e : r.getFlags().entrySet()) {
            switch (e.getKey().getName()) {
                case "passthrough": // TODO: check
                    break;
                // arbitrary
                case "build": 
                    break;
                // deprecated
                case "construct": 
                    break;
                case "pvp":
                    settings.put(ZoneVar.PLAYER_PVP_DAMAGE.getName(), r.getFlag(DefaultFlag.PVP) == State.ALLOW);
                    break;
                case "mob-damage": // specific flag.
                    settings.put(ZoneVar.PLAYER_ENTITY_DAMAGE.getName(), r.getFlag(DefaultFlag.MOB_DAMAGE) == State.ALLOW);
                    break;
                case "mob-spawning":
                    settings.put(ZoneVar.MOBS.getName(), r.getFlag(DefaultFlag.MOB_SPAWNING) == State.ALLOW);
                    break;
                case "creeper-explosion":
                    settings.put(ZoneVar.CREEPER_EXPLOSION.getName(), r.getFlag(DefaultFlag.CREEPER_EXPLOSION) == State.ALLOW);
                    break;
                case "enderdragon-block-damage":
                    break; // TODO: implement
                case "ghast-fireball":
                    break; // TODO: implement
                case "other-explosion":
                    break; // TODO: implement
                case "sleep":
                    break; // TODO: implement
                case "tnt":
                    settings.put(ZoneVar.DYNAMITE.getName(), r.getFlag(DefaultFlag.TNT) == State.ALLOW);
                    break;
                case "lighter":
                    settings.put(ZoneVar.LIGHTER.getName(), r.getFlag(DefaultFlag.TNT) == State.ALLOW);
                    break;
                case "fire-spread":
                    settings.put(ZoneVar.FIRE.getName(), r.getFlag(DefaultFlag.FIRE_SPREAD) == State.ALLOW);
                    break;
                case "lava-fire":
                    settings.put(ZoneVar.FIRE.getName(), r.getFlag(DefaultFlag.LAVA_FIRE) == State.ALLOW);
                    break;
                case "lightning":
                    settings.put(ZoneVar.LIGHTNING.getName(), r.getFlag(DefaultFlag.LIGHTNING) == State.ALLOW);
                    break;
                case "chest-access": // specific flag.
                    break;
                case "water-flow":
                    settings.put(ZoneVar.WATER.getName(), r.getFlag(DefaultFlag.WATER_FLOW) == State.ALLOW);
                    break;
                case "lava-flow":
                    settings.put(ZoneVar.LAVA.getName(), r.getFlag(DefaultFlag.LAVA_FLOW) == State.ALLOW);
                    break;
                case "use": // specific flag.
                    break;
                case "vehicle-place": // specific flag.
                    break;
                case "vehicle-destroy": // specific flag.
                    break;
                case "pistons": // WHY THE FUDGE?
                    break; // TODO: implement
                case "snow-fall":
                    settings.put(ZoneVar.SNOW_FALL.getName(), r.getFlag(DefaultFlag.SNOW_FALL) == State.ALLOW);
                    break;
                case "snow-melt":
                    settings.put(ZoneVar.SNOW_MELT.getName(), r.getFlag(DefaultFlag.SNOW_MELT) == State.ALLOW);
                    break;
                case "ice-form":
                    settings.put(ZoneVar.ICE_FORM.getName(), r.getFlag(DefaultFlag.ICE_FORM) == State.ALLOW);
                    break;
                case "ice-melt":
                    settings.put(ZoneVar.ICE_MELT.getName(), r.getFlag(DefaultFlag.ICE_MELT) == State.ALLOW);
                    break;
                case "mushroom-growth":
                    settings.put(ZoneVar.MUSHROOM_SPREAD.getName(), r.getFlag(DefaultFlag.MUSHROOMS) == State.ALLOW);
                    break;
                case "leaf-decay":
                    settings.put(ZoneVar.LEAF_DECAY.getName(), r.getFlag(DefaultFlag.LEAF_DECAY) == State.ALLOW);
                    break;
                case "grass-growth":
                    settings.put(ZoneVar.GRASS_GROWTH.getName(), r.getFlag(DefaultFlag.GRASS_SPREAD) == State.ALLOW);
                    break;
                case "mycelium-spread":
                    settings.put(ZoneVar.MYCELIUM_SPREAD.getName(), r.getFlag(DefaultFlag.GRASS_SPREAD) == State.ALLOW);
                    break;
                case "vine-growth":
                    settings.put(ZoneVar.VINES_GROWTH.getName(), r.getFlag(DefaultFlag.VINE_GROWTH) == State.ALLOW);
                    break;
                case "soil-dry":
                    settings.put(ZoneVar.SOIL_DRY.getName(), r.getFlag(DefaultFlag.SOIL_DRY) == State.ALLOW);
                    break;
                case "enderman-grief":
                    settings.put(ZoneVar.ENDER_GRIEFING.getName(), r.getFlag(DefaultFlag.ENDER_BUILD) == State.ALLOW);                    
                    break;
                case "invincible":
                    break; // TODO: implement
                case "exp-drops":
                    break; // TODO: implement
                case "send-chat":
                    break;
                case "receive-chat":
                    break;
                case "entry": // specific flag.
                    break;
                case "exit": // specific flag.
                    break;
                case "item-drop": // specific flag.
                    break;
                case "enderpearl":
                    settings.put(ZoneVar.ENDERPEARL.getName(), r.getFlag(DefaultFlag.ENDERPEARL) == State.ALLOW);     
                    break;
                case "entity-painting-destroy": // specific flag.
                    break;
                case "entity-item-frame-destroy": // specific flag.
                    break;
                case "potion-splash":
                    break; // TODO: implement
                case "greeting":
                    settings.put(ZoneVar.ENTER_MESSAGE.getName(), r.getFlag(DefaultFlag.GREET_MESSAGE));
                    break;
                case "farewell":
                    settings.put(ZoneVar.LEAVE_MESSAGE.getName(), r.getFlag(DefaultFlag.FAREWELL_MESSAGE));
                    break;
                case "notify-enter":
                    settings.put(ZoneVar.NOTIFY.getName(), r.getFlag(DefaultFlag.NOTIFY_ENTER));
                    break;
                case "notify-leave":
                    settings.put(ZoneVar.NOTIFY.getName(), r.getFlag(DefaultFlag.NOTIFY_LEAVE));
                    break;
                case "deny-spawn":
                    JSONArray arr = new JSONArray();
                    for(EntityType type : EntityType.values()) {
                        if(!r.getFlag(DefaultFlag.DENY_SPAWN).contains(type)) {
                            arr.add(type.name());
                        }
                    }
                    settings.put(ZoneVar.ALLOWED_ANIMALS.getName(), arr);
                    settings.put(ZoneVar.ALLOWED_MOBS.getName(), arr);
                    break;
                case "game-mode":
                    break; // TODO: implement
                case "heal-delay": // We are not a very statefull plugin.
                    break; // TODO: implement
                case "heal-amount": // We are not a very statefull plugin.
                    break; // TODO: implement
                case "heal-min-health": // We are not a very statefull plugin.
                    break; // TODO: implement
                case "heal-max-health": // We are not a very statefull plugin.
                    break; // TODO: implement
                case "feed-delay": // We are not a very statefull plugin.
                    break; // TODO: implement
                case "feed-amount": // We are not a very statefull plugin.
                    break; // TODO: implement
                case "feed-min-hunger": // We are not a very statefull plugin.
                    break; // TODO: implement
                case "feed-max-hunger": // We are not a very statefull plugin.
                    break; // TODO: implement
                case "teleport":
                    break; // TODO: implement
                case "spawn":
                    JSONObject obj = new JSONObject();
                    Location loc = r.getFlag(DefaultFlag.SPAWN_LOC);
                    obj.put("world", loc.getWorld().getName())
                        .put("x", loc.getPosition().getX())
                        .put("y", loc.getPosition().getY())
                        .put("z", loc.getPosition().getZ())
                        .put("yaw", loc.getYaw())
                        .put("pitch", loc.getPitch());
                    settings.put(ZoneVar.SPAWN_LOCATION.getName(), obj);
                    break;
                case "allow-shop":
                    break; // TODO: implement
                case "buyable":
                    settings.put(ZoneVar.BUY_ALLOWED.getName(), r.getFlag(DefaultFlag.BUYABLE));
                    break;
                case "price":
                    settings.put(ZoneVar.BUY_PRICE.getName(), r.getFlag(DefaultFlag.PRICE));
                    break;
                case "blocked-cmds":
                    settings.put(ZoneVar.DENIED_COMMANDS.getName(), new JSONArray(r.getFlag(DefaultFlag.BLOCKED_CMDS)));
                    break;
                case "allowed-cmds":
                    settings.put(ZoneVar.ALLOWED_COMMANDS.getName(), new JSONArray(r.getFlag(DefaultFlag.ALLOWED_CMDS)));
                    break;
            }
        }
    }
}
