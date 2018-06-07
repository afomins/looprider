//------------------------------------------------------------------------------
package com.matalok.looprider;

//------------------------------------------------------------------------------
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

//------------------------------------------------------------------------------
public class Config {
    //**************************************************************************
    // STATIC
    //**************************************************************************
    public static final String version = "v0.4.0";
    public static final String file_name = "config.boot";
    public static Config inst = Config.Load();
    public static Config inst_default = new Config();
    public static ConfigParser parser = new ConfigParser(inst);
    public static ConfigEditor editor = new ConfigEditor(parser);

    //--------------------------------------------------------------------------
    public static boolean Save() {
        return Save(inst);
    }

    //--------------------------------------------------------------------------
    public static boolean Save(Config cfg) {
        FileHandle h_file = Gdx.files.local(file_name);
        if(h_file == null) {
            return false;
        }

        try {
            h_file.writeString(GsonUtils.Serialize(cfg, true), false);
            return true;

        } catch(Exception ex) {
            Utils.Log("Failed to save config");
            return false;
        }
    }

    //--------------------------------------------------------------------------
    public static Config Load() {
        Config cfg = null;
        FileHandle h_file = Gdx.files.local(file_name);

        // Try reading existing config
        if(h_file != null && h_file.exists()) {
            Utils.Log("Loading existing config");
            cfg = (Config)GsonUtils.Deserialize(h_file.readString(), Config.class);
        }

        // Create & save default config
        if(cfg == null) {
            Utils.Log("Creating default config");
            Save(cfg = LoadDefault());
        }
        return cfg;
    }

    //--------------------------------------------------------------------------
    public static Config LoadDefault() {
        return new Config();
    }

    //**************************************************************************
    // Config
    //**************************************************************************

    // Generic
    public String  gen_version                  = Config.version;
    public boolean gen_show_dbg                 = false;
    public long    gen_drive_timeout            = 300;

    // Track
    public float track_width                    = 50.0f;
    public float track_height                   = 80.0f;
    public float track_inner_offset_x           = 18.0f;
    public float track_inner_offset_y           = 18.0f;
    public float track_outer_offset_x           = 5.0f;
    public float track_outer_offset_y           = 5.0f;

    // Wheel
    public float wheel_width                     = 2.0f;
    public float wheel_height                    = 3.0f;
    public float wheen_center_x                  = 0.5f;
    public float wheen_center_y                  = -0.4f;
    public float wheel_mass                      = 0.8f;
    public float wheel_torque                    = 28.0f;
    public float wheel_traction                  = 1.0f;
    public float wheel_angular_resistance        = -0.11f;
    public float wheel_forward_resistance        = -0.3f;
    public float wheel_max_forward_speed         = 32.0f;
    public float wheel_max_backward_speed        = -20.0f;
    public float wheel_max_drive_force           = 34.0f;
    public float wheel_max_lateral_impulse       = 0.96f;

    // Stats
    public long stats_score_max                 = 0;
}
