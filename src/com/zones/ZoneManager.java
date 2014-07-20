package com.zones;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import com.zones.model.forms.ZoneCuboid;
import com.zones.model.forms.ZoneCylinder;
import com.zones.model.forms.ZoneNPoly;
import com.zones.model.forms.ZoneSphere;
import com.zones.model.types.ZoneNormal;
import com.zones.persistence.Vertice;
import com.zones.persistence.Zone;
import com.zones.selection.ZoneSelection;
import com.zones.world.WorldManager;

/**
 * 
 * @author Meaglin
 *
 */
public class ZoneManager {
    private HashMap<Integer, ZoneNormal>       zones;
    private HashMap<Integer, ZoneSelection>  selections;
    private HashMap<Integer, Integer>        selectedZones;
    protected static final Logger             log = Logger.getLogger("Minecraft");
    private Zones                             plugin;
    
    protected ZoneManager(Zones plugin) {
        zones = new HashMap<Integer, ZoneNormal>();
        selections = new HashMap<Integer, ZoneSelection>();
        selectedZones = new HashMap<Integer, Integer>();
        this.plugin = plugin;
    }

    public void cleanUp(WorldManager world) {
        selectedZones.clear();
        Iterator<Entry<Integer, ZoneNormal>> it = zones.entrySet().iterator();
        while(it.hasNext()) {
            Entry<Integer, ZoneNormal> zone = it.next();
            if(zone.getValue().getWorldManager().equals(world)) {
                it.remove();
            }
        }
    }
    public void load(WorldManager world) {
        cleanUp(world);
        try {
            List<Zone> zones = plugin.getMysqlDatabase().get(world.getWorldName());
             addZones(world, loadFromPersistentData(world, zones));
        } catch(Exception e) {
            log.warning("[Zones] Error loading world " + world.getWorldName() + ".");
            e.printStackTrace();
            return;
        }
    }

    public List<ZoneNormal> loadFromPersistentData(WorldManager world, List<Zone> zones) {
        List<ZoneNormal> list = new ArrayList<>();
        for(Zone zone : zones) {
            list.add(loadFromPersistentData(world, zone));
        }
        return list;
    }
    
    public ZoneNormal loadFromPersistentData(WorldManager world, Zone zone) {
        Class<?> newZone = null;
        ZoneNormal temp = null;
        try {
            try {
                newZone = Class.forName("com.zones.model.types."+zone.getZonetype());
            } catch (ClassNotFoundException e) {
                log.warning("[Zones] No such zone class: " + zone.getZonetype() + " id: " + zone.getId());
                return null;
            }
            try {
                Constructor<?> zoneConstructor = newZone.getConstructor();
                temp = (ZoneNormal) zoneConstructor.newInstance();
            } catch(Exception e) {
                log.warning("[Zones] Error in constructing zone: " + zone.getId() + ".");
                e.printStackTrace();
                return null;
            }
            temp.initialize(plugin, world, zone);
            List<Vertice> vertices = zone.getVertices();
            
            if(zone.getFormtype().equalsIgnoreCase("ZoneCuboid")) {
                if (vertices.size() == 2) {
                    temp.setForm(new ZoneCuboid(vertices, zone.getMiny(), zone.getMaxy()));
                } else {
                    log.info("[Zones] Missing zone vertex for cuboid zone id: " + zone.getId());
                    return null;
                }
            } else if(zone.getFormtype().equalsIgnoreCase("ZoneNPoly")) {
                if (vertices.size() > 2) {
                    temp.setForm(new ZoneNPoly(vertices, zone.getMiny() , zone.getMaxy()));
                } else {
                    log.warning("[Zones] Bad data for zone: " + zone.getId());
                    return null;
                }
            } else if(zone.getFormtype().equalsIgnoreCase("ZoneCylinder")) {
                if (vertices.size() == 2) {
                    temp.setForm(new ZoneCylinder(vertices, zone.getMiny(), zone.getMaxy()));
                } else {
                    log.info("[Zones] Missing zone vertex for Cylinder zone id: " + zone.getId());
                    return null;
                }
            } else if(zone.getFormtype().equalsIgnoreCase("ZoneSphere")) {
                if (vertices.size() == 1) {
                    temp.setForm(new ZoneSphere(vertices, zone.getMiny(), zone.getMaxy()));
                } else {
                    log.info("[Zones] Missing zone vertex for Sphere zone id: " + zone.getId());
                    return null;
                }
            }
        } catch(Exception e) {
            log.warning("[Zones] Error loading zone " + zone.getId() + ".");
            e.printStackTrace();
            return null;
        }
        return temp;
    }
    
    public ZoneNormal addZone(ZoneNormal zone) {
        zone.getWorldManager().addZone(zone);
        zones.put(zone.getId(), zone);
        return zone;
    }
    
    public void addZones(WorldManager world, List<ZoneNormal> zones) {
        world.addZones(zones);
        for(ZoneNormal zone: zones) {
            this.zones.put(zone.getId(), zone); 
        }
    }
    
    public ZoneNormal getZone(int id) {
        return zones.get(id);
    }

    public boolean delete(ZoneNormal toDelete) {
        if (!zones.containsKey(toDelete.getId()))
            return false;

        plugin.getMysqlDatabase().delete(toDelete.getPersistence());

        removeZone(toDelete);
        return true;
    }

    /*
     * A little note on using playerId(entity id):
     * I used this mainly because it is waay more efficient then using strings.
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
    
    public ZoneNormal getSelectedZone(int playerId) {
        return getZone(getSelected(playerId));
    }

    public void removeSelected(int playerId) {
        selectedZones.remove(playerId);
    }

    public Collection<ZoneNormal> getAllZones() {
        return zones.values();
    }

    public void reloadZone(int id) {
        ZoneNormal zone = getZone(id);
        if(zone != null)
            removeZone(zone);
        
        Zone persistentZone = plugin.getMysqlDatabase().get(id);
        if(persistentZone != null) {
            WorldManager wm = plugin.getWorldManager(persistentZone.getWorld());
            if(wm == null) {
                log.warning("[Zones] Trying to load zone: " + id + " with invalid world " + persistentZone.getWorld() + "!");
            } else {                
                addZone(loadFromPersistentData(wm, persistentZone));
            }
        }
    }

    public void removeZone(ZoneNormal zone) {
        zone.getWorldManager().removeZone(zone);
        zones.remove(zone.getId());
    }
    
    public int getZoneCount() {
        return zones.size();
    }
    
    
    public List<ZoneNormal> matchZone(String search) {
        List<ZoneNormal> list = new ArrayList<ZoneNormal>();
        try {
            int index = Integer.parseInt(search);
            ZoneNormal b = getZone(index);
            if(b != null) list.add(b);
        } catch(NumberFormatException e) {
            String var = search.toLowerCase();
            for(ZoneNormal b : getAllZones())
                if(b != null && b.getName().toLowerCase().contains(var))
                    list.add(b);
        }
        return list;
    }
    
    public List<ZoneNormal> matchZone(Player player, String search) {
        List<ZoneNormal> list = new ArrayList<ZoneNormal>();
        try {
            int index = Integer.parseInt(search);
            ZoneNormal b = getZone(index);
            if(b != null && b.canAdministrate(player)) list.add(b);
        } catch(NumberFormatException e) {
            String var = search.toLowerCase();
            for(ZoneNormal b : getAllZones())
                if(b != null && b.getName().toLowerCase().contains(var) && b.canAdministrate(player))
                    list.add(b);
        }
        return list;
    }
}
