package com.hyper.weapon;

import com.hyper.Game;
import com.hyper.entity.LivingEntity;
import com.hyper.entity.bullet.Projectile;
import com.hyper.render.Texture;

public abstract class Weapon {
	protected boolean automatic = false;

	public float loadTime = 1, reloadTime = 2, reloadTimer = 0, loadTimer = 0, damage = 1, stun = 1.2f;

	public int magazineCapacity = 6, ammo = 6;

	public void weaponShoot(LivingEntity source) {
		if(!canShoot())
			return;
		this.ammo--;
		this.loadTimer = loadTime;
		Projectile[] bullets = shoot(source);
		if(bullets != null)
			for(Projectile b : bullets)
				source.getWorld().addEntity(b);
	}

	public void reload() {
		if(reloadTimer > 0) return;
		this.reloadTimer = reloadTime;
	}

	protected abstract Projectile[] shoot(LivingEntity source);

	public void weaponUpdate(LivingEntity owner) {
		float frameTime = (float) Game.getInstance().frame_cap;
		update();
		if(loadTimer > 0)
			loadTimer -= frameTime;
		if(reloadTimer > 0) {
			reloadTimer -= frameTime;
			if(reloadTimer <= 0) {
				this.ammo = this.magazineCapacity;
				reloadTimer = 0;
			}
		}
	}

	protected abstract void update();

	public boolean canShoot() {
		return reloadTimer <= 0 && loadTimer <= 0 && ammo > 0;
	}

	public abstract Texture getTexture();

	public boolean isAutomatic() {
		return automatic;
	}

	public float getDamage() {
		return damage;
	}
}