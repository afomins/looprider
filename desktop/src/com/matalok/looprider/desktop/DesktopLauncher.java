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
package com.matalok.looprider.desktop;

//------------------------------------------------------------------------------
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.matalok.looprider.Config;
import com.matalok.looprider.Main;

//------------------------------------------------------------------------------
public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = 
          new LwjglApplicationConfiguration();
        config.width = 320;//480;//320;
        config.height = 480;//800;//480;
        config.vSyncEnabled = true;
        config.foregroundFPS = 0;
        config.title = "Loop rider " + Config.version + " | afomins@gmail.com";
        new LwjglApplication(new Main(), config);
    }
}
