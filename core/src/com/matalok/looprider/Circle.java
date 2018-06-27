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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

//------------------------------------------------------------------------------
public class Circle {
    //**************************************************************************
    // Quarter
    //**************************************************************************
    public static class Quarter {
        //----------------------------------------------------------------------
        public Vector2[] vertices;

        //----------------------------------------------------------------------
        public Quarter(int segment_num) {
            // Generate 1st quarter vertices for circle with unit radius
            vertices = new Vector2[segment_num + 1];
            float segment_step = (MathUtils.PI / 2.0f) / segment_num;
            for(int i = 0; i < vertices.length; i++) {
                float angle = i * segment_step; 
                vertices[i] = 
                  new Vector2(MathUtils.cos(angle), MathUtils.sin(angle));
            }
        }
    }

    //**************************************************************************
    // STATIC
    //**************************************************************************
    private static final float min_distance = 0.05f;

    //**************************************************************************
    // Circle
    //**************************************************************************
    public float[] vertices;
    public Vector2 offset;
    public float radius;

    //--------------------------------------------------------------------------
    public Circle(Quarter q, float radius, float offset_x, float offset_y) {
        Vector2 offset = new Vector2();
        int vidx = 0;

        // Box2D will crash if neighbor vertices are too close,
        // ensure minimal distance between them
        radius = (radius < min_distance) ? min_distance : radius;
        offset_x = (offset_x > -min_distance && offset_x < min_distance) ?
          min_distance * (offset_x >= 0 ? 1 : -1) : offset_x;
        offset_y = (offset_y > -min_distance && offset_y < min_distance) ?
          min_distance * (offset_y >= 0 ? 1 : -1) : offset_y;

        this.offset = new Vector2(offset_x, offset_y);
        this.radius = radius;

        // Build array of vertices to form full circle from Q1
        //       ^ y     
        //       |       
        //   Q2  |  Q1   
        //       |      x
        // ------+------>
        //   Q3  |  Q4   
        //       |       
        //       |       
        Vector2 v = new Vector2();
        vertices = new float[q.vertices.length * 4 * 2];
        for(int qidx = 1; qidx <= 4; qidx++) {
            for(Vector2 qv : q.vertices) {
                // Copy raw vertex from template and apply radius
                v.set(qv).scl(radius);

                // Calculate rotation&translation per quarter
                float rot = 0.0f;
                switch(qidx) {
                case 1: { rot = 000.0f; offset.set(+offset_x, +offset_y); } break;
                case 2: { rot = 090.0f; offset.set(-offset_x, +offset_y); } break;
                case 3: { rot = 180.0f; offset.set(-offset_x, -offset_y); } break;
                case 4: { rot = 270.0f; offset.set(+offset_x, -offset_y); } break;
                }

                // Rotate&translate 
                if(rot > 0.0f) {
                    v.rotate(rot);
                }
                v.add(offset);

                // Save
                vertices[vidx++] = v.x;
                vertices[vidx++] = v.y;
            }
        }
    }
}
