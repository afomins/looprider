//------------------------------------------------------------------------------
package com.matalok.looprider;

//------------------------------------------------------------------------------
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

//------------------------------------------------------------------------------
public class GameObjectFinish 
  extends GameObject {
    //**************************************************************************
    // GameObjectFinish
    //**************************************************************************
    protected GameObjectFinish(World world, float x, float y, float width, 
      float height) {
        // Def
        BodyDef def = new BodyDef();
        def.type = BodyType.StaticBody;
        def.position.set(x, y);
        body = world.createBody(def);

        // Size
        size.set(width * 0.8f, height);

        // Body
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2.0f, height / 2.0f);
        body.createFixture(shape, 1).setSensor(true);
        body.setUserData(this);
        shape.dispose();
    }

    //**************************************************************************
    // GameObject
    //**************************************************************************
    @Override public void Render(ShapeRenderer r) {
        Vector2 pos = body.getPosition();
        r.translate(pos.x - size.x / 2.0f, pos.y - size.y / 2.0f, 0.0f);
        r.begin(ShapeType.Line);
            r.setColor(Color.WHITE);
            r.rect(0.0f, 0.0f, size.x, size.y);
        r.end();
    }
}
