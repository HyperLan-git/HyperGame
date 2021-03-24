package com.hyper.entity;

import org.joml.Vector2f;
import org.joml.Vector2i;

public enum Direction {
	DOWN(new Vector2i(0, -1)),
	UP(new Vector2i(0, 1)),
	LEFT(new Vector2i(-1, 0)),
	RIGHT(new Vector2i(1, 0));
	
	private Vector2i directionVector;
	
	private Direction(Vector2i vector) {
		this.directionVector = vector;
	}
	
	public Vector2f move(float speed) {
		return new Vector2f(speed, speed).mul(this.directionVector.x, this.directionVector.y);
	}

	public static Direction getDirection(Vector2f heading) {
		Direction result = DOWN;
		float angle = (float) Math.PI;
		for(Direction d : values()) {
			float f = heading.angle(new Vector2f(d.directionVector.x, d.directionVector.y));
			if(f < angle) {
				angle = f;
				result = d;
			}
		}
		return result;
	}
}
