package com.hyper.collision;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import com.hyper.world.World;

public class CircularHitbox implements Hitbox {
	private Vector2f position;
	private float radius;

	public CircularHitbox(Vector2fc position, float radius) {
		this.position = new Vector2f(position);
		this.radius = radius;
	}

	public void translate(Vector2f translation) {
		this.position.add(translation);
	}

	@Override
	public Vector2f getPosition() {
		return position;
	}

	public float getRadius() {
		return radius;
	}

	@Override
	public boolean isInside(Vector2f position) {
		return this.position.distanceSquared(position) <= this.radius*this.radius;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	@Override
	public final boolean correctPosition(World worldIn) {
		if(this.position.x < this.radius-1) {
			this.position.x = this.radius-1;
			return true;
		}

		if(this.position.x > worldIn.getWidth()*2-this.radius-1) {
			this.position.x = worldIn.getWidth()*2-this.radius-1;
			return true;
		}

		if(this.position.y > -this.radius+1) {
			this.position.y = -this.radius+1;
			return true;
		}

		if(this.position.y < -worldIn.getHeight()*2+this.radius+1) {
			this.position.y = -worldIn.getHeight()*2+this.radius+1;
			return true;
		}

		return false;
	}
}