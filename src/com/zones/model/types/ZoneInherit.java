package com.zones.model.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;

public class ZoneInherit extends ZoneNormal {

    private List<ZoneNormal> inheritedZones;
    /**
     * Warning: this list only contains zones that have this zone as inherited zone.
     */
    private List<ZoneNormal> subZones;
    
    public ZoneInherit() {
        super();
        inheritedZones = new ArrayList<ZoneNormal>();
        subZones = new ArrayList<ZoneNormal>();
    }
    
    public void addInherited(ZoneNormal b) {
        inheritedZones.add(b);
    }
    
    public void removeInherited(ZoneNormal b) {
        inheritedZones.remove(b);
    }

    public boolean containsInherited(ZoneNormal b) {
        return inheritedZones.contains(b);
    }
    
    public List<ZoneNormal> getInheritedZones() {
        return inheritedZones;
    }
    
    public void addSub(ZoneNormal b) {
        subZones.add(b);
    }
    
    public void removeSub(ZoneNormal b) {
        subZones.remove(b);
    }

    public boolean containsSub(ZoneNormal b) {
        return subZones.contains(b);
    }
    
    public List<ZoneNormal> getSubZones() {
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
        if (getPermissions().playerHas(getWorld().getName(), player, "zones.admin"))
            return true;
        
        for(ZoneNormal b : inheritedZones) {
            if(b instanceof ZoneNormal) {
                if(b.isAdmin(player))
                    return true;
            } else {
                if(b.canAdministrate(player))
                    return true;
            }
        }
        
        return false;
    }
}
