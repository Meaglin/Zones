package com.zones.selection;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.regions.Region;
import com.zones.model.ZoneForm;
import com.zones.model.ZoneVertice;
import com.zones.model.forms.ZoneCuboid;

public class CuboidSelection extends Selection {

    private ZoneVertice p1;
    private ZoneVertice p2;

    public CuboidSelection(ZoneSelection selection) {
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
            long size = Math.abs(p1.getX() - p2.getX())+1;
            size *= Math.abs(p1.getZ() - p2.getZ())+1;
            size *= getHeight().getMax() - getHeight().getMin() + 1;
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
        return ZoneCuboid.class;
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
        
        if(!(region instanceof com.sk89q.worldedit.regions.CuboidRegion)) {
           // getPlayer().sendMessage(ChatColor.RED + "Your worldedit selection is invalid type.");
            return false;
        }
        
        com.sk89q.worldedit.regions.CuboidRegion cuboidregion = (com.sk89q.worldedit.regions.CuboidRegion) region;
        
        setPoint1(new ZoneVertice(cuboidregion.getMinimumPoint().getBlockX(), cuboidregion.getMinimumPoint().getBlockZ()));
        setPoint2(new ZoneVertice(cuboidregion.getMaximumPoint().getBlockX(), cuboidregion.getMaximumPoint().getBlockZ()));
        setHeight(new ZoneVertice(cuboidregion.getMinimumPoint().getBlockY(), cuboidregion.getMaximumPoint().getBlockY()), true);
        
        return true;
    }
    
}
