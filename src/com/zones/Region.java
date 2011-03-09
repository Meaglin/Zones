package com.zones;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Region {

    private ArrayList<ZoneType> _zones;
    private int                 x, y;

    public Region(int x, int y) {
        this.x = x;
        this.y = y;
        _zones = new ArrayList<ZoneType>();
    }

    public void addZone(ZoneType zone) {
        if (_zones.contains(zone))
            return;

        _zones.add(zone);
    }

    public void removeZone(ZoneType zone) {
        for (int i = 0; i < _zones.size(); i++) {
            if (_zones.get(i).getId() == zone.getId())
                _zones.remove(i);
        }
    }

    public ArrayList<ZoneType> getZones() {
        return _zones;
    }

    public ZoneType getActiveZone(Player player)                             {return getActiveZone(player.getLocation());}
    public ZoneType getActiveZone(Location loc)                              {return getActiveZone(loc.getX(), loc.getZ(), loc.getY(),loc.getWorld().getName());}
    public ZoneType getActiveZone(double x, double y, double z,String world) {return getActiveZone(World.toInt(x), World.toInt(y), World.toInt(z),world);}

    public ArrayList<ZoneType> getActiveZones(Player player)                             {return getActiveZones(player.getLocation());}
    public ArrayList<ZoneType> getActiveZones(Location loc)                              {return getActiveZones(loc.getX(), loc.getZ(), loc.getY(),loc.getWorld().getName());}
    public ArrayList<ZoneType> getActiveZones(double x, double y, double z,String world) {return getActiveZones(World.toInt(x), World.toInt(y), World.toInt(z),world);}

    public ZoneType getActiveZone(int x, int y, int z,String world) {
        ZoneType primary = null;

        for (ZoneType zone : getZones())
            if (zone.isInsideZone(x, y, z,world) && (primary == null || primary.getZone().getSize() > zone.getZone().getSize()))
                primary = zone;

        return primary;
    }
    
    public ArrayList<ZoneType> getActiveZones(int x, int y, int z,String world) {
        ArrayList<ZoneType> zones = new ArrayList<ZoneType>();

        for (ZoneType zone : getZones())
            if (zone.isInsideZone(x, y, z,world))
                zones.add(zone);

        return zones;
    }

    public ArrayList<ZoneType> getAdminZones(Player player) {
        ArrayList<ZoneType> zones = new ArrayList<ZoneType>();

        for (ZoneType zone : getZones())
            if (zone.isInsideZone(player) && zone.canAdministrate(player))
                zones.add(zone);

        return zones;
    }

    public ArrayList<ZoneType> getAdminZones(Player player, Location loc) {
        ArrayList<ZoneType> zones = new ArrayList<ZoneType>();

        for (ZoneType zone : getZones())
            if (zone.isInsideZone(loc) && zone.canAdministrate(player))
                zones.add(zone);

        return zones;
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public void revalidateZones(Player player) {
        for (ZoneType z : getZones()) {
            if (z != null)
                z.revalidateInZone(player);
        }
    }
    
    public void revalidateZones(Player player, Location loc) {
        for (ZoneType z : getZones()) {
            if (z != null)
                z.revalidateInZone(player, loc);
        }
    }
}
