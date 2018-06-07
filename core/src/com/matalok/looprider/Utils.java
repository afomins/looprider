//------------------------------------------------------------------------------
package com.matalok.looprider;

//------------------------------------------------------------------------------
import java.util.Calendar;

//------------------------------------------------------------------------------
public class Utils {
    //**************************************************************************
    // STATIC
    //**************************************************************************
    private static long startup_time = 0;

    //**************************************************************************
    // Utils
    //**************************************************************************
    public static void Reset() {
        startup_time = Calendar.getInstance().getTimeInMillis();
    }

    //--------------------------------------------------------------------------
    public static void Log(String fmt, Object... args) {
        System.out.printf(fmt + "\n", (Object[])args);
    }

    //--------------------------------------------------------------------------
    public static long GetMsec() {
        return Calendar.getInstance().getTimeInMillis() - startup_time;
    }

    //--------------------------------------------------------------------------
    public static float GetSec() {
        return GetMsec() / 1000.0f;
    }

    //--------------------------------------------------------------------------
    public static float GetDensityFromMass(float mass, float width, float height) {
        return (mass / (width * height));
    }
}
