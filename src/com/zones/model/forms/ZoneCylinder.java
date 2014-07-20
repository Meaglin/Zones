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
    private int _x, _z, _y1, _y2;
    private int radius;
    private int radiusSqr;
    
    public ZoneCylinder(int x1, int x2, int y1, int y2, int z1, int z2) {
        _x = x1;
        _z = z1;
        
        double xdiff = x1 - x2;
        double zdiff = z1 - z2;
        
        radius = (int) Math.sqrt(xdiff * xdiff + zdiff * zdiff);
        radiusSqr = radius * radius + 1;
        
        _y1 = y1;
        _y2 = y2;
        if (_y1 > _y2) // switch them if alignment is wrong
        {
            _y1 = z2;
            _y2 = z1;
        }
    }
    
    public ZoneCylinder(List<Vertice> vertices, int minz, int maxz) {
        this(vertices.get(0).getX(),vertices.get(1).getX(), minz, maxz, vertices.get(0).getZ(), vertices.get(1).getZ());
    }
    
    @Override
    public boolean isInsideZone(int x, int z) {
        if ((((_x - x)*(_x - x)) + ((_z - z)*(_z - z))) > radiusSqr)
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
        long size = radiusSqr + 1;
        size *= Math.PI;
        size *= (_y2 - _y1 + 1);
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
        return _y1;
    }

    @Override
    public int getHighY() {
        return _y2;
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

    public int getCenterZ() {
        return _z;
    }
    
    public int getRadius() {
        return radius;
    }
    
}
