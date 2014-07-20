package com.zones.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.zones.Zones;
import com.zones.model.types.ZoneNormal;

public class Test {
    
    public Zones plugin;
    public Player player;
    public ZoneNormal zone;
    public int cnt = 0;
    
    public class expect {
        private Object val;
        private boolean invert = false;
        public expect(Object val) {
            this.val = val;
        }
        
        public expect not() {
            invert = !invert;
            return this;
        }
        
        public expect equal(Object other) {
            if(invert) {
                if((val == null && other == null) || (val != null && val.equals(other))) {
                    throw new RuntimeException(val + " should not equal " + other);
                }
            } else {
                if((val == null && other != null) || (val != null && !val.equals(other))) {
                    throw new RuntimeException(val + " should equal " + other);
                }
            }
            cnt += 1;
            return this;
        }
    }
    
    public Test(Zones plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    
    public expect Expect(Object val) {
        return new expect(val);
    }
    
    public List<Method> getTestMethods() {
        List<Method> l = new ArrayList<>();
        for(Method method : this.getClass().getDeclaredMethods()) {
            if(method.isAnnotationPresent(testFunction.class)) {
                l.add(method);
            }
        }
        return l;
    }
}
