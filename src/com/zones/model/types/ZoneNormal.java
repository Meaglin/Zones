package com.zones.model.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.meaglin.json.JSONObject;
import com.zones.ZonesConfig;
import com.zones.accessresolver.AccessResolver;
import com.zones.accessresolver.interfaces.Resolver;
import com.zones.model.ZoneBase;
import com.zones.model.ZonesAccess;
import com.zones.model.settings.ZoneVar;
import com.zones.model.types.normal.NormalBlockFireResolver;
import com.zones.model.types.normal.NormalBlockFromToResolver;
import com.zones.model.types.normal.NormalBlockResolver;
import com.zones.model.types.normal.NormalEntitySpawnResolver;
import com.zones.model.types.normal.NormalPlayerAttackEntityResolver;
import com.zones.model.types.normal.NormalPlayerBlockCreateResolver;
import com.zones.model.types.normal.NormalPlayerBlockDestroyResolver;
import com.zones.model.types.normal.NormalPlayerBlockHitResolver;
import com.zones.model.types.normal.NormalPlayerBlockModifyResolver;
import com.zones.model.types.normal.NormalPlayerDamageResolver;
import com.zones.model.types.normal.NormalPlayerEnterResolver;
import com.zones.model.types.normal.NormalPlayerFoodResolver;
import com.zones.model.types.normal.NormalPlayerHitEntityResolver;
import com.zones.model.types.normal.NormalPlayerTeleportResolver;
import com.zones.persistence.Zone;

/**
 * 
 * @author Meaglin
 *
 */
public class ZoneNormal extends ZoneBase{
    protected Set<UUID>                 admins;
    protected HashMap<String, ZonesAccess> groups;
    protected HashMap<UUID, ZonesAccess> users;
    
    private static final Resolver[] resolvers;
    
    // We don't want to make a new list every time we need a default empty array.
    public static final List<Integer> emptyIntList = new ArrayList<Integer>();
    
    static {
        resolvers = new Resolver[AccessResolver.size()];
        resolvers[AccessResolver.DYNAMITE.ordinal()]        = new NormalBlockResolver(ZoneVar.DYNAMITE);
        resolvers[AccessResolver.LEAF_DECAY.ordinal()]      = new NormalBlockResolver(ZoneVar.LEAF_DECAY);
        resolvers[AccessResolver.SNOW_FALL.ordinal()]       = new NormalBlockResolver(ZoneVar.SNOW_FALL);
        resolvers[AccessResolver.SNOW_MELT.ordinal()]       = new NormalBlockResolver(ZoneVar.SNOW_MELT);
        resolvers[AccessResolver.ICE_FORM.ordinal()]        = new NormalBlockResolver(ZoneVar.ICE_FORM);
        resolvers[AccessResolver.ICE_MELT.ordinal()]        = new NormalBlockResolver(ZoneVar.ICE_MELT);
        resolvers[AccessResolver.MUSHROOM_SPREAD.ordinal()] = new NormalBlockResolver(ZoneVar.MUSHROOM_SPREAD);
        resolvers[AccessResolver.PHYSICS.ordinal()]         = new NormalBlockResolver(ZoneVar.PHYSICS);
        resolvers[AccessResolver.DYNAMITE.ordinal()]        = new NormalBlockResolver(ZoneVar.DYNAMITE);
        resolvers[AccessResolver.LAVA_FLOW.ordinal()]       = new NormalBlockFromToResolver(ZoneVar.WATER);
        resolvers[AccessResolver.WATER_FLOW.ordinal()]      = new NormalBlockFromToResolver(ZoneVar.LAVA);
        resolvers[AccessResolver.FIRE.ordinal()]            = new NormalBlockFireResolver();
        resolvers[AccessResolver.ENTITY_SPAWN.ordinal()]    = new NormalEntitySpawnResolver();
        resolvers[AccessResolver.FOOD.ordinal()]            = new NormalPlayerFoodResolver();
        resolvers[AccessResolver.PLAYER_BLOCK_CREATE.ordinal()]     = new NormalPlayerBlockCreateResolver();
        resolvers[AccessResolver.PLAYER_BLOCK_MODIFY.ordinal()]     = new NormalPlayerBlockModifyResolver();
        resolvers[AccessResolver.PLAYER_BLOCK_DESTROY.ordinal()]    = new NormalPlayerBlockDestroyResolver();
        resolvers[AccessResolver.PLAYER_BLOCK_HIT.ordinal()]        = new NormalPlayerBlockHitResolver();
        resolvers[AccessResolver.PLAYER_ENTITY_HIT.ordinal()]       = new NormalPlayerHitEntityResolver();
        resolvers[AccessResolver.PLAYER_ENTITY_ATTACK.ordinal()]       = new NormalPlayerAttackEntityResolver();
        resolvers[AccessResolver.PLAYER_ENTER.ordinal()]            = new NormalPlayerEnterResolver();
        resolvers[AccessResolver.PLAYER_TELEPORT.ordinal()]         = new NormalPlayerTeleportResolver();
        resolvers[AccessResolver.PLAYER_RECEIVE_DAMAGE.ordinal()]   = new NormalPlayerDamageResolver();
    }
    
