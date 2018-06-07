//------------------------------------------------------------------------------
package com.matalok.looprider;

//------------------------------------------------------------------------------
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;

//------------------------------------------------------------------------------
public class GameObjectBorder 
  extends GameObject {
    //**************************************************************************
    // GameObjectBorder
    //**************************************************************************
    private float[] vertices;

    //--------------------------------------------------------------------------
    protected GameObjectBorder(World world, float x, float y, float[] vertices) {
        BodyDef def = new BodyDef();
        def.type = BodyType.StaticBody;
        def.position.set(x, y);
        body = world.createBody(def);

        ChainShape shape = new ChainShape();
        shape.createLoop(vertices);
        body.createFixture(shape, 1.0f);
        body.setUserData(this);
        shape.dispose();

        this.vertices = vertices;
    }

    //**************************************************************************
    // GameObjectBorder
    //**************************************************************************
    @Override public void Render(ShapeRenderer r) {
        Vector2 pos = body.getPosition();
        r.translate(pos.x, pos.y, 0);

        Gdx.gl.glLineWidth(10);
        r.begin(ShapeType.Line);
            r.setColor(Color.ORANGE);
            r.polyline(vertices);
            r.line(vertices[0], vertices[1], 
              vertices[vertices.length - 2], vertices[vertices.length - 1]);
        r.end();

        Gdx.gl.glLineWidth(3);
        r.begin(ShapeType.Line);
            r.getColor().mul(0.8f);
            r.polyline(vertices);
            r.line(vertices[0], vertices[1], 
              vertices[vertices.length - 2], vertices[vertices.length - 1]);
        r.end();
        Gdx.gl.glLineWidth(1);
    }
}
