package com.zones.model.types;


import org.bukkit.block.Block;

import com.meaglin.json.JSONObject;
import com.zones.persistence.Zone;

public class ZoneStone extends ZoneNormal {
    
//    private boolean isGovernor;
//
//    private ZoneStone parent;
//    private List<ZoneStone> children;
    
    @Override
    protected void onLoad(Zone persistence) {
        super.onLoad(persistence);
//        if(!getConfig().has("parentId")) {
//            getConfig().put("parentId", 0);
//        }
//        int parentId = getConfig().getInt("parentId");
//        ZoneNormal zone = getPlugin().getZoneManager().getZone(parentId);
//        if(zone == null || !(zone instanceof ZoneStone)) {
//            getConfig().put("parentId", 0);
//        }
//        parent = (ZoneStone) zone;
//        parent.tellParent(this);
        if(!getConfig().has("centerUpgrades")) {
            getConfig().put("centerUpgrades", new JSONObject());
        }
    }
    
    public void addUpgrade(Block block) {
        getConfig().getJSONObject("centerUpgrades").put(blockToString(block), new JSONObject()
                .put("x", block.getX())
                .put("y", block.getY())
                .put("z", block.getZ()));
    }
    
    public void removeUpgrade(Block block) {
        getConfig().remove(blockToString(block));
    }
    
    public boolean isCenter(Block block) {
        JSONObject center = getConfig().getJSONObject("center");
        return center.getInt("x") == block.getX()
                && center.getInt("y") == block.getY()
                && center.getInt("z") == block.getZ();
    }
    
    public boolean isCenterUpgrade(Block block) {
        return getConfig().getJSONObject("centerUpgrades").has(blockToString(block));
    }
    
    public boolean isProtectedBlock(Block block) {
        return isCenter(block) || isCenterUpgrade(block);
    }
    
    private String blockToString(Block b) {
        return "x" + b.getX() + "y" + b.getY() + "z" + b.getZ();
    }
    
    public boolean isNearCenter(Block b, int range) {
        JSONObject center = getConfig().getJSONObject("center");
        int x = center.getInt("x"), y = center.getInt("y"), z = center.getInt("z");
        return (b.getX() >= (x-range) && b.getX() <= (x+range)) &&
                (b.getY() >= (y-range) && b.getY() <= (y+range)) &&
                (b.getZ() >= (z-range) && b.getZ() <= (z+range));
    }

//    public boolean isGovernor() {
//        return isGovernor;
//    }
//    
//    public boolean hasParent() {
//        return parent != null;
//    }
//    
//    private void tellParent(ZoneStone child) {
//        if(children == null) {
//            children = new ArrayList<>();
//        }
//        isGovernor = true;
//        children.add(child);
//    }
//    
//    @Override
//    public JSONObject getSettings() {
//        return hasParent() ? parent.getSettings() : super.getSettings();
//    }
//    
//    @Override
//    public boolean canModify(Player player, ZonesAccess.Rights right) {
//        return hasParent() ? parent.canModify(player, right) : super.canModify(player, right);
//    }
//
//    @Override
//    public ZonesAccess getAccess(String group) {
//        return hasParent() ? parent.getAccess(group) : super.getAccess(group);
//    }
//
//    @Override
//    public ZonesAccess getAccess(OfflinePlayer player) {
//        return hasParent() ? parent.getAccess(player) : super.getAccess(player);
//    }
//
//    @Override
//    protected boolean isAdmin(OfflinePlayer player) {
//        return hasParent() ? parent.isAdmin(player) : super.isAdmin(player);
//    }
//    
//    @Override
//    public boolean isAdminUser(OfflinePlayer player) {
//        return hasParent() ? parent.isAdminUser(player) : super.isAdminUser(player);
//    }
//    
//    @Override
//    protected String usersToString() {
//        return hasParent() ? parent.usersToString() : super.usersToString();
//    }
//    
//    @Override
//    protected String adminsToString() {
//        return hasParent() ? parent.adminsToString() : super.adminsToString();
//    }
//    
//    @Override
//    public void setAdmin(OfflinePlayer player, boolean isAdmin) {
//        if(hasParent()) {
//            parent.setAdmin(player, isAdmin);
//        } else {
//            super.setAdmin(player, isAdmin);
//        }
//    }
//    
//    @Override
//    public ZonesAccess setUser(OfflinePlayer player, String access) {
//        return hasParent() ? parent.setUser(player, access) : super.setUser(player, access);
//    }
//    
//    @Override
//    public ZonesAccess setGroup(String group, String access) {
//        return hasParent() ? parent.setGroup(group, access) : super.setGroup(group, access);
//    }
//    
//    @Override
//    public void removeAdmin(JSONObject admin) {
//        if(hasParent()) {
//            parent.removeAdmin(admin);
//        } else {
//            super.removeAdmin(admin);
//        }
//    }
//    
//    @Override
//    public JSONObject matchUser(String name) {
//        return hasParent() ? parent.matchUser(name) : super.matchUser(name);
//    }
}
