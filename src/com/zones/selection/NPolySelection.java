package com.zones.selection;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import com.sk89q.worldedit.BlockVector2D;
import com.zones.model.ZoneForm;
import com.zones.model.ZoneVertice;
import com.zones.model.forms.ZoneNPoly;

public class NPolySelection extends Selection {

    private List<ZoneVertice> points;
    
    public NPolySelection(ZoneSelection selection) {
        super(selection);
        points = new ArrayList<ZoneVertice>();
    }

    @Override
    public void onRightClick(Block block) {
        points.add(new ZoneVertice(block));
        getSelection().addGhostBlock(block);
        getPlayer().sendMessage(ChatColor.GREEN + "Added point.");
    }

    @Override
    public void onLeftClick(Block block) {
        points.clear();
        getSelection().revertGhostBlocks();
        
        points.add(new ZoneVertice(block));
        getSelection().addGhostBlock(block);
        getPlayer().sendMessage(ChatColor.GREEN + "First point set, add the other points with right click.");
    }

    /*
     * see Greens theorem http://en.wikipedia.org/wiki/Green%27s_theorem
     * http://stackoverflow
     * .com/questions/451426/how-do-i-calculate-the-surface-area-of-a-2d-polygon
     */
    @Override
    public long getSize() {
        if(points.size() < 3) 
            return -1;
        
        int size = 0;
        for (int i = 0, j = points.size() - 1; i < points.size(); j = i++) {
            int x0 = points.get(j).getX();
            int y0 = points.get(j).getY();
            int x1 = points.get(i).getX();
            int y1 = points.get(i).getY();
            size += x0 * y1 - x1 * y0;
        }
        return Math.round(Math.abs(size) * 0.5) * ((long)(getHeight().getMax() - getHeight().getMin() + 1));
    }

    @Override
    public boolean isValid() {
        return points.size() > 2;
    }
    
    @Override
    public int getPointsSize() {
        return points.size();
    }

    public void addPoint(ZoneVertice vertice) {
        points.add(vertice);
    }
    
    public void clearPoints() {
        points.clear();
    }
    
    @Override
    public List<ZoneVertice> getPoints() {
        return points;
    }

    @Override
    public Class<? extends ZoneForm> getType() {
        return ZoneNPoly.class;
    }

    @Override
    public boolean importWorldeditSelection() {
        com.sk89q.worldedit.bukkit.selections.Selection worldeditSelection = getSelection().getPlugin().getWorldEdit().getSelection(getPlayer());
        if(worldeditSelection == null) {
            getPlayer().sendMessage(ChatColor.RED + "No WorldEdit selection found.");
            return false;
        }
        
        if(worldeditSelection.getArea() < 1) {
            getPlayer().sendMessage(ChatColor.RED + "Your WorldEdit selection is not a valid selection.");
            return false;
        }
        
        if(!(worldeditSelection instanceof com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection)) {
            getPlayer().sendMessage(ChatColor.RED + "Your worldedit selection is invalid type.");
            return false;
        }
        
        com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection npolysel = (com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection) worldeditSelection;
        clearPoints();
        for(BlockVector2D vec : npolysel.getNativePoints()) {
            addPoint(new ZoneVertice(vec.getBlockX(), vec.getBlockZ()));
        }
        setHeight(new ZoneVertice(npolysel.getMinimumPoint().getBlockY(), npolysel.getMaximumPoint().getBlockY()), true);
        
        return true;
    }

}
