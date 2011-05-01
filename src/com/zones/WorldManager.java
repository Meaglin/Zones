package com.zones;

import gnu.trove.TLongObjectHashMap;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.zones.model.WorldConfig;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneForm;

/**
 * 
 * @author Meaglin
 *
 */
public class WorldManager {
    public static final int            MIN_X       = Integer.MIN_VALUE;
    public static final int            MAX_X       = Integer.MAX_VALUE;

    public static final int            MIN_Y       = Integer.MIN_VALUE;
    public static final int            MAX_Y       = Integer.MAX_VALUE;

    public static final int            MIN_Z       = 0;
    public static final int            MAX_Z       = 127;

    public static final int            SHIFT_SIZE  = 7;
    public static final int            BLOCK_SIZE  = (int) (Math.pow(2, SHIFT_SIZE) - 1);

    public static final int            X_REGIONS   = ((MAX_X - MIN_X) >> SHIFT_SIZE) + 1;
    public static final int            Y_REGIONS   = ((MAX_Y - MIN_Y) >> SHIFT_SIZE) + 1;

    public static final int            XMOD        = (MIN_X < 0 ? -1 : 1);
    public static final int            YMOD        = (MIN_Y < 0 ? -1 : 1);

    public static final int            OFFSET_X    = ((MIN_X * XMOD) >> SHIFT_SIZE) * XMOD;
    public static final int            OFFSET_Y    = ((MIN_Y * YMOD) >> SHIFT_SIZE) * YMOD;

    private TLongObjectHashMap<Region> regions     = new TLongObjectHashMap<Region>();
    private static final Region        emptyRegion = new Region(0, 0);

    private WorldConfig                worldConfig;

    private World                      world;
    private Zones                      plugin;
    
    public WorldManager(Zones plugin, World world) {
        if(world == null) {
            Zones.log.warning("[Zones] --------------------------");
            Zones.log.warning("[Zones] Trying to create a WorldManager with a NULL world, this should NEVER occur!");
            Zones.log.warning("[Zones] if this error occurs you have either a faulty plugin running or your server is seriously screwed up!");
            Zones.log.warning("[Zones] restarting your server is highly recommended.");
            Zones.log.warning("[Zones] --------------------------");
        }
        this.world = world;
        this.plugin = plugin;
        worldConfig = new WorldConfig(this, plugin.getDataFolder().getPath() + "/" + world.getName() + ".properties");
        plugin.getZoneManager().load(this);
    }
    
    public void load() {
        regions.clear();
        plugin.getZoneManager().load(this);
        worldConfig.load();
        //ZoneManager.log.info("[Zones]Loaded " + X_REGIONS * Y_REGIONS + " regions.");
    }
    
    public void loadRegions() {
        regions.clear();
        plugin.getZoneManager().load(this);
    }
    
    public void loadConfig() {
        worldConfig.load();
    }

    public Region getRegion(Player player)      {return getRegion(player.getLocation());}
    public Region getRegion(Location loc)       {return getRegion(loc.getX(),loc.getZ()); }
    public Region getRegion(double x, double y) {return getRegion(toInt(x), toInt(y));}
    
    
    public List<ZoneBase> getAdminZones(Player player)                     {return getRegion(player).getAdminZones(player);}
    public List<ZoneBase> getAdminZones(Player player,Location loc)        {return getRegion(loc).getAdminZones(player,loc);}
    
    public List<ZoneBase> getActiveZones(Player player)                         {return getActiveZones(player.getLocation());}
    public List<ZoneBase> getActiveZones(Location loc)                          {return getActiveZones(loc.getX(),loc.getZ(),loc.getY()); } 
    public List<ZoneBase> getActiveZones(double x,double y,double z)            {return getActiveZones(toInt(x),toInt(y),toInt(z)); }
    public List<ZoneBase> getActiveZones(int x,int y,int z)                     {return getRegion(x,y).getActiveZones(x, y, z); }
    
    public ZoneBase getActiveZone(Player player)                                {return getRegion(player).getActiveZone(player);}
    public ZoneBase getActiveZone(double x, double y, double z)                 {return getRegion(x, y).getActiveZone(x, y, z);}
    public ZoneBase getActiveZone(Location loc)                                 {return getRegion(loc).getActiveZone(loc);}
    public ZoneBase getActiveZone(int x, int y, int z)                          {return getRegion(x,y).getActiveZone(x, y, z);}
    public ZoneBase getActiveZone(Block block)                                  {return getActiveZone(block.getX(),block.getZ(),block.getY());}
    
    public boolean regionChange(Location from,Location to)                      {return getRegion(from).equals(getRegion(to)); }
    
    public void revalidateZones(Player player) {getRegion(player).revalidateZones(player);}
    
    public Region getRegion(int x, int y) {
        // debug only ;) .
        // System.out.println("get region " + ((x - MIN_X) >> SHIFT_SIZE) + " "
        // + ((y - MIN_Y) >> SHIFT_SIZE));
        /*        
        if (x > MAX_X || x < MIN_X || y > MAX_Y || y < MIN_Y) {
            ZoneManager.log.warning("[Zones]Warning: Player moving outside world!");
            return emptyRegion;
        }*/

        long index = toLong((x - MIN_X) >> SHIFT_SIZE, (y - MIN_Y) >> SHIFT_SIZE);
        if(regions.containsKey(index))
            return regions.get(index);
        else
            return emptyRegion;
    }

    public void addZone(ZoneBase zone) {
        
        /*
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
        } */
        ZoneForm f = zone.getZone();
        int ax, ay, bx, by;
        for (int x = (f.getLowX() - MIN_X) >> SHIFT_SIZE; x <= (f.getHighX() - MIN_X) >> SHIFT_SIZE; x++) {
            for (int y = (f.getLowY() - MIN_Y) >> SHIFT_SIZE; y <= (f.getHighY() - MIN_Y) >> SHIFT_SIZE; y++) {
                ax = (x + WorldManager.OFFSET_X) << WorldManager.SHIFT_SIZE;
                bx = ((x + 1) + WorldManager.OFFSET_X) << WorldManager.SHIFT_SIZE;
                ay = (y + WorldManager.OFFSET_Y) << WorldManager.SHIFT_SIZE;
                by = ((y + 1) + WorldManager.OFFSET_Y) << WorldManager.SHIFT_SIZE;
                if (zone.getZone().intersectsRectangle(ax, bx, ay, by)) {
                    long index = toLong(x,y);
                    if(regions.containsKey(index)) {
                        regions.get(index).addZone(zone);
                    } else {
                        regions.put(index, new Region(x,y));
                        regions.get(index).addZone(zone);
                    }
                }
            }
        }
    }

    public void revalidateZones(Player player, Location from, Location to) {
        // region changes.
        if (regionChange(from,to)) {
            getRegion(from).revalidateZones(player, to);
        }
        // default revalidation.
        getRegion(to).revalidateZones(player, to);
    }

    public void removeZone(ZoneBase toDelete) {
        for(Region reg : regions.getValues(new Region[regions.size()]))
            reg.removeZone(toDelete);
    }
    
    public String getWorldName() { return getWorld().getName(); }
    public World getWorld() {
        return world;
    }
    
    public Zones getPlugin() {
        return plugin;
    }
    
    public WorldConfig getConfig() {
        return worldConfig;
    }
    
    public static int toInt(double b) {
        int r = (int) b;
        return b < r ? r - 1 : r;
    }
    
    public static long toLong(int x, int y) {
        return ((((long)x) << 32) | ((long)y & 0xFFFFFFFFL));
    }
}
