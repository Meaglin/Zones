package com.zones.model.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.CreatureType;

import com.zones.model.ZoneSettings;


public enum ZoneVar {
    
    
        TELEPORT("PreventTeleport", Boolean.class , ZoneSettings.bool),
        FIRE("AllowFire", Boolean.class , ZoneSettings.bool),
        LAVA("LavaEnabled", Boolean.class , ZoneSettings.bool),
        WATER("WaterEnabled", Boolean.class , ZoneSettings.bool),
        HEALTH("HealthEnabled", Boolean.class , ZoneSettings.bool),
        DYNAMITE("DynamiteEnabled", Boolean.class , ZoneSettings.bool),
        SPAWN_MOBS("SpawnMobs", Boolean.class , ZoneSettings.bool),
        SPAWN_ANIMALS("SpawnAnimals", Boolean.class , ZoneSettings.bool),
        LEAF_DECAY("LeafDecay", Boolean.class , ZoneSettings.bool),
        PLACE_BLOCKS("ProtectedPlaceBlocks", List.class,Integer.class, ZoneSettings.intlist),
        BREAK_BLOCKS("ProtectedBreakBlocks", List.class,Integer.class, ZoneSettings.intlist),
        MOBS("AllowedMobs", List.class,CreatureType.class, ZoneSettings.creaturelist),
        ANIMALS("AllowedAnimals", List.class,CreatureType.class, ZoneSettings.creaturelist),
        ENTER_MESSAGE("EnterMessage", String.class, ZoneSettings.string),
        LEAVE_MESSAGE("LeaveMessage", String.class, ZoneSettings.string);


        
        private final String name;
        private final Class<? extends Object> type;
        private Class<? extends Object> listtype;
        private final Serializeble serializer;
        private ZoneVar(String name, Class<? extends Object> type, Serializeble serializer) {
            this.type = type;
            this.name = name;
            this.serializer = serializer;
        }
        private ZoneVar(String name, Class<? extends Object> type,Class<? extends Object> listtype, Serializeble serializer) {
            this.type = type;
            this.name = name;
            this.listtype = listtype;
            this.serializer = serializer;
        }
        
        public String getName() { return name; }
        public Class<? extends Object> getType() { return type; }
        public Class<? extends Object> getListType() { return listtype; }
        public Object unSerialize(String serializedData) { return serializer.UnSerialize(serializedData); }
        public String serialize(Object data) { return serializer.Serialize(data); }
        
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
