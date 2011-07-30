package com.zones.model.types.normal;


import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.zones.ZonesConfig;
import com.zones.accessresolver.interfaces.PlayerBlockResolver;
import com.zones.model.ZoneBase;
import com.zones.model.ZonesAccess.Rights;
import com.zones.model.types.ZoneNormal;

public class NormalPlayerBlockModifyResolver implements PlayerBlockResolver {

    @Override
    public void sendDeniedMessage(ZoneBase zone, Player player) {
        zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_MODIFY_BLOCKS_IN_ZONE, player);
    }

    @Override
    public boolean isAllowed(ZoneBase zone, Player player, Block block, int typeId) {
        return ((ZoneNormal)zone).canModify(player, Rights.MODIFY);
    }

}
