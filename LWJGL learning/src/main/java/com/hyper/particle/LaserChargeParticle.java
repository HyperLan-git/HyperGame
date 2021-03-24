package com.hyper.particle;

import org.joml.Vector2f;

import com.hyper.entity.Entity;
import com.hyper.io.Resource;
import com.hyper.render.Texture;
import com.hyper.weapon.ChargeableWeapon;

public class LaserChargeParticle extends Particle {
	@Resource(path={"textures/entities/laser/charge.png"})
	public static final Texture TEXTURE = null;

	private final Entity charger;
	private final ChargeableWeapon weapon;

	public LaserChargeParticle(Entity charger, ChargeableWeapon weapon) {
		super(charger.getWorld());

		this.charger = charger;
		this.weapon = weapon;
	}

	@Override
	public void render() {
		float charge = 2*(1-weapon.chargeTimer/weapon.chargeTime)+1f;
		this.size = charge;
		if(weapon.chargeTimer > weapon.chargeTime || weapon.chargeTimer == 0)
			this.dead = true;
		super.render();
	}

	@Override
	protected Vector2f getPosition() {
		return charger.getPosition();
	}

	@Override
	protected Texture getCurrentTexture() {
		return TEXTURE;
	}

	@Override
	protected String getModelName() {
		return "square2x2";
	}
}
