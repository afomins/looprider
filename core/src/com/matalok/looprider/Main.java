//------------------------------------------------------------------------------
package com.matalok.looprider;

//------------------------------------------------------------------------------
import java.util.EnumSet;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

//------------------------------------------------------------------------------
public class Main 
  extends ApplicationAdapter {
    //**************************************************************************
    // STATIC
    //**************************************************************************
    public static Stats stats = null;

    //**************************************************************************
    // GameState
    //**************************************************************************
    public enum GameState {
        //----------------------------------------------------------------------
        IDLE, DRIVE, CRASH;
    };

    //**************************************************************************
    // TurnState
    //**************************************************************************
    public enum TurnState {
        //----------------------------------------------------------------------
        IDLE, TAP, HOLD;
    };

    //**************************************************************************
    // Stats
    //**************************************************************************
    public class Stats {
        //----------------------------------------------------------------------
        public int fps;
        public int speed;
        public float lap_duration;
        public int score;
        public long score_max;
    };

    //**************************************************************************
    // Main
    //**************************************************************************
    private boolean is_free_ride;
    private Gui gui;
    private World world;
    private OrthographicCamera camera;
    private ShapeRenderer shape_renderer;
    private Box2DDebugRenderer debug_renderer;

    private GameState game_state;
    private long game_state_start_time;

    private long turn_button_press_time;
    private TurnState turn_state;

    private Circle.Quarter quarter;
    private EnumSet<GameObjectWheel.Ctrl> ctrl;
    private GameObject[] game_objects;
    private GameObject hero;
    private GameObjectBorder border_inner, border_outer;
    private GameObjectFinish finish_left, finish_right;

    //--------------------------------------------------------------------------
    private void SwitchGameState(GameState new_state) {
        game_state = new_state;
        game_state_start_time = Utils.GetMsec();
        turn_state = TurnState.IDLE;
    }

    //--------------------------------------------------------------------------
    private void HandleTurnState(boolean is_pressed) {
        boolean was_pressed = (turn_button_press_time != -1);
        boolean is_long_press = (was_pressed && 
          (Utils.GetMsec() - turn_button_press_time) > 100);

        if(!is_pressed) {
            turn_button_press_time = -1;
        } else if(turn_button_press_time == -1) {
            turn_button_press_time = Utils.GetMsec();
        }

        TurnState prev_turn_state = turn_state;

        // IDLE
        if(turn_state == TurnState.IDLE) {
            if(was_pressed && !is_pressed) {
                turn_state = TurnState.TAP;

            } else if(was_pressed && is_pressed && is_long_press) {
                turn_state = TurnState.HOLD;
            }

        // Not IDLE
        } else if(!is_pressed) {
            turn_state = TurnState.IDLE;
        }
        if(prev_turn_state != turn_state) {
            Utils.Log("Switching turn-state: %s->%s", prev_turn_state, turn_state);
        }
    }

    //--------------------------------------------------------------------------
    private long GetStateDuration() {
        return Utils.GetMsec() - game_state_start_time; 
    }

    //--------------------------------------------------------------------------
    private void HandleInput() {
        // Reset
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            SwitchGameState(GameState.CRASH);
        }

        switch(game_state) {
        //......................................................................
        case IDLE: {
            // Toggle free ride
            if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                is_free_ride = !is_free_ride;
            }

            // Free ride
            if(is_free_ride) {
                if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
                    ctrl.add(GameObjectWheel.Ctrl.FORWARD);
                }
                if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    ctrl.add(GameObjectWheel.Ctrl.BACKWARD);
                }
                if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    ctrl.add(GameObjectWheel.Ctrl.LEFT);
                }
                if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    ctrl.add(GameObjectWheel.Ctrl.RIGHT);
                }

            // Normal game
            } else { 
                if(Gdx.input.isKeyPressed(Input.Keys.SPACE) || 
                  Gdx.input.isTouched(0)) {
                    SwitchGameState(GameState.DRIVE);
                }
            }
        } break;

        //......................................................................
        case DRIVE: {
            HandleTurnState(Gdx.input.isKeyPressed(Input.Keys.SPACE) || 
              Gdx.input.isTouched(0));

            if(GetStateDuration() > Config.inst.gen_drive_timeout) {
                if(turn_state == TurnState.HOLD) {
                    ctrl.add(GameObjectWheel.Ctrl.RIGHT);
                } else if(turn_state == TurnState.TAP) {
                    ctrl.add(GameObjectWheel.Ctrl.RIGHT_STEP);
                }
            }
        } break;

        //......................................................................
        case CRASH: {
            if(stats.score > stats.score_max) {
                stats.score_max = stats.score;
            }
            stats.score = 0;

            Config.inst.stats_score_max = stats.score_max;
            Config.Save();
        } break;
        }
    }

    //--------------------------------------------------------------------------
    private void HandleGame() {
        switch(game_state) {
        //......................................................................
        case IDLE: {
        } break;

        //......................................................................
        case DRIVE: {
            ctrl.add(GameObjectWheel.Ctrl.FORWARD);
        } break;

        //......................................................................
        case CRASH: {
            Reset(true);
        } break;
        }
    }

    //--------------------------------------------------------------------------
    private float accumulator = 0.0f;
    private float time_step = 1.0f / 60.0f;
    private float prev_time = Utils.GetSec();
    private void HandlePhysics() {
        float cur_time = Utils.GetSec();
        float delta_time = cur_time - prev_time;
        prev_time = cur_time;
        if(delta_time < 0.0f) {
            return;
        }

        float frame_time = Math.min(delta_time, time_step * 10.0f);
        accumulator += frame_time;
        while(accumulator >= time_step) {
            hero.Update(ctrl);
            world.step(time_step, 6+2, 2+2);
            accumulator -= time_step;
        }
    }

    //--------------------------------------------------------------------------
    private void HandleRendering() {
        // GL cleanup
        Color c = Color.GRAY;
        Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render all game objects
        if(game_objects != null) {
            shape_renderer.setProjectionMatrix(camera.combined);
            for(GameObject obj : game_objects) {
                if(obj == null || !obj.IsVisible()) {
                    continue;
                }
                shape_renderer.identity();
                obj.Render(shape_renderer);
            }
        }

        // Debug Box2D
        if(Config.inst.gen_show_dbg) {
            debug_renderer.render(world, camera.combined);
        }
    }

    //--------------------------------------------------------------------------
    private void HandleGui() {
        // Toggle GUI screens
        if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            gui.ToggleScreen();
        }

        // Update config
        boolean update_screen = true,
                reset_game = false;
        Gui.Screen screen = gui.GetScreen();
        if(screen.getClass() == Gui.ScreenConfig.class) {
            // Select next config entry
            if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                Config.editor.SelectNext();

            // Select previous config entry
            } else if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                Config.editor.SelectPrev();

            // Increase current config entry
            } else if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                Config.editor.Increase(1);
                Config.Save();
                reset_game = true;

            // Decrease current config entry
            } else if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                Config.editor.Decrease(1);
                Config.Save();
                reset_game = true;

            // Increase current config entry
            } else if(Gdx.input.isKeyJustPressed(Input.Keys.PAGE_UP)) {
                Config.editor.Increase(10);
                Config.Save();
                reset_game = true;

            // Decrrease current config entry
            } else if(Gdx.input.isKeyJustPressed(Input.Keys.PAGE_DOWN)) {
                Config.editor.Decrease(10);
                Config.Save();
                reset_game = true;

            // No config change
            } else {
                update_screen = false;
            }
        }

        if(update_screen) {
            screen.UpdateScreen();
        }
        if(reset_game) {
            Reset(false);
        }
        gui.render();
    }

    //--------------------------------------------------------------------------
    private Circle CreateCircle(float screen_offset_x, float screen_offset_y) {
        float
          hw = Config.inst.track_width / 2.0f,
          hh = Config.inst.track_height / 2.0f,
          radius = (hw - screen_offset_x) * 0.8f,
          off_x = hw - (screen_offset_x + radius),
          off_y = hh - (screen_offset_y + radius);
        return new Circle(quarter, radius, off_x, off_y);
    }

    //--------------------------------------------------------------------------
    private void Reset(boolean reset_only_wheel) {
        float 
          screen_width = (float)Gdx.graphics.getWidth(),
          screen_height = (float)Gdx.graphics.getHeight(),
          screen_aspect_ratio = (float)screen_height / screen_width,
          track_aspect_ratio = Config.inst.track_height / Config.inst.track_width;

        // TODO: refactor camera to support any resolution

        // Move camera to the center of the track
        camera.viewportWidth = Config.inst.track_width;
        camera.viewportHeight = camera.viewportWidth * screen_aspect_ratio;
        camera.zoom = track_aspect_ratio / screen_aspect_ratio; 
        camera.position.set(
          camera.viewportWidth / 2.0f, camera.viewportHeight / 2.0f, 0.0f);
        camera.update();

        // Create new world
        if(world == null) {
            world = new World(new Vector2(0, 0), true);
            world.setContactListener(new ContactListener() {
                //--------------------------------------------------------------
                public Object TestContact(Contact contact, 
                  Class<?> class_a, Class<?> class_b) {
                    Object a = contact.getFixtureA().getBody().getUserData(),
                           b = contact.getFixtureB().getBody().getUserData();

                    boolean is_hit = 
                      (a != null && b != null) && 
                      ((a.getClass() == class_a && b.getClass() == class_b) ||
                       (a.getClass() == class_b && b.getClass() == class_a));

                    // Return instance of B on hit 
                    return (is_hit) ? (class_b == b.getClass() ? b : a) : null;
                }

                //--------------------------------------------------------------
                @Override public void beginContact(Contact contact) {
                    // Test if wheel hit border
                    if(game_state == GameState.DRIVE) {
                        if(TestContact(contact, GameObjectWheel.class, GameObjectBorder.class) != null) {
                            SwitchGameState(GameState.CRASH);
                            return;
                        }
                    }

                    // Test if wheel hit finish line
                    GameObjectFinish finish = (GameObjectFinish)TestContact(contact, 
                      hero.getClass(), GameObjectFinish.class);
                    if(finish != null && finish.IsVisible()) {
                        stats.score++;

                        // Toggle finish line 
                        finish.SetVisible(false);
                        if(finish == finish_left) {
                            finish_right.SetVisible(true);
                        } else if(finish == finish_right) {
                            finish_left.SetVisible(true);
                        }
                        return;
                    }
                }

                //--------------------------------------------------------------
                @Override public void endContact(Contact contact) { }
                @Override public void preSolve(Contact contact, Manifold oldManifold) { }
                @Override public void postSolve(Contact contact, ContactImpulse impulse) { }
            });

            // Init turn state
            turn_button_press_time = -1;
            turn_state = TurnState.IDLE;

        // Cleanup existing world
        } else {
            // Borders of the track
            if(!reset_only_wheel) {
                border_inner.DestroyBody(world);
                border_outer.DestroyBody(world);
                finish_left.DestroyBody(world);
                finish_right.DestroyBody(world);
            }

            // Hero
            hero.DestroyBody(world);
        }

        // Create border of the track
        float cx = camera.position.x, cy = camera.position.y;
        if(!reset_only_wheel) {
            Utils.Log("=============================");
            Utils.Log("  screen-size     = %.2f:%.2f", screen_width, screen_height);
            Utils.Log("  track-size      = %.2f:%.2f", Config.inst.track_width, Config.inst.track_height);
            Utils.Log("  camera-viewport = %.2f:%.2f", camera.viewportWidth, camera.viewportHeight);
            Utils.Log("  camera-center   = %.2f:%.2f", camera.position.x, camera.position.y);
            Utils.Log("  camera-zoom     = %.2f", camera.zoom);

            // Inner
            border_inner = new GameObjectBorder(world, cx, cy, 
              CreateCircle(Config.inst.track_inner_offset_x, 
                Config.inst.track_inner_offset_y).vertices);

            // Outer
            border_outer = new GameObjectBorder(world, cx, cy, 
              CreateCircle(Config.inst.track_outer_offset_x, 
                Config.inst.track_outer_offset_y).vertices);
        }

        // Create finish lines
        if(!reset_only_wheel) {
            float width = 
              Config.inst.track_inner_offset_x - Config.inst.track_outer_offset_x;
            float height = 1.0f;
            float offset_x = Config.inst.track_outer_offset_x + width / 2.0f;
            float offset_y = 5.0f;

            finish_left = new GameObjectFinish(
              world, offset_x, cy + offset_y, width, height);
            finish_right = new GameObjectFinish(
              world, Config.inst.track_width - offset_x, cy - offset_y, width, height);
        }
        finish_left.SetVisible(true);
        finish_right.SetVisible(false);

        // Create hero
        float 
          pos_x = cx - Config.inst.track_width / 3.5f, 
          pos_y = cy - Config.inst.track_height / 4.0f;
        hero = new GameObjectWheel(world, pos_x, pos_y);

        // Put all game objects to the list
        game_objects = new GameObject[] {border_inner, border_outer, 
          hero, finish_left, finish_right};

        // Become idle after reset
        SwitchGameState(GameState.IDLE);
    }

    //**************************************************************************
    // ApplicationAdapter
    //**************************************************************************
    @Override public void create() {
        Box2D.init();

        Main.stats = new Stats();
        stats.score_max = Config.inst.stats_score_max;

        camera = new OrthographicCamera();
        quarter = new Circle.Quarter(4);
        debug_renderer = new Box2DDebugRenderer();
        shape_renderer = new ShapeRenderer();

        gui = new Gui(stats);
        gui.create();

        ctrl = EnumSet.noneOf(GameObjectWheel.Ctrl.class);

        Utils.Reset();
    }

    //--------------------------------------------------------------------------
    @Override public void render() {
        // Reset wheel controls
        ctrl.remove(GameObjectWheel.Ctrl.LEFT);
        ctrl.remove(GameObjectWheel.Ctrl.RIGHT);
        ctrl.remove(GameObjectWheel.Ctrl.FORWARD);
        ctrl.remove(GameObjectWheel.Ctrl.BACKWARD);

        // Run game handlers
        HandleInput();
        HandleGame();
        HandlePhysics();
        HandleRendering();

        // Update stats
        stats.fps = Gdx.graphics.getFramesPerSecond();
        stats.speed = (int)hero.GetBody().getLinearVelocity().len();
        stats.lap_duration = (game_state != GameState.DRIVE) ? 0.0f :  
          (Utils.GetMsec() - game_state_start_time) / 1000.0f;

        // Run gui handler
        HandleGui();
    }

    //--------------------------------------------------------------------------
    @Override public void resize(int width, int height) {
        Reset(false);
        gui.resize(width, height);
    }

    //--------------------------------------------------------------------------
    @Override public void dispose() {
        world.dispose();
        gui.dispose();
        debug_renderer.dispose();
        shape_renderer.dispose();
    }
}
