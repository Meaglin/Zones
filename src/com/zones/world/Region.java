package com.zones.world;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.zones.model.ZoneForm;
import com.zones.model.types.ZoneInherit;
import com.zones.model.types.ZoneNormal;

/**
 * 
 * @author Meaglin
 *
 */
public class Region {

    private ArrayList<ZoneNormal> _zones;
    private int                 x, z;

    public Region(int x, int z) {
        this.x = x;
        this.z = z;
        _zones = new ArrayList<ZoneNormal>();
    }

    public void addZone(ZoneNormal zone) {
        if (_zones.contains(zone))
            return;

        
        for(ZoneNormal b : _zones) {
            if(b instanceof ZoneInherit && !((ZoneInherit)b).containsInherited(zone) && zone.getForm().contains(b.getForm())) {
                ((ZoneInherit)b).addInherited(zone);
                if(zone instanceof ZoneInherit && !((ZoneInherit)zone).containsSub(b))
                    ((ZoneInherit)zone).addSub(b);
            } 
            if(zone instanceof ZoneInherit && !((ZoneInherit)zone).containsInherited(b) && b.getForm().contains(zone.getForm())) {
                ((ZoneInherit)zone).addInherited(b);
                if(b instanceof ZoneInherit && !((ZoneInherit)b).containsSub(zone))
                    ((ZoneInherit)b).addSub(zone);
            }
        }
        _zones.add(zone);
    }

    public void removeZone(ZoneNormal zone) {
        _zones.remove(zone);
        for(ZoneNormal b : _zones) {
            if(b instanceof ZoneInherit) {
                ((ZoneInherit)b).removeInherited(zone);
                ((ZoneInherit)b).removeSub(zone);
            }
        }
    }

    public ArrayList<ZoneNormal> getZones() {
        return _zones;
    }

    public ZoneNormal getActiveZone(int x, int y, int z) {
        ZoneNormal primary = null;

        for (ZoneNormal zone : getZones())
            if (zone.isInsideZone(x, y, z) && (primary == null || primary.getForm().getSize() > zone.getForm().getSize()))
                primary = zone;

        return primary;
    }
    
    public ArrayList<ZoneNormal> getActiveZones(int x, int y, int z) {
        ArrayList<ZoneNormal> zones = new ArrayList<ZoneNormal>();

        for (ZoneNormal zone : getZones())
            if (zone.isInsideZone(x, y, z))
                zones.add(zone);

        return zones;
    }

    public ArrayList<ZoneNormal> getAdminZones(Player player, int x, int y, int z) {
        ArrayList<ZoneNormal> zones = new ArrayList<ZoneNormal>();

        for (ZoneNormal zone : getZones())
            if (zone.isInsideZone(x, y, z) && zone.canAdministrate(player))
                zones.add(zone);

        return zones;
    }
    
    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
    
    public void revalidateZones(Player player) {
        for (ZoneNormal z : getZones()) {
            if (z != null)
                z.revalidateInZone(player);
        }
    }
    
    public void revalidateZones(Player player, Location loc) {
        for (ZoneNormal z : getZones()) {
            if (z != null)
                z.revalidateInZone(player, loc);
        }
    }

    public void revalidateOutZones(Player player, Location from) {
        for (ZoneNormal z : getZones()) {
            if (z != null)
                z.removePlayer(player);
        }
    }
    
    public boolean contains(ZoneNormal zone) {
        ZoneForm form = zone.getForm();
        int ax = getX() << RegionWorldManager.SHIFT_SIZE;
        int bx = (getX() + 1) << RegionWorldManager.SHIFT_SIZE;
        int ay = getZ() << RegionWorldManager.SHIFT_SIZE;
        int by = (getZ() + 1) << RegionWorldManager.SHIFT_SIZE;
        return form.intersectsRectangle(ax, bx, ay, by);
    }
    
    @Override
    public boolean equals(Object object) {
        if(!(object instanceof Region))
            return false;
        
        Region r = (Region)object;
        
        return (r.getX() == getX() && r.getZ() == getZ());
    }

    @Override
    public int hashCode() {
        return getX() ^ getZ();
    }
}
