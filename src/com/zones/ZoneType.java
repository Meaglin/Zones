package com.zones;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Abstract base class for any zone type Handles basic operations
 * 
 * @author durgus, Meaglin
 */
public abstract class ZoneType {
    protected static final Logger        log           = Logger.getLogger(ZoneType.class.getName());

    private final int                    _id;
    protected ZoneForm                  _zone;
    protected HashMap<String, Player>    _characterList;

    private String                       _name;
    private List<String>                 _admingroups;
    private List<String>                 _adminusers;

    private HashMap<String, ZonesAccess> _groups;
    private HashMap<String, ZonesAccess> _users;

    protected boolean                    allowLava     = false;
    protected boolean                    allowWater    = false;
    protected boolean                    allowDynamite = false;
    protected boolean                    allowHealth   = false;
    protected boolean                    allowMobs     = false;
    protected boolean                    allowAnimals  = false;

    private Zones                        zones;
    private String                       world;
    
    protected ZoneType(Zones zones,String world, int id) {
        _id = id;
        this.zones = zones;
        this.world = world;
        _characterList = new HashMap<String, Player>();

        _admingroups = new ArrayList<String>();
        _adminusers = new ArrayList<String>();

        _groups = new HashMap<String, ZonesAccess>();
        _users = new HashMap<String, ZonesAccess>();

    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    /**
     * Setup new parameters for this zone
     * 
     * @param type
     * @param value
     */
    public void setParameter(String name, String value) {
        if (value == null || value.equals(""))
            return;

        if (name.equals("admins")) {
            String[] list = value.split(";");
            for (int i = 0; i < list.length; i++) {
                String[] item = list[i].split(",");

                switch (Integer.parseInt(item[0])) {
                    // user
                    case 1:
                        _adminusers.add(item[1]);
                        break;
                    // group
                    case 2:
                        if (zones.getP().getGroup(world,item[1]) != null)
                            _admingroups.add(item[1]);
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
                        _users.put(itemname, new ZonesAccess(itemrights));
                        break;
                    // group
                    case 2:
                        if (zones.getP().getGroup(world,item[1]) != null)
                            // addGroup(itemname,itemrights);
                            _groups.put(itemname, new ZonesAccess(itemrights));
                        else
                            log.info("Invalid grouptype in zone id: " + getId());
                        break;
                    default:
                        log.info("Unknown grouptype in zone id: " + getId());
                        break;
                }
            }
        } else if (name.equals("name")) {
            _name = value;
        } else if (name.equalsIgnoreCase("water")) {
            if (value.equalsIgnoreCase("1"))
                allowWater = true;
            else
                allowWater = false;
        } else if (name.equalsIgnoreCase("lava")) {
            if (value.equalsIgnoreCase("1"))
                allowLava = true;
            else
                allowLava = false;
        } else if (name.equalsIgnoreCase("health")) {
            if (value.equalsIgnoreCase("1"))
                allowHealth = true;
            else
                allowHealth = false;
        } else if (name.equalsIgnoreCase("dynamite")) {
            if (value.equalsIgnoreCase("1"))
                allowDynamite = true;
            else
                allowDynamite = false;
        } else if (name.equalsIgnoreCase("mobs")) {
            if (value.equalsIgnoreCase("1"))
                allowMobs = true;
            else
                allowMobs = false;
        } else if (name.equalsIgnoreCase("animals")) {
            if (value.equalsIgnoreCase("1"))
                allowAnimals = true;
            else
                allowAnimals = false;
        } else
            log.info(getClass().getSimpleName() + ": Unknown parameter - " + name + " in zone: " + getId());
    }

    /**
     * Checks if the given character is affected by this zone
     * 
     * @param character
     * @return
     */
    private boolean isAffected(Player character) {
        return true;
    }

    /**
     * Set the zone for this L2ZoneType Instance
     * 
     * @param zone
     */
    public void setZone(ZoneForm zone) {
        _zone = zone;
    }

    /**
     * Returns this zones zone form
     * 
     * @param zone
     * @return
     */
    public ZoneForm getZone() {
        return _zone;
    }

    /**
     * Checks if the given coordinates are within zone's plane
     * 
     * @param x
     * @param y
     */
    public boolean isInsideZone(int x, int y,String world) {
        if (this.world.equals(world) && _zone.isInsideZone(x, y, _zone.getHighZ()))
            return true;
        else
            return false;
    }

    /**
     * Checks if the given coordinates are within the zone
     * 
     * @param x
     * @param y
     * @param z
     */
    public boolean isInsideZone(int x, int y, int z,String world) {
        if (this.world.equals(world) && _zone.isInsideZone(x, y, z))
            return true;
        else
            return false;
    }

    /**
     * Checks if the given object is inside the zone.
     * 
     * @param player
     */
    public boolean isInsideZone(Player player)      {return isInsideZone(player.getLocation());}
    public boolean isInsideZone(Location loc)       {return isInsideZone(World.toInt(loc.getX()), World.toInt(loc.getZ()), World.toInt(loc.getY()),loc.getWorld().getName());}
    
    public double getDistanceToZone(int x, int y)   {return getZone().getDistanceToZone(x, y);}

    public double getDistanceToZone(Player player) {
        Location loc = player.getLocation();
        return getZone().getDistanceToZone(World.toInt(loc.getX()), World.toInt(loc.getZ()));
    }

    public void revalidateInZone(Player player) {

        Location loc = player.getLocation();

        // System.out.println("Revalidating zone " + getId());
        if (!isAffected(player))
            return;

        // If the object is inside the zone...
        if (isInsideZone(loc)) {
            // Was the character not yet inside this zone?
            if (!_characterList.containsKey(player.getName())) {
                _characterList.put(player.getName(), player);
                onEnter(player);
            }
        } else {
            // Was the character inside this zone?
            if (_characterList.containsKey(player.getName())) {
                _characterList.remove(player.getName());
                onExit(player);
            }
        }
    }

    /**
     * Force fully removes a character from the zone Should use during teleport
     * / logoff
     * 
     * @param player
     */
    public void removeCharacter(Player player) {
        if (_characterList.containsKey(player.getName())) {
            _characterList.remove(player.getName());
            onExit(player);
        }
    }

    /**
     * Will scan the zones char list for the character
     * 
     * @param player
     * @return
     */
    public boolean isCharacterInZone(Player player) {
        return _characterList.containsKey(player.getName());
    }

    protected abstract void onEnter(Player character);

    protected abstract void onExit(Player character);

    public abstract boolean allowWater(Block b);

    public abstract boolean allowLava(Block b);

    public abstract boolean allowDynamite(Block b);

    public abstract boolean allowHealth();

    public HashMap<String, Player> getCharactersInside() {
        return _characterList;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + _id + "]";
    }

    public boolean canModify(Player player, ZonesAccess.Rights right) {

        if (_users.containsKey(player.getName().toLowerCase()) && _users.get(player.getName().toLowerCase()).canDo(right))
            return true;

        for (Entry<String, ZonesAccess> e : _groups.entrySet())
            if (zones.getP().inGroup(world, player.getName(), e.getKey())) {
                if (e.getValue().canDo(right))
                    return true;
            }

        // Admins always have full access to the zone.
        return canAdministrate(player);
    }

    public ZonesAccess getAccess(String group) {
        ZonesAccess z = new ZonesAccess("-");
        for (Entry<String, ZonesAccess> e : _groups.entrySet()) {
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

        if (_users.containsKey(name))
            base = base.merge(_users.get(name));

        for (Entry<String, ZonesAccess> e : _groups.entrySet())
            if (zones.getP().inGroup(world, player.getName(), e.getKey())) {
                base = base.merge(e.getValue());
            }

        return base;
    }

    public boolean canAdministrate(Player player) {
        if (zones.getP().permission(player, "zones.admin"))
            return true;

        if (_adminusers.contains(player.getName().toLowerCase()))
            return true;

        for (String group : zones.getP().getGroups(world, player.getName()))
            if (_admingroups.contains(group.toLowerCase()))
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

        for (String t : _adminusers)
            rt += t + ", ";

        if (rt.equals(""))
            return "";

        rt = rt.substring(0, rt.length() - 2);

        return rt;
    }

    public void sendAccess(Player player) {
        player.sendMessage("AccesList of " + getName() + ":");
        player.sendMessage("   Users: " + mapToString(_users) + ".");
        player.sendMessage("   Groups: " + mapToString(_groups) + ".");
        player.sendMessage("   Admins: " + adminsToString() + ".");
    }

    public void addUser(String user, ZonesAccess a) {
        user = user.toLowerCase();

        if (_users.containsKey(user)) {
            _users.remove(user);
        }

        if (!a.canNothing())
            _users.put(user, a);

        updateUsers();
    }

    public void addGroup(String group, ZonesAccess a) {
        group = group.toLowerCase();

        if (_groups.containsKey(group)) {
            _groups.remove(group);
        }

        if (zones.getP().getGroup(world, group) == null) {
            log.info("Trying to add an invalid group '" + group + "' in zone '" + getName() + "'[" + getId() + "].");
            return;
        }

        if (!a.canNothing())
            _groups.put(group, a);

        updateUsers();
    }

    public void addAdmin(String admin) {
        if (_adminusers.contains(admin.toLowerCase()))
            return;

        _adminusers.add(admin.toLowerCase());
        updateAdmins();
    }

    public void addAdminGroup(String group) {
        if (_admingroups.contains(group.toLowerCase()))
            return;

        if (zones.getP().getGroup(world, group) == null) {
            log.info("Trying to add an invalid adminGroup '" + group + "' in zone '" + getName() + "'[" + getId() + "].");
            return;
        }
        _admingroups.add(group.toLowerCase());
        updateAdmins();
    }

    public void removeAdmin(String admin) {
        if (_adminusers.contains(admin.toLowerCase())) {
            _adminusers.remove(admin.toLowerCase());
            updateAdmins();
        } else
            return;

    }

    private void updateAdmins() {
        String admins = "";

        for (String user : _adminusers) {
            admins += "1," + user + ";";
        }
        for (String group : _admingroups) {
            admins += "2," + group + ";";
        }

        if (admins.length() > 0)
            admins = admins.substring(0, admins.length() - 1);

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = zones.getConnection();
            st = conn.prepareStatement("UPDATE " + ZonesConfig.ZONES_TABLE + " SET admins = ? WHERE id = ?");
            st.setString(1, admins);
            st.setInt(2, getId());
            st.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (st != null)
                    st.close();
            } catch (Exception e) {
            }
        }
    }

