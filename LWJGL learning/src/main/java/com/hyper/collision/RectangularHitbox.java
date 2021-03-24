package com.hyper.collision;

import org.joml.Vector2f;

import com.hyper.Utils;
import com.hyper.world.World;

public class RectangularHitbox implements Hitbox {
	private Vector2f start, direction;

	private float width;

	public RectangularHitbox(Vector2f start, Vector2f direction, float width) {
		this.start = start;
		this.direction = direction;
		this.width = width;
	}

	@Override
	public boolean isInside(Vector2f point) {
		return Utils.getDistanceFromPointToLine(point, start, direction) <= width;
	}

	@Override
	public boolean correctPosition(World worldIn) {
		if(worldIn.getWidth()*2 < this.start.x) {
			this.start.x = worldIn.getWidth();
			return true;
		}
		if(this.start.x < 0) {
			this.start.x = 0;
			return true;
		}
		if(this.start.y > 0) {
			this.start.y = 0;
			return true;
		}
		if(this.start.y < -worldIn.getHeight()*2) {
			this.start.y = worldIn.getHeight();
			return true;
		}
		return false;
	}

	@Override
	public Vector2f getPosition() {
		return start;
	}

	@Override
	public void translate(Vector2f motion) {
		this.start.add(motion);
	}

	@Override
	public void setPosition(Vector2f position) {
		this.start = position;
	}

	public Vector2f getDirection() {
		return this.direction;
	}

	public float getWidth() {
		return this.width;
	}
}
