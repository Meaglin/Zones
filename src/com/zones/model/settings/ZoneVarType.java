package com.zones.model.settings;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import com.meaglin.json.JSONArray;
import com.zones.util.JSONUtil;

public enum ZoneVarType {
    
    BOOLEAN {
        @Override
        public Object serialize(Object value) {
            return Boolean.parseBoolean((String) value);
        }
    },
    INTEGER {
        @Override
        public Object serialize(Object value) {
            return Integer.parseInt((String) value);
        }
    },
    DOUBLE {
        @Override
        public Object serialize(Object value) {
            return Double.parseDouble((String) value);
        }
    },
    LONG {
        @Override
        public Object serialize(Object value) {
            return Long.parseLong((String) value);
        }
    },
    STRING,
    LOCATION {
        @Override
        public Object serialize(Object value) {
            Location loc = (Location) value;
            return JSONUtil.saveLocation(loc);
        }
    },
    MATERIAL {
        @Override
        public Object serialize(Object value) {
            return Material.valueOf((String) value).name();
        }
    },
    ENTITY {
        @Override
        public Object serialize(Object value) {
            return EntityType.valueOf((String) value).name();
        }
    },
    ENTITYLIST(ZoneVarType.ENTITY),
    INTEGERLIST(ZoneVarType.INTEGER),
    MATERIALLIST(ZoneVarType.MATERIAL),
    STRINGLIST(ZoneVarType.STRING),
    MATERIALMAP;
    
    
    private ZoneVarType listType;
    
    private ZoneVarType() {
        
    }
    
    private ZoneVarType(ZoneVarType listtype) {
        this.listType = listtype;
    }
    
    public Object serialize(Object value) {
        if(this.isList()) {
            JSONArray arr = new JSONArray();
            for(String part : ((String) value).split(",")) {
                arr.put(getListType().serialize(part));
            }
        }
        return value;
    }
    
    public boolean isList() {
        return listType != null;
    }
    
    public ZoneVarType getListType() {
        return listType;
    }
}
