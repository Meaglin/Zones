package com.zones.model;

/**
 * Abstract base class for any zone form
 * 
 * @author durgus , Meaglin
 */
public abstract class ZoneForm {
    public boolean isInsideZone(int x, int y, int z) {
        if(z > getHighZ() || z < getLowZ())
            return false;
        
        return isInsideZone(x,y);
    }
    public abstract boolean isInsideZone(int x, int y);

    public abstract boolean intersectsRectangle(int x1, int x2, int y1, int y2);

    public abstract double getDistanceToZone(int x, int y);

    public abstract int getLowZ(); // Support for the ability to extract the z

    // coordinates of zones.

    public abstract int getHighZ();

    public abstract int getSize();

    // landing coordinates.

    protected boolean lineSegmentsIntersect(int ax1, int ay1, int ax2, int ay2, int bx1, int by1, int bx2, int by2) {
        return java.awt.geom.Line2D.linesIntersect(ax1, ay1, ax2, ay2, bx1, by1, bx2, by2);
    }

    public abstract int getLowX();

    public abstract int getHighX();

    public abstract int getLowY();

    public abstract int getHighY();
    
    public abstract int[][] getPoints();
    public abstract int getPointsSize();
    
    /*
     *  A Zone can only contain an other zone when the other zone is
     *  1. smaller.
     *  2. min/max z is within bounds.
     *  3. every corner of the zone is inside this zone. 
     */
    public boolean contains(ZoneForm f) {
        
        if(f.getSize() > getSize())
            return false;
        
        if(f.getLowZ() < getLowZ())
            return false;
        if(f.getHighZ() > getHighZ())
            return false;
        
        int[][] points = f.getPoints();
        for(int i = 0;i < f.getPointsSize();i++) {
            if(!isInsideZone(points[0][i], points[1][i]))
                return false;
        }
        
        return true;
    }
}
