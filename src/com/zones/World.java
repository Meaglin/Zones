package com.zones;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class World {
    public static final int MIN_X      = -10240;
    public static final int MAX_X      = 10240;

    public static final int MIN_Y      = -10240;
    public static final int MAX_Y      = 10240;

    public static final int MIN_Z      = 0;
    public static final int MAX_Z      = 127;

    public static final int SHIFT_SIZE = 8;
    public static final int BLOCK_SIZE = (int) (Math.pow(2, SHIFT_SIZE) - 1);

    public static final int X_REGIONS  = ((MAX_X - MIN_X) >> SHIFT_SIZE) + 1;
    public static final int Y_REGIONS  = ((MAX_Y - MIN_Y) >> SHIFT_SIZE) + 1;

    @SuppressWarnings("unused")
    public static final int XMOD       = (MIN_X < 0 ? -1 : 1);
    @SuppressWarnings("unused")
    public static final int YMOD       = (MIN_Y < 0 ? -1 : 1);

    public static final int OFFSET_X   = ((MIN_X * XMOD) >> SHIFT_SIZE) * XMOD;
    public static final int OFFSET_Y   = ((MIN_Y * YMOD) >> SHIFT_SIZE) * YMOD;

    private Region[][]      regions;
    private Region          emptyRegion = new Region(0,0);
    
    public World() {
        load();
    }
    public void load() {
        try {
            regions = new Region[X_REGIONS][Y_REGIONS];
            for (int x = 0; x < X_REGIONS; x++) {
                for (int y = 0; y < Y_REGIONS; y++) {
                    regions[x][y] = new Region(x, y);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ZoneManager.log.info("[Zones]Loaded " + X_REGIONS * Y_REGIONS + " regions.");
    }

    public Region getRegion(Player player)      {return getRegion(player.getLocation());}
    public Region getRegion(Location loc)       {return getRegion(loc.getX(),loc.getZ()); }
    public Region getRegion(double x, double y) {return getRegion(toInt(x), toInt(y));}
    
    
    public ArrayList<ZoneBase> getAdminZones(Player player)                     {return getRegion(player).getAdminZones(player);}

    public ArrayList<ZoneBase> getAdminZones(Player player,Location loc)        {return getRegion(loc).getAdminZones(player,loc);}
    
    public ArrayList<ZoneBase> getActiveZones(Player player)                    {return getRegion(player).getActiveZones(player);}

    public ZoneBase getActiveZone(Player player)                                {return getRegion(player).getActiveZone(player);}
    public ZoneBase getActiveZone(double x, double y, double z,String world)    {return getRegion(x, y).getActiveZone(x, y, z,world);}
    public ZoneBase getActiveZone(Location loc)                                 {return getRegion(loc).getActiveZone(loc);}

    public boolean regionChange(Location from,Location to)                       {return getRegion(from).equals(getRegion(to)); }
    
    public void revalidateZones(Player player) {getRegion(player).revalidateZones(player);}
    
    public Region getRegion(int x, int y) {
        // debug only ;) .
        // System.out.println("get region " + ((x - MIN_X) >> SHIFT_SIZE) + " "
        // + ((y - MIN_Y) >> SHIFT_SIZE));
        if (x > MAX_X || x < MIN_X || y > MAX_Y || y < MIN_Y) {
            ZoneManager.log.warning("[Zones]Warning: Player moving outside world!");
            return emptyRegion;
        }

        return regions[(x - MIN_X) >> SHIFT_SIZE][(y - MIN_Y) >> SHIFT_SIZE];
    }

    public void addZone(int x, int y, ZoneBase zone) {
        regions[x][y].addZone(zone);
    }

    public static final World getInstance() {
        return SingletonHolder._instance;
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final World _instance = new World();
    }

    public void revalidateZones(Player player, Location from, Location to) {
        // region changes.
        if (regionChange(from,to)) {
            getRegion(from).revalidateZones(player, to);
        }
        // default revalidation.
        getRegion(to).revalidateZones(player, to);
    }

    public static int toInt(double b) {
        int r = (int) b;
        return b < r ? r - 1 : r;
    }
}
