package com.hyper.entity;

import org.joml.Vector2f;

import com.hyper.render.Texture;
import com.hyper.weapon.Weapon;
import com.hyper.world.World;

public class WeaponItem extends EntityPickable {

	private Weapon weapon;

	public WeaponItem(World world, Vector2f position, Weapon weapon) {
		super(world, position, 0.8f);
	}

	@Override
	public Texture getCurrentTexture() {
		return weapon.getTexture();
	}
	
	@Override
	public String getModelName() {
		return "square1x1";
	}
}
