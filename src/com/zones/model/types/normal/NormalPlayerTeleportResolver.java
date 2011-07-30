package com.zones.model.types.normal;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.zones.ZonesConfig;
import com.zones.accessresolver.interfaces.PlayerLocationResolver;
import com.zones.model.ZoneBase;
import com.zones.model.ZonesAccess.Rights;
import com.zones.model.settings.ZoneVar;
import com.zones.model.types.ZoneNormal;

public class NormalPlayerTeleportResolver implements PlayerLocationResolver {

    @Override
    public void sendDeniedMessage(ZoneBase zone, Player player) {
    }

    @Override
    public boolean isAllowed(ZoneBase zone, Player player, Location from, Location to) {
        if(!zone.getFlag(ZoneVar.TELEPORT) && !zone.canAdministrate(player)) {
            zone.sendMarkupMessage(ZonesConfig.TELEPORT_INTO_ZONE_DISABLED, player);
            return false;
        } 
        if(!((ZoneNormal)zone).canModify(player, Rights.ENTER)) {
            zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_ENTER_INTO_ZONE, player);
            return false;
        }
        return true;
    }

} 
