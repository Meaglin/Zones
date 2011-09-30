package com.zones.model.settings;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.CreatureType;

import com.zones.model.ZoneVertice;
import com.zones.util.Point;

public enum Serializer {
    INTEGER {
        @Override
        public String serialize(Object data) {
            if(data != null && data instanceof Integer) {
                return ((Integer)data).toString();
            }
            return null;
        }

        @Override
        public Object unSerialize(String serializedData) {
            try {
                return Integer.parseInt(serializedData);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        @Override
        public Class<? extends Object> getType() {
            return Integer.class;
        }
    },
    BOOLEAN {
        @Override
        public String serialize(Object data) {
            if(data != null && data instanceof Boolean) {
                return ((Boolean)data).toString();
            } else {
                return null;
            }
        }

        @Override
        public Object unSerialize(String serializedData) {
            return Boolean.parseBoolean(serializedData);
        }
        
        @Override
        public Class<? extends Object> getType() {
            return Boolean.class;
        }
    },
    STRING {
        @Override
        public String serialize(Object data) {
            if(data != null && data instanceof String) {
                return escape((String)data);
            } else {
                return null;
            }
        }

        @Override
        public Object unSerialize(String serializedData) {
            return unEscape(serializedData);
        }
        
        @Override
        public Class<? extends Object> getType() {
            return String.class;
        }
    },
    INTEGERLIST {
        @Override
        public String serialize(Object data) {
            if(data != null && data instanceof List<?>) {
                
                List<?> list =  ( List<?> ) data;
                if(!list.isEmpty() && list.get(0) instanceof Integer) {
                    String rt = "";
                    for(Object i : list)
                        rt += ((Integer)i) + ",";
                    
                    rt = rt.substring(0, rt.length()-1);
                    
                    return escape(rt);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        public Object unSerialize(String serializedData) {
            List<Integer> list = new ArrayList<Integer>();
            for(String i : unEscape(serializedData).split(",")) {
                try {
                        int x = Integer.parseInt(i);
                        list.add(x);
                } catch(NumberFormatException e) {}
            }
            if(list.isEmpty()) return null;
            return list;
        }
        
        @Override
        public Class<? extends Object> getType() {
            return List.class;
        }
        
        @Override
        public Class<? extends Object> getListType() {
            return Integer.class;
        }
    },
    CREATURELIST {
        @Override
        public String serialize(Object data) {
            if(data != null && data instanceof List<?>) {
                
                List<?> list =  ( List<?> ) data;
                if(!list.isEmpty() && list.get(0) instanceof CreatureType) {
                    String rt = "";
                    for(Object i : list)
                        rt += ((CreatureType)i).getName() + ",";
                    
                    rt = rt.substring(0, rt.length()-1);
                    
                    return escape(rt);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        public Object unSerialize(String serializedData) {
            List<CreatureType> list = new ArrayList<CreatureType>();
            for(String i : unEscape(serializedData).split(",")) {
                CreatureType t = CreatureType.fromName(i); 
                if(t != null)list.add(t);
            }
            if(list.isEmpty()) return null;
            return list;
        }
        
        @Override
        public Class<? extends Object> getType() {
            return List.class;
        }
        
        @Override
        public Class<? extends Object> getListType() {
            return CreatureType.class;
        }
    },
    
    ZONEVERTICE {

        @Override
        public String serialize(Object data) {
            if(data != null && data instanceof ZoneVertice) {
                return "(" + ((ZoneVertice)data).getX() + ":" + ((ZoneVertice)data).getY() + ")";
            } return null;
        }

        @Override
        public Object unSerialize(String serializedData) {
            ZoneVertice z = null;
            String[] split = serializedData.replace("(","").replace(")", "").split(":");
            try {
                z = new ZoneVertice(Integer.parseInt(split[0]),Integer.parseInt(split[1]));
            } catch (NumberFormatException e) { return null; }
            catch(ArrayIndexOutOfBoundsException e) { return null; }
            
            return z;
        }
        
        @Override
        public Class<? extends Object> getType() {
            return ZoneVertice.class;
        }
    },
    
    LOCATION {
        @Override
        public String serialize(Object data) {
            if(data != null && data instanceof Point) {
                return "(" + ((Point)data).getX() + ":" + ((Point)data).getY() + ":" + ((Point)data).getZ() + ")";
            } return null;
        }

        @Override
        public Object unSerialize(String serializedData) {
            Point p = null;
            String[] split = serializedData.replace("(","").replace(")", "").split(":");
            try {
                p = new Point(Integer.parseInt(split[0]),Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            } catch (NumberFormatException e) { return null; }
            catch(ArrayIndexOutOfBoundsException e) { return null; }
            
            return p;
        }
        
        @Override
        public Class<? extends Object> getType() {
            return Point.class;
        }
    };
    
    
    
    public abstract String serialize(Object data);
    public abstract Object unSerialize(String serializedData);
    public abstract Class<? extends Object> getType();
    public Class<? extends Object> getListType() {
        return null;
    }
    
    
    public static final String unEscape(String str) {
        return str.replace("$1", ",").replace("$2", ";");
    }
    
    public static final String escape(String str) {
        return str.replace(",", "$1").replace(";" , "$2");
    }
}
