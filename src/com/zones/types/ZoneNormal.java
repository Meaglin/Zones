package com.zones.types;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.zones.World;
import com.zones.ZoneBase;
import com.zones.Zones;
import com.zones.ZonesAccess;
import com.zones.ZonesConfig;
import com.zones.ZonesAccess.Rights;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

/**
 * 
 * @author Meaglin
 *
 */
public class ZoneNormal extends ZoneBase {

    private List<String>                 admingroups;
    private List<String>                 adminusers;

    private HashMap<String, ZonesAccess> groups;
    private HashMap<String, ZonesAccess> users;
    
    public ZoneNormal(Zones zones, String world, int id) {
        super(zones, world, id);
        
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
                        if (zones.getP().getGroup(world,item[1]) != null)
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
                        if (zones.getP().getGroup(world,item[1]) != null)
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
                if (zones.getP().inGroup(world, player.getName(), e.getKey())) 
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
            if (zones.getP().inGroup(world, player.getName(), e.getKey())) {
                base = base.merge(e.getValue());
            }

        return base;
    }

    public boolean canAdministrate(Player player) {
        if (zones.getP().permission(player, "zones.admin"))
            return true;

        if (adminusers.contains(player.getName().toLowerCase()))
            return true;

        for (String group : zones.getP().getGroups(world, player.getName()))
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

        if (zones.getP().getGroup(world, group) == null) {
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

        if (zones.getP().getGroup(world, group) == null) {
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
        ZoneBase zone = World.getInstance().getActiveZone(player);
        if (zone == null || zone.getZone().getSize() > getZone().getSize())
            zone = this;

        player.sendMessage("You have just entered " + getName() + "[" + zone.getAccess(player).toColorCode() + "].");
        if (zone.allowHealth(player)) {
            player.sendMessage(ChatColor.RED.toString() + "WARNING: you can die in this zone!");
        }
    }

    @Override
    public void onExit(Player character) {
        character.sendMessage("You have just exited " + getName() + ".");

    }

    @Override
    public boolean allowWater(Block b) {
        return getSettings().getBool(ZonesConfig.WATER_ENABLED_NAME,true);
    }

    @Override
    public boolean allowLava(Block b) {
        return getSettings().getBool(ZonesConfig.LAVA_ENABLED_NAME,true);
    }

    @Override
    public boolean allowDynamite(Block b) {
        return getSettings().getBool(ZonesConfig.DYNAMITE_ENABLED_NAME,ZonesConfig.TNT_ENABLED);
    }

    @Override
    public boolean allowHealth(Player player) {
        return getSettings().getBool(ZonesConfig.HEALTH_ENABLED_NAME,ZonesConfig.HEALTH_ENABLED);
    }

    @Override
    public boolean allowLeafDecay(Block b) {
        return getSettings().getBool(ZonesConfig.LEAF_DECAY_ENABLED_NAME, true);
    }
    
    @Override
    public boolean allowFire(Player player, Block block) {
        return getSettings().getBool(ZonesConfig.ALLOW_FIRE_NAME, ZonesConfig.FIRE_ENABLED);
    }
    
    @Override
    public boolean allowSpawn(Entity entity) {
        if(entity instanceof Animals) {
            return getSettings().getBool(ZonesConfig.SPAWN_ANIMALS_NAME, ZonesConfig.ANIMALS_ENABLED);
        } else if(entity instanceof Monster) {
            return getSettings().getBool(ZonesConfig.SPAWN_MOBS_NAME, ZonesConfig.MOBS_ENABLED);            
        } else
            return true;
    }

    @Override
    public boolean allowBlockCreate(Player player, Block block) {
        return this.canModify(player, Rights.BUILD);
    }

    @Override
    public boolean allowBlockDestroy(Player player, Block block) {
        return this.canModify(player, Rights.DESTROY);
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
        return this.canModify(player, Rights.ENTER) && getSettings().getBool(ZonesConfig.ALLOW_TELEPORT_NAME, true);
    }


}
