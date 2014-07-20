package com.zones.backwardscompat;

import java.util.HashMap;
import java.util.Map;

public enum OldZoneVar {
    
    
        TELEPORT("TeleportEnabled", OldSerializer.BOOLEAN) {
        },
        LIGHTER("LighterEnabled", OldSerializer.BOOLEAN) {
        },
        FIRE("FireEnabled", OldSerializer.BOOLEAN) {
        },
        LAVA("LavaEnabled", OldSerializer.BOOLEAN) {
        },
        WATER("WaterEnabled", OldSerializer.BOOLEAN) {
        },
        FOOD("FoodEnabled", OldSerializer.BOOLEAN) {
        },
        HEALTH("HealthEnabled", OldSerializer.BOOLEAN) {
        },
        DYNAMITE("DynamiteEnabled", OldSerializer.BOOLEAN) {
        },
        PHYSICS("PhysicsEnabled", OldSerializer.BOOLEAN) {
        },
        NOTIFY("NotifyEnabled", OldSerializer.BOOLEAN) {
        },
        MOBS("MobsEnabled", OldSerializer.BOOLEAN) {
        },
        ANIMALS("AnimalsEnabled", OldSerializer.BOOLEAN) {
        },
        CROP_PROTECTION("CropProtectionEnabled", OldSerializer.BOOLEAN) {
        },
        
        ENDER_GRIEFING("EnderGriefingEnabled", OldSerializer.BOOLEAN) {
        },
        LEAF_DECAY("LeafDecayEnabled", OldSerializer.BOOLEAN) {
        },
        SNOW_FALL("SnowForm", OldSerializer.BOOLEAN) {
        },
        ICE_FORM("IceForm", OldSerializer.BOOLEAN) {
        },
        SNOW_MELT("SnowMelt", OldSerializer.BOOLEAN) {
        },
        ICE_MELT("IceMelt", OldSerializer.BOOLEAN) {
        },
        MUSHROOM_SPREAD("MushroomGrowth", OldSerializer.BOOLEAN) {
        },
        VINES_GROWTH("VinesGrowth", OldSerializer.BOOLEAN) {
        },
        GRASS_GROWTH("GrassGrowth", OldSerializer.BOOLEAN) {
        },
        TREE_GROWTH("TreeGrowth", OldSerializer.BOOLEAN) {
        },
        
        OLD_PLACE_BLOCKS("ProtectedPlaceBlocks", OldSerializer.INTEGERLIST) {
        },
        OLD_BREAK_BLOCKS("ProtectedBreakBlocks", OldSerializer.INTEGERLIST) {
        },
        PLACE_BLOCKS("ProtectedPlaceMaterials", OldSerializer.STRINGLIST) {
        },
        BREAK_BLOCKS("ProtectedBreakMaterials", OldSerializer.STRINGLIST) {
        },
        
        ALLOWED_MOBS("AllowedMobs", OldSerializer.ENTITYLIST) {
        },
        ALLOWED_ANIMALS("AllowedAnimals", OldSerializer.ENTITYLIST) {
        },
        
        TEXTURE_PACK("TexturePack", OldSerializer.STRING) {
        },
        
        ENTER_MESSAGE("EnterMessage", OldSerializer.STRING) ,
        LEAVE_MESSAGE("LeaveMessage", OldSerializer.STRING),
        
        SPAWN_LOCATION("SpawnLocation", OldSerializer.LOCATION),
        
        INHERIT_GROUP("GroupInherit", OldSerializer.BOOLEAN),
        
        BUY_ALLOWED("Buyable", OldSerializer.BOOLEAN),
        
        BUY_PRICE("BuyPrice", OldSerializer.DOUBLE),
        
        DENIED_COMMANDS("DeniedCommands", OldSerializer.STRINGLIST),
        
        ALLOWED_COMMANDS("AllowedCommands", OldSerializer.STRINGLIST);

        
        private final String name;
        private final OldSerializer serializer;
        private OldZoneVar(String name, OldSerializer serializer) {
            this.name = name;
            this.serializer = serializer;
        }
        
        public String getName() { return name; }
        public OldSerializer getSerializer() { return serializer; }
        public Class<? extends Object> getType() { return serializer.getType(); }
        public Class<? extends Object> getListType() { return serializer.getListType(); }
        public Object unSerialize(String serializedData) { return serializer.unSerialize(serializedData); }
        public String serialize(Object data) { return serializer.serialize(data); }
        // Not needed, since this is only needed for conversion.
//        public abstract Object getDefault(ZoneBase zone);
        
        
        public static OldZoneVar fromName(String name) {
            return names.get(name);
        }
        
        private static final Map<String,OldZoneVar> names;
        static {
            names = new HashMap<String,OldZoneVar>();
            for(OldZoneVar n : values())
                names.put(n.getName(),n);
        }
    }
