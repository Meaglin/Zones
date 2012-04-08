package com.zones.model;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.zones.ZonesConfig;

public class GhostBlock {
    
    private final Block block;
    
    public GhostBlock(Block block) {
        this.block = block;
    }
    
    public void show(Player player) {
        for(int i = 0; i < ZonesConfig.CREATION_PILON_HEIGHT;i++) {
            Location loc = getBlock().getLocation();
            loc.setY(loc.getY() + i + 1);
            if(loc.getY() < 128)
                player.sendBlockChange(loc, ZonesConfig.CREATION_PILON_TYPE, (byte) 0);
        }
    }
    
    public void hide(Player player) {
        for(int i = 0; i < ZonesConfig.CREATION_PILON_HEIGHT;i++) {
            Location loc = getBlock().getLocation();
            loc.setY(loc.getY() + i + 1);
            if(loc.getY() < 128) {
                Block b = getBlock().getWorld().getBlockAt(loc);
                player.sendBlockChange(loc, b.getTypeId(), b.getData());
            }
        }
    }
    
    public Block getBlock() { return block; }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof GhostBlock) {
            GhostBlock rb = (GhostBlock) o;
            if(rb.getBlock().getX() == block.getX() && rb.getBlock().getY() == block.getY() && rb.getBlock().getZ() == block.getZ())
                return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return block.hashCode();
    }
}