    public ZoneNormal() {
        super();
        admins = new HashSet<>();

        groups = new HashMap<>();
        users = new HashMap<>();
    }

    public Permission getPermissions() {
        return getPlugin().getPermissions();
    }
    
    @Override
    protected void upgradeFrom0To1(JSONObject cfg) {
        super.upgradeFrom0To1(cfg);
        if(persistence.getUsers() != null && !persistence.getUsers().trim().equals("")) {
            JSONObject users = getConfig().getJSONObject("users");
            JSONObject groups = getConfig().getJSONObject("groups");
            String[] list = persistence.getUsers().split(";");
            for (int i = 0; i < list.length; i++) {
                String[] item = list[i].split(",");
                int type = Integer.parseInt(item[0]);

                String itemname = item[1];
                String itemrights = item[2];

                switch (type) {
                    case 1:
                        OfflinePlayer p = getPlugin().getOfflinePlayer(itemname);
                        if(p == null) {
                            log.info("Unknown user: " + list[i] + " in zone " + getId());
                            continue;
                        }
                        JSONObject user = new JSONObject();
                        user.put("admin", false);
                        user.put("access", itemrights);
                        user.put("name", p.getName());
                        user.put("uuid", p.getUniqueId().toString());
                        users.put(p.getUniqueId().toString(), user);
                        break;
                    case 2:
                        boolean valid = false;
                        for(String group : getPlugin().getPermissions().getGroups()) {
                            if(group.equalsIgnoreCase(itemname)) {
                                valid = true;
                                break;
                            }
                        }
                        if (valid) {
                            JSONObject group = new JSONObject();
                            group.put("access", itemrights);
                            group.put("name", itemname);
                            groups.put(itemname, group);
                        }
                        else
                            log.info("Invalid grouptype in zone id: " + getId());
                        break;
                    default:
                        log.info("Unknown grouptype in zone id: " + getId());
                        break;
                }
            }
        }
        if(persistence.getAdmins() != null && !persistence.getAdmins().trim().equals("")) {
            JSONObject users = getConfig().getJSONObject("users");
            String[] list = persistence.getAdmins().split(";");
            for (int i = 0; i < list.length; i++) {
                String[] item = list[i].split(",");
                switch (Integer.parseInt(item[0])) {
                    case 1:
                        OfflinePlayer p = getPlugin().getOfflinePlayer(item[1]);
                        if(p == null) {
                            log.info("Unknown user: " + list[i] + " in zone " + getId());
                            continue;
                        }
                        
                        JSONObject user = new JSONObject();
                        user.put("admin", true);
                        user.put("access", "*");
                        user.put("name", p.getName());
                        user.put("uuid", p.getUniqueId().toString());
                        users.put(p.getUniqueId().toString(), user);
                        break;
                    default:
                        log.info("Unknown admin grouptype in zone id: " + getId());
                        break;
                }
            }
        }
    }
    @Override
    protected void onLoad(Zone persistence) {
        super.onLoad(persistence);
        JSONObject usersobject = getConfig().getJSONObject("users");
        for(String uuid : usersobject.keySet()) {
            JSONObject user = usersobject.getJSONObject(uuid);
            if(user.getBoolean("admin")) {
                admins.add(UUID.fromString(user.getString("uuid")));
            } else {
                users.put(UUID.fromString(user.getString("uuid")), ZonesAccess.factory(user.getString("access")));
            }
        }
        JSONObject groupsobject = getConfig().getJSONObject("groups");
        for(String groupname : groupsobject.keySet()) {
            JSONObject group = groupsobject.getJSONObject(groupname);
            groups.put(groupname, ZonesAccess.factory(group.getString("access")));
        }
    }
    
    public boolean canModify(Player player, ZonesAccess.Rights right) {

        ZonesAccess z = users.get(player.getName().toLowerCase());
        if (z != null && z.canDo(right))
            return true;

        if(getFlag(ZoneVar.INHERIT_GROUP)) {
            String[] pgroups = getPermissions().getPlayerGroups(getWorld().getName(), player.getName()); 
            
            for (Entry<String, ZonesAccess> e : groups.entrySet())
                if (e.getValue().canDo(right)) {
                    if(e.getKey().equalsIgnoreCase(ZonesConfig.DEFAULT_GROUP))
                        return true;
                    //if(getPermissions().inGroup(player, e.getKey().toLowerCase()));
                    if (pgroups!= null && contains(pgroups, e.getKey())) { 
                        return true;
                    }
                }
        } else {
            String group = getPermissions().getPrimaryGroup(player);
            if(group != null) {
                ZonesAccess a = groups.get(group);
                if(a != null && a.canDo(right)) {
                    return true;
                }
            }
        }

        return canAdministrate(player);
    }

