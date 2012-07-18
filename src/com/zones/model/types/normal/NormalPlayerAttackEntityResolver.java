package com.zones.model.types.normal;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.zones.ZonesConfig;
import com.zones.accessresolver.interfaces.PlayerHitEntityResolver;
import com.zones.model.ZoneBase;
import com.zones.model.ZonesAccess.Rights;
import com.zones.model.types.ZoneNormal;
import com.zones.util.Log;

public class NormalPlayerAttackEntityResolver implements PlayerHitEntityResolver{

    @Override
    public void sendDeniedMessage(ZoneBase zone, Player player) {
        zone.sendMarkupMessage(ZonesConfig.PLAYER_CANT_HIT_ENTITYS_IN_ZONE, player);
    }

    @Override
    public boolean isAllowed(ZoneBase zone, Player attacker, Entity defender, int damage) {
        Log.info(attacker, "trigger attack '" + zone.getName() + "'[" + zone.getId() + "] " + defender.getClass().getName() + " " + damage);

        return ((ZoneNormal)zone).canModify(attacker, Rights.ATTACK);
    }

}
