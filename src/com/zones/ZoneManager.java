package com.zones;

import com.zones.forms.ZoneNPoly;
import com.zones.forms.ZoneCuboid;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

public class ZoneManager {
    private HashMap<Integer, ZoneType>      _zones;
    private HashMap<String, ZonesDummyZone> _dummyZones;
    private HashMap<String, Integer>        _selectedZones;
    protected static final Logger           log = Logger.getLogger("Minecraft");
    private Zones                           zones;

    private ZoneManager() {
        _zones = new HashMap<Integer, ZoneType>();
        _dummyZones = new HashMap<String, ZonesDummyZone>();
        _selectedZones = new HashMap<String, Integer>();
    }

    public void load(Zones zones) {
        this.zones = zones;
        World.getInstance();
        Connection conn = null;
        try {
            conn = zones.getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * FROM " + ZonesConfig.ZONES_TABLE);
            PreparedStatement st2 = conn.prepareStatement("SELECT `x`,`y` FROM " + ZonesConfig.ZONES_VERTICES_TABLE + " WHERE id = ? ORDER BY `order` ASC LIMIT ? ");
            ResultSet rset = st.executeQuery();

            int id, type, size, minz, maxz, water, lava, dynamite, health, mobs, animals;
            String zoneClass, admins, users, name, world;
            ArrayList<int[]> points = new ArrayList<int[]>();

            while (rset.next()) {
                id = rset.getInt("id");
                name = rset.getString("name");
                zoneClass = rset.getString("class");
                world = rset.getString("world");
                type = rset.getInt("type");
                size = rset.getInt("size");
                admins = rset.getString("admins");
                users = rset.getString("users");
                minz = rset.getInt("minz");
                maxz = rset.getInt("maxz");
                water = rset.getInt("allowwater");
                lava = rset.getInt("allowlava");
                dynamite = rset.getInt("allowdynamite");
                health = rset.getInt("enablehealth");
                mobs = rset.getInt("allowmobs");
                animals = rset.getInt("allowanimals");
                Class<?> newZone;
                try {
                    newZone = Class.forName("com.zones.types."+zoneClass);
                } catch (ClassNotFoundException e) {
                    log.warning("[Zones]No such zone class: " + zoneClass + " id: " + id);
                    continue;
                }
                Constructor<?> zoneConstructor = newZone.getConstructor(Zones.class, String.class, int.class);
                ZoneType temp = (ZoneType) zoneConstructor.newInstance(zones,world,id);

                points.clear();

                try {

                    st2.setInt(1, id);
                    st2.setInt(2, size);

                    ResultSet rset2 = st2.executeQuery();
                    while (rset2.next()) {
                        int[] point = new int[2];
                        point[0] = rset2.getInt("x");
                        point[1] = rset2.getInt("y");
                        points.add(point);
                    }
                    rset2.close();
                } finally {
                    st2.clearParameters();
                }
                int[][] coords = points.toArray(new int[points.size()][]);
                switch (type) {
                    case 1:
                        if (points.size() == 2) {
                            temp.setZone(new ZoneCuboid(coords[0][0], coords[1][0], coords[0][1], coords[1][1], minz, maxz));
                        } else {
                            log.info("[Zones]Missing zone vertex for cuboid zone id: " + id);
                            continue;
                        }
                        break;
                    case 2:
                        if (coords.length > 2) {
                            final int[] aX = new int[coords.length];
                            final int[] aY = new int[coords.length];
                            for (int i = 0; i < coords.length; i++) {
                                aX[i] = coords[i][0];
                                aY[i] = coords[i][1];
                            }
                            temp.setZone(new ZoneNPoly(aX, aY, minz, maxz));
                        } else {
                            log.warning("[Zones]Bad data for zone: " + id);
                            continue;
                        }
                        break;
                    default:
                        log.severe("[Zones]Unknown zone form " + type + " for id " + id);
                        break;
                }

                temp.setParameter("admins", admins);
                temp.setParameter("users", users);
                temp.setParameter("name", name);
                temp.setParameter("health", Integer.toString(health));
                temp.setParameter("water", Integer.toString(water));
                temp.setParameter("lava", Integer.toString(lava));
                temp.setParameter("dynamite", Integer.toString(dynamite));
                temp.setParameter("animals", Integer.toString(animals));
                temp.setParameter("mobs", Integer.toString(mobs));
                addZone(temp);
            }
            rset.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (_zones.size() == 1)
            log.info("[Zones]Loaded " + _zones.size() + " Zone.");
        else
            log.info("[Zones]Loaded " + _zones.size() + " Zones.");
    }

    public void addZone(ZoneType zone) {
        int ax, ay, bx, by;
        for (int x = 0; x < World.X_REGIONS; x++) {
            for (int y = 0; y < World.Y_REGIONS; y++) {

                ax = (x + World.OFFSET_X) << World.SHIFT_SIZE;
                bx = ((x + 1) + World.OFFSET_X) << World.SHIFT_SIZE;
                ay = (y + World.OFFSET_Y) << World.SHIFT_SIZE;
                by = ((y + 1) + World.OFFSET_Y) << World.SHIFT_SIZE;

                if (zone.getZone().intersectsRectangle(ax, bx, ay, by)) {
                    World.getInstance().addZone(x, y, zone);
                    // log.info("adding zone["+zone.getId()+"] to region " + x +
                    // " " + y);
                }
            }
        }

        _zones.put(zone.getId(), zone);
    }

    public ZoneType getZone(int id) {
        return _zones.get(id);
    }

    public static final ZoneManager getInstance() {
        return SingletonHolder._instance;
    }

    public boolean delete(ZoneType toDelete) {
        if (!_zones.containsKey(toDelete.getId()))
            return false;

        // first delete sql data.
        Connection conn = null;
        PreparedStatement st = null;
        int u = 0;
        try {
            conn = zones.getConnection();
            st = conn.prepareStatement("DELETE FROM " + ZonesConfig.ZONES_VERTICES_TABLE + " WHERE id = ?");
            st.setInt(1, toDelete.getId());

            u = st.executeUpdate();
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

        if (u == 0)
            return false;

        u = 0;
        try {
            conn = zones.getConnection();
            st = conn.prepareStatement("DELETE FROM " + ZonesConfig.ZONES_TABLE + " WHERE id = ?");
            st.setInt(1, toDelete.getId());

            u = st.executeUpdate();
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

        if (u == 0)
            return false;

        // then delete the zone from all regions
        int ax, ay, bx, by;
        for (int x = 0; x < World.X_REGIONS; x++) {
            for (int y = 0; y < World.Y_REGIONS; y++) {

                ax = (x + World.OFFSET_X) << World.SHIFT_SIZE;
                bx = ((x + 1) + World.OFFSET_X) << World.SHIFT_SIZE;
                ay = (y + World.OFFSET_Y) << World.SHIFT_SIZE;
                by = ((y + 1) + World.OFFSET_Y) << World.SHIFT_SIZE;

                // System.out.println(ax + " " + bx + " " + ay + " " + by);

                if (toDelete.getZone().intersectsRectangle(ax, bx, ay, by)) {
                    World.getInstance().getRegion(ax, ay).removeZone(toDelete);
                    // log.info("adding zone["+zone.getId()+"] to region " + x +
                    // " " + y);
                }
            }
        }

        // finally remove the zone from the main zones list.
        _zones.remove(toDelete.getId());

        return true;
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final ZoneManager _instance = new ZoneManager();
    }

    public void addDummy(String name, ZonesDummyZone zone) {
        _dummyZones.put(name, zone);
    }

    public ZonesDummyZone getDummy(String name) {
        return _dummyZones.get(name);
    }

    public boolean zoneExists(int id) {
        return _zones.containsKey(id);
    }

    public void removeDummy(String name) {
        _dummyZones.remove(name);
    }

    public void setSelected(String name, int id) {
        if (_zones.containsKey(id))
            _selectedZones.put(name, id);
    }

    public int getSelected(String name) {
        if (!_selectedZones.containsKey(name))
            return 0;

        return _selectedZones.get(name);
    }

    public void removeSelected(String name) {
        _selectedZones.remove(name);
    }

    public Collection<ZoneType> getAllZones() {
        return _zones.values();
    }

}
