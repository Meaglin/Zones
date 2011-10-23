package com.zones.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.config.ServerConfig;
import com.mysql.jdbc.Statement;
import com.zones.Zones;
public class Database {
    
    private String url,username,password;
    //private Zones plugin;
    public static final String SAVE_ZONE = "INSERT INTO `zones` (name, zonetype, formtype, world, admins, users, settings, minz, maxz, size) VALUES (?,?,?,?,?,?,?,?,?,?)";
    public static final String SAVE_VERTICE = "INSERT INTO `zones_vertices` (id, vertexorder, x, y) values (?,?,?,?)";
    
    public static final String UPDATE_ZONE = "UPDATE `zones` SET " +
    		"`name` = ?, `zonetype` = ?, `formtype` = ?, `world` = ?, `admins` = ?, " +
    		"`users` = ?, `settings` = ?, `minz` = ?, `maxz` = ?, size = ? " +
    		"WHERE id = ? LIMIT 1";
    
    public static final String DELETE_ZONE =    "DELETE FROM `zones` WHERE id = ? LIMIT 1";
    public static final String DELETE_VERTICE = "DELETE FROM `zones_vertices` WHERE id = ?";
    
    public static final String SELECT_WORLD =   "SELECT * FROM `zones` WHERE world = ? ";
    public static final String SELECT_VERTICE = "SELECT * FROM `zones_vertices` WHERE id = ? ORDER BY `vertexorder` ASC ";
    public static final String SELECT_ZONE =    "SELECT * FROM `zones` WHERE id = ? LIMIT 1";
    
    public Database(Zones plugin) {
        /*
         * Hacky but does the job.
         * Reason for doing this:
         * i don't want to be the 5th plugins that has just a config file for
         * database connection information....
         * 
         * So i just grab it from bukkit :).
         */
        ServerConfig db = new ServerConfig();
        plugin.getServer().configureDbConfig(db);

        try {
            Class.forName(db.getDataSourceConfig().getDriver());
        } catch(Exception e) {
            Zones.log.warning("[Zones]Warning JDBC not availeble.");
            return;
        }
        
        this.url = db.getDataSourceConfig().getUrl();
        this.username = db.getDataSourceConfig().getUsername();
        this.password = db.getDataSourceConfig().getPassword();
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
    
    public List<Zone> get(String world) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Zone> zones = new ArrayList<Zone>();
        try {
            conn = getConnection();
            st = conn.prepareStatement(SELECT_WORLD);
            st.setString(1, world);
            rs = st.executeQuery();
            while(rs.next()) {
                Zone z = new Zone();
                z.setId(        rs.getInt(1));
                z.setName(      rs.getString(2));
                z.setZonetype(  rs.getString(3));
                z.setFormtype(  rs.getString(4));
                z.setWorld(     rs.getString(5));
                z.setAdmins(    rs.getString(6));
                z.setUsers(     rs.getString(7));
                z.setSettings(  rs.getString(8));
                z.setMinz(      rs.getInt(9));
                z.setMaxz(      rs.getInt(10));
                z.setSize(      rs.getInt(11));
                z.setVertices(get(z));
                zones.add(z);
            }
        } catch(Exception e) {
            Zones.log.warning("[Zones]Error loading zones of world " + world + ":");
            e.printStackTrace();
        } finally {
            try{
                if(conn != null) conn.close();
                if(st != null) st.close();
                if(rs != null) rs.close();
            } catch(Exception e) {}
        }
        return zones;
    }
    
