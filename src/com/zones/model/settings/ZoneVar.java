package com.zones.model.settings;

import java.util.HashMap;
import java.util.Map;

import com.zones.ZonesConfig;
import com.zones.model.ZoneBase;


public enum ZoneVar {
    
    
        TELEPORT("TeleportEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {return true;}
        },
        LIGHTER("LighterEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().LIGHTER_ALLOWED;
            }
        },
        FIRE("FireEnabled", Serializer.BOOLEAN) {
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
        FOOD("FoodEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().PLAYER_FOOD_ENABLED;
            }
        },
        HEALTH("HealthEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().PLAYER_HEALTH_ENABLED && !(zone.getWorldConfig().GOD_MODE_ENABLED && zone.getWorldConfig().GOD_MODE_AUTOMATIC);
            }
        },
        DYNAMITE("DynamiteEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().DYNAMITE_ENABLED;
            }
        },
        PHYSICS("PhysicsEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().PHYSICS_ENABLED;
            }
        },
        NOTIFY("NotifyEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) { return false; }
        },
        MOBS("MobsEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().MOB_SPAWNING_ENABLED;
            }
        },
        ANIMALS("AnimalsEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().ANIMAL_SPAWNING_ENABLED;
            }
        },
        CROP_PROTECTION("CropProtectionEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {return zone.getWorldConfig().CROP_PROTECTION_ENABLED;}
        },
        
        ENDER_GRIEFING("EnderGriefingEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {return zone.getWorldConfig().ENDER_GRIEFING_ENABLED;}
        },
        LEAF_DECAY("LeafDecayEnabled", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().LEAF_DECAY_ENABLED;
            }
        },
        SNOW_FALL("SnowForm", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().SNOW_FORM_ENABLED;
            }
        },
        ICE_FORM("IceForm", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().ICE_FORM_ENABLED;
            }
        },
        SNOW_MELT("SnowMelt", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().SNOW_MELT_ENABLED;
            }
        },
        ICE_MELT("IceMelt", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().ICE_MELT_ENABLED;
            }
        },
        MUSHROOM_SPREAD("MushroomGrowth", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().MUSHROOM_GROWTH_ENABLED;
            }
        },
        VINES_GROWTH("VinesGrowth", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().VINES_GROWTH_ENABLED;
            }
        },
        GRASS_GROWTH("GrassGrowth", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().GRASS_GROWTH_ENABLED;
            }
        },
        TREE_GROWTH("TreeGrowth", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return zone.getWorldConfig().TREE_GROWTH_ENABLED;
            }
        },
        
        OLD_PLACE_BLOCKS("ProtectedPlaceBlocks", Serializer.INTEGERLIST) {
            @Override
            public Object getDefault(ZoneBase zone) {return null;}
        },
        OLD_BREAK_BLOCKS("ProtectedBreakBlocks", Serializer.INTEGERLIST) {
            @Override
            public Object getDefault(ZoneBase zone) {return null;}
        },
        PLACE_BLOCKS("ProtectedPlaceMaterials", Serializer.STRINGLIST) {
            @Override
            public Object getDefault(ZoneBase zone) {return null;}
        },
        BREAK_BLOCKS("ProtectedBreakMaterials", Serializer.STRINGLIST) {
            @Override
            public Object getDefault(ZoneBase zone) {return null;}
        },
        
        ALLOWED_MOBS("AllowedMobs", Serializer.ENTITYLIST) {
            @Override
            public Object getDefault(ZoneBase zone) {
                if(zone.getWorldConfig().ALLOWED_MOBS_ENABLED)
                    return zone.getWorldConfig().ALLOWED_MOBS;
                return null;
            }
        },
        ALLOWED_ANIMALS("AllowedAnimals", Serializer.ENTITYLIST) {
            @Override
            public Object getDefault(ZoneBase zone) {
                if(zone.getWorldConfig().ALLOWED_ANIMALS_ENABLED)
                    return zone.getWorldConfig().ALLOWED_ANIMALS;
                return null;
            }
        },
        
        TEXTURE_PACK("TexturePack", Serializer.STRING) {
            @Override
            public Object getDefault(ZoneBase zone) {
                return null;
            }
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
        
        SPAWN_LOCATION("SpawnLocation", Serializer.LOCATION) {
            @Override
            public Object getDefault(ZoneBase zone) {return null; }
        }, 
        INHERIT_GROUP("GroupInherit", Serializer.BOOLEAN) {
            @Override
            public Object getDefault(ZoneBase zone) { return true; }
        };

        
        private final String name;
        private final Serializer serializer;
        private ZoneVar(String name, Serializer serializer) {
            this.name = name;
            this.serializer = serializer;
        }
        
        public String getName() { return name; }
        public Serializer getSerializer() { return serializer; }
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
