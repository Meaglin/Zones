package com.zones.model;

/**
 * Abstract base class for any zone form
 * 
 * @author durgus , Meaglin
 */
public abstract class ZoneForm {
    public abstract boolean isInsideZone(int x, int y, int z);

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
}
