package com.zones;

import com.zones.model.ZoneBase;
import com.zones.model.forms.ZoneCuboid;
import com.zones.model.forms.ZoneNPoly;

import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntObjectHashMap;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * 
 * @author Meaglin
 *
 */
public class ZoneManager {
    private TIntObjectHashMap<ZoneBase>       zones;
    private TIntObjectHashMap<ZonesDummyZone> dummyZones;
    private TIntIntHashMap                    selectedZones;
    protected static final Logger             log = Logger.getLogger("Minecraft");
    private Zones                             plugin;

    protected ZoneManager() {
        zones = new TIntObjectHashMap<ZoneBase>();
        dummyZones = new TIntObjectHashMap<ZonesDummyZone>();
        selectedZones = new TIntIntHashMap();
    }

    public void load(Zones plugin) {
        zones.clear();
        dummyZones.clear();
        selectedZones.clear();
        this.plugin = plugin;
        Connection conn = null;
        try {
            conn = plugin.getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * FROM " + ZonesConfig.ZONES_TABLE);
            PreparedStatement st2 = conn.prepareStatement("SELECT `x`,`y` FROM " + ZonesConfig.ZONES_VERTICES_TABLE + " WHERE id = ? ORDER BY `order` ASC LIMIT ? ");
            ResultSet rset = st.executeQuery();

            int id, type, size, minz, maxz;
            String zoneClass, admins, users, name, world, settings;
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
                settings = rset.getString("settings");
                Class<?> newZone;
                try {
                    newZone = Class.forName("com.zones.model.types."+zoneClass);
                } catch (ClassNotFoundException e) {
                    log.warning("[Zones]No such zone class: " + zoneClass + " id: " + id);
                    continue;
                }
                Constructor<?> zoneConstructor = newZone.getConstructor(Zones.class, WorldManager.class, int.class);
                ZoneBase temp = (ZoneBase) zoneConstructor.newInstance(zones,plugin.getWorldManager(world),id);

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
                temp.loadSettings(settings);
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
        if (zones.size() == 1)
            log.info("[Zones]Loaded " + zones.size() + " Zone.");
        else
            log.info("[Zones]Loaded " + zones.size() + " Zones.");
    }

    public void addZone(ZoneBase zone) {
        int ax, ay, bx, by;
        for (int x = 0; x < WorldManager.X_REGIONS; x++) {
            for (int y = 0; y < WorldManager.Y_REGIONS; y++) {

                ax = (x + WorldManager.OFFSET_X) << WorldManager.SHIFT_SIZE;
                bx = ((x + 1) + WorldManager.OFFSET_X) << WorldManager.SHIFT_SIZE;
                ay = (y + WorldManager.OFFSET_Y) << WorldManager.SHIFT_SIZE;
                by = ((y + 1) + WorldManager.OFFSET_Y) << WorldManager.SHIFT_SIZE;

                if (zone.getZone().intersectsRectangle(ax, bx, ay, by)) {
                    plugin.getWorldManager(zone.getWorld()).addZone(x, y, zone);
                    // log.info("adding zone["+zone.getId()+"] to region " + x +
                    // " " + y);
                }
            }
        }

        zones.put(zone.getId(), zone);
    }

    public ZoneBase getZone(int id) {
        return zones.get(id);
    }

    public boolean delete(ZoneBase toDelete) {
        if (!zones.containsKey(toDelete.getId()))
            return false;

        // first delete sql data.
        Connection conn = null;
        PreparedStatement st = null;
        int u = 0;
        try {
            conn = plugin.getConnection();
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
            conn = plugin.getConnection();
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
/*        int ax, ay, bx, by;
        for (int x = 0; x < WorldManager.X_REGIONS; x++) {
            for (int y = 0; y < WorldManager.Y_REGIONS; y++) {

                ax = (x + WorldManager.OFFSET_X) << WorldManager.SHIFT_SIZE;
                bx = ((x + 1) + WorldManager.OFFSET_X) << WorldManager.SHIFT_SIZE;
                ay = (y + WorldManager.OFFSET_Y) << WorldManager.SHIFT_SIZE;
                by = ((y + 1) + WorldManager.OFFSET_Y) << WorldManager.SHIFT_SIZE;

                // System.out.println(ax + " " + bx + " " + ay + " " + by);

                if (toDelete.getZone().intersectsRectangle(ax, bx, ay, by)) {
                    plugin.getWorldManager(toDelete.getWorld()).getRegion(ax, ay).removeZone(toDelete);
                    // log.info("adding zone["+zone.getId()+"] to region " + x +
                    // " " + y);
                }
            }
        }*/
        plugin.getWorldManager(toDelete.getWorld()).removeZone(toDelete);

        // finally remove the zone from the main zones list.
        zones.remove(toDelete.getId());

        return true;
    }

    public void addDummy(int playerId, ZonesDummyZone zone) {
        dummyZones.put(playerId, zone);
    }

    public ZonesDummyZone getDummy(int playerId) {
        return dummyZones.get(playerId);
    }

    public boolean zoneExists(int id) {
        return zones.containsKey(id);
    }

    public void removeDummy(int playerId) {
        dummyZones.remove(playerId);
    }

    public void setSelected(int playerId, int id) {
        if (zones.containsKey(id))
            selectedZones.put(playerId, id);
    }

    public int getSelected(int playerId) {
        if (!selectedZones.containsKey(playerId))
            return 0;

        return selectedZones.get(playerId);
    }
    
    public ZoneBase getSelectedZone(int playerId) {
        return getZone(getSelected(playerId));
    }

    public void removeSelected(int playerId) {
        selectedZones.remove(playerId);
    }

    public ZoneBase[] getAllZones() {
        return (ZoneBase[]) zones.getValues();
    }

}
