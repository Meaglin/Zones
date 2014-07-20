package com.zones.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mc.alk.virtualPlayer.VirtualPlayer;
import mc.alk.virtualPlayer.VirtualPlayers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.types.ZoneNormal;
import com.zones.persistence.Vertice;
import com.zones.persistence.Zone;
import com.zones.world.WorldManager;

public class TestManager {

    public Zones plugin;
    public Player player;
    public CommandSender sender;
    ZoneNormal zone;
    Class<?>[] classes = new Class<?>[] { 
            AccessTest.class, 
            BlockTest.class
    };
    
    public TestManager(Zones plugin, CommandSender sender) throws Exception {
        this.plugin = plugin;
        if(sender instanceof Player) {
            this.player = (Player) sender;
        } else {
            this.player = VirtualPlayers.makeVirtualPlayer("testPlayer");
        }
        this.sender = sender;
        factory();
    }
    
    
    public void run() throws Exception {
        int t = 0, c = 0, f = 0, a = 0;
        long start = System.currentTimeMillis();
        for(Class<?> tc : classes) {
            @SuppressWarnings("unchecked")
            Class<? extends Test> cls = (Class<? extends Test>) tc;
            c += 1;
            Test test = cls.getConstructor(Zones.class, Player.class).newInstance(plugin, player);
            test.zone = zone;
            for(Method m : test.getTestMethods()) {
                t += 1;
                try {
                    m.invoke(test);
                } catch (InvocationTargetException|RuntimeException e) {
                    f += 1;
                    sender.sendMessage(ChatColor.RED + test.getClass().getName() + "." + m.getName() + "()");
                    e.printStackTrace();
                }
                System.out.println(test.getClass().getName() + "." + m.getName() + "()");
            }
            a += test.cnt;
        }
        plugin.getZoneManager().delete(zone);
        if (player instanceof VirtualPlayer) {
            VirtualPlayers.deleteVirtualPlayer((VirtualPlayer) player);
        }
        sender.sendMessage("Done: " + 
            c + " classes, " + 
            t + " tests, " + 
            (t - f) + " success, " + 
            f + " fails, " + 
            a + " asserts, " + 
            (System.currentTimeMillis() - start) + " time.");
    }
    
    public ZoneNormal factory() {
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        
        WorldManager wm = plugin.getWorldManager(player.getWorld());
        
        Zone zoneCfg = new Zone();
        zoneCfg.setName("test zone");
        zoneCfg.setZonetype("ZoneNormal");
        zoneCfg.setFormtype("ZoneCuboid");
        zoneCfg.setWorld(player.getWorld().getName());
        zoneCfg.setSize(2);
        zoneCfg.getConfig().put("version", 1);
        zoneCfg.setMinY(y - 10);
        zoneCfg.setMaxY(y + 10);
        Vertice v = new Vertice();
        v.setVertexorder(0);
        v.setX(x + 10);
        v.setZ(z - 10);
        zoneCfg.addVertice(v);
        v = new Vertice();
        v.setVertexorder(1);
        v.setX(x + 30);
        v.setZ(z + 10);
        zoneCfg.addVertice(v);
//
//        JSONObject user = new JSONObject();
//        user.put("admin", true);
//        user.put("access", "*");
//        user.put("name", player.getName());
//        user.put("uuid", player.getUniqueId().toString());
//        zone.getConfig().getJSONObject("users").put(player.getUniqueId().toString(), user);
//        zone.getConfig().put("center", (new JSONObject())
//                .put("x", b.getX())
//                .put("y", b.getY())
//                .put("z", b.getZ())
//        );
        zoneCfg.saveConfig();
        
        plugin.getMysqlDatabase().save(zoneCfg);
        plugin.getZoneManager().loadFromPersistentData(wm, zoneCfg);
        return zone = plugin.getZoneManager().addZone(plugin.getZoneManager().loadFromPersistentData(wm, zoneCfg));
    }
}
