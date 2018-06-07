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
        config.width = 480;//320;
        config.height = 800;//480;
        config.vSyncEnabled = false;
        config.foregroundFPS = 0;
        config.title = "Looprider " + Config.version + " | afomins@gmail.com";
        new LwjglApplication(new Main(), config);
    }
}