    public List<Vertice> get(Zone zone) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Vertice> vertices = new ArrayList<Vertice>();
        try {
            conn = getConnection();
            st = conn.prepareStatement(SELECT_VERTICE);
            st.setInt(1, zone.getId());
            rs = st.executeQuery();
            while(rs.next()) {
                Vertice v = new Vertice();
                v.setId(rs.getInt(1));
                v.setVertexorder(rs.getInt(2));
                v.setX(rs.getInt(3));
                v.setY(rs.getInt(4));
                vertices.add(v);
            }
        } catch(Exception e) {
            Zones.log.warning("[Zones]Error loading vertices of " + zone.getName() + "[" + zone.getId() + "] :");
            e.printStackTrace();
        } finally {
            try{
                if(conn != null) conn.close();
                if(st != null) st.close();
                if(rs != null) rs.close();
            } catch(Exception e) {}
        }
        return vertices;
    }
    
    public Zone get(int id) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(SELECT_ZONE);
            st.setInt(1, id);
            rs = st.executeQuery();
            if(rs.next()) {
                Zone z = new Zone();
                z.setId(        rs.getInt(1));
                z.setName(      rs.getString(2));
                z.setZonetype(  rs.getString(3));
                z.setFormtype(  rs.getString(4));
                z.setWorld(     rs.getString(5));
                z.setAdmins(    rs.getString(6));
                z.setUsers(     rs.getString(7));
                z.setSettings(  rs.getString(8));
                z.setMinz(      rs.getInt(9));
                z.setMaxz(      rs.getInt(10));
                z.setSize(      rs.getInt(11));
                z.setVertices(get(z));
                return z;
            }
        } catch(Exception e) {
            Zones.log.warning("[Zones]Error getting zone with id " + id + ":");
            e.printStackTrace();
        } finally {
            try{
                if(conn != null) conn.close();
                if(st != null) st.close();
                if(rs != null) rs.close();
            } catch(Exception e) {}
        }
        return null;
    }
    
    public boolean save(Zone zone) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(SAVE_ZONE, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, zone.getName());
            st.setString(2, zone.getZonetype());
            st.setString(3, zone.getFormtype());
            st.setString(4, zone.getWorld());
            st.setString(5, zone.getAdmins());
            st.setString(6, zone.getUsers());
            st.setString(7, zone.getSettings());
            st.setInt(8,    zone.getMinz());
            st.setInt(9,    zone.getMaxz());
            st.setInt(10,   zone.getSize());
            st.execute();
            rs = st.getGeneratedKeys();
            if(rs.next()) {
                zone.setId(rs.getInt(1));
            }
        } catch(Exception e) {
            Zones.log.warning("[Zones]Error deleting " + zone.getName() + "[" + zone.getId() + "] :");
            e.printStackTrace();
            return false;
        } finally {
            try{
                if(conn != null) conn.close();
                if(st != null) st.close();
                if(rs != null) rs.close();
            } catch(Exception e) {}
        }
        if(zone.getId() == 0 ) return false;
        for(Vertice vertice : zone.getVertices()){
            vertice.setId(zone.getId());
            save(vertice);
        }
        return true;
    }
    
    public boolean save(Vertice vertice) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(SAVE_VERTICE);
            st.setInt(1, vertice.getId());
            st.setInt(2, vertice.getVertexorder());
            st.setInt(3, vertice.getX());
            st.setInt(4, vertice.getY());
            st.execute();
        } catch(Exception e) {
            Zones.log.warning("[Zones]Error saving vertices " + vertice.getId() + "[" + vertice.getVertexorder() + "] :");
            e.printStackTrace();
            return false;
        } finally {
            try{
                if(conn != null) conn.close();
                if(st != null) st.close();
                if(rs != null) rs.close();
            } catch(Exception e) {}
        }
        return true;
    }
    
    public boolean update(Zone zone) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(UPDATE_ZONE);
            st.setString(1, zone.getName());
            st.setString(2, zone.getZonetype());
            st.setString(3, zone.getFormtype());
            st.setString(4, zone.getWorld());
            st.setString(5, zone.getAdmins());
            st.setString(6, zone.getUsers());
            st.setString(7, zone.getSettings());
            st.setInt(8, zone.getMinz());
            st.setInt(9, zone.getMaxz());
            st.setInt(10, zone.getSize());
            st.setInt(11, zone.getId());
            st.executeUpdate();
        } catch(Exception e) {
            Zones.log.warning("[Zones]Error updating " + zone.getName() + "[" + zone.getId() + "] :");
            e.printStackTrace();
            return false;
        } finally {
            try{
                if(conn != null) conn.close();
                if(st != null) st.close();
                if(rs != null) rs.close();
            } catch(Exception e) {}
        }
        return true;
    }
    
    public boolean delete(Zone zone) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(DELETE_ZONE);
            st.setInt(1, zone.getId());
            st.execute();
        } catch(Exception e) {
            Zones.log.warning("[Zones]Error deleting " + zone.getName() + "[" + zone.getId() + "] :");
            e.printStackTrace();
            return false;
        } finally {
            try{
                if(conn != null) conn.close();
                if(st != null) st.close();
                if(rs != null) rs.close();
            } catch(Exception e) {}
        }
        deleteVertices(zone);
        return true;
    }
    
    public boolean deleteVertices(Zone zone) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(DELETE_VERTICE);
            st.setInt(1, zone.getId());
            st.execute();
        } catch(Exception e) {
            Zones.log.warning("[Zones]Error deleting vertices of " + zone.getName() + "[" + zone.getId() + "] :");
            e.printStackTrace();
            return false;
        } finally {
            try{
                if(conn != null) conn.close();
                if(st != null) st.close();
                if(rs != null) rs.close();
            } catch(Exception e) {}
        }
        return true;
    }
    
}