    @Override
    public ZonesAccess getAccess(String group) {
        ZonesAccess z = ZonesAccess.factory(0);
        for (Entry<String, ZonesAccess> e : groups.entrySet()) {
            if (e.getKey().equalsIgnoreCase(group))
                z = z.merge(e.getValue());
        }
        return z;
    }

    @Override
    public ZonesAccess getAccess(OfflinePlayer player) {

        // admins can do anything ;).
        if (canAdministrate(player)) {
            return ZonesAccess.ALL;
        }

        // default access with 0 access.
        ZonesAccess base = ZonesAccess.factory(0);

        ZonesAccess user = users.get(player.getUniqueId());
        if(user != null) {
            base = base.merge(user);
        }
        
        String[] pgroups = getPermissions().getPlayerGroups(getWorld().getName(), player.getName()); 
        for (Entry<String, ZonesAccess> e : groups.entrySet())
            if (e.getKey().equalsIgnoreCase(ZonesConfig.DEFAULT_GROUP) || (pgroups!= null && contains(pgroups, e.getKey()))) {
                base = base.merge(e.getValue());
            }

        return base;
    }

    @Override
    public boolean canAdministrate(OfflinePlayer player) {
        return isAdmin(player);
    }

    protected boolean isAdmin(OfflinePlayer player) {
        if (getPermissions().has(getWorld().getName(), player.getName(), "zones.admin")) {
            return true;
        }

        if (admins.contains(player.getUniqueId())) {
            return true;
        }

        return false;
    }
    
    public boolean isAdminUser(OfflinePlayer player) {
        return admins.contains(player.getUniqueId());
    }
    
    
    private String mapToString(HashMap<String, ZonesAccess> map) {
        String rt = "";

        for (Entry<String, ZonesAccess> e : map.entrySet()) {
            rt += e.getKey() + "[" + e.getValue().toColorCode() + "], ";
        }

        if (rt.equals("")) {
            return "";
        }
        rt = rt.substring(0, rt.length() - 2);
        return rt;
    }

    private String usersToString() {
        String rt = "";

        JSONObject users = getConfig().getJSONObject("users");
        for(String userid : users.keySet()) {
            JSONObject user = users.getJSONObject(userid);
            if(!user.getBoolean("admin")) {
                rt += user.getString("name") + "[" + (ZonesAccess.factory(user.getString("access")).toColorCode()) + "], ";
            }
            rt += user.getString("name") + ", ";
        }

        if (rt.equals("")) {
            return "";
        }

        rt = rt.substring(0, rt.length() - 2);

        return rt;
    }
    
    private String adminsToString() {
        String rt = "";

        JSONObject users = getConfig().getJSONObject("users");
        for(String userid : users.keySet()) {
            JSONObject user = users.getJSONObject(userid);
            if(user.getBoolean("admin")) {
                rt += user.getString("name") + ", ";
            }
        }

        if (rt.equals(""))
            return "";

        rt = rt.substring(0, rt.length() - 2);

        return rt;
    }

    public void sendAccess(Player player) {
        player.sendMessage("AccessList of " + getName() + ":");
        player.sendMessage("   Users: " + usersToString() + ".");
        player.sendMessage("   Groups: " + mapToString(groups) + ".");
        player.sendMessage("   Admins: " + adminsToString() + ".");
    }
    
    protected void updateRights() {
        zones.getMysqlDatabase().update(getPersistence());
    }
    
    @Override
    public void onEnter(Player player, Location to) {
        ZoneBase zone = zones.getWorldManager(to).getActiveZone(to);
        if(zone == null) zone = this;
        /*
         * Possible codes:
         * {zname} - Zone name.
         * {access} - BCDEH
         * {pname} - Player name.
         */
        String message = getString(ZoneVar.ENTER_MESSAGE);
        sendMarkupMessage(message, player);
        if (getFlag(ZoneVar.HEALTH)) {
            this.sendMarkupMessage(ZonesConfig.PLAYER_CAN_DIE_IN_ZONE, player);
        }
        
        if(ZonesConfig.TEXTURE_MANAGER_ENABLED) {
            String texturepack = zone.getString(ZoneVar.TEXTURE_PACK);
            getPlugin().newTexture(player, texturepack);
        }
        
        if(getFlag(ZoneVar.NOTIFY)) {
            for(Player insidePlayer : getPlayersInside()) {
                if(!insidePlayer.equals(player) && canAdministrate(insidePlayer)) {
                    this.sendMarkupMessage(ZonesConfig.PLAYER_ENTERED_ZONE, player, insidePlayer);
                }
            }
        }
    }

