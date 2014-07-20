package com.zones.model;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

/**
 * 
 * @author Meaglin
 *
 */
public class ZonesAccess {
    
    private static final ZonesAccess[] all;
    private static Map<Character, Integer> charToAccess;
    private static final int size = 64;
    
    static {
        all = new ZonesAccess[size];
        for(int i = 0; i < size; i += 1) {
            all[i] = new ZonesAccess(i);
        }
        charToAccess = new HashMap<>();
        for(Rights r : Rights.values()) {
            charToAccess.put(r.getCode(), r.getFlag());
        }
    }
    
    public static final ZonesAccess ALL = ZonesAccess.factory(size - 1);

    public enum Rights {
        ATTACK(32, 'a', "Attack Entity's"),//
        BUILD(1, 'b', "Build blocks"),//
        DESTROY(2, 'd', "Destroy blocks"),//
        MODIFY(4, 'c', "Chest access"),//
        ENTER(8, 'e', "Enter zone"),//
        HIT(16, 'h', "Hit Entity's"),//
        ALL(63, '*', "Anything & everything");

        private int    flag;
        private char code;
        private String textual;

        private Rights(int flag, char code, String textual) {
            this.flag = flag;
            this.code = code;
            this.textual = textual;
        }

        public int getFlag() {
            return flag;
        }

        public char getCode() {
            return code;
        }

        public String getTextual() {
            return textual;
        }

        public boolean canDo(int rights) {
            return (rights & flag) == flag;
        }
    }

    private int rights = 0;

    private ZonesAccess(int right) {
        rights = right;
    }

    public static ZonesAccess factory(String access) {
        int r = 0;
        for(int i = 0; i < access.length(); i += 1) {
            Integer a = charToAccess.get(access.charAt(i));
            if(a == null) {
                continue;
            }
            r = r | a.intValue();
        }
        return all[r];
    }
    
    public static ZonesAccess factory(int rights) {
        if(rights < 0 || rights >= size) {
            return all[0];
        }
        return all[rights];
    }

    public ZonesAccess merge(ZonesAccess acs) {
        return all[rights | acs.getRights()];
    }

    public int getRights() {
        return rights;
    }

    public boolean canDo(Rights right) {
        return right.canDo(rights);
    }

    public boolean canBuild() {
        return canDo(Rights.BUILD);
    }

    public boolean canDestroy() {
        return canDo(Rights.DESTROY);
    }

    public boolean canModify() {
        return canDo(Rights.MODIFY);
    }

    public boolean canEnter() {
        return canDo(Rights.ENTER);
    }

    public boolean canHit() {
        return canDo(Rights.HIT);
    }
    
    public boolean canAttack() {
        return canDo(Rights.ATTACK);
    }

    public boolean canAll() {
        return canDo(Rights.ALL);
    }

    public boolean canNothing() {
        return (rights & Rights.ALL.flag) == 0;
    }

    @Override
    public String toString() {
        // Short circuit on 'all'
        if (canDo(Rights.ALL)) {
            return Rights.ALL.getCode() + "";
        }
        if (canNothing())
            return "-";
        // Build list of access codes.
        String rights = "";
        for (Rights right : Rights.values())
            if (canDo(right))
                rights += right.getCode();

        return rights;
    }

    public String textual() {

        // Short circuit on 'all'
        if (canDo(Rights.ALL))
            return Rights.ALL.getTextual();
        if (canNothing())
            return "Nothing";

        // Build list of access codes.
        String text = "";
        for (Rights right : Rights.values())
            if (canDo(right))
                text += right.getTextual() + ", ";

        // Remove last comma.
        text = text.substring(0, text.length() - 2);

        // Replace last comma with "and"
        // when there is just 1 item we don't need a "," ;).
        if (text.lastIndexOf(',') < 0)
            return text;
        else
            return text.substring(0, text.lastIndexOf(',')) + " and" + text.substring(text.lastIndexOf(',') + 1, text.length());
    }

    public String toColorCode() {
        String rt = "";

        if (canAttack())
            rt += ChatColor.GREEN.toString();
        else
            rt += ChatColor.RED.toString();
        rt += "A";
        
        if (canBuild())
            rt += ChatColor.GREEN.toString();
        else
            rt += ChatColor.RED.toString();
        rt += "B";

        if (canModify())
            rt += ChatColor.GREEN.toString();
        else
            rt += ChatColor.RED.toString();
        rt += "C";

        if (canDestroy())
            rt += ChatColor.GREEN.toString();
        else
            rt += ChatColor.RED.toString();
        rt += "D";

        if (canEnter())
            rt += ChatColor.GREEN.toString();
        else
            rt += ChatColor.RED.toString();
        rt += "E";

        if (canHit())
            rt += ChatColor.GREEN.toString();
        else
            rt += ChatColor.RED.toString();
        rt += "H";

        rt += ChatColor.WHITE.toString();

        return rt;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof ZonesAccess)) {
            return false;
        }
        
        return ((ZonesAccess) o).getRights() == getRights();
    }
    
    @Override
    public int hashCode() {
        return getRights();
    }
}
