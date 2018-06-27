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
