package com.zones.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.ZoneForm;
import com.zones.model.types.ZoneNormal;

/**
 * 
 * @author Meaglin
 *
 */
public class RegionWorldManager extends WorldManager {
    public static final int            MIN_X       = Integer.MIN_VALUE;
    public static final int            MAX_X       = Integer.MAX_VALUE;

    public static final int            MIN_Y       = Integer.MIN_VALUE;
    public static final int            MAX_Y       = Integer.MAX_VALUE;

    public static final int            MIN_Z       = 0;
    public static final int            MAX_Z       = 127;

    public static final int            SHIFT_SIZE  = 6;
    public static final int            BLOCK_SIZE  = (int) (Math.pow(2, SHIFT_SIZE) - 1);

    public static final int            X_REGIONS   = ((MAX_X - MIN_X) >> SHIFT_SIZE) + 1;
    public static final int            Y_REGIONS   = ((MAX_Y - MIN_Y) >> SHIFT_SIZE) + 1;

    public static final int            XMOD        = -1;
    public static final int            YMOD        = -1;

    public static final int            OFFSET_X    = ((MIN_X * XMOD) >> SHIFT_SIZE) * XMOD;
    public static final int            OFFSET_Y    = ((MIN_Y * YMOD) >> SHIFT_SIZE) * YMOD;

    private HashMap<Long, Region> regions;     
    private static final Region        emptyRegion = new Region(0, 0);

    
    public RegionWorldManager(Zones plugin, World world) {
        super(plugin, world);
        regions = new HashMap<Long, Region>();
    }
    
    @Override
    public void load() {
        regions.clear();
        super.load();
    }

    private Region getRegion(Player player)      {return getRegion(player.getLocation());}
    private Region getRegion(Location loc)       {return getRegion(loc.getX(),loc.getZ()); }
    private Region getRegion(double x, double z) {return getRegion(toInt(x), toInt(z));}
    
    @Override
    public List<ZoneNormal> getAdminZones(Player player, int x, int y, int z) { return getRegion(x, z).getAdminZones(player, x, y, z); }
    @Override
    public List<ZoneNormal> getActiveZones(int x,int y,int z)                     { return getActiveZones(x, y, z); }
    @Override
    public ZoneNormal getActiveZone(int x, int y, int z)                          { return getRegion(x, y).getActiveZone(x, y, z); }

    private boolean regionChange(Location from, Location to)                      { return !getRegion(from).equals(getRegion(to)); }
    
    @Override
    public void revalidateZones(Player player) { getRegion(player).revalidateZones(player); }
    
    public Region getRegion(int x, int z) {
        long index = toLong((x) >> SHIFT_SIZE, (z) >> SHIFT_SIZE);
        Region rg = regions.get(index);
        if(rg != null) {
            return rg;
        } else {
            return emptyRegion;
        }
    }

    @Override
    public void addZone(ZoneNormal zone) {
        ZoneForm f = zone.getForm();
        int ax, az, bx, bz;
        for (int x = (f.getLowX() >> SHIFT_SIZE); x <= (f.getHighX() >> SHIFT_SIZE); x++) {
            for (int z = (f.getLowZ() >> SHIFT_SIZE); z <= (f.getHighZ() >> SHIFT_SIZE); z++) {
                ax = x << RegionWorldManager.SHIFT_SIZE;
                bx = (x + 1) << RegionWorldManager.SHIFT_SIZE;
                az = z << RegionWorldManager.SHIFT_SIZE;
                bz = (z + 1) << RegionWorldManager.SHIFT_SIZE;
                if (zone.getForm().intersectsRectangle(ax, bx, az, bz)) {
                    long index = toLong(x,z);
                    Region rg = regions.get(index);
                    if(rg != null) {
                        rg.addZone(zone);
                    } else {
                        rg = new Region(x,z);
                        rg.addZone(zone);
                        regions.put(index, rg);
                    }
                }
            }
        }
    }
    
    @Override
    public void addZones(List<ZoneNormal> zones) {
        for(ZoneNormal zone : zones) {
            addZone(zone);
        }
    }
    
    @Override
    public void removeZone(ZoneNormal toDelete) {
        for(Region reg : regions.values())
            reg.removeZone(toDelete);
    }

    @Override
    public void revalidateZones(Player player, Location from, Location to) {
        // region changes.
        if (regionChange(from, to)) {
            getRegion(from).revalidateZones(player, to);
        }
        // default revalidation.
        getRegion(to).revalidateZones(player, to);
    }

    @Override
    public void revalidateOutZones(Player player, Location from) {
        getRegion(from).revalidateOutZones(player, from);
    }

    
    public static final long toLong(int x, int z) {
        return ((((long)x) << 32) | ((long)z & 0xFFFFFFFFL));
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof RegionWorldManager)) {
            return false;
        }
        return ((RegionWorldManager)o).getWorld().getName().equals(getWorld().getName());
    }

    @Override
    public List<ZoneNormal> getZones(int minx, int maxx, int miny, int maxy, int minz, int maxz) {
        HashSet<ZoneNormal> set = new HashSet<>();
        for (int x = (minx >> SHIFT_SIZE); x <= (maxx >> SHIFT_SIZE); x++) {
            for (int z = (minz >> SHIFT_SIZE); z <= (maxz >> SHIFT_SIZE); z++) {
                long index = toLong(x,z);
                Region rg = regions.get(index);
                for(ZoneNormal zone : rg.getZones()) {
                    if(zone.getForm().intersectsRectangle(minx, maxx, minz, maxz) && (zone.getForm().isInY(miny) || zone.getForm().isInY(maxy))) {
                        set.add(zone);
                    }
                }
            }
        }
        return new ArrayList<>(set);
    }
}
