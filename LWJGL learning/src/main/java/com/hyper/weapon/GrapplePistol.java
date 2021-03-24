package com.hyper.weapon;

import java.awt.Color;

import com.hyper.entity.LivingEntity;
import com.hyper.entity.bullet.GrappleBullet;
import com.hyper.entity.bullet.Projectile;

public class GrapplePistol extends Pistol {

	@Override
	protected Projectile[] shoot(LivingEntity source) {
		float bulletSpeed = 0.6f*source.getWorld().getPower();
		Projectile[] result = new Projectile[] {new GrappleBullet(source, Color.RED, bulletSpeed, this.damage, this.stun)};
		return result;
	}
}
