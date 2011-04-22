package com.zones.model;

public final class ZoneVertice {

    private long index;
    public ZoneVertice(int x, int y) {
        index = toLong(x,y);
    }
    
    public long getIndex() { return index; }
    
    public int getX() { return ((int) (index >> 32)); }
    public int getY() { return ((int) (index & 0xFFFFFFFFL));  }
    
    public int getMin() { return getX() < getY() ? getX() : getY() ; }
    public int getMax() { return getX() > getY() ? getX() : getY() ; }
    
    public ZoneVertice merge(ZoneVertice z) {
        int min = z.getMin() < getMin() ? z.getMin() : getMin();
        int max = z.getMax() > getMax() ? z.getMax() : getMax();
        return new ZoneVertice(min,max);
    }
    
    @Override
    public String toString() {
        return "Zv: " + getIndex() + " X:" + getX() + " Y:" + getY() ;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof ZoneVertice) {
            return ((ZoneVertice)o).getIndex() == getIndex();
        } else {
            return false;
        }
    }
    
    
    public static long toLong(int x, int y) {
        return ((((long)x) << 32) | ((long)y & 0xFFFFFFFFL));
    }
    
}
