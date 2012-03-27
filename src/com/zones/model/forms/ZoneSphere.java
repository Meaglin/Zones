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
public class ZoneSphere extends ZoneForm {
    private int _x, _y, _z;
    
    private int radius;
    private int radiusSqr;
    private long size;
    
    
    public ZoneSphere(int x, int y, int z, int z2) {
        _x = x;
        _y = y;
        
        radius = Math.abs(z2 - z);
        radiusSqr = radius * radius;
        size = (long) (( 4L / 3L) * Math.PI * ((long)radiusSqr) * ((long)radius));
        _z = z;
    }
    
    public ZoneSphere(List<Vertice> vertices, int minz, int maxz) {
        this(vertices.get(0).getX(), vertices.get(0).getY(), minz, maxz);
    }
    
    @Override
    public boolean isInsideZone(int x, int y, int z) {
        if ((((_x - x) * (_x - x)) + ((_y - y) * (_y - y)) + ((_z - z) * (_z - z))) > radiusSqr)
            return false;
        return true;
    }
    
    @Override
    public boolean isInsideZone(int x, int y) {
        if ((((_x - x) * (_x - x)) + ((_y - y) * (_y - y))) > radiusSqr)
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
        return _z - radius;
    }

    @Override
    public int getHighZ() {
        return _z + radius;
    }

    @Override
    public long getSize() {
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
    
    public int getCenterZ() {
        return _z;
    }
    
    public int getRadius() {
        return radius;
    }
}
