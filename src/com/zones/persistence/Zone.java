package com.zones.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;
import com.meaglin.json.JSONObject;
import com.zones.Zones;
/*
@Entity()
@Table(name = "zones")
*/
public class Zone {
    
    
    @Id
    private Integer id;
    
    @NotEmpty
    @Length(max=255)
    private String name;
    
    @NotEmpty
    @Length(max=255)
    private String zonetype;
    
    @NotEmpty
    @Length(max=255)
    private String formtype;
    
    @NotEmpty
    @Length(max=255)
    private String world;
    
    @Column(columnDefinition= "LONGTEXT")
    private String admins;
    
    @Column(columnDefinition= "LONGTEXT")
    private String users;
    
    @Column(columnDefinition= "LONGTEXT")
    private String settings;
    
    @Column(columnDefinition= "LONGTEXT")
    private String config;
    private JSONObject json;
    
    private int minz;
    
    private int maxz;
    
    private int size;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL)
    @OrderBy("vertexorder ASC")
    private List<Vertice> vertices;

    public Zone() {
        vertices = new ArrayList<Vertice>();
    }
    
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getZonetype() {
        return zonetype;
    }

    public String getFormtype() {
        return formtype;
    }

    public String getWorld() {
        return world;
    }

    public String getAdmins() {
        return admins;
    }

    public String getUsers() {
        return users;
    }

    public String getSettings() {
        return settings;
    }

    public int getMinz() {
        return minz;
    }

    public int getMaxz() {
        return maxz;
    }

    public int getSize() {
        return size;
    }

    public List<Vertice> getVertices() {
        return vertices;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setZonetype(String zonetype) {
        this.zonetype = zonetype;
    }

    public void setFormtype(String formtype) {
        this.formtype = formtype;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setAdmins(String admins) {
        this.admins = admins;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public void setMinz(int minz) {
        this.minz = minz;
    }

    public void setMaxz(int maxz) {
        this.maxz = maxz;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setVertices(List<Vertice> vertices) {
        this.vertices = vertices;
    }
    
    /**
     * @return the config
     */
    public String getRawConfig() {
        return config;
    }
    
    public void saveConfig() {
        this.setConfig(getConfig().toString());
    }
    
    public JSONObject getConfig() {
        if(json == null) {
            json = new JSONObject(getRawConfig());
            json.defaults("{"
                    + "version: 0,"
                    + "users: {},"
                    + "groups: {},"
                    + "settings: {}"
                    + "}");
        }
        return json;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(String config) {
        this.config = config;
    }

    public void addVertice(Vertice vertice) {
        this.vertices.add(vertice);
    }
    
    public void clearVertices() {
        vertices.clear();
    }
    
    @Deprecated
    public boolean save(Zones plugin) {
        try {
            if(getId() == 0) {
                plugin.getDatabase().save(this);
            } else {
                plugin.getDatabase().update(this);
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    @Deprecated
    public boolean saveAll(Zones plugin) {
        if(save(plugin)) {
            try {
                for(Vertice vertice : getVertices())
                    vertice.setId(getId());
                plugin.getDatabase().save(getVertices());
            } catch(Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
}
