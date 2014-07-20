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
public class ZoneCuboid extends ZoneForm {
    private int _x1, _x2, _y1, _y2, _z1, _z2;

    public ZoneCuboid(int x1, int x2, int y1, int y2, int z1, int z2) {
        _x1 = x1;
        _x2 = x2;
        if (_x1 > _x2) // switch them if alignment is wrong
        {
            _x1 = x2;
            _x2 = x1;
        }

        _y1 = y1;
        _y2 = y2;
        if (_y1 > _y2) // switch them if alignment is wrong
        {
            _y1 = y2;
            _y2 = y1;
        }

        _z1 = z1;
        _z2 = z2;
        if (_z1 > _z2) // switch them if alignment is wrong
        {
            _z1 = z2;
            _z2 = z1;
        }
    }
    
    public ZoneCuboid(List<Vertice> vertices, int minz, int maxz) {
        this(vertices.get(0).getX(), vertices.get(1).getX(), minz, maxz, vertices.get(0).getZ(), vertices.get(1).getZ());
    }
    @Override
    public boolean isInsideZone(int x, int z) {
        if (x < _x1 || x > _x2 || z < _z1 || z > _z2)
            return false;
        return true;
    }

    @Override
    public boolean intersectsRectangle(int ax1, int ax2, int az1, int az2) {
        // Check if any point inside this rectangle
        if (isInsideZone(ax1, az1, (_z2 - 1)))
            return true;
        if (isInsideZone(ax1, az2, (_z2 - 1)))
            return true;
        if (isInsideZone(ax2, az1, (_z2 - 1)))
            return true;
        if (isInsideZone(ax2, az2, (_z2 - 1)))
            return true;

        // Check if any point from this rectangle is inside the other one
        if (_x1 > ax1 && _x1 < ax2 && _z1 > az1 && _z1 < az2)
            return true;
        if (_x1 > ax1 && _x1 < ax2 && _z2 > az1 && _z2 < az2)
            return true;
        if (_x2 > ax1 && _x2 < ax2 && _z1 > az1 && _z1 < az2)
            return true;
        if (_x2 > ax1 && _x2 < ax2 && _z2 > az1 && _z2 < az2)
            return true;

        // Horizontal lines may intersect vertical lines
        if (lineSegmentsIntersect(_x1, _z1, _x2, _z1, ax1, az1, ax1, az2))
            return true;
        if (lineSegmentsIntersect(_x1, _z1, _x2, _z1, ax2, az1, ax2, az2))
            return true;
        if (lineSegmentsIntersect(_x1, _z2, _x2, _z2, ax1, az1, ax1, az2))
            return true;
        if (lineSegmentsIntersect(_x1, _z2, _x2, _z2, ax2, az1, ax2, az2))
            return true;

        // Vertical lines may intersect horizontal lines
        if (lineSegmentsIntersect(_x1, _z1, _x1, _z2, ax1, az1, ax2, az1))
            return true;
        if (lineSegmentsIntersect(_x1, _z1, _x1, _z2, ax1, az2, ax2, az2))
            return true;
        if (lineSegmentsIntersect(_x2, _z1, _x2, _z2, ax1, az1, ax2, az1))
            return true;
        if (lineSegmentsIntersect(_x2, _z1, _x2, _z2, ax1, az2, ax2, az2))
            return true;

        return false;
    }

    @Override
    public double getDistanceToZone(int x, int z) {
        double test, shortestDist = Math.pow(_x1 - x, 2) + Math.pow(_z1 - z, 2);

        test = Math.pow(_x1 - x, 2) + Math.pow(_z2 - z, 2);
        if (test < shortestDist)
            shortestDist = test;

        test = Math.pow(_x2 - x, 2) + Math.pow(_z1 - z, 2);
        if (test < shortestDist)
            shortestDist = test;

        test = Math.pow(_x2 - x, 2) + Math.pow(_z2 - z, 2);
        if (test < shortestDist)
            shortestDist = test;

        return Math.sqrt(shortestDist);
    }

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
        long size = _x2 - _x1 + 1;
        size *= (_y2 - _y1 + 1);
        size *= (_z2 - _z1 + 1);
        return size;
    }

    @Override
    public int getLowX() {
        return _x1;
    }

    @Override
    public int getHighX() {
        return _x2;
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
        return new int[][] { new int[] { _x1 , _x2 } , new int[] { _z1 , _z2 }  };
    }

    @Override
    public int getPointsSize() {
        return 2;
    }
}
