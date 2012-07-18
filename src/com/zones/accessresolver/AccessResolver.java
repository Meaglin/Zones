package com.zones.accessresolver;

import com.zones.accessresolver.interfaces.*;

public enum AccessResolver {
    DYNAMITE(BlockResolver.class),
    LEAF_DECAY(BlockResolver.class),
    SNOW_FALL(BlockResolver.class),
    SNOW_MELT(BlockResolver.class),
    ICE_FORM(BlockResolver.class),
    ICE_MELT(BlockResolver.class),
    MUSHROOM_SPREAD(BlockResolver.class),
    PHYSICS(BlockResolver.class),
    WATER_FLOW(BlockFromToResolver.class),
    LAVA_FLOW(BlockFromToResolver.class),
    FIRE(PlayerBlockResolver.class),
    FOOD(PlayerFoodResolver.class),
    ENTITY_SPAWN(EntitySpawnResolver.class),
    PLAYER_BLOCK_CREATE(PlayerBlockResolver.class),
    PLAYER_BLOCK_MODIFY(PlayerBlockResolver.class),
    PLAYER_BLOCK_DESTROY(PlayerBlockResolver.class),
    PLAYER_BLOCK_HIT(PlayerBlockResolver.class),
    PLAYER_ENTITY_HIT(PlayerHitEntityResolver.class),
    PLAYER_ENTITY_ATTACK(PlayerAttackEntityResolver.class),
    PLAYER_ENTER(PlayerLocationResolver.class),
    PLAYER_TELEPORT(PlayerLocationResolver.class),
    PLAYER_RECEIVE_DAMAGE(PlayerDamageResolver.class);
    
    private Class<? extends Resolver> iface;
    AccessResolver(Class<? extends Resolver> iface) {
        this.iface = iface;
    }
    
    public boolean isValid(Object object) {
        return object != null && iface.isInstance(object);
    }
    
    public static int size() {
        return AccessResolver.values().length;
    }
}
