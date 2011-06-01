package com.zones.model.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.CreatureType;

import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;
import com.zones.model.ZoneVertice;


public enum ZoneVar {
    
    
        TELEPORT("PreventTeleport", Boolean.class , Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {return false;}
        },
        FIRE("AllowFire", Boolean.class , Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().FIRE_ENABLED;
            }
        },
        LAVA("LavaEnabled", Boolean.class , Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {return true;}
        },
        WATER("WaterEnabled", Boolean.class , Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {return true;}
        },
        HEALTH("HealthEnabled", Boolean.class , Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().PLAYER_HEALTH_ENABLED;
            }
        },
        DYNAMITE("DynamiteEnabled", Boolean.class , Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().ALLOW_TNT_TRIGGER;
            }
        },
        SPAWN_MOBS("SpawnMobs", Boolean.class , Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().MOB_SPAWNING_ENABLED;
            }
        },
        SPAWN_ANIMALS("SpawnAnimals", Boolean.class , Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().ANIMAL_SPAWNING_ENABLED;
            }
        },
        LEAF_DECAY("LeafDecay", Boolean.class , Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().LEAF_DECAY_ENABLED;
            }
        },
        SNOW_FALL("SnowFall", Boolean.class, Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().SNOW_FALL_ENABLED;
            }
        },
        ICE_FORM("IceForm", Boolean.class, Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().ICE_FORM_ENABLED;
            }
        },
        PHYSICS("PhysicsEnabled", Boolean.class, Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().PHYSICS_ENABLED;
            }
        },
        NOTIFY("Notify", Boolean.class, Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {return false;}
        },
        
        PLACE_BLOCKS("ProtectedPlaceBlocks", List.class,Integer.class, Serializer.INTEGERLIST) {
            @Override
            public Object getDefault(ZoneBase zone) {return null;}
        },
        BREAK_BLOCKS("ProtectedBreakBlocks", List.class,Integer.class, Serializer.INTEGERLIST) {
            @Override
            public Object getDefault(ZoneBase zone) {return null;}
        },
        
        MOBS("AllowedMobs", List.class,CreatureType.class, Serializer.CREATURELIST) {
            @Override
            public Object getDefault(ZoneBase zone) {return null;}
        },
        ANIMALS("AllowedAnimals", List.class,CreatureType.class, Serializer.CREATURELIST) {
            @Override
            public Object getDefault(ZoneBase zone) {return null;}
        },
        
        ENTER_MESSAGE("EnterMessage", String.class, Serializer.STRING) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return ZonesConfig.DEFAULT_ENTER_MESSAGE;
            }
        },
        LEAVE_MESSAGE("LeaveMessage", String.class, Serializer.STRING) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return ZonesConfig.DEFAULT_LEAVE_MESSAGE;
            }
        },
        SPAWN_LOCATION("SpawnLocation",ZoneVertice.class, Serializer.ZONEVERTICE) {
            @Override
            public Object getDefault(ZoneBase zone) {return null;}
        }; 

        
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
        public abstract Object getDefault(ZoneBase zone);
        
        
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
