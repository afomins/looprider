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
