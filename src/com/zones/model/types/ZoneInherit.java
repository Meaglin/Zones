package com.zones.model.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;

import com.zones.model.ZoneBase;

public class ZoneInherit extends ZoneNormal {

    private List<ZoneBase> inheritedZones;
    /**
     * Warning: this list only contains zones that have this zone as inherited zone.
     */
    private List<ZoneBase> subZones;
    
    public ZoneInherit() {
        super();
        inheritedZones = new ArrayList<ZoneBase>();
        subZones = new ArrayList<ZoneBase>();
    }
    
    public void addInherited(ZoneBase b) {
        inheritedZones.add(b);
    }
    
    public void removeInherited(ZoneBase b) {
        inheritedZones.remove(b);
    }

    public boolean containsInherited(ZoneBase b) {
        return inheritedZones.contains(b);
    }
    
    public List<ZoneBase> getInheritedZones() {
        return inheritedZones;
    }
    
    public void addSub(ZoneBase b) {
        subZones.add(b);
    }
    
    public void removeSub(ZoneBase b) {
        subZones.remove(b);
    }

    public boolean containsSub(ZoneBase b) {
        return subZones.contains(b);
    }
    
    public List<ZoneBase> getSubZones() {
        return subZones;
    }
    
    @Override
    public boolean canAdministrate(OfflinePlayer player) {
        if(this.isAdmin(player)) {
            return true;
        }
        
        return isInheritAdmin(player);
    }
    
    @Override
    public boolean isAdmin(OfflinePlayer player) {
        return super.isAdmin(player);
    }
    
    public boolean isInheritAdmin(OfflinePlayer player) {
        if (getPermissions().has(getWorld().getName(), player.getName(), "zones.admin"))
            return true;
        
        for(ZoneBase b : inheritedZones) {
            if(b instanceof ZoneNormal) {
                if(((ZoneNormal)b).isAdmin(player))
                    return true;
            } else {
                if(b.canAdministrate(player))
                    return true;
            }
        }
        
        return false;
    }
}
