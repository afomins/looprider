//------------------------------------------------------------------------------
package com.matalok.looprider;

//------------------------------------------------------------------------------
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

//------------------------------------------------------------------------------
public class ConfigParser {
    //**************************************************************************
    // ConfigParser
    //**************************************************************************
    private Object inst;
    private String[] field_names;
    private Map<String, Field> field_map;

    //------------------------------------------------------------------------------
    public ConfigParser(Object inst) {
        this.inst = inst;

        // Field map
        field_map = new LinkedHashMap<String, Field>();
        for(Field f : inst.getClass().getFields()) {
            if(Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            field_map.put(f.getName(), f);
        }

        // Field names
        field_names = new String[field_map.size()];
        int idx = 0;
        for(Map.Entry<String, Field> e : field_map.entrySet()) {
            field_names[idx++] = e.getKey();
        }
    }

    //------------------------------------------------------------------------------
    public String[] GetFieldNames() {
        return field_names;
    }

    //------------------------------------------------------------------------------
    public Class<?> GetType(String name) {
        return field_map.get(name).getType();
    }

    //------------------------------------------------------------------------------
    public float GetValueF(String name) {
        try {
            return (Float)field_map.get(name).get(inst);
        } catch(Exception ex) {
            return 0.0f;
        }
    }

    //------------------------------------------------------------------------------
    public float SetValueF(String name, float value) {
        try {
            field_map.get(name).setFloat(inst, value);
        } catch(Exception ex) { }
        return value;
    }

    //------------------------------------------------------------------------------
    public long GetValueL(String name) {
        try {
            return (Long)field_map.get(name).get(inst);
        } catch(Exception ex) {
            return 0;
        }
    }

    //------------------------------------------------------------------------------
    public long SetValueL(String name, long value) {
        try {
            field_map.get(name).setLong(inst, value);
        } catch(Exception ex) { }
        return value;
    }

    //------------------------------------------------------------------------------
    public String GetValueS(String name) {
        try {
            return (String)field_map.get(name).get(inst);
        } catch(Exception ex) {
            return "fuck";
        }
    }

    //------------------------------------------------------------------------------
    public String SetValueS(String name, String value) {
        try {
            field_map.get(name).set(inst, value);
        } catch(Exception ex) { }
        return value;
    }

    //------------------------------------------------------------------------------
    public boolean GetValueB(String name) {
        try {
            return (Boolean)field_map.get(name).get(inst);
        } catch(Exception ex) {
            return false;
        }
    }

    //------------------------------------------------------------------------------
    public boolean SetValueB(String name, boolean value) {
        try {
            field_map.get(name).setBoolean(inst, value);
        } catch(Exception ex) { }
        return value;
    }

    //------------------------------------------------------------------------------
    public String GetValue(String name) {
        try {
            Field field = field_map.get(name);
            Object val = field.get(inst);
            return (field.getType() == Float.TYPE) ? 
              String.format("%.2f", (Float)val) : val.toString();

        } catch(Exception ex) {
            return "fuck";
        }
    }
}