    private void updateUsers() {
        String users = "";

        for (Entry<String, ZonesAccess> e : _users.entrySet()) {
            users += "1," + e.getKey() + "," + e.getValue().toString() + ";";
        }
        for (Entry<String, ZonesAccess> e : _groups.entrySet()) {
            users += "2," + e.getKey() + "," + e.getValue().toString() + ";";
        }

        if (users.length() > 0)
            users = users.substring(0, users.length() - 1);

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = zones.getConnection();
            st = conn.prepareStatement("UPDATE " + ZonesConfig.ZONES_TABLE + " SET users = ? WHERE id = ?");
            st.setString(1, users);
            st.setInt(2, getId());
            st.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (st != null)
                    st.close();
            } catch (Exception e) {
            }
        }
    }

    @SuppressWarnings("unused")
    private void updateRights() {
        String admins = "";
        String users = "";
        for (Entry<String, ZonesAccess> e : _users.entrySet()) {
            users += "1," + e.getKey() + "," + e.getValue().toString() + ";";
        }
        for (Entry<String, ZonesAccess> e : _groups.entrySet()) {
            users += "2," + e.getKey() + "," + e.getValue().toString() + ";";
        }

        if (users.length() > 0)
            users = users.substring(0, users.length() - 1);

        for (String user : _adminusers) {
            admins += "1," + user + ";";
        }
        for (String group : _admingroups) {
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
                if (conn != null)
                    conn.close();
                if (st != null)
                    st.close();
            } catch (Exception e) {
            }
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

    public boolean setName(String name) {

        Connection conn = null;
        PreparedStatement st = null;
        int u = 0;
        try {
            conn = zones.getConnection();
            st = conn.prepareStatement("UPDATE " + ZonesConfig.ZONES_TABLE + " SET name = ? WHERE id = ?");
            st.setString(1, name);
            st.setInt(2, getId());
            u = st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                st.close();
            } catch (Exception e) {
            }
        }

        if (u < 1)
            return false;

        _name = name;

        return true;
    }

    public boolean toggleHealth() {
        Connection conn = null;
        PreparedStatement st = null;
        int u = 0;
        try {
            conn = zones.getConnection();
            st = conn.prepareStatement("UPDATE " + ZonesConfig.ZONES_TABLE + " SET enablehealth = ? WHERE id = ?");
            st.setInt(1, (!allowHealth) ? 1 : 0);
            st.setInt(2, getId());
            u = st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                st.close();
            } catch (Exception e) {
            }
        }

        if (u < 1)
            return false;

        allowHealth = !allowHealth;

        return true;
    }

    public boolean toggleWater() {
        Connection conn = null;
        PreparedStatement st = null;
        int u = 0;
        try {
            conn = zones.getConnection();
            st = conn.prepareStatement("UPDATE " + ZonesConfig.ZONES_TABLE + " SET allowwater = ? WHERE id = ?");
            st.setInt(1, (!allowWater) ? 1 : 0);
            st.setInt(2, getId());
            u = st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                st.close();
            } catch (Exception e) {
            }
        }

        if (u < 1)
            return false;

        allowWater = !allowWater;

        return true;
    }

    public boolean toggleLava() {
        Connection conn = null;
        PreparedStatement st = null;
        int u = 0;
        try {
            conn = zones.getConnection();
            st = conn.prepareStatement("UPDATE " + ZonesConfig.ZONES_TABLE + " SET allowlava = ? WHERE id = ?");
            st.setInt(1, (!allowLava) ? 1 : 0);
            st.setInt(2, getId());
            u = st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                st.close();
            } catch (Exception e) {
            }
        }

        if (u < 1)
            return false;

        allowLava = !allowLava;

        return true;
    }

    public boolean toggleDynamite() {
        Connection conn = null;
        PreparedStatement st = null;
        int u = 0;
        try {
            conn = zones.getConnection();
            st = conn.prepareStatement("UPDATE " + ZonesConfig.ZONES_TABLE + " SET allowdynamite = ? WHERE id = ?");
            st.setInt(1, (!allowDynamite) ? 1 : 0);
            st.setInt(2, getId());
            u = st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                st.close();
            } catch (Exception e) {
            }
        }

        if (u < 1)
            return false;

        allowDynamite = !allowDynamite;

        return true;
    }

    public boolean toggleMobs() {
        Connection conn = null;
        PreparedStatement st = null;
        int u = 0;
        try {
            conn = zones.getConnection();
            st = conn.prepareStatement("UPDATE " + ZonesConfig.ZONES_TABLE + " SET allowmobs = ? WHERE id = ?");
            st.setInt(1, (!allowMobs) ? 1 : 0);
            st.setInt(2, getId());
            u = st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                st.close();
            } catch (Exception e) {
            }
        }

        if (u < 1)
            return false;

        allowMobs = !allowMobs;

        return true;
    }

    public boolean toggleAnimals() {
        Connection conn = null;
        PreparedStatement st = null;
        int u = 0;
        try {
            conn = zones.getConnection();
            st = conn.prepareStatement("UPDATE " + ZonesConfig.ZONES_TABLE + " SET allowanimals = ? WHERE id = ?");
            st.setInt(1, (!allowAnimals) ? 1 : 0);
            st.setInt(2, getId());
            u = st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                st.close();
            } catch (Exception e) {
            }
        }

        if (u < 1)
            return false;

        allowAnimals = !allowAnimals;

        return true;
    }

    public boolean isHealthAllowed() {
        return allowHealth;
    }

    public boolean isWaterAllowed() {
        return allowWater;
    }

    public boolean isLavaAllowed() {
        return allowLava;
    }

    public boolean isDynamiteAllowed() {
        return allowDynamite;
    }

    public boolean isMobsAllowed() {
        return allowMobs;
    }

    public boolean isAnimalsAllowed() {
        return allowAnimals;
    }

    public void revalidateInZone(Player player, Location loc) {

        // System.out.println("Revalidating zone " + getId());
        if (!isAffected(player))
            return;

        // If the object is inside the zone...
        if (isInsideZone(loc)) {
            // Was the character not yet inside this zone?
            if (!_characterList.containsKey(player.getName())) {
                _characterList.put(player.getName(), player);
                onEnter(player);
            }
        } else {
            // Was the character inside this zone?
            if (_characterList.containsKey(player.getName())) {
                _characterList.remove(player.getName());
                onExit(player);
            }
        }
    }

}
