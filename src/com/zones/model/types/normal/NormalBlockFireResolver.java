package com.zones.model.types.normal;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.zones.ZonesConfig;
import com.zones.accessresolver.interfaces.PlayerBlockResolver;
import com.zones.model.ZoneBase;
import com.zones.model.settings.ZoneVar;

public class NormalBlockFireResolver implements PlayerBlockResolver {

    @Override
    public boolean isAllowed(ZoneBase zone,Player player, Block block, int typeId) {
        if(player == null)
            return zone.getFlag(ZoneVar.FIRE);
        else
            return zone.getFlag(ZoneVar.FIRE) || (zone.getWorldConfig().LIGHTER_ALLOWED && zone.canAdministrate(player));
    }

    @Override
    public void sendDeniedMessage(ZoneBase zone, Player player) {
        if(player != null)
            zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_USE_LIGHTER, player);
    }

}
