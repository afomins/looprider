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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

//------------------------------------------------------------------------------
public class GameObjectWheel 
  extends GameObject {
    //**************************************************************************
    // STATIC
    //**************************************************************************
    private static final Vector2 forward_dir = Vector2.Y;
    private static final Vector2 right_dir = Vector2.X;

    //**************************************************************************
    // GameObjectWheel
    // 
    // Box2D C++ tutorials - Top-down car physics:
    //   http://www.iforce2d.net/b2dtut/top-down-car
    //
    //**************************************************************************
    public GameObjectWheel(World world, float x, float y) {
        // Def
        BodyDef def = new BodyDef();
        def.type = BodyType.DynamicBody;
        def.position.set(x, y);
        body = world.createBody(def);

        // Size
        float 
          width = Config.inst.wheel_width, 
          height = Config.inst.wheel_height,
          mass = Config.inst.wheel_mass,
          cx = Config.inst.wheen_center_x,
          cy = Config.inst.wheen_center_y;
        size.set(width, height);

        // Body
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2.0f, height / 2.0f, new Vector2(cx, cy), 0.0f);
        body.createFixture(shape, Utils.GetDensityFromMass(mass, width, height));
        body.setUserData(this);
        shape.dispose();
    }

    //--------------------------------------------------------------------------
    public void UpdateFriction() {
        // Lateral linear velocity
        float t = Config.inst.wheel_traction;
        Vector2 impulse = GetLateralVelocity().scl(-body.getMass() * t);
        float impulse_len = impulse.len();
        float max_lateral_impulse = Config.inst.wheel_max_lateral_impulse;
        if(impulse_len > max_lateral_impulse) {
            impulse.scl(max_lateral_impulse / impulse_len);
        }
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);

        // Angular velocity
        body.applyAngularImpulse(Config.inst.wheel_angular_resistance * 
          body.getInertia() * body.getAngularVelocity() * t, true);

        // Forward linear velocity
        Vector2 current_forward_normal = GetForwardVelocity();
        float current_forward_speed = current_forward_normal.len();
        float drag_force_magnitude = 
          Config.inst.wheel_forward_resistance * current_forward_speed;
        body.applyForceToCenter(
          current_forward_normal.nor().scl(drag_force_magnitude * t), true);
    }

    //--------------------------------------------------------------------------
    public void UpdateDrive(EnumSet<GameObjectWheel.Ctrl> ctrl) {
        // Find desired speed
        float desired_speed = 
          ctrl.contains(Ctrl.FORWARD) ? Config.inst.wheel_max_forward_speed : 
          ctrl.contains(Ctrl.BACKWARD) ? Config.inst.wheel_max_backward_speed : 0.0f;
        if(desired_speed == 0.0f) {
            return;
        }

        // Find current speed in forward direction
        Vector2 current_forward_normal = new Vector2(body.getWorldVector(Vector2.Y));
        Vector2 forward_velocity = GetForwardVelocity();
        float current_speed = forward_velocity.dot(current_forward_normal);

        // Apply necessary force
        float max_drive_force = Config.inst.wheel_max_drive_force;
        float force = 
          (desired_speed > current_speed) ? max_drive_force : 
          (desired_speed < current_speed) ? -max_drive_force : 0.0f;
        if(force == 0.0f) {
            return;
        }

        float t = Config.inst.wheel_traction;
        body.applyForceToCenter(current_forward_normal.scl(force * t), true);
    }

    //--------------------------------------------------------------------------
    private void UpdateTurn(EnumSet<GameObjectWheel.Ctrl> ctrl) {
        if(ctrl.contains(Ctrl.RIGHT_STEP)) {
            ctrl.remove(Ctrl.RIGHT_STEP);
            body.applyTorque(-Config.inst.wheel_torque * 4.0f, true);

        } else {
            float desired_torque = 
              ctrl.contains(Ctrl.LEFT) ? Config.inst.wheel_torque :
              ctrl.contains(Ctrl.RIGHT) ? -Config.inst.wheel_torque : 0.0f;
            if(desired_torque == 0.0f) {
                return;
            }
            body.applyTorque(desired_torque, true);
        }
    }

    //--------------------------------------------------------------------------
    private Vector2 GetLinearVelocity(Vector2 dir) {
        Vector2 dir_normal = body.getWorldVector(dir);
        return new Vector2(dir_normal)
          .scl(dir_normal.dot(body.getLinearVelocity()));
    }

    //--------------------------------------------------------------------------
    private Vector2 GetLateralVelocity() {
        return GetLinearVelocity(right_dir);
    }

    //--------------------------------------------------------------------------
    private Vector2 GetForwardVelocity() {
        return GetLinearVelocity(forward_dir);
    }

    //**************************************************************************
    // GameObject
    //**************************************************************************
    @Override public void Update(EnumSet<GameObjectWheel.Ctrl> ctrl) {
        UpdateFriction();
        UpdateTurn(ctrl);
        UpdateDrive(ctrl);
    }

    //--------------------------------------------------------------------------
    @Override public void Render(ShapeRenderer r) {
        Vector2 pos = body.getPosition();
        r.translate(pos.x, pos.y, 0.0f);
        r.rotate(0.0f, 0.0f, 1.0f, body.getAngle() * MathUtils.radiansToDegrees);

        r.begin(ShapeType.Filled);
            r.setColor(Color.YELLOW);
            r.rect(-size.x / 2.0f, -size.y / 2.0f, size.x, size.y);
        r.end();

        Vector2 c = body.getLocalCenter();
        r.begin(ShapeType.Line);
            r.setColor(Color.RED);
            r.line(-size.x / 2.0f, -size.y / 2.0f, c.x, c.y);
            r.line(-size.x / 2.0f, +size.y / 2.0f, c.x, c.y);
            r.line(+size.x / 2.0f, +size.y / 2.0f, c.x, c.y);
            r.line(+size.x / 2.0f, -size.y / 2.0f, c.x, c.y);
        r.end();

        r.begin(ShapeType.Line);
            r.setColor(Color.BLACK);
            r.rect(-size.x / 2.0f, -size.y / 2.0f, size.x, size.y);
        r.end();
    }
}
