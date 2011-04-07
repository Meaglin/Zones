package com.zones.util;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * 
 * @author Meaglin
 *
 */
public class Settings {
    private HashMap<String, Object> settings;
    
    public Settings() {
        settings = new HashMap<String,Object>();
    }
    
    public void set(String name, boolean value) {
        set(name, new Boolean(value));
    }
    
    public void set(String name, int value) {
        set(name , new Integer(value));
    }
    
    public void set(String name, String value) {
        set(name , value);
    }
    
    public void set(String name, Object value) {
        settings.put(name, value);
    }
    
    public boolean getBool(String name) {
        Object o = get(name);
        if(o != null && o instanceof Boolean)
            return ((Boolean)o).booleanValue();
        else {
            return false;
        }
    }
    
    public boolean getBool(String name, boolean def) {
        Object o = get(name);
        if(o != null && o instanceof Boolean)
            return ((Boolean)o).booleanValue();
        else {
            return def;
        }
    }
    
    public int getInt(String name) {
        Object o = get(name);
        if(o != null && o instanceof Integer)
            return ((Integer)o).intValue();
        else {
            return 0;
        }
    }
    
    public String getString(String name) {
        Object o = get(name);
        if(o != null && o instanceof String)
            return ((String)o);
        else {
            return "";
        }
    }
    
    public Object get(String name) {
        return settings.get(name);
    }
    
    public static Settings unserialize(String serializedData) {
        
        Settings rt = new Settings();
        try {
            String[] split = new String[3];
            for(String part : serializedData.split(";")) {
                split = part.split(",");
                switch(Integer.parseInt(split[0])){
                    case 1:
                        rt.set(split[1],Boolean.parseBoolean(split[2]));
                        break;
                    case 2:
                        rt.set(split[1],Integer.parseInt(split[2]));                    
                        break;
                    case 3:
                        rt.set(split[1],split[2]);
                        break;
                    default:
                        break;
                }
                
            }
        } catch(Exception e) {
            return null;
        }
        
        return rt;
    }
    
    public String serialize() {
        return toString();
    }
    
    public String toString() {
        String rt = "";
        for(Entry<String, Object> e : settings.entrySet()) {
            
            if(e.getValue() instanceof Boolean)
                rt += "1";
            else if(e.getValue() instanceof Integer)
                rt += "2";
            else if(e.getValue() instanceof String)
                rt += "3";
            else 
                rt += "0";
            
            rt +=  "," + e.getKey() + "," + e.getValue().toString() + ";";
        }
        
        return rt;
    }
}
