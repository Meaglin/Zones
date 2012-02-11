package com.zones.model.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.zones.ZonesConfig;
import com.zones.accessresolver.AccessResolver;
import com.zones.accessresolver.interfaces.Resolver;
import com.zones.model.ZoneBase;
import com.zones.model.ZonesAccess;
import com.zones.model.settings.ZoneVar;
import com.zones.model.types.normal.*;
import com.zones.permissions.Permissions;
import com.zones.persistence.Zone;
import com.zones.util.Point;

/**
 * 
 * @author Meaglin
 *
 */
public class ZoneNormal extends ZoneBase{
    protected List<String>                 adminusers;

    protected HashMap<String, ZonesAccess> groups;
    protected HashMap<String, ZonesAccess> users;
    
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
        resolvers[AccessResolver.PLAYER_ENTER.ordinal()]            = new NormalPlayerEnterResolver();
        resolvers[AccessResolver.PLAYER_TELEPORT.ordinal()]         = new NormalPlayerTeleportResolver();
        resolvers[AccessResolver.PLAYER_RECEIVE_DAMAGE.ordinal()]   = new NormalPlayerDamageResolver();
    }
    
    public ZoneNormal() {
        super();
        adminusers = new ArrayList<String>();

        groups = new HashMap<String, ZonesAccess>();
        users = new HashMap<String, ZonesAccess>();
    }

    public Permissions getPermissions() {
        return getPlugin().getPermissions();
    }
    
    @Override
    protected void onLoad(Zone persistence) {
        super.onLoad(persistence);
        if(persistence.getAdmins() != null && !persistence.getAdmins().trim().equals("")) {
            String[] list = persistence.getAdmins().split(";");
            for (int i = 0; i < list.length; i++) {
                String[] item = list[i].split(",");

                switch (Integer.parseInt(item[0])) {
                    case 1:
                        adminusers.add(item[1].toLowerCase());
                        break;
                    default:
                        log.info("Unknown admin grouptype in zone id: " + getId());
                        break;
                }
            }
        }
        if(persistence.getUsers() != null && !persistence.getUsers().trim().equals("")) {
            String[] list = persistence.getUsers().split(";");
            for (int i = 0; i < list.length; i++) {
                String[] item = list[i].split(",");
                int type = Integer.parseInt(item[0]);

                String itemname = item[1];
                String itemrights = "";
                // compatibility with old system.
                if (item.length < 3)
                    itemrights = "*";
                else
                    itemrights = item[2];

                switch (type) {
                    case 1:
                        users.put(itemname.toLowerCase(), new ZonesAccess(itemrights));
                        break;
                    case 2:
                        if (getPermissions().isValid(getWorld().getName(),itemname))
                            groups.put(itemname, new ZonesAccess(itemrights));
                        else
                            log.info("Invalid grouptype in zone id: " + getId());
                        break;
                    default:
                        log.info("Unknown grouptype in zone id: " + getId());
                        break;
                }
            }
        }
    }
    
    public boolean canModify(Player player, ZonesAccess.Rights right) {

        ZonesAccess z = users.get(player.getName().toLowerCase());
        if (z != null && z.canDo(right))
            return true;

        if(this.getFlag(ZoneVar.INHERIT_GROUP)) {
            List<String> pgroups = getPermissions().getGroups(player, getWorld().getName());
            
            for (Entry<String, ZonesAccess> e : groups.entrySet())
                if (e.getValue().canDo(right)) {
                    if(e.getKey().equals("default"))
                        return true;
                    //if(getPermissions().inGroup(player, e.getKey().toLowerCase()));
                    if (pgroups!= null && pgroups.contains(e.getKey().toLowerCase())) { 
                        return true;
                    }
                }
        } else {
            String group = getPermissions().getGroup(player);
            if(group != null) {
                ZonesAccess a = groups.get(group);
                if(a != null && a.canDo(right)) return true;
            }
        }

        return canAdministrate(player);
    }

    public ZonesAccess getAccess(String group) {
        ZonesAccess z = new ZonesAccess("-");
        for (Entry<String, ZonesAccess> e : groups.entrySet()) {
            if (e.getKey().equalsIgnoreCase(group))
                z = z.merge(e.getValue());
        }
        return z;
    }

    public ZonesAccess getAccess(Player player) {

        // admins can do anything ;).
        if (canAdministrate(player))
            return new ZonesAccess("*");

        // default access with 0 access.
        ZonesAccess base = new ZonesAccess("-");
        String name = player.getName().toLowerCase();

        if (users.containsKey(name))
            base = base.merge(users.get(name));

        List<String> pgroups = getPermissions().getGroups(player, getWorld().getName());
        for (Entry<String, ZonesAccess> e : groups.entrySet())
            if (e.getKey().equals("default") || (pgroups!= null && pgroups.contains(e.getKey()))) {
                base = base.merge(e.getValue());
            }

        return base;
    }

    public boolean canAdministrate(Player player) {
        return isAdmin(player);
    }

    protected boolean isAdmin(Player player) {
        if (getPermissions().canUse(player, getWorld().getName(), "zones.admin"))
            return true;

        if (adminusers.contains(player.getName().toLowerCase()))
            return true;

        return false;
    }
    
    protected boolean isAdminUser(Player player) {
        if (adminusers.contains(player.getName().toLowerCase()))
            return true;
        
        return false;
    }
    
    private String mapToString(HashMap<String, ZonesAccess> map) {
        String rt = "";

        for (Entry<String, ZonesAccess> e : map.entrySet())
            rt += e.getKey() + "[" + e.getValue().toColorCode() + "], ";

        if (rt.equals(""))
            return "";

        rt = rt.substring(0, rt.length() - 2);

        return rt;
    }

    private String adminsToString() {
        String rt = "";

        for (String t : adminusers)
            rt += t + ", ";

        if (rt.equals(""))
            return "";

        rt = rt.substring(0, rt.length() - 2);

        return rt;
    }

    public void sendAccess(Player player) {
        player.sendMessage("AccesList of " + getName() + ":");
        player.sendMessage("   Users: " + mapToString(users) + ".");
        player.sendMessage("   Groups: " + mapToString(groups) + ".");
        player.sendMessage("   Admins: " + adminsToString() + ".");
    }

    public void addUser(String user, ZonesAccess a) {
        user = user.toLowerCase();

        if (users.containsKey(user)) {
            users.remove(user);
        }

        if (!a.canNothing())
            users.put(user, a);

        updateRights();
    }

    public void addGroup(String group, ZonesAccess a) {
        group = group.toLowerCase();

        if (groups.containsKey(group)) {
            groups.remove(group);
        }

        if (!a.canNothing())
            groups.put(group, a);

        updateRights();
    }

    public void addAdmin(String admin) {
        if (adminusers.contains(admin.toLowerCase()))
            return;

        adminusers.add(admin.toLowerCase());
        updateRights();
    }

    public void removeAdmin(String admin) {
        if (adminusers.contains(admin.toLowerCase())) {
            adminusers.remove(admin.toLowerCase());
            updateRights();
        } else
            return;

    }
    
    private void updateRights() {
        String admins = "";
        String users = "";
        for (Entry<String, ZonesAccess> e : this.users.entrySet()) {
            users += "1," + e.getKey() + "," + e.getValue().toString() + ";";
        }
        for (Entry<String, ZonesAccess> e : groups.entrySet()) {
            users += "2," + e.getKey() + "," + e.getValue().toString() + ";";
        }

        if (users.length() > 0)
            users = users.substring(0, users.length() - 1);

        for (String user : adminusers) {
            admins += "1," + user + ";";
        }

        if (admins.length() > 0)
            admins = admins.substring(0, admins.length() - 1);

        getPersistence().setUsers(users);
        getPersistence().setAdmins(admins);
        zones.getMysqlDatabase().update(getPersistence());
        //zones.getDatabase().update(getPersistence());
    }

    public void addUser(String username) {
        addUser(username, new ZonesAccess("*"));
    }

    public void addUser(String username, String access) {
        addUser(username, new ZonesAccess(access));
    }

    public void addGroup(String groupname) {
        addGroup(groupname, new ZonesAccess("*"));
    }

    public void addGroup(String groupname, String access) {
        addGroup(groupname, new ZonesAccess(access));
    }
    
    @Override
    public void onEnter(Player player) {
        ZoneBase zone = zones.getWorldManager(player).getActiveZone(player);
        if (zone == null || zone.getForm().getSize() > getForm().getSize())
            zone = this;

        /*
         * Possible codes:
         * {zname} - Zone name.
         * {access} - BCDEH
         * {pname} - Player name.
         */
        String message = getSettings().getString(ZoneVar.ENTER_MESSAGE, (String)ZoneVar.ENTER_MESSAGE.getDefault(this));
        sendMarkupMessage(message, player);
        if (getFlag(ZoneVar.HEALTH)) {
            this.sendMarkupMessage(ZonesConfig.PLAYER_CAN_DIE_IN_ZONE, player);
        }
        
        if(getSettings().getBool(ZoneVar.NOTIFY, false)) {
            for(Player insidePlayer : getPlayersInside()) {
                if(!insidePlayer.equals(player) && canAdministrate(insidePlayer)) {
                    this.sendMarkupMessage(ZonesConfig.PLAYER_ENTERED_ZONE, player, insidePlayer);
                }
            }
        }
    }

    @Override
    public void onExit(Player player) {
        String message = getSettings().getString(ZoneVar.LEAVE_MESSAGE, (String)ZoneVar.LEAVE_MESSAGE.getDefault(this));
        sendMarkupMessage(message, player);
        
        if(getSettings().getBool(ZoneVar.NOTIFY, false)) {
            for(Player insidePlayer : getPlayersInside()) {
                if(!insidePlayer.equals(player) && canAdministrate(insidePlayer)) {
                    this.sendMarkupMessage(ZonesConfig.PLAYER_LEFT_ZONE, player, insidePlayer);
                }
            }
        }
    }
    
    @Override
    public Location getSpawnLocation(Player player) {
        Object o = getSettings().get(ZoneVar.SPAWN_LOCATION);
        if(o == null) { 
            return null;
        } else {
            Point p = (Point)o;
            return new Location(getWorld(),p.getX(), p.getY(), p.getZ());
        }
    }

    @Override
    public Resolver getResolver(AccessResolver access) {
        return resolvers[access.ordinal()];
    }

}
