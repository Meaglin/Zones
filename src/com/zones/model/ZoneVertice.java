package com.zones.model;

import org.bukkit.block.Block;

public final class ZoneVertice {

    private long index;
    public ZoneVertice(Block block) {
        this(block.getX(), block.getZ());
    }
    public ZoneVertice(int x, int y) {
        index = toLong(x,y);
    }
    
    public long getIndex() { return index; }
    
    public int getX() { return ((int) (index >> 32)); }
    public int getZ() { return ((int) (index & 0xFFFFFFFFL));  }
    
    public int getMin() { return getX() < getZ() ? getX() : getZ() ; }
    public int getMax() { return getX() > getZ() ? getX() : getZ() ; }
    
    public ZoneVertice merge(ZoneVertice z) {
        int min = z.getMin() < getMin() ? z.getMin() : getMin();
        int max = z.getMax() > getMax() ? z.getMax() : getMax();
        return new ZoneVertice(min,max);
    }
    
    @Override
    public String toString() {
        return "Zv: " + getIndex() + " X:" + getX() + " Y:" + getZ() ;
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
