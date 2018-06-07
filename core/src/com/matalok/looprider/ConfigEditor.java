//------------------------------------------------------------------------------
package com.matalok.looprider;

//------------------------------------------------------------------------------
import java.util.HashMap;
import java.util.Map;

//------------------------------------------------------------------------------
public class ConfigEditor {
    //**************************************************************************
    // ConfigEditor
    //**************************************************************************
    private ConfigParser parser;
    private Map<String, Object> step_map;
    private int selected_idx;

    //--------------------------------------------------------------------------
    public ConfigEditor(ConfigParser parser) {
        this.parser = parser;

        // Find optimal step per field
        step_map = new HashMap<String, Object>();
        for(String name : parser.GetFieldNames()) {
            Class<?> type = parser.GetType(name);
            Object step = null;
            if(type == Float.TYPE) {
                float val = Math.abs(parser.GetValueF(name));
                step = (Float)(
                  (val <= 2.0f) ? 0.01f :
                  (val <= 10.0f) ? 0.1f :
                  (val <= 100.0f) ? 1.0f : 
                  (val <= 1000.0f) ? 10.0f : 25.0f);

            } else if(type == Long.TYPE) {
                long val = Math.abs(parser.GetValueL(name));
                step = (Long)(
                  (val <= 10L) ? 1L :
                  (val <= 100L) ? 5L : 
                  (val <= 1000L) ? 10L : 25L);

            } else if(type == Boolean.TYPE) {
                step = (Boolean)true;
            }
            step_map.put(name, step);
        }
    }

    //--------------------------------------------------------------------------
    public int GetSelectedIdx() {
        return selected_idx;
    }

    //--------------------------------------------------------------------------
    public String GetSelectedName() {
        return parser.GetFieldNames()[selected_idx];
    }

    //--------------------------------------------------------------------------
    public ConfigParser GetParser() {
        return parser;
    }

    //--------------------------------------------------------------------------
    public void Select(int idx) {
        int max_idx = parser.GetFieldNames().length - 1;
        selected_idx = 
          (idx > max_idx) ? 0 :
          (idx < 0)       ? max_idx : idx;
    }

    //--------------------------------------------------------------------------
    public void SelectNext() {
        Select(selected_idx + 1);
    }

    //--------------------------------------------------------------------------
    public void SelectPrev() {
        Select(selected_idx - 1);
    }

    //--------------------------------------------------------------------------
    public void Update(int dir) {
        String name = GetSelectedName();
        Object step = step_map.get(name);
        if(step == null) {
            return;
        }

        Class<?> type = parser.GetType(name);
        if(type == Float.TYPE) {
            parser.SetValueF(name, 
              parser.GetValueF(name) + ((Float)step) * dir);

        } else if(type == Long.TYPE) {
            parser.SetValueL(name, 
              parser.GetValueL(name) + ((Long)step) * dir);

        } else if(type == Boolean.TYPE) {
            parser.SetValueB(name, !parser.GetValueB(name));
        }
    }

    //--------------------------------------------------------------------------
    public void Increase(int step) {
        Update(step);
    }

    //--------------------------------------------------------------------------
    public void Decrease(int step) {
        Update(-step);
    }
}
