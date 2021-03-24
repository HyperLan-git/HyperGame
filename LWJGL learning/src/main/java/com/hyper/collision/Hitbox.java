package com.hyper.collision;

import org.joml.Vector2f;

import com.hyper.world.World;

public interface Hitbox {
	public boolean isInside(Vector2f point);
	
	public boolean correctPosition(World worldIn);
	
	public Vector2f getPosition();

	public void translate(Vector2f motion);

	public void setPosition(Vector2f position);
}
