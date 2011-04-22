package com.zones.model.settings;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.CreatureType;

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
    };
    
    public abstract String serialize(Object data);
    public abstract Object unSerialize(String serializedData);
    
    public static final String unEscape(String str) {
        return str.replace("$1", ",").replace("$2", ";");
    }
    
    public static final String escape(String str) {
        return str.replace(",", "$1").replace(";" , "$2");
    }
}
