package com.zones.model.types.normal;

import org.bukkit.block.Block;

import com.zones.accessresolver.interfaces.BlockResolver;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;

public class NormalBlockResolver implements BlockResolver {

    private final ZoneVar flag;
    public NormalBlockResolver(ZoneVar flag) {
        this.flag = flag;
    }
    
    @Override
    public boolean isAllowed(ZoneBase zone, Block block) {
        return zone.getFlag(flag);
    }

}