    @Override
    public void onExit(Player player, Location to) {
        String message = getString(ZoneVar.LEAVE_MESSAGE);
        sendMarkupMessage(message, player);
        
        if(getFlag(ZoneVar.NOTIFY)) {
            for(Player insidePlayer : getPlayersInside()) {
                if(!insidePlayer.equals(player) && canAdministrate(insidePlayer)) {
                    this.sendMarkupMessage(ZonesConfig.PLAYER_LEFT_ZONE, player, insidePlayer);
                }
            }
        }
        if(ZonesConfig.TEXTURE_MANAGER_ENABLED) {
            ZoneBase zone = zones.getWorldManager(to).getActiveZone(to);
            
            String texturepack = zone == null ? null : zone.getString(ZoneVar.TEXTURE_PACK);
            getPlugin().newTexture(player, texturepack);
        }
    }
    
    @Override
    public Location getSpawnLocation(Player player) {
        if(getSettings().has(ZoneVar.SPAWN_LOCATION.getName())) {
            JSONObject loc = getSettings().getJSONObject(ZoneVar.SPAWN_LOCATION.getName());
            return new Location(getWorld(), 
                    loc.getDouble("x"),
                    loc.getDouble("y"),
                    loc.getDouble("z"),
                    loc.getFloat("yaw"),
                    loc.getFloat("pitch")
                );
        }
        return null;
    }
    
    public void setAdmin(OfflinePlayer player, boolean isAdmin) {
        String uuid = player.getUniqueId().toString();
        JSONObject userlist = getConfig().getJSONObject("users");
        if(userlist.has(uuid)) {
            JSONObject user = userlist.getJSONObject(uuid);
            if(isAdmin && !user.getBoolean("admin")) {
                users.remove(player.getUniqueId());
                admins.add(player.getUniqueId());
                user.put("admin", true);
            } else if(!isAdmin && user.getBoolean("admin")) {
                userlist.remove(uuid);
                users.remove(player.getUniqueId());
                admins.remove(player.getUniqueId());
            }
        } else {
            if(isAdmin) {
                JSONObject user = new JSONObject();
                user.put("admin", true);
                user.put("access", "*");
                user.put("name", player.getName());
                user.put("uuid", player.getUniqueId().toString());
                userlist.put(uuid, user);
                admins.add(player.getUniqueId());
            }
        }
    }
    
    public ZonesAccess setUser(OfflinePlayer player, String access) {
        ZonesAccess a = ZonesAccess.factory(access);
        String uuid = player.getUniqueId().toString();
        JSONObject userlist = getConfig().getJSONObject("users");
        if(a.canNothing()) {
            userlist.remove(uuid);
            users.remove(player.getUniqueId());
            return a;
        }
        if(userlist.has(uuid)) {
            JSONObject user = userlist.getJSONObject(uuid);
            if(user.getBoolean("admin")) {
                return a;
            }
            user.put("access", a.toString());
        } else {
            JSONObject user = new JSONObject();
            user.put("admin", false);
            user.put("access", a.toString());
            user.put("name", player.getName());
            user.put("uuid", player.getUniqueId().toString());
            userlist.put(uuid, user);
        }
        users.put(player.getUniqueId(), a);
        return a;
    }
    
    public ZonesAccess setGroup(String group, String access) {
        group = group.toLowerCase();
        ZonesAccess a = ZonesAccess.factory(access);
        JSONObject grouplist = getConfig().getJSONObject("groups");
        if(a.canNothing()) {
            grouplist.remove(group);
            groups.remove(group);
            return a;
        }
        if(grouplist.has(group)) {
            JSONObject g = grouplist.getJSONObject(group);
            g.put("access", a.toString());
        } else {
            JSONObject g = new JSONObject();
            g.put("access", a.toString());
            g.put("name", group);
            grouplist.put(group, g);
        }
        groups.put(group, a);
        return a;
    }
    
    public void removeAdmin(JSONObject admin) {
        admins.remove(UUID.fromString(admin.getString("uuid")));
        getConfig().getJSONObject("users").remove(admin.getString("uuid"));
    }
    
    public JSONObject matchUser(String name) {
        JSONObject userlist = getConfig().getJSONObject("users");
        for(String uuid : userlist.keySet()) {
            JSONObject user = userlist.getJSONObject(uuid);
            if(user.getString("name").equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }
    
    private boolean contains(String[] values, String test) {
        for(String t : values) {
            if(t.equalsIgnoreCase(test)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Resolver getResolver(AccessResolver access) {
        return resolvers[access.ordinal()];
    }

}
