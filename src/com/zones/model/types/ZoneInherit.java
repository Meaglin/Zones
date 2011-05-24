package com.zones.model.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.zones.model.ZoneBase;

public class ZoneInherit extends ZoneNormal {

    private List<ZoneBase> inheritedZones;
    
    public ZoneInherit() {
        super();
        inheritedZones = new ArrayList<ZoneBase>();
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
    
    @Override
    public boolean canAdministrate(Player player) {
        if(this.isAdmin(player))
            return true;
        
        return isInheritAdmin(player);
    }
    
    public boolean isInheritAdmin(Player player) {
        for(ZoneBase b : inheritedZones) {
            if(b instanceof ZoneNormal) {
                if(((ZoneNormal)b).isAdmin(player))
                    return true;
            } else {
                if(b.canAdministrate(player)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
