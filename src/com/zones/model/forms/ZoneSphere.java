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
    
    
    public ZoneSphere(int x, int z, int y, int y2) {
        _x = x;
        _y = y;
        
        radius = Math.abs(y2 - y);
        radiusSqr = radius * radius;
        size = (long) (( 4L / 3L) * Math.PI * ((long)radiusSqr) * ((long)radius));
        _z = z;
    }
    
    public ZoneSphere(List<Vertice> vertices, int minz, int maxz) {
        this(vertices.get(0).getX(), vertices.get(0).getZ(), minz, maxz);
    }
    
    @Override
    public boolean isInsideZone(int x, int y, int z) {
        if ((((_x - x) * (_x - x)) + ((_y - y) * (_y - y)) + ((_z - z) * (_z - z))) > radiusSqr)
            return false;
        return true;
    }
    
    @Override
    public boolean isInsideZone(int x, int z) {
        if ((((_x - x) * (_x - x)) + ((_z - z) * (_z - z))) > radiusSqr)
            return false;
        return true;
    }

    @Override
    public boolean intersectsRectangle(int ax1, int ax2, int az1, int az2) {
     // Circles point inside the rectangle?
        if (_x > ax1 && _x < ax2 && _z > az1 && _z < az2)
            return true;
        
        // Any point of the rectangle intersecting the Circle?
        if ((Math.pow(ax1 - _x, 2) + Math.pow(az1 - _z, 2)) < radiusSqr)
            return true;
        if ((Math.pow(ax1 - _x, 2) + Math.pow(az2 - _z, 2)) < radiusSqr)
            return true;
        if ((Math.pow(ax2 - _x, 2) + Math.pow(az1 - _z, 2)) < radiusSqr)
            return true;
        if ((Math.pow(ax2 - _x, 2) + Math.pow(az2 - _z, 2)) < radiusSqr)
            return true;
        
        // Collision on any side of the rectangle?
        if (_x > ax1 && _x < ax2)
        {
            if (Math.abs(_z - az2) < radiusSqr)
                return true;
            if (Math.abs(_z - az1) < radiusSqr)
                return true;
        }
        if (_z > az1 && _z < az2)
        {
            if (Math.abs(_x - ax2) < radiusSqr)
                return true;
            if (Math.abs(_x - ax1) < radiusSqr)
                return true;
        }
        
        return false;
    }

    @Override
    public double getDistanceToZone(int x, int z) {
        return (Math.sqrt((Math.pow(_x - x, 2) + Math.pow(_z - z, 2))) - radius);
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
        return new int[][] { new int[] { getLowX() , getHighX() } , new int[] { getLowZ() , getHighZ() }  };
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
