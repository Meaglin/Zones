package com.zones.model.types;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import com.zones.WorldManager;
import com.zones.Zones;
import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;
import com.zones.model.ZonesAccess;
import com.zones.model.ZonesAccess.Rights;
import com.zones.model.settings.ZoneVar;

/**
 * 
 * @author Meaglin
 *
 */
public class ZoneNormal extends ZoneBase{

    private List<String>                 admingroups;
    private List<String>                 adminusers;

    private HashMap<String, ZonesAccess> groups;
    private HashMap<String, ZonesAccess> users;
    
    // We don't want to make a new list every time we need a default empty array.
    public static final List<Integer> emptyIntList = Arrays.asList();
    
    public ZoneNormal(Zones zones, WorldManager worldManager, int id) {
        super(zones, worldManager, id);
        
        admingroups = new ArrayList<String>();
        adminusers = new ArrayList<String>();

        groups = new HashMap<String, ZonesAccess>();
        users = new HashMap<String, ZonesAccess>();
    }

    public void setParameter(String name,String value){
        if (name.equals("admins")) {
            String[] list = value.split(";");
            for (int i = 0; i < list.length; i++) {
                String[] item = list[i].split(",");

                switch (Integer.parseInt(item[0])) {
                    // user
                    case 1:
                        adminusers.add(item[1]);
                        break;
                    // group
                    case 2:
                        if (zones.getP().getGroup(getWorld().getName(),item[1]) != null)
                            admingroups.add(item[1]);
                        else
                            log.info("Invalid admin grouptype in zone id: " + getId());
                        break;
                    default:
                        log.info("Unknown admin grouptype in zone id: " + getId());
                        break;
                }
            }
        } else if (name.equals("users")) {
            String[] list = value.split(";");
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
                    // user
                    case 1:
                        // addUser(itemname,itemrights );
                        users.put(itemname, new ZonesAccess(itemrights));
                        break;
                    // group
                    case 2:
                        if (zones.getP().getGroup(getWorld().getName(),item[1]) != null)
                            // addGroup(itemname,itemrights);
                            groups.put(itemname, new ZonesAccess(itemrights));
                        else
                            log.info("Invalid grouptype in zone id: " + getId());
                        break;
                    default:
                        log.info("Unknown grouptype in zone id: " + getId());
                        break;
                }
            }
        } else {
            super.setParameter(name, value);
        }
        
    }
    
    public boolean canModify(Player player, ZonesAccess.Rights right) {

        if (users.containsKey(player.getName().toLowerCase()) && users.get(player.getName().toLowerCase()).canDo(right))
            return true;

        for (Entry<String, ZonesAccess> e : groups.entrySet())
            if (e.getValue().canDo(right))
                if (zones.getP().inGroup(getWorld().getName(), player.getName(), e.getKey())) 
                    return true;
            

        // Admins always have full access to the zone.
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

        for (Entry<String, ZonesAccess> e : groups.entrySet())
            if (zones.getP().inGroup(getWorld().getName(), player.getName(), e.getKey())) {
                base = base.merge(e.getValue());
            }

        return base;
    }

    public boolean canAdministrate(Player player) {
        if (zones.getP().permission(player, "zones.admin"))
            return true;

        if (adminusers.contains(player.getName().toLowerCase()))
            return true;

        for (String group : zones.getP().getGroups(getWorld().getName(), player.getName()))
            if (admingroups.contains(group.toLowerCase()))
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

        if (zones.getP().getGroup(getWorld().getName(), group) == null) {
            log.info("Trying to add an invalid group '" + group + "' in zone '" + getName() + "'[" + getId() + "].");
            return;
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

    public void addAdminGroup(String group) {
        if (admingroups.contains(group.toLowerCase()))
            return;

        if (zones.getP().getGroup(getWorld().getName(), group) == null) {
            log.info("Trying to add an invalid adminGroup '" + group + "' in zone '" + getName() + "'[" + getId() + "].");
            return;
        }
        admingroups.add(group.toLowerCase());
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
        for (String group : admingroups) {
            admins += "2," + group + ";";
        }

        if (admins.length() > 0)
            admins = admins.substring(0, admins.length() - 1);

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = zones.getConnection();
            st = conn.prepareStatement("UPDATE " + ZonesConfig.ZONES_TABLE + " SET users = ?,admins = ? WHERE id = ?");
            st.setString(1, users);
            st.setString(2, admins);
            st.setInt(3, getId());
            st.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)conn.close();
                if (st != null)st.close();
            } catch (Exception e) {}
        }
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
        if (zone == null || zone.getZone().getSize() > getZone().getSize())
            zone = this;

        /*
         * Possible codes:
         * {zname} - Zone name.
         * {access} - BCDEH
         * {pname} - Player name.
         */
        String message = getSettings().getString(ZoneVar.ENTER_MESSAGE, ZonesConfig.DEFAULT_ENTER_MESSAGE);
        message = message.replace("{zname}", getName());
        if(message.contains("{access}")) message = message.replace("{access}", zone.getAccess(player).toColorCode());
        message = message.replace("{pname}", player.getName());
        
        player.sendMessage(message);
        if (zone.allowHealth(player)) {
            player.sendMessage(ChatColor.RED + "WARNING: you can die in this zone!");
        }
    }

    @Override
    public void onExit(Player player) {
        
        String message = getSettings().getString(ZoneVar.LEAVE_MESSAGE, ZonesConfig.DEFAULT_LEAVE_MESSAGE);
        message = message.replace("{zname}", getName());
        if(message.contains("{access}")) message = message.replace("{access}", getAccess(player).toColorCode());
        message = message.replace("{pname}", player.getName());
        
        player.sendMessage(message);

    }

    @Override
    public boolean allowWater(Block from, Block to) {
        if(!isInsideZone(from.getLocation()))
            return getSettings().getBool(ZoneVar.WATER,true);
        else
            return true;
    }

    @Override
    public boolean allowLava(Block from, Block to) {
        if(!isInsideZone(from.getLocation()))
            return getSettings().getBool(ZoneVar.LAVA,true);
        else
            return true;
    }

    @Override
    public boolean allowDynamite(Block b) {
        return getSettings().getBool(ZoneVar.DYNAMITE,getWorldManager().getConfig().ALLOW_TNT_TRIGGER);
    }

    @Override
    public boolean allowHealth(Player player) {
        return getSettings().getBool(ZoneVar.HEALTH,getWorldManager().getConfig().PLAYER_HEALTH_ENABLED);
    }

    @Override
    public boolean allowLeafDecay(Block block) {
        return getSettings().getBool(ZoneVar.LEAF_DECAY, true);
    }
    
    @Override
    public boolean allowFire(Player player,Block block) {
        return getSettings().getBool(ZoneVar.FIRE , getWorldManager().getConfig().FIRE_ENABLED);
    }
    
    @Override
    public boolean allowSpawn(Entity entity,CreatureType type) {
        if(entity instanceof Animals) {
            if(getSettings().getBool(ZoneVar.SPAWN_ANIMALS, getWorldManager().getConfig().ANIMAL_SPAWNING_ENABLED)) {
                List<CreatureType> list = getSettings().getCreatureList(ZoneVar.ANIMALS);
                if(list != null && !list.contains(type))
                    return false;
                else
                    return true;
            } else {
                return false;
            }
        } else if(entity instanceof Monster) {
            if(getSettings().getBool(ZoneVar.SPAWN_MOBS, getWorldManager().getConfig().MOB_SPAWNING_ENABLED)) {
                List<CreatureType> list = getSettings().getCreatureList(ZoneVar.MOBS);
                if(list != null && !list.contains(type))
                    return false;
                else
                    return true;
            } else {
                return false;
            }       
        } else
            return true;
    }

    @Override
    public boolean allowBlockCreate(Player player, Block block) {
        
        if(!this.canModify(player, Rights.BUILD)) {
            player.sendMessage(ChatColor.RED + "You cannot place blocks in '" + getName() + "' !");
            return false;
        } else {
            List<Integer> list = getSettings().getIntList(ZoneVar.PLACE_BLOCKS);
            if(list != null && list.contains(block.getTypeId()) && !this.canAdministrate(player)) {
                player.sendMessage(ChatColor.RED + "This block type is blacklisted in '" + getName() + "' !");
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public boolean allowBlockDestroy(Player player, Block block) {
        if(!this.canModify(player, Rights.DESTROY)) {
            player.sendMessage(ChatColor.RED + "You cannot destroy blocks in '" + getName() + "' !");
            return false;
        } else {
            List<Integer> list = getSettings().getIntList(ZoneVar.BREAK_BLOCKS);
            if(list != null && list.contains(block.getTypeId()) && !this.canAdministrate(player)) {
                player.sendMessage(ChatColor.RED + "This block type is protected in '" + getName() + "' !");
                return false;
            } else {
                return true;
            }
        }
        
    }

    @Override
    public boolean allowBlockHit(Player attacker, Block defender) {
        return this.canModify(attacker, Rights.HIT);
    }

    @Override
    public boolean allowBlockModify(Player player, Block block) {
        return this.canModify(player, Rights.MODIFY);
    }

    @Override
    public boolean allowEnter(Player player, Location to) {
        return this.canModify(player, Rights.ENTER);
    }

    @Override
    public boolean allowEntityHit(Player attacker, Entity defender) {
        return this.canModify(attacker, Rights.HIT);
    }

    @Override
    public boolean allowTeleport(Player player, Location to) {
        return this.canModify(player, Rights.ENTER) && getSettings().getBool(ZoneVar.TELEPORT, true);
    }
}
