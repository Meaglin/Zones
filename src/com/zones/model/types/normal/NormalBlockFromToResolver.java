package com.zones.model.types.normal;

import org.bukkit.block.Block;

import com.zones.accessresolver.interfaces.BlockFromToResolver;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;

public class NormalBlockFromToResolver implements BlockFromToResolver {

    private final ZoneVar flag;
    public NormalBlockFromToResolver(ZoneVar flag) {
        this.flag = flag;
    }
    
    @Override
    public boolean isAllowed(ZoneBase zone, Block from, Block to) {
        if(!zone.isInsideZone(from)) {
            return zone.getFlag(flag);
        } else {
            return zone.getFlag(ZoneVar.PHYSICS);
        }
    }

}
