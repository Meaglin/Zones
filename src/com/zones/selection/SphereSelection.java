package com.zones.selection;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Region;
import com.zones.model.ZoneForm;
import com.zones.model.ZoneVertice;
import com.zones.model.forms.ZoneSphere;

public class SphereSelection extends Selection {

    private ZoneVertice p1;

    public SphereSelection(ZoneSelection selection) {
        super(selection);
    }
    
    @Override
    public void onRightClick(Block block) {
        if(p1 != null) {
            getSelection().removeGhostBlock(p1.getX(), p1.getY());
        }
        getSelection().addGhostBlock(block);
        p1 = new ZoneVertice(block.getX(),block.getZ());
        getPlayer().sendMessage(ChatColor.GREEN + "Selected X:" + block.getX() + ", Z:" + block.getZ() + " as main point"  + (getSize() != -1 ? ", size: " + getSize() + " blocks" : "" ) + ".");
    }

    @Override
    public void onLeftClick(Block block) {
        if(p1 != null) {
            getSelection().removeGhostBlock(p1.getX(), p1.getY());
        }
        getSelection().addGhostBlock(block);
        p1 = new ZoneVertice(block.getX(),block.getZ());
        getPlayer().sendMessage(ChatColor.GREEN + "Selected X:" + block.getX() + ", Z:" + block.getZ() + " as main point"  + (getSize() != -1 ? ", size: " + getSize() + " blocks" : "" ) + ".");
    }

    @Override
    public long getSize() {
        return (long) (( 4 / 3) * Math.PI * Math.pow(getHeight().getMax() - getHeight().getMin(), 3));
    }

    @Override
    public boolean isValid() {
        return p1 != null;
    }
    
    @Override
    public int getPointsSize() {
       return 1;
    }

    @Override
    public List<ZoneVertice> getPoints() {
        return Arrays.asList(p1);
    }

    @Override
    public Class<? extends ZoneForm> getType() {
        return ZoneSphere.class;
    }

    public void setPoint1(ZoneVertice p1) {
        this.p1 = p1;
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
        
        if(!(region instanceof EllipsoidRegion)) {
            return false;
        }
        
        EllipsoidRegion cyl = (EllipsoidRegion) region;
        
        setPoint1(new ZoneVertice(cyl.getCenter().getBlockX(), cyl.getCenter().getBlockZ()));
        setHeight(new ZoneVertice(cyl.getCenter().getBlockY(), cyl.getCenter().getBlockY() + cyl.getRadius().getBlockY()), true);
        
        return true;
    }
    
}
