package com.zones.model.types;


import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.meaglin.json.JSONObject;
import com.zones.model.ZoneForm;
import com.zones.persistence.Vertice;
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
    
    public void addUpgrade(Block block, JSONObject setting) {
        int cx = setting.getInt("radiusUpgradeX"), cy = setting.getInt("radiusUpgradeY"), cz = setting.getInt("radiusUpgradeZ");
        
        getConfig().getJSONObject("centerUpgrades").put(blockToString(block), new JSONObject()
            .put("x", block.getX())
            .put("y", block.getY())
            .put("z", block.getZ())
            .put("xChange", cx)
            .put("yChange", cy)
            .put("zChange", cz)
        );
        List<Vertice> vert = this.getPersistence().getVertices();
        vert.get(0).setX(vert.get(0).getX() - cx);
        vert.get(0).setZ(vert.get(0).getZ() - cz);
        
        vert.get(1).setX(vert.get(1).getX() + cx);
        vert.get(1).setZ(vert.get(1).getZ() + cz);        
        
        getPersistence().setMinY(getPersistence().getMiny() - cy);
        getPersistence().setMaxY(getPersistence().getMaxy() + cy);
        loadForm(); // Reload form.
    }
    
    public void removeCenter(Player player, Block block) {
        JSONObject c = getConfig().getJSONObject("centerUpgrades");
        for(String key : c.keySet()) {
            JSONObject u = c.getJSONObject(key);
            Block b = block.getWorld().getBlockAt(u.getInt("x"), u.getInt("y"), u.getInt("z"));
            
            giveOrDrop(player, new ItemStack(b.getType()));
            b.setType(Material.AIR);
        }
        giveOrDrop(player, new ItemStack(block.getType()));
        block.setType(Material.AIR);
    }
    
    public void removeUpgrade(Player player, Block block) {
//        JSONObject b = getConfig().getJSONObject("centerUpgrades").getJSONObject(blockToString(block));
        JSONObject c = getConfig().getJSONObject("centerUpgrades");
        JSONObject b = (JSONObject) c.remove(blockToString(block));
        int totalY = 0;
        for(String key : c.keySet()) {
            totalY += c.getJSONObject(key).getInt("yChange");
        }
        totalY += getConfig().getJSONObject("center").getInt("yChange");
        int centerY = getConfig().getJSONObject("center").getInt("y");
        int lowY = centerY - totalY, highY = centerY + totalY;
        if(lowY < 0) {
            lowY = 0;
        }
        if(highY > 260) {
            highY = 260;
        }
        getPersistence().setMinY(lowY);
        getPersistence().setMaxY(highY);
        
        int cx = b.getInt("xChange"), cz = b.getInt("zChange");
        List<Vertice> vert = this.getPersistence().getVertices();
        vert.get(0).setX(vert.get(0).getX() + cx);
        vert.get(0).setZ(vert.get(0).getZ() + cz);
        
        vert.get(1).setX(vert.get(1).getX() - cx);
        vert.get(1).setZ(vert.get(1).getZ() - cz);   
        
        giveOrDrop(player, new ItemStack(block.getType()));
        block.setType(Material.AIR);
    }
    
    public boolean canUpgrade(JSONObject setting) {
        int cx = setting.getInt("radiusUpgradeX"), cy = setting.getInt("radiusUpgradeY"), cz = setting.getInt("radiusUpgradeZ");
        ZoneForm f = getForm();
        List<ZoneNormal> zones = getWorldManager().getZones(f.getLowX() - cx, f.getHighX() + cx, f.getLowY() - cy, f.getHighY() + cy, f.getLowZ() - cz, f.getHighZ() + cz);
        for(ZoneNormal zone : zones) {
            if(zone.equals(this)) { // Skip self
                continue;
            }
            return false; // Other zones, don't allow
        }
        return true;
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
    
    private void giveOrDrop(Player player, ItemStack stack) {
        Map<Integer, ItemStack> rest = player.getInventory().addItem(stack);
        ItemStack drop = rest.get(0);
        if(drop != null) {
            player.getWorld().dropItem(player.getLocation(), drop);
        }
    }
    
    /*
    public boolean isGovernor() {
        return isGovernor;
    }
    
    public boolean hasParent() {
        return parent != null;
    }
    
    private void tellParent(ZoneStone child) {
        if(children == null) {
            children = new ArrayList<>();
        }
        isGovernor = true;
        children.add(child);
    }
    
    @Override
    public JSONObject getSettings() {
        return hasParent() ? parent.getSettings() : super.getSettings();
    }
    
    @Override
    public boolean canModify(Player player, ZonesAccess.Rights right) {
        return hasParent() ? parent.canModify(player, right) : super.canModify(player, right);
    }

    @Override
    public ZonesAccess getAccess(String group) {
        return hasParent() ? parent.getAccess(group) : super.getAccess(group);
    }

    @Override
    public ZonesAccess getAccess(OfflinePlayer player) {
        return hasParent() ? parent.getAccess(player) : super.getAccess(player);
    }

    @Override
    protected boolean isAdmin(OfflinePlayer player) {
        return hasParent() ? parent.isAdmin(player) : super.isAdmin(player);
    }
    
    @Override
    public boolean isAdminUser(OfflinePlayer player) {
        return hasParent() ? parent.isAdminUser(player) : super.isAdminUser(player);
    }
    
    @Override
    protected String usersToString() {
        return hasParent() ? parent.usersToString() : super.usersToString();
    }
    
    @Override
    protected String adminsToString() {
        return hasParent() ? parent.adminsToString() : super.adminsToString();
    }
    
    @Override
    public void setAdmin(OfflinePlayer player, boolean isAdmin) {
        if(hasParent()) {
            parent.setAdmin(player, isAdmin);
        } else {
            super.setAdmin(player, isAdmin);
        }
    }
    
    @Override
    public ZonesAccess setUser(OfflinePlayer player, String access) {
        return hasParent() ? parent.setUser(player, access) : super.setUser(player, access);
    }
    
    @Override
    public ZonesAccess setGroup(String group, String access) {
        return hasParent() ? parent.setGroup(group, access) : super.setGroup(group, access);
    }
    
    @Override
    public void removeAdmin(JSONObject admin) {
        if(hasParent()) {
            parent.removeAdmin(admin);
        } else {
            super.removeAdmin(admin);
        }
    }
    
    @Override
    public JSONObject matchUser(String name) {
        return hasParent() ? parent.matchUser(name) : super.matchUser(name);
    }
    */
}
