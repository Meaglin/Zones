package com.zones.selection;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

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
            getSelection().removeGhostBlock(p2.getX(), p2.getY());
        }
        getSelection().addGhostBlock(block);
        p2 = new ZoneVertice(block.getX(),block.getZ());
        getPlayer().sendMessage(ChatColor.GREEN + "Selected " + block.getX() + "," + block.getZ() + " as second point"  + (getSize() != -1 ? ", size: " + getSize() + " blocks" : "" ) + ".");
    }

    @Override
    public void onLeftClick(Block block) {
        if(p1 != null) {
            getSelection().removeGhostBlock(p1.getX(), p1.getY());
        }
        getSelection().addGhostBlock(block);
        p1 = new ZoneVertice(block.getX(),block.getZ());
        getPlayer().sendMessage(ChatColor.GREEN + "Selected " + block.getX() + "," + block.getZ() + " as first point"  + (getSize() != -1 ? ", size: " + getSize() + " blocks" : "" ) + ".");
    }

    @Override
    public long getSize() {
        if(p1 != null && p2 != null) {
            long size = Math.abs(p1.getX() - p2.getX())+1;
            size *= Math.abs(p1.getY() - p2.getY())+1;
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
        com.sk89q.worldedit.bukkit.selections.Selection worldeditSelection = getSelection().getPlugin().getWorldEdit().getSelection(getPlayer());
        if(worldeditSelection == null) {
           // getPlayer().sendMessage(ChatColor.RED + "No WorldEdit selection found.");
            return false;
        }
        
        if(worldeditSelection.getArea() < 1) {
           // getPlayer().sendMessage(ChatColor.RED + "Your WorldEdit selection is not a valid selection.");
            return false;
        }
        
        if(!(worldeditSelection instanceof com.sk89q.worldedit.bukkit.selections.CuboidSelection)) {
           // getPlayer().sendMessage(ChatColor.RED + "Your worldedit selection is invalid type.");
            return false;
        }
        
        com.sk89q.worldedit.bukkit.selections.CuboidSelection csel = (com.sk89q.worldedit.bukkit.selections.CuboidSelection) worldeditSelection;
        
        setPoint1(new ZoneVertice(csel.getMinimumPoint().getBlockX(), csel.getMinimumPoint().getBlockZ()));
        setPoint2(new ZoneVertice(csel.getMaximumPoint().getBlockX(), csel.getMaximumPoint().getBlockZ()));
        setHeight(new ZoneVertice(csel.getMinimumPoint().getBlockY(), csel.getMaximumPoint().getBlockY()), true);
        
        return true;
    }
    
}
