package com.zones.model.forms;

import java.util.List;

import com.zones.model.ZoneForm;
import com.zones.persistence.Vertice;

/**
 * A not so primitive npoly zone
 * 
 * 
 * @author durgus, Meaglin
 */
public class ZoneNPoly extends ZoneForm {
    private int[] _x;
    private int[] _z;
    private int   _y1;
    private int   _y2;
    private long   _size;
    
    private int minX, minZ, maxX, maxZ;

    public ZoneNPoly(int[] x, int[] z, int y1, int y2) {
        _x = x;
        _z = z;
        _y1 = y1;
        _y2 = y2;
        if (_y1 > _y2) // switch them if alignment is wrong
        {
            _y1 = y2;
            _y2 = y1;
        }
        calculate();
    }

    public ZoneNPoly(List<Vertice> vertices, int minz, int maxz) {
        _x = new int[vertices.size()];
        _z = new int[vertices.size()];
        for (Vertice v : vertices) {
            _x[v.getVertexorder()] = v.getX();
            _z[v.getVertexorder()] = v.getZ();
        }
        _y1 = minz;
        _y2 = maxz;
        if (_y1 > _y2) // switch them if alignment is wrong
        {
            _y1 = maxz;
            _y2 = minz;
        }
        calculate();
    }

    @Override
    public boolean isInsideZone(int x, int z) {
        boolean inside = false;
        for (int i = 0, j = _x.length - 1; i < _x.length; j = i++) {
            if(_z[i] == _z[j] && _z[i] == z && x <= max(_x[i], _x[j]) && x >= min(_x[i], _x[j])) return true;
            if(_x[i] == _x[j] && _x[i] == x && z <= max(_z[i], _z[j]) && z >= min(_z[i], _z[j])) return true;

            if ((((_z[i] <= z) && (z < _z[j])) || ((_z[j] <= z) && (z < _z[i]))) && (x < (_x[j] - _x[i]) * (z - _z[i]) / (_z[j] - _z[i]) + _x[i])) {
                inside = !inside;
            }
        }
        return inside;
    }

    private static final int min(int a, int b) {
        return a > b ? b : a;
    }
    
    private static final int max(int a, int b) {
        return a > b ? a : b;
    }
    
    @Override
    public boolean intersectsRectangle(int ax1, int ax2, int az1, int az2) {
        int tX, tZ, uX, uZ;

        // First check if a point of the polygon lies inside the rectangle
        if (_x[0] > ax1 && _x[0] < ax2 && _z[0] > az1 && _z[0] < az2)
            return true;

        // Or a point of the rectangle inside the polygon
        if (isInsideZone(ax1, az1, (_y2 - 1)))
            return true;

        // If the first point wasn't inside the rectangle it might still have
        // any line crossing any side
        // of the rectangle

        // Check every possible line of the polygon for a collision with any of
        // the rectangles side
        for (int i = 0; i < _z.length; i++) {
            tX = _x[i];
            tZ = _z[i];
            uX = _x[(i + 1) % _x.length];
            uZ = _z[(i + 1) % _x.length];

            // Check if this line intersects any of the four sites of the
            // rectangle
            if (lineSegmentsIntersect(tX, tZ, uX, uZ, ax1, az1, ax1, az2))
                return true;
            if (lineSegmentsIntersect(tX, tZ, uX, uZ, ax1, az1, ax2, az1))
                return true;
            if (lineSegmentsIntersect(tX, tZ, uX, uZ, ax2, az2, ax1, az2))
                return true;
            if (lineSegmentsIntersect(tX, tZ, uX, uZ, ax2, az2, ax2, az1))
                return true;
        }

        return false;
    }

    @Override
    public double getDistanceToZone(int x, int z) {
        double test, shortestDist = Math.pow(_x[0] - x, 2) + Math.pow(_z[0] - z, 2);

        for (int i = 1; i < _z.length; i++) {
            test = Math.pow(_x[i] - x, 2) + Math.pow(_z[i] - z, 2);
            if (test < shortestDist)
                shortestDist = test;
        }

        return Math.sqrt(shortestDist);
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
    public long getSize() {
        return _size;
    }

    /*
     * 
     * see Greens theorem http://en.wikipedia.org/wiki/Green%27s_theorem
     * http://stackoverflow.com/questions/451426/how-do-i-calculate-the-surface-area-of-a-2d-polygon
     * 
     */
    private void calculate() {
        long size = 0;
        for (int i = 0, j = _x.length - 1; i < _x.length; j = i++) {
            int x0 = _x[j];
            int y0 = _z[j];
            int x1 = _x[i];
            int y1 = _z[i];
            size += x0 * y1 - x1 * y0;
        }
        _size = Math.round(Math.abs(size) * 0.5) * ((long)(_y2 - _y1 + 1));
        
        minX = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        minZ = Integer.MAX_VALUE;
        maxZ = Integer.MIN_VALUE;
        for(int i = 0; i < _x.length;i += 1) {
            if(_x[i] < minX) {
                minX = _x[i];
            }
            if(_x[i] > maxX) {
                maxX = _x[i];
            }
            if(_z[i] < minZ) {
                minZ = _z[i];
            }
            if(_z[i] > maxZ) {
                maxZ = _z[i];
            }
        }
    }

    @Override
    public int getLowX() {
        return minX;
    }

    @Override
    public int getHighX() {
        return maxX;
    }

    @Override
    public int getLowZ() {
        return minZ;
    }

    @Override
    public int getHighZ() {
        return maxZ;
    }

    public int[] getX() {
        return _x;
    }

    public int[] getZ() {
        return _z;
    }

    @Override
    public int[][] getPoints() {
        return new int[][] { _x , _z };
    }

    @Override
    public int getPointsSize() {
        return _x.length;
    }


}
