package com.zones.model.settings;

public enum ZoneVarScope {
    WORLD(1),
    GLOBAL(3),
    LOCAL(2);
    
    private int level;
    private ZoneVarScope(int level) {
        this.level = level;
    }
    public int getLevel() {
        return level;
    }
}
