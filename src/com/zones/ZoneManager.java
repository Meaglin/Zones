package com.zones;

import com.zones.model.ZoneBase;
import com.zones.model.forms.ZoneCuboid;
import com.zones.model.forms.ZoneNPoly;
import com.zones.persistence.Vertice;
import com.zones.persistence.Zone;
import com.zones.selection.ZoneSelection;

import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntObjectHashMap;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Logger;

/**
 * 
 * @author Meaglin
 *
 */
public class ZoneManager {
    private TIntObjectHashMap<ZoneBase>       zones;
    private TIntObjectHashMap<ZoneSelection>  selections;
    private TIntIntHashMap                    selectedZones;
    protected static final Logger             log = Logger.getLogger("Minecraft");
    private Zones                             plugin;
    
    protected ZoneManager(Zones plugin) {
        zones = new TIntObjectHashMap<ZoneBase>();
        selections = new TIntObjectHashMap<ZoneSelection>();
        selectedZones = new TIntIntHashMap();
        this.plugin = plugin;
    }

    public void cleanUp(WorldManager world) {
        selectedZones.clear();
        for(ZoneBase z : zones.getValues(new ZoneBase[zones.size()])) {
            if(z.getWorldManager().equals(world)) {
                zones.remove(z.getId());
            }
        }
    }
    public void load(WorldManager world) {
        cleanUp(world);
        
        int count = 0;
        try {
            List<Zone> zones = plugin.getDatabase().find(Zone.class).where().ieq("world", world.getWorldName()).findList();
            for(Zone zone : zones) {
                if(loadFromPersistentData(world, zone) != null)
                    count++;
            }
        } catch(Exception e) {
            log.warning("[Zones]Error loading world " + world.getWorldName() + ".");
            e.printStackTrace();
            return;
        } finally {
            if (count == 1)
                log.info("[Zones]Loaded " + count + " Zone for world " + world.getWorldName() + ".");
            else
                log.info("[Zones]Loaded " + count + " Zones for world " + world.getWorldName() + ".");
        }
    }

    public ZoneBase loadFromPersistentData(WorldManager world, Zone zone) {
        Class<?> newZone = null;
        ZoneBase temp = null;
        try {
            try {
                newZone = Class.forName("com.zones.model.types."+zone.getZonetype());
            } catch (ClassNotFoundException e) {
                log.warning("[Zones]No such zone class: " + zone.getZonetype() + " id: " + zone.getId());
                return null;
            }
            try {
                Constructor<?> zoneConstructor = newZone.getConstructor();
                temp = (ZoneBase) zoneConstructor.newInstance();
            } catch(Exception e) {
                log.warning("[Zones]Error in constructing zone: " + zone.getId() + ".");
                e.printStackTrace();
                return null;
            }
            temp.initialize(plugin, world, zone);
            List<Vertice> vertices = zone.getVertices();
            
            if(zone.getFormtype().equalsIgnoreCase("ZoneCuboid")) {
                if (vertices.size() == 2) {
                    temp.setForm(new ZoneCuboid(vertices, zone.getMinz(), zone.getMaxz()));
                } else {
                    log.info("[Zones]Missing zone vertex for cuboid zone id: " + zone.getId());
                    return null;
                }
            } else if(zone.getFormtype().equalsIgnoreCase("ZoneNPoly")) {
                if (vertices.size() > 2) {
                    temp.setForm(new ZoneNPoly(vertices, zone.getMinz() , zone.getMaxz()));
                } else {
                    log.warning("[Zones]Bad data for zone: " + zone.getId());
                    return null;
                }
            }
        } catch(Exception e) {
            log.warning("[Zones]Error loading zone " + zone.getId() + ".");
            e.printStackTrace();
            return null;
        }
        addZone(temp);
        return temp;
    }
    
    public void addZone(ZoneBase zone) {
        zone.getWorldManager().addZone(zone);
        zones.put(zone.getId(), zone);
    }

    public ZoneBase getZone(int id) {
        return zones.get(id);
    }

    public boolean delete(ZoneBase toDelete) {
        if (!zones.containsKey(toDelete.getId()))
            return false;

        //plugin.getDatabase().find(Vertice.class).where().gt("id", toDelete.getId());
        //plugin.getDatabase().delete(toDelete.getPersistence().getVertices());
        plugin.getDatabase().execute(plugin.getDatabase().createCallableSql("DELETE FROM zones_vertices WHERE id  = " + toDelete.getId() + ""));
        plugin.getDatabase().delete(toDelete.getPersistence());
        //plugin.getDatabase().createUpdate(Vertice.class, "delete from zones_vertices where id = " + toDelete.getId()).execute();

        removeZone(toDelete);
        return true;
    }

    /*
     * A little note on using playerId(entity id):
     * I used this mainly because it is waay more efficient then using strings.
     * 
     */
    public void addSelection(int playerId, ZoneSelection zone) {
        selections.put(playerId, zone);
    }

    public ZoneSelection getSelection(int playerId) {
        return selections.get(playerId);
    }

    public boolean zoneExists(int id) {
        return zones.containsKey(id);
    }

    public void removeSelection(int playerId) {
        selections.remove(playerId);
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
        return zones.getValues(new ZoneBase[zones.size()]);
    }

    public void reloadZone(int id) {
        ZoneBase zone = getZone(id);
        removeZone(zone);
        Zone persistentZone = plugin.getDatabase().find(Zone.class, id);
        if(persistentZone != null) {
            WorldManager wm = plugin.getWorldManager(persistentZone.getWorld());
            if(wm == null) {
                log.warning("[Zones] Trying to load zone: " + id + " with invalid world " + persistentZone.getWorld() + "!");
            } else {                
                loadFromPersistentData(wm, persistentZone);
            }
        }
    }

    public void removeZone(ZoneBase zone) {
        zone.getWorldManager().removeZone(zone);
        zones.remove(zone.getId());
    }

}
