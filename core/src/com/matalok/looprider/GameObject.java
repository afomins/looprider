//------------------------------------------------------------------------------
package com.matalok.looprider;

//------------------------------------------------------------------------------
import java.util.EnumSet;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

//------------------------------------------------------------------------------
public class GameObject {
    //**************************************************************************
    // Ctrl
    //**************************************************************************
    public enum Ctrl {
        //----------------------------------------------------------------------
        FORWARD, BACKWARD, LEFT, RIGHT, RIGHT_STEP;
    }

    //**************************************************************************
    // GameObject
    //**************************************************************************
    protected Body body;
    protected Vector2 size;
    protected boolean is_visible;

    //--------------------------------------------------------------------------
    public GameObject() {
        is_visible = true;
        size = new Vector2();
    }

    //--------------------------------------------------------------------------
    public boolean IsVisible() {
        return is_visible;
    }

    //--------------------------------------------------------------------------
    public void SetVisible(boolean value) {
        is_visible = value;
    }

    //--------------------------------------------------------------------------
    public Body GetBody() {
        return body;
    }

    //--------------------------------------------------------------------------
    public void DestroyBody(World world) {
        world.destroyBody(body);
    }

    //--------------------------------------------------------------------------
    public void Update(EnumSet<GameObjectWheel.Ctrl> ctrl) {};
    public void Render(ShapeRenderer r) {};
}
