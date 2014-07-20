package com.zones.command.imports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import com.meaglin.json.JSONObject;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.zones.Zones;
import com.zones.persistence.Vertice;
import com.zones.persistence.Zone;

public class WorldGuardImport {
    public static List<Zone> importAll(Zones plugin) {
        List<Zone> zones = new ArrayList<>();
        WorldGuardPlugin wg = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        for(World w : plugin.getServer().getWorlds()) {
            RegionManager m = wg.getRegionManager(w);
            for(Entry<String, ProtectedRegion> e : m.getRegions().entrySet()) {
                Zone z = new Zone();
                ProtectedRegion r = e.getValue();
                if(r instanceof ProtectedCuboidRegion) {
                    Vertice v = new Vertice();
                    v.setVertexorder(0);
                    v.setX(r.getMinimumPoint().getBlockX());
                    v.setZ(r.getMinimumPoint().getBlockZ());
                    z.addVertice(v);
                    v = new Vertice();
                    v.setVertexorder(1);
                    v.setX(r.getMaximumPoint().getBlockX());
                    v.setZ(r.getMaximumPoint().getBlockZ());
                    z.addVertice(v);
                } else if(r instanceof ProtectedPolygonalRegion) {
                    int order = 0;
                    for(BlockVector2D p : ((ProtectedPolygonalRegion) r).getPoints()) {
                        Vertice v = new Vertice();
                        v.setVertexorder(order);
                        v.setX(p.getBlockX());
                        v.setZ(p.getBlockZ());
                        z.addVertice(v);
                        order++;
                    }
                }
                z.setMinY(r.getMinimumPoint().getBlockY());
                z.setMaxY(r.getMaximumPoint().getBlockY());
                JSONObject userlist = z.getConfig().getJSONObject("users");
                JSONObject grouplist = z.getConfig().getJSONObject("groups");
                for(String admin : r.getOwners().getPlayers()) {
                    OfflinePlayer p = plugin.getOfflinePlayer(admin);
                    if(p == null) {
                        // Log?
                        continue;
                    }
                    JSONObject user = new JSONObject();
                    user.put("admin", true);
                    user.put("name", p.getName());
                    user.put("access", "*");
                    user.put("uuid", p.getUniqueId().toString());
                    userlist.put(p.getUniqueId().toString(), user);
                }
                for(String group : r.getOwners().getGroups()) {
                    JSONObject user = new JSONObject();
                    user.put("name", group);
                    user.put("access", "*");
                    grouplist.put(group, user);
                }
                String defaultAccess = "*";
                for(String admin : r.getMembers().getPlayers()) {
                    OfflinePlayer p = plugin.getOfflinePlayer(admin);
                    if(p == null) {
                        // Log?
                        continue;
                    }
                    JSONObject user = new JSONObject();
                    user.put("admin", false);
                    user.put("name", p.getName());
                    user.put("access", defaultAccess);
                    user.put("uuid", p.getUniqueId().toString());
                    userlist.put(p.getUniqueId().toString(), user);
                }
                for(String group : r.getMembers().getGroups()) {
                    JSONObject user = new JSONObject();
                    user.put("name", group);
                    user.put("access", defaultAccess);
                    grouplist.put(group, user);
                }
            }
        }
        return zones;
    }
}
