package com.zones.model.settings;

import java.util.HashMap;
import java.util.Map;

import com.meaglin.json.JSONArray;
import com.meaglin.json.JSONObject;


public enum ZoneVar {
    TELEPORT("TeleportEnabled", ZoneVarType.BOOLEAN, true) {},
    ENDERPEARL("EnderPearl", ZoneVarType.BOOLEAN, true),
    
    LIGHTER("LighterEnabled", ZoneVarType.BOOLEAN, true) {},
    FIRE("FireEnabled", ZoneVarType.BOOLEAN, true) {},
    FIRE_PROTECTED_BLOCKS("FireProtectedBlocks", ZoneVarType.MATERIALLIST, new JSONArray()),
    
    LAVA("LavaEnabled", ZoneVarType.BOOLEAN, true) {},
    LAVA_PROTECTED_BLOCKS("LavaProtectedBlocks", ZoneVarType.MATERIALLIST, new JSONArray()),
    
    WATER("WaterEnabled", ZoneVarType.BOOLEAN, true) {},
    WATER_PROTECTED_BLOCKS("WaterProtectedBlocks", ZoneVarType.MATERIALLIST, new JSONArray()),
    
    FOOD("FoodEnabled", ZoneVarType.BOOLEAN, true) {},
    
    HEALTH("HealthEnabled", ZoneVarType.BOOLEAN, true) {},
    PLAYER_ENTITY_DAMAGE("PlayerEntityDamageEnabled", ZoneVarType.BOOLEAN, true),
    PLAYER_PVP_DAMAGE("PlayerPVPDamageEnabled", ZoneVarType.BOOLEAN, true),
    PLAYER_FALL_DAMAGE("PlayerFallDamageEnabled", ZoneVarType.BOOLEAN, true),
    PLAYER_LAVA_DAMAGE("PlayerLavaDamageEnabled", ZoneVarType.BOOLEAN, true),
    PLAYER_SUFFOCATION_DAMAGE("PlayerSuffocationDamageEnabled", ZoneVarType.BOOLEAN, true),
    PLAYER_FIRE_DAMAGE("PlayerFireDamageEnabled", ZoneVarType.BOOLEAN, true),
    
    PLAYER_BURN_DAMAGE("PlayerBurnDamageEnabled", ZoneVarType.BOOLEAN, true),
    PLAYER_DROWNING_DAMAGE("PlayerDrowningDamageEnabled", ZoneVarType.BOOLEAN, true),
    PLAYER_TNT_DAMAGE("PlayerTntDamageEnabled", ZoneVarType.BOOLEAN, true),
    PLAYER_CREEPER_DAMAGE("PlayerCreeperDamageEnabled", ZoneVarType.BOOLEAN, true),
    PLAYER_VOID_DAMAGE("PlayerVoidDamageEnabled", ZoneVarType.BOOLEAN, true),
    PLAYER_CONTACT_DAMAGE("PlayerContactDamageEnabled", ZoneVarType.BOOLEAN, true),
    
    DYNAMITE("DynamiteEnabled", ZoneVarType.BOOLEAN, true) {},
    CREEPER_EXPLOSION("CreeperExplosionEnabled", ZoneVarType.BOOLEAN, true),
    EXPLOSION_PROTECT_ENTITIES("ExplosionProtectEntites", ZoneVarType.BOOLEAN, false),
    EXPLOSION_PROTECTED_BLOCKS("ExplosionProtectedBlocks", ZoneVarType.MATERIALLIST, new JSONArray()),
    
    SOIL_DRY("SoilDry", ZoneVarType.BOOLEAN, true),
    PHYSICS("PhysicsEnabled", ZoneVarType.BOOLEAN, true) {},
    NOTIFY("NotifyEnabled", ZoneVarType.BOOLEAN, false) {},
    CROP_PROTECTION("CropProtectionEnabled", ZoneVarType.BOOLEAN, false) {},
    ENDER_GRIEFING("EnderGriefingEnabled", ZoneVarType.BOOLEAN, true) {},
    LEAF_DECAY("LeafDecayEnabled", ZoneVarType.BOOLEAN, true) {},
    SNOW_FALL("SnowForm", ZoneVarType.BOOLEAN, true) {},
    ICE_FORM("IceForm", ZoneVarType.BOOLEAN, true) {},
    SNOW_MELT("SnowMelt", ZoneVarType.BOOLEAN, true) {},
    ICE_MELT("IceMelt", ZoneVarType.BOOLEAN, true) {},
    MUSHROOM_SPREAD("MushroomGrowth", ZoneVarType.BOOLEAN, true) {},
    VINES_GROWTH("VinesGrowth", ZoneVarType.BOOLEAN, true) {},
    GRASS_GROWTH("GrassGrowth", ZoneVarType.BOOLEAN, true) {},
    TREE_GROWTH("TreeGrowth", ZoneVarType.BOOLEAN, true) {},
    MYCELIUM_SPREAD("MyceliumSpread", ZoneVarType.BOOLEAN, true) {},
    
