package com.zones.model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zones.model.settings.*;

/**
 * 
 * @author Meaglin
 *
 */
public class ZoneSettings {
    
    private Map<ZoneVar, Object> settings;
    
    public ZoneSettings() {
        settings = new EnumMap<ZoneVar,Object>(ZoneVar.class);
    }
    
    public void set(ZoneVar name, boolean value) {
        set(name, new Boolean(value));
    }
    
    public void set(ZoneVar name, int value) {
        set(name , new Integer(value));
    }
    
    public void set(ZoneVar name, String value) {
        set(name , escape(value));
    }
    
    public void set(ZoneVar name, Object value) {
        if(value == null) {
            settings.remove(name);
            return;
        }
        settings.put(name, value);
    }
    
    public boolean getBool(ZoneVar name) {
        return getBool(name, false);
    }
    
    public boolean getBool(ZoneVar name, boolean def) {
        Object o = get(name);
        if(o != null && o instanceof Boolean)
            return ((Boolean)o).booleanValue();
        else {
            return def;
        }
    }
    
    public int getInt(ZoneVar name) {
        return getInt(name,0);
    }
    
    public int getInt(ZoneVar name, int def) {
        Object o = get(name);
        if(o != null && o instanceof Integer)
            return ((Integer)o).intValue();
        else {
            return def;
        }
    }
    
    public String getString(ZoneVar name) {
        return getString(name,null);
    }
    public String getString(ZoneVar name,String def) {
        Object o = get(name);
        if(o != null && o instanceof String)
            return unEscape(((String)o));
        else {
            return def;
        }
    }
    public List<?> getList(ZoneVar name) {
        return getList(name,null);
    }
    
    public List<?> getList(ZoneVar name,List<?> def) {
        Object o = get(name);
        if(o != null && o instanceof List && !((List<?>)o).isEmpty()) {
            return (List<?>)o;
        } else {
            return def;
        }
    }
    
    public Object get(ZoneVar name) {
        return settings.get(name);
    }
    
    public static ZoneSettings unserialize(String serializedData) {
        
        ZoneSettings rt = new ZoneSettings();
        try {
            String[] split = new String[2];
            ZoneVar type = null;
            for(String part : serializedData.split(";")) {
                if(part == null || part.trim().equals(""))continue;
                
                split = part.split(",");
                if(split.length != 2) continue;
                type = ZoneVar.fromName(split[0]);
                if(type != null) {
                    rt.set(type, type.unSerialize(split[1]));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            return rt;
        }
        
        return rt;
    }
    
    public String serialize() {
        return toString();
    }
    
    public String toString() {
        String rt = "";
        for(Entry<ZoneVar, Object> e : settings.entrySet()) {
            if(e.getValue() == null)continue;

            String data = e.getKey().serialize(e.getValue());
            if(data == null || data.equals(""))continue;
            
            rt += e.getKey().getName() + "," + data + ";";
        }
        
        return rt;
    }
    

    
    
    
    public static final String unEscape(String str) {
        if(str ==  null) return "";
        return str.replace("$1", ",").replace("$2", ";");
    }
    
    public static final String escape(String str) {
        if(str == null) return "";
        return str.replace(",", "$1").replace(";" , "$2");
    }
}
