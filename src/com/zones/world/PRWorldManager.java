package com.zones.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.khelekore.prtree.MBRConverter;
import org.khelekore.prtree.PRTree;
import org.khelekore.prtree.SimpleMBR;

import com.zones.Zones;
import com.zones.model.types.ZoneNormal;

/**
 * 
 * @author Meaglin
 *
 */
public class PRWorldManager extends WorldManager {
    
    
    private PRTree<ZoneNormal> tree;
    private Map<Integer, ZoneNormal> known;
    
    private static MBRConverter<ZoneNormal> converter;
    private List<ZoneNormal> cacheList;
    
    public PRWorldManager(Zones plugin, World world) {
        super(plugin, world);
    }
    
    @Override
    public void init() {
        converter = new ZoneMBRConverter();
        
        cacheList = new ArrayList<>();
        tree = new PRTree<>(converter, 30);
        known = new HashMap<>();
    }

    @Override
    public List<ZoneNormal> getAdminZones(Player player, int x, int y, int z) {
        cacheList.clear();
        tree.find(new SimpleMBR(x, x, y, y, z, z), cacheList);
        List<ZoneNormal> list = new ArrayList<>();
        for(ZoneNormal zone : cacheList) {
            if(zone.isInsideZone(x, y, z) && zone.canAdministrate(player)) {
                list.add(zone);
            }
        }
        return list;
    }
    
    @Override
    public List<ZoneNormal> getActiveZones(int x,int y,int z) {
        cacheList.clear();
        tree.find(new SimpleMBR(x, x, y, y, z, z), cacheList);
        List<ZoneNormal> list = new ArrayList<>();
        for(ZoneNormal zone : cacheList) {
            if(zone.isInsideZone(x, y, z)) {
                list.add(zone);
            }
        }
        return list;
    }

    @Override
    public ZoneNormal getActiveZone(int x, int y, int z) {
        cacheList.clear();
        tree.find(new SimpleMBR(x, x, y, y, z, z), cacheList);
        ZoneNormal zone = null;
        for(ZoneNormal zo : cacheList) {
            if(zo.isInsideZone(x, y, z)
                && (
                    zone == null
                    || 
                    zo.getForm().getSize() <= zone.getForm().getSize()
                )
            ) {
                zone = zo;
            }
        }
        return zone;
    }

    @Override
    public List<ZoneNormal> getZones(int minx, int maxx, int miny, int maxy, int minz, int maxz) {
        cacheList.clear();
        tree.find(new SimpleMBR(minx, maxx, miny, maxy, minz, maxz), cacheList);
        List<ZoneNormal> list = new ArrayList<>();
        for(ZoneNormal zone : cacheList) {
            if(zone.getForm().isInY(miny)
                    || zone.getForm().isInY(maxy)) {
                list.add(zone);
            }
        }
        return list;
    }
    
    @Override
    public void revalidateZones(Player player) { 
        cacheList.clear();
        Location loc = player.getLocation();
        tree.find(new SimpleMBR(loc.getBlockX(), loc.getBlockX(), loc.getBlockY(), loc.getBlockY(), loc.getBlockZ(), loc.getBlockZ()), cacheList);
        for(ZoneNormal zone : cacheList) {
            zone.revalidateInZone(player);
        }
    }
    
    @Override
    public void revalidateZones(Player player, Location from, Location to) {
        cacheList.clear();
        tree.find(new SimpleMBR(from.getBlockX(), from.getBlockX(), from.getBlockY(), from.getBlockY(), from.getBlockZ(), from.getBlockZ()), cacheList);
        for(ZoneNormal zone : cacheList) {
            zone.revalidateInZone(player, from);
        }
        cacheList.clear();
        tree.find(new SimpleMBR(to.getBlockX(), to.getBlockX(), to.getBlockY(), to.getBlockY(), to.getBlockZ(), to.getBlockZ()), cacheList);
        for(ZoneNormal zone : cacheList) {
            zone.revalidateInZone(player, to);
        }
    }
    
    @Override
    public void revalidateOutZones(Player player, Location from) {
        cacheList.clear();
        tree.find(new SimpleMBR(from.getBlockX(), from.getBlockX(), from.getBlockY(), from.getBlockY(), from.getBlockZ(), from.getBlockZ()), cacheList);
        for(ZoneNormal zone : cacheList) {
            if(zone.isInsideZone(from)) {
                zone.removePlayer(player);
            }
        }
    }
    
    @Override
    public void addZone(ZoneNormal zone) {
        known.put(zone.getId(), zone);
        rebuild();
    }

    @Override
    public void addZones(List<ZoneNormal> zone) {
        for(ZoneNormal z : zone) {
            known.put(z.getId(), z);
        }
        rebuild();
    }
    
    private void rebuild() {
        tree = new PRTree<>(converter, 30);
        tree.load(known.values());
    }

    @Override
    public void removeZone(ZoneNormal toDelete) {
        if(known.remove(toDelete.getId()) != null) {
            rebuild();
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof PRWorldManager)) {
            return false;
        }
        return ((PRWorldManager)o).getWorld().getName().equals(getWorld().getName());
    }
    
    @Override
    public int hashCode() {
        return getWorldName().hashCode();
    }
}
