package com.hyper.weapon;

import java.awt.Color;

import org.joml.Vector2f;

import com.hyper.entity.LivingEntity;
import com.hyper.entity.bullet.Projectile;
import com.hyper.entity.bullet.Bullet;
import com.hyper.render.Texture;

public class Shotgun extends Weapon {

	public Shotgun() {
		this.magazineCapacity = 3;
		this.ammo = 3;
	}

	@Override
	protected Projectile[] shoot(LivingEntity source) {
		Projectile[] result = new Projectile[6];
		float entityAngle = (float) Math.atan2(source.getAim().y, source.getAim().x),
				bulletSpeed = 0.3f*source.getWorld().getPower();
		for(int i = 0; i < 6; i++) {
			float angle = (float) ((i-2.5)*Math.PI/32 + entityAngle);
			Projectile b = new Bullet(source, Color.RED, 0, this.damage, this.stun);
			b.setMotion(new Vector2f(bulletSpeed).mul((float)Math.cos(angle), (float)Math.sin(angle)));
			result[i] = b;
		}
		return result;
	}

	@Override
	protected void update() {}

	@Override
	public Texture getTexture() {
		return null;
	}

}
