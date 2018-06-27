/*
 * Loop rider
 * Copyright (C) 2018 Alex Fomins
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

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
