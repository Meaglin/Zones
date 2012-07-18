package com.zones.util.properties;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtendedProperties extends Properties {
	
    public ExtendedProperties(File file) {
        super(file);
    }

    public ExtendedProperties(InputStream resourceAsStream) {
        super(resourceAsStream);
    }
    
    public boolean getBool(String key, boolean defaultvalue) {
    	Property val = getProperty(key);
        return (val == null || !val.isBoolean() ? defaultvalue : val.toBoolean());
    }
    public int getInt(String key, int defaultvalue) {
        Property val = getProperty(key);
        return (val == null || !val.isInt() ? defaultvalue : val.toInt());
    }

    public int getInt(String key, int defaultvalue, int min) {
        return limit(getInt(key, defaultvalue), min);
    }

    public int getInt(String key, int defaultvalue, int min, int max) {
        return limit(getInt(key, defaultvalue), min, max);
    }

    private static int limit(int val, int min) {
        return (val < min ? min : val);
    }

    private static int limit(int val, int min, int max) {
        return (val > max ? max : (val < min ? min : val));
    }

    private static float limit(float val, float min, float max) {
        return (val > max ? max : (val < min ? min : val));
    }

    private static float limit(float val, float min) {
        return (val < min ? min : val);
    }

    public float getFloat(String key, float defaultvalue) {
        Property val = getProperty(key);
        return (val == null || !val.isFloat() ? defaultvalue : val.toFloat());
    }

    public float getFloat(String key, float defaultvalue, float min) {
        return limit(getFloat(key, defaultvalue), min);
    }

    public float getFloat(String key, float defaultvalue, float min, float max) {
        return limit(getFloat(key, defaultvalue), min, max);
    }
    
    public List<Integer> getIntList(String key, String defaultvalue) {
        List<Integer> rt = new ArrayList<Integer>();
        Property property = getProperty(key);
        String value = property == null ? defaultvalue : property.getValue();
        if(value == null || value.equals("")) return rt;
        
        for(String item : value.split(","))
            if(item != null && !item.trim().equals("")) {
                try { rt.add(Integer.parseInt(item)); } catch(NumberFormatException e) {}
            }
        
        return rt;
    }
    
    public List<Integer> getIntList(String key, Integer... defaultvalue) {
        return getIntList(key, Arrays.asList(defaultvalue));
    }
    
    public List<Integer> getIntList(String key, List<Integer> defaultvalue) {
        String property = getProperty(key) == null ? null : getProperty(key).getValue();
        if(property == null || property.trim().equals("")) return defaultvalue;
        
        List<Integer> rt = null;
        if(defaultvalue == null) rt = new ArrayList<Integer>();
        else {
            // Object recycling.
            rt = defaultvalue;
            rt.clear();
        }
        
        for(String item : property.split(","))
            if(item != null && !item.trim().equals("")) {
                try { rt.add(Integer.parseInt(item)); } catch(NumberFormatException e) {}
            }
        
        return rt;
    }

    public String getProperty(String property, String defaultvalue) {
        Property p = this.getProperty(property);
        if(p != null) return p.getValue();
        else return defaultvalue;
    }
}
