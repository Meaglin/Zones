package com.zones.forms;

import com.zones.ZoneForm;

/**
 * A primitive rectangular zone
 * 
 * 
 * @author durgus
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

    @Override
    public boolean isInsideZone(int x, int y, int z) {
        if (x < _x1 || x > _x2 || y < _y1 || y > _y2 || z < _z1 || z > _z2)
            return false;
        return true;
    }

    @Override
    public boolean intersectsRectangle(int ax1, int ax2, int ay1, int ay2) {
        // Check if any point inside this rectangle
        if (isInsideZone(ax1, ay1, (_z2 - 1)))
            return true;
        if (isInsideZone(ax1, ay2, (_z2 - 1)))
            return true;
        if (isInsideZone(ax2, ay1, (_z2 - 1)))
            return true;
        if (isInsideZone(ax2, ay2, (_z2 - 1)))
            return true;

        // Check if any point from this rectangle is inside the other one
        if (_x1 > ax1 && _x1 < ax2 && _y1 > ay1 && _y1 < ay2)
            return true;
        if (_x1 > ax1 && _x1 < ax2 && _y2 > ay1 && _y2 < ay2)
            return true;
        if (_x2 > ax1 && _x2 < ax2 && _y1 > ay1 && _y1 < ay2)
            return true;
        if (_x2 > ax1 && _x2 < ax2 && _y2 > ay1 && _y2 < ay2)
            return true;

        // Horizontal lines may intersect vertical lines
        if (lineSegmentsIntersect(_x1, _y1, _x2, _y1, ax1, ay1, ax1, ay2))
            return true;
        if (lineSegmentsIntersect(_x1, _y1, _x2, _y1, ax2, ay1, ax2, ay2))
            return true;
        if (lineSegmentsIntersect(_x1, _y2, _x2, _y2, ax1, ay1, ax1, ay2))
            return true;
        if (lineSegmentsIntersect(_x1, _y2, _x2, _y2, ax2, ay1, ax2, ay2))
            return true;

        // Vertical lines may intersect horizontal lines
        if (lineSegmentsIntersect(_x1, _y1, _x1, _y2, ax1, ay1, ax2, ay1))
            return true;
        if (lineSegmentsIntersect(_x1, _y1, _x1, _y2, ax1, ay2, ax2, ay2))
            return true;
        if (lineSegmentsIntersect(_x2, _y1, _x2, _y2, ax1, ay1, ax2, ay1))
            return true;
        if (lineSegmentsIntersect(_x2, _y1, _x2, _y2, ax1, ay2, ax2, ay2))
            return true;

        return false;
    }

    @Override
    public double getDistanceToZone(int x, int y) {
        double test, shortestDist = Math.pow(_x1 - x, 2) + Math.pow(_y1 - y, 2);

        test = Math.pow(_x1 - x, 2) + Math.pow(_y2 - y, 2);
        if (test < shortestDist)
            shortestDist = test;

        test = Math.pow(_x2 - x, 2) + Math.pow(_y1 - y, 2);
        if (test < shortestDist)
            shortestDist = test;

        test = Math.pow(_x2 - x, 2) + Math.pow(_y2 - y, 2);
        if (test < shortestDist)
            shortestDist = test;

        return Math.sqrt(shortestDist);
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
    public int getSize() {

        return ((_x2 - _x1) * (_y2 - _y1) * (_z2 - _z1));
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
}
