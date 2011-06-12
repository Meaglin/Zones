package com.zones.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * 
 * @author Meaglin
 *
 */
public final class Properties extends java.util.Properties {
    private static final long serialVersionUID = 1L;

    private static Logger     log              = Logger.getLogger(Properties.class.getName());
    private boolean missingProperties          = false;
    public Properties() {
    }

    public Properties(String name) throws IOException {
        load(new FileInputStream(name));
    }

    public Properties(File file) throws IOException {
        load(new FileInputStream(file));
    }

    public Properties(InputStream inStream) throws IOException {
        load(inStream);
    }

    public Properties(Reader reader) throws IOException {
        load(reader);
    }

    public void load(String name) throws IOException {
        load(new FileInputStream(name));
    }

    public void load(File file) throws IOException {
        load(new FileInputStream(file));
    }

    @Override
    public void load(InputStream inStream) throws IOException {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(inStream, Charset.defaultCharset());
            super.load(reader);
        } finally {
            inStream.close();
            if (reader != null)
                reader.close();
        }
    }

    @Override
    public void load(Reader reader) throws IOException {
        try {
            super.load(reader);
        } finally {
            reader.close();
        }
    }

    @Override
    public String getProperty(String key) {
        String property = super.getProperty(key);

        if (property == null) {
            log.info("Properties: Missing property for key - " + key);
            missingProperties = true;
            return null;
        }

        return property.trim();
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String property = super.getProperty(key, defaultValue);

        if (property == null) {
            log.warning("Properties: Missing defaultValue for key - " + key);

            return null;
        }

        return property.trim();
    }

    public boolean isMissingProperties() {
        return missingProperties;
    }
    
    public boolean getBool(String key, String defaultvalue) {
        return Boolean.parseBoolean(getProperty(key, defaultvalue));
    }

    public boolean getBool(String key, boolean defaultvalue) {
        String val = getProperty(key);
        return (val == null ? defaultvalue : Boolean.parseBoolean(val));
    }

    public int getInt(String key, String defaultvalue) {
        return Integer.parseInt(getProperty(key, defaultvalue));
    }

    public int getInt(String key, int defaultvalue) {
        String val = getProperty(key);
        return (val == null ? defaultvalue : Integer.parseInt(val));
    }

    public int getInt(String key, String defaultvalue, int min) {
        return limit(getInt(key, defaultvalue), min);
    }

    public int getInt(String key, String defaultvalue, int min, int max) {
        return limit(getInt(key, defaultvalue), min, max);
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

    public float getFloat(String key, String defaultvalue) {
        return Float.parseFloat(getProperty(key, defaultvalue));
    }

    public float getFloat(String key, float defaultvalue) {
        String val = getProperty(key);
        return (val == null ? defaultvalue : Float.parseFloat(val));
    }

    public float getFloat(String key, String defaultvalue, float min) {
        return limit(getFloat(key, defaultvalue), min);
    }

    public float getFloat(String key, String defaultvalue, float min, float max) {
        return limit(getFloat(key, defaultvalue), min, max);
    }

    public float getFloat(String key, float defaultvalue, float min) {
        return limit(getFloat(key, defaultvalue), min);
    }

    public float getFloat(String key, float defaultvalue, float min, float max) {
        return limit(getFloat(key, defaultvalue), min, max);
    }
    
    public List<Integer> getIntList(String key, String defaultvalue) {
        List<Integer> rt = new ArrayList<Integer>();
        String property = getProperty(key, defaultvalue);
        if(property == null || property.trim().equals("")) return rt;
        
        for(String item : property.split(","))
            if(item != null && !item.trim().equals("")) {
                try { rt.add(Integer.parseInt(item)); } catch(NumberFormatException e) {}
            }
        
        return rt;
    }
    
    public List<Integer> getIntList(String key, Integer... defaultvalue) {
        return getIntList(key, Arrays.asList(defaultvalue));
    }
    
    public List<Integer> getIntList(String key, List<Integer> defaultvalue) {
        String property = getProperty(key);
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
}
