package com.zones.model.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.CreatureType;

import com.zones.model.ZoneVertice;


public enum ZoneVar {
    
    
        TELEPORT("PreventTeleport", Boolean.class , Serializer.BOOLEAN),
        FIRE("AllowFire", Boolean.class , Serializer.BOOLEAN),
        LAVA("LavaEnabled", Boolean.class , Serializer.BOOLEAN),
        WATER("WaterEnabled", Boolean.class , Serializer.BOOLEAN),
        HEALTH("HealthEnabled", Boolean.class , Serializer.BOOLEAN),
        DYNAMITE("DynamiteEnabled", Boolean.class , Serializer.BOOLEAN),
        SPAWN_MOBS("SpawnMobs", Boolean.class , Serializer.BOOLEAN),
        SPAWN_ANIMALS("SpawnAnimals", Boolean.class , Serializer.BOOLEAN),
        LEAF_DECAY("LeafDecay", Boolean.class , Serializer.BOOLEAN),
        PLACE_BLOCKS("ProtectedPlaceBlocks", List.class,Integer.class, Serializer.INTEGERLIST),
        BREAK_BLOCKS("ProtectedBreakBlocks", List.class,Integer.class, Serializer.INTEGERLIST),
        MOBS("AllowedMobs", List.class,CreatureType.class, Serializer.CREATURELIST),
        ANIMALS("AllowedAnimals", List.class,CreatureType.class, Serializer.CREATURELIST),
        ENTER_MESSAGE("EnterMessage", String.class, Serializer.STRING),
        LEAVE_MESSAGE("LeaveMessage", String.class, Serializer.STRING),
        SPAWN_LOCATION("SpawnLocation",ZoneVertice.class, Serializer.ZONEVERTICE); 

        
        private final String name;
        private final Class<? extends Object> type;
        private Class<? extends Object> listtype;
        private final Serializer serializer;
        private ZoneVar(String name, Class<? extends Object> type, Serializer serializer) {
            this.type = type;
            this.name = name;
            this.serializer = serializer;
        }
        private ZoneVar(String name, Class<? extends Object> type,Class<? extends Object> listtype, Serializer serializer) {
            this.type = type;
            this.name = name;
            this.listtype = listtype;
            this.serializer = serializer;
        }
        
        public String getName() { return name; }
        public Class<? extends Object> getType() { return type; }
        public Class<? extends Object> getListType() { return listtype; }
        public Object unSerialize(String serializedData) { return serializer.unSerialize(serializedData); }
        public String serialize(Object data) { return serializer.serialize(data); }
        
        public static ZoneVar fromName(String name) {
            return names.get(name);
        }
        
        private static final Map<String,ZoneVar> names;
        static {
            names = new HashMap<String,ZoneVar>();
            for(ZoneVar n : values())
                names.put(n.getName(),n);
        }
    }
