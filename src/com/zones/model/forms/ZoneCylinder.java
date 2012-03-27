package com.zones.model.forms;

import java.util.List;

import com.zones.model.ZoneForm;
import com.zones.persistence.Vertice;

/**
 * A primitive rectangular zone
 * 
 * 
 * @author durgus, Meaglin
 */
public class ZoneCylinder extends ZoneForm {
    private int _x, _y, _z1, _z2;
    private int radius;
    private int radiusSqr;
    
    public ZoneCylinder(int x1, int x2, int y1, int y2, int z1, int z2) {
        _x = x1;
        _y = y1;
        
        double xdiff = x1 - x2;
        double ydiff = y1 - y2;
        
        radius = (int) Math.sqrt(xdiff * xdiff + ydiff * ydiff);
        radiusSqr = radius * radius;
        
        _z1 = z1;
        _z2 = z2;
        if (_z1 > _z2) // switch them if alignment is wrong
        {
            _z1 = z2;
            _z2 = z1;
        }
    }
    
    public ZoneCylinder(List<Vertice> vertices, int minz, int maxz) {
        this(vertices.get(0).getX(),vertices.get(1).getX(),vertices.get(0).getY(),vertices.get(1).getY(),minz,maxz);
    }
    
    @Override
    public boolean isInsideZone(int x, int y) {
        if ((Math.pow(_x - x, 2) + Math.pow(_y - y, 2)) > radiusSqr)
            return false;
        return true;
    }

    @Override
    public boolean intersectsRectangle(int ax1, int ax2, int ay1, int ay2) {
     // Circles point inside the rectangle?
        if (_x > ax1 && _x < ax2 && _y > ay1 && _y < ay2)
            return true;
        
        // Any point of the rectangle intersecting the Circle?
        if ((Math.pow(ax1 - _x, 2) + Math.pow(ay1 - _y, 2)) < radiusSqr)
            return true;
        if ((Math.pow(ax1 - _x, 2) + Math.pow(ay2 - _y, 2)) < radiusSqr)
            return true;
        if ((Math.pow(ax2 - _x, 2) + Math.pow(ay1 - _y, 2)) < radiusSqr)
            return true;
        if ((Math.pow(ax2 - _x, 2) + Math.pow(ay2 - _y, 2)) < radiusSqr)
            return true;
        
        // Collision on any side of the rectangle?
        if (_x > ax1 && _x < ax2)
        {
            if (Math.abs(_y - ay2) < radiusSqr)
                return true;
            if (Math.abs(_y - ay1) < radiusSqr)
                return true;
        }
        if (_y > ay1 && _y < ay2)
        {
            if (Math.abs(_x - ax2) < radiusSqr)
                return true;
            if (Math.abs(_x - ax1) < radiusSqr)
                return true;
        }
        
        return false;
    }

    @Override
    public double getDistanceToZone(int x, int y) {
        return (Math.sqrt((Math.pow(_x - x, 2) + Math.pow(_y - y, 2))) - radius);
    }

    /*
     * getLowZ() / getHighZ() - These two functions were added to cope with the
     * demand of the new fishing algorithms, wich are now able to correctly
     * place the hook in the water, thanks to getHighZ(). getLowZ() was added,
     * considering potential future modifications.
     */
    @Override
    public int getLowZ() {
        return _z1;
    }

    @Override
    public int getHighZ() {
        return _z2;
    }

    @Override
    public long getSize() {
        long size = radiusSqr + 1;
        size *= Math.PI;
        size *= (_z2 - _z1 + 1);
        return size;
    }

    @Override
    public int getLowX() {
        return _x - radius;
    }

    @Override
    public int getHighX() {
        return _x + radius;
    }

    @Override
    public int getLowY() {
        return _y - radius;
    }

    @Override
    public int getHighY() {
        return _y + radius;
    }

    @Override
    public int[][] getPoints() {
        return new int[][] { new int[] { getLowX() , getHighX() } , new int[] { getLowY() , getHighY() }  };
    }

    @Override
    public int getPointsSize() {
        return 2;
    }
    
    public int getCenterX() {
        return _x;
    }

    public int getCenterY() {
        return _y;
    }
    
    public int getRadius() {
        return radius;
    }
    
}
