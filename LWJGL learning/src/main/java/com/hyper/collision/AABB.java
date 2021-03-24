package com.hyper.collision;

import org.joml.Vector2f;

import com.hyper.world.World;

public class AABB implements Hitbox {
	private float width, height;

	private Vector2f position;

	public AABB(Vector2f position, float width, float height) {
		this.position = position;
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean isInside(Vector2f point) {
		return (point.x >= this.position.x - width/2 || point.x <= this.position.x + width/2) && (point.y <= this.position.y + height/2 || point.y >= this.position.y - height/2);
	}

	@Override
	public boolean correctPosition(World worldIn) {
		if(this.position.x < this.width/2) {
			this.position.x = this.width/2;
			return true;
		}
		if(this.position.x > worldIn.getWidth()*2 - this.width/2) {
			this.position.x = worldIn.getWidth()*2 - this.width/2;
			return true;
		}
		return false;
	}

	@Override
	public Vector2f getPosition() {
		return this.position;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	@Override
	public void translate(Vector2f motion) {
		this.position.add(motion);
	}

	@Override
	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public Vector2f[] getCorners() {
		return new Vector2f[] {
				new Vector2f(this.position).add(-width, -height),
				new Vector2f(this.position).add(width, -height),
				new Vector2f(this.position).add(width, height),
				new Vector2f(this.position).add(-width, height),	
		};
	}
}
