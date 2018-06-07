//------------------------------------------------------------------------------
package com.matalok.looprider;

//------------------------------------------------------------------------------
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

//------------------------------------------------------------------------------
public class Gui 
  extends ApplicationAdapter {
    //**************************************************************************
    // Screen
    //**************************************************************************
    public static abstract class Screen 
      extends VisTable {
        //----------------------------------------------------------------------
        private int align;

        //----------------------------------------------------------------------
        public Screen(int align) {
            this.align = align;
        }

        //----------------------------------------------------------------------
        public int GetScreenAlign() {
            return align;
        }

        //----------------------------------------------------------------------
        public abstract Screen UpdateScreen();
    }

    //**************************************************************************
    // ScreenConfig
    //**************************************************************************
    public static class ScreenConfig 
      extends Screen {
        //----------------------------------------------------------------------
        private Map<String, VisLabel[]> cfg;

        //----------------------------------------------------------------------
        public ScreenConfig() {
            super(Align.topLeft);

            cfg = new HashMap<String, VisLabel[]>();
            for(String name : Config.parser.GetFieldNames()) {
                VisLabel[] labels = new VisLabel[] {
                  new VisLabel(name), new VisLabel()};
                cfg.put(name, labels);

                add(labels[0]).align(Align.left);
                add(labels[1]).padLeft(5).align(Align.left).row();
            }
        }

        //----------------------------------------------------------------------
        @Override public Screen UpdateScreen() {
            for(Entry<String, VisLabel[]> e : cfg.entrySet()) {
                String name = e.getKey();
                VisLabel[] label = e.getValue();
                String value = Config.parser.GetValue(name);
                Color c = Color.WHITE;
                if(name.equals(Config.editor.GetSelectedName())) {
                    value += "  <<<";
                    c = Color.YELLOW;
                }

                label[1].setText(value);
                for(VisLabel l : label) {
                    l.setColor(c);
                }
            }
            return this;
        }
    };

    //**************************************************************************
    // ScreenGame
    //**************************************************************************
    public static class ScreenGame
      extends Screen {
        //----------------------------------------------------------------------
        private VisLabel score, score_max;

        //----------------------------------------------------------------------
        public ScreenGame() {
            super(Align.center);
            row();
            score = add(new VisLabel()).getActor();
            score.setFontScale(2.0f);

            row();
            score_max = add(new VisLabel()).getActor();
            score_max.setFontScale(2.0f);
        }

        //----------------------------------------------------------------------
        @Override public Screen UpdateScreen() {
            score.setText(Integer.toString(Main.stats.score));
            score_max.setText(Long.toString(Main.stats.score_max));
            return this;
        }
    };

    //**************************************************************************
    // Gui
    //**************************************************************************
    private Main.Stats stats;
    private Stage stage;
    private VisTable root;
    private VisLabel footer;
    private Screen screen_current, screen_config, screen_game;

    //--------------------------------------------------------------------------
    public Gui(Main.Stats stats) {
        this.stats = stats;
    }

    //--------------------------------------------------------------------------
    public void ToggleScreen() {
        screen_current = 
          (screen_current == null) ? screen_game :
          (screen_current == screen_game) ? screen_config : 
          (screen_current == screen_config) ? screen_game : null;
        if(screen_current == null) {
            return;
        }

        Screen screen = GetScreen().UpdateScreen();
        int screen_align = screen.GetScreenAlign();

        // Header
        root.clear();
        if((screen_align & Align.center) != 0) {
            root.add().expand().fill();
        }

        // Screen
        root.row();
        root.add(screen).align(screen_align);

        // Filler
        root.row();
        root.add().expand().fill();

        // Footer
        root.row();
        root.add(new VisLabel("SPACE - turn right"))
        .align(Align.left);
        root.row();
        root.add(new VisLabel("ENTER - toggle \"free-ride\" mode"))
          .align(Align.left);
        root.row();
        root.add(new VisLabel("F1        - toggle config window"))
          .align(Align.left);
        root.row();
        footer = root.add(new VisLabel())
          .align(Align.bottomRight).getActor();
        root.row();
    }

    //--------------------------------------------------------------------------
    public Screen GetScreen() {
        return screen_current;
    }

    //**************************************************************************
    // ApplicationAdapter
    //**************************************************************************
    @Override public void create() {
        // Init stage
        stage = new Stage(new ScreenViewport());
//        stage.setDebugAll(true);
        VisUI.load();

        // Create root table
        stage.addActor(root = new VisTable());
        root.setFillParent(true);

        // Create screens
        screen_game = new ScreenGame();
        screen_config = new ScreenConfig();
        ToggleScreen();
    }

    //--------------------------------------------------------------------------
    @Override public void render() {
        if(footer != null) {
            footer.setText(
              String.format("uptime=%.1f speed=%d fps=%d", 
                stats.lap_duration, stats.speed, stats.fps));
        }
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    //--------------------------------------------------------------------------
    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    //--------------------------------------------------------------------------
    @Override public void dispose() {
        VisUI.dispose();
        stage.dispose();
    }
}