    PLACE_BLOCKS("ProtectedPlaceMaterials", ZoneVarType.MATERIALLIST, new JSONArray()) {},
    BREAK_BLOCKS("ProtectedBreakMaterials", ZoneVarType.MATERIALLIST, new JSONArray()) {},
    
    MOBS("MobsEnabled", ZoneVarType.BOOLEAN, true) {},
    ALLOWED_MOBS("AllowedMobs", ZoneVarType.ENTITYLIST, new JSONArray()) {},
    
    ANIMALS("AnimalsEnabled", ZoneVarType.BOOLEAN, true) {},
    ALLOWED_ANIMALS("AllowedAnimals", ZoneVarType.ENTITYLIST, new JSONArray()) {},
    
    RESOURCE_PACK("ResourcePack", ZoneVarType.STRING, "") {},
    ENTER_MESSAGE("EnterMessage", ZoneVarType.STRING, "You've entered {zone}[{access}]") {},
    LEAVE_MESSAGE("LeaveMessage", ZoneVarType.STRING, "You've left {zone}.") {},
    
    
    LIGHTNING("LightningStrike", ZoneVarType.BOOLEAN, true),
    
    RAIN("RainEnabled", ZoneVarType.BOOLEAN, ZoneVarScope.WORLD, true),
    RAIN_DIVIDER("RainDivider", ZoneVarType.INTEGER, ZoneVarScope.WORLD, 1),
    THUNDER("ThunderEnabled", ZoneVarType.BOOLEAN, ZoneVarScope.WORLD, true),
    
    BUILD_PERMISSION_REQUIRED("BuildPermissionRequired", ZoneVarType.BOOLEAN, ZoneVarScope.WORLD, false),
    
    BORDER("BorderEnabled", ZoneVarType.BOOLEAN, ZoneVarScope.WORLD, false),
    BORDER_EXCEMPT_ADMIN("BorderExcemptAdmin", ZoneVarType.BOOLEAN, ZoneVarScope.WORLD, false),
    BORDER_RANGE("BorderRange", ZoneVarType.INTEGER, ZoneVarScope.WORLD, 1),
    BORDER_USE_SPAWN("BorderUseSpawn", ZoneVarType.BOOLEAN, ZoneVarScope.WORLD, false),
    BORDER_SHAPE("BorderShape", ZoneVarType.STRING, ZoneVarScope.WORLD, "CUBOID"),
    BORDER_ALTERNATIVE_CENTER("BorderAlternativeCenter", ZoneVarType.LOCATION, ZoneVarScope.WORLD, (new JSONObject())
        .put("x", 1)
        .put("y", 1)
        .put("z", 1)),
    
    PROTECT_STONE("ProtectStones", ZoneVarType.MATERIALMAP, ZoneVarScope.WORLD, (new JSONObject()).put("GOLD_BLOCK", 
        (new JSONObject())
        .put("radiusX", 1)
        .put("radiusY", 1)
        .put("radiusZ", 1)
        .put("radiusUpgradeX", 1)
        .put("radiusUpgradeY", 1)
        .put("radiusUpgradeZ", 1)
    )),
    
    SPAWN_LOCATION("SpawnLocation", ZoneVarType.LOCATION, ZoneVarScope.LOCAL, new JSONObject()) {},
    INHERIT_GROUP("GroupInherit", ZoneVarType.BOOLEAN, ZoneVarScope.LOCAL, true) {},
    BUY_ALLOWED("Buyable", ZoneVarType.BOOLEAN, ZoneVarScope.LOCAL, false) {},
    BUY_PRICE("BuyPrice", ZoneVarType.DOUBLE, ZoneVarScope.LOCAL, 0) {},
    DENIED_COMMANDS("DeniedCommands", ZoneVarType.STRINGLIST, new JSONArray()) {},
    ALLOWED_COMMANDS("AllowedCommands", ZoneVarType.STRINGLIST, new JSONArray()) {};
    
        
    private final String name;
    private final ZoneVarType type;
    private final ZoneVarScope scope;
    
    private final Object def;
    private ZoneVar(String name, ZoneVarType type, Object def) {
        this.name = name;
        this.type = type;
        this.def = def;
        scope = ZoneVarScope.GLOBAL;
    }
    
    private ZoneVar(String name, ZoneVarType type, ZoneVarScope scope, Object def) {
        this.name = name;
        this.type = type;
        this.def = def;
        this.scope = scope;
    }
    
    public String getName() { return name; }
    public ZoneVarType getType() { return type; }
    public ZoneVarScope getScope() { return scope; }
    public Object getDefault() { return def; }
    
    public boolean inScope(ZoneVarScope scope) {
        return (getScope().getLevel() & scope.getLevel()) == scope.getLevel();
    }
    
    public static ZoneVar fromName(String name) {
        return names.get(name);
    }
    
    private static final Map<String,ZoneVar> names;
    static {
        names = new HashMap<String,ZoneVar>();
        for(ZoneVar n : values()) {
            names.put(n.getName(),n);
        }
    }
}
