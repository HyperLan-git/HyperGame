package com.hyper.weapon;

import java.awt.Color;

import com.hyper.entity.LivingEntity;
import com.hyper.entity.bullet.Projectile;
import com.hyper.io.Resource;
import com.hyper.entity.bullet.Bullet;
import com.hyper.render.Texture;

public class Pistol extends Weapon {
	@Resource(path={"textures/weapon/pistol.png"})
	public static final Texture TEXTURE = null;

	@Override
	protected Projectile[] shoot(LivingEntity source) {
		float bulletSpeed = 0.6f*source.getWorld().getPower();
		return new Projectile[] {new Bullet(source, Color.RED, bulletSpeed, this.damage, this.stun)};
	}

	@Override
	protected void update() {
		
	}

	@Override
	public Texture getTexture() {
		return TEXTURE;
	}

}
