package com.zones.model.settings;

import java.util.HashMap;
import java.util.Map;

import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;


public enum ZoneVar {
    
    
        TELEPORT("AllowTeleport", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {return true;}
        },
        FIRE("AllowFire", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().FIRE_ENABLED;
            }
        },
        LAVA("LavaEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {return true;}
        },
        WATER("WaterEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {return true;}
        },
        HEALTH("HealthEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().PLAYER_HEALTH_ENABLED;
            }
        },
        DYNAMITE("DynamiteEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().ALLOW_TNT_TRIGGER;
            }
        },
        SPAWN_MOBS("SpawnMobs", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().MOB_SPAWNING_ENABLED;
            }
        },
        SPAWN_ANIMALS("SpawnAnimals", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().ANIMAL_SPAWNING_ENABLED;
            }
        },
        LEAF_DECAY("LeafDecay", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().LEAF_DECAY_ENABLED;
            }
        },
        SNOW_FALL("SnowFall", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().SNOW_FALL_ENABLED;
            }
        },
        ICE_FORM("IceForm", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().ICE_FORM_ENABLED;
            }
        },
        MUSHROOM_SPREAD("MushroomSpread", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().MUSHROOM_SPREAD_ENABLED;
            }
        },
        PHYSICS("PhysicsEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().PHYSICS_ENABLED;
            }
        },
        NOTIFY("Notify", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {return false;}
        },
        
        PLACE_BLOCKS("ProtectedPlaceBlocks", Serializer.INTEGERLIST) {
            @Override
            public Object getDefault(ZoneBase zone) {return null;}
        },
        BREAK_BLOCKS("ProtectedBreakBlocks", Serializer.INTEGERLIST) {
            @Override
            public Object getDefault(ZoneBase zone) {return null;}
        },
        
        MOBS("AllowedMobs", Serializer.CREATURELIST) {
            @Override
            public Object getDefault(ZoneBase zone) {return null;}
        },
        ANIMALS("AllowedAnimals", Serializer.CREATURELIST) {
            @Override
            public Object getDefault(ZoneBase zone) {return null;}
        },
        
        ENTER_MESSAGE("EnterMessage", Serializer.STRING) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return ZonesConfig.DEFAULT_ENTER_MESSAGE;
            }
        },
        LEAVE_MESSAGE("LeaveMessage", Serializer.STRING) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return ZonesConfig.DEFAULT_LEAVE_MESSAGE;
            }
        },
        SPAWN_LOCATION("SpawnLocation", Serializer.ZONEVERTICE) {
            @Override
            public Object getDefault(ZoneBase zone) {return null;}
        }; 

        
        private final String name;
        private final Serializer serializer;
        private ZoneVar(String name, Serializer serializer) {
            this.name = name;
            this.serializer = serializer;
        }
        
        public String getName() { return name; }
        public Class<? extends Object> getType() { return serializer.getType(); }
        public Class<? extends Object> getListType() { return serializer.getListType(); }
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
