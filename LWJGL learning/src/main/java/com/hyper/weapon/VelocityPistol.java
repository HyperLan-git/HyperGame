package com.hyper.weapon;

import org.joml.Vector2f;

import com.hyper.entity.LivingEntity;
import com.hyper.entity.bullet.Projectile;

public class VelocityPistol extends Pistol {
	
	{
		this.stun = 0.8f;
	}

	@Override
	protected Projectile[] shoot(LivingEntity source) {
		Projectile[] result = super.shoot(source);
		result[0].setMotion(result[0].getMotion().add(source.getMotion(), new Vector2f()));
		return result;
	}
}
