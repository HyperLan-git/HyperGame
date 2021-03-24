package com.hyper.entity;

import org.joml.Vector2f;

import com.hyper.world.World;

public abstract class EntityPickable extends Entity {
	public EntityPickable(World world, Vector2f position, float hitboxRadius) {
		super(world, position, hitboxRadius);
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean shouldPush(Entity e) {
		return e instanceof EntityPickable && e != this;
	}
}
