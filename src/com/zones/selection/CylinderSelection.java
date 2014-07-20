package com.zones.selection;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.Region;
import com.zones.model.ZoneForm;
import com.zones.model.ZoneVertice;
import com.zones.model.forms.ZoneCylinder;

public class CylinderSelection extends Selection {

    private ZoneVertice p1;
    private ZoneVertice p2;

    public CylinderSelection(ZoneSelection selection) {
        super(selection);
    }
    
    @Override
    public void onRightClick(Block block) {
        if(p2 != null) {
            getSelection().removeGhostBlock(p2.getX(), p2.getZ());
        }
        getSelection().addGhostBlock(block);
        p2 = new ZoneVertice(block.getX(),block.getZ());
        getPlayer().sendMessage(ChatColor.GREEN + "Selected X:" + block.getX() + ", Z:" + block.getZ() + " as second point"  + (getSize() != -1 ? ", size: " + getSize() + " blocks" : "" ) + ".");
    }

    @Override
    public void onLeftClick(Block block) {
        if(p1 != null) {
            getSelection().removeGhostBlock(p1.getX(), p1.getZ());
        }
        getSelection().addGhostBlock(block);
        p1 = new ZoneVertice(block.getX(),block.getZ());
        getPlayer().sendMessage(ChatColor.GREEN + "Selected X:" + block.getX() + ", Z:" + block.getZ() + " as first point"  + (getSize() != -1 ? ", size: " + getSize() + " blocks" : "" ) + ".");
    }

    @Override
    public long getSize() {
        if(p1 != null && p2 != null) {
            double xdiff = p1.getX() - p2.getX();
            double zdiff = p1.getZ() - p2.getZ();
            
            long size = (long) ((xdiff * xdiff + zdiff * zdiff) + 1);
            size *= Math.PI;
            size *= (getHeight().getMax() - getHeight().getMin() + 1);
            return size;
        } else {
            return -1;
        }
    }

    @Override
    public boolean isValid() {
        return p1 != null && p2 != null;
    }
    
    @Override
    public int getPointsSize() {
       return 2;
    }

    @Override
    public List<ZoneVertice> getPoints() {
        return Arrays.asList(p1,p2);
    }

    @Override
    public Class<? extends ZoneForm> getType() {
        return ZoneCylinder.class;
    }

    public void setPoint1(ZoneVertice p1) {
        this.p1 = p1;
    }

    public void setPoint2(ZoneVertice p2) {
        this.p2 = p2;
    }

    @Override
    public boolean importWorldeditSelection() {
        LocalSession worldeditsession = getSelection().getPlugin().getWorldEdit().getSession(getPlayer());
        if(worldeditsession == null) {
            return false;
        }
        
        Region region;
        try {
            region = worldeditsession.getSelection(worldeditsession.getSelectionWorld());
        } catch (IncompleteRegionException e) {
            return false;
        }
        if(region.getArea() < 1) {
            return false;
        }
        
        if(!(region instanceof CylinderRegion)) {
           // getPlayer().sendMessage(ChatColor.RED + "Your worldedit selection is invalid type.");
            return false;
        }
        
        CylinderRegion cyl = (CylinderRegion) region;
        
        setPoint1(new ZoneVertice(cyl.getCenter().getBlockX(), cyl.getCenter().getBlockZ()));
        setPoint2(new ZoneVertice(cyl.getRadius().getBlockX() + cyl.getCenter().getBlockX(), cyl.getRadius().getBlockZ() + cyl.getCenter().getBlockZ()));
        setHeight(new ZoneVertice(cyl.getMinimumY(), cyl.getMaximumY()), true);
        
        return true;
    }
    
}
