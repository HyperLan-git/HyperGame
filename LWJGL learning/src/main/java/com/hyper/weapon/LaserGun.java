package com.hyper.weapon;

import java.awt.Color;

import com.hyper.entity.LivingEntity;
import com.hyper.entity.bullet.Laser;
import com.hyper.entity.bullet.Projectile;
import com.hyper.particle.LaserChargeParticle;
import com.hyper.render.Texture;

public class LaserGun extends ChargeableWeapon {
	private float width = 3;

	@Override
	public void weaponShoot(LivingEntity source) {
		boolean spawn = chargeTimer == 0;
		
		super.weaponShoot(source);
		if(spawn) source.getWorld().addParticle(new LaserChargeParticle(source, this));
	}
	
	@Override
	protected Projectile[] shoot(LivingEntity source) {
		return new Projectile[] {new Laser(source, this.width, Color.RED, this.damage, this.stun)};
	}

	@Override
	protected void update() {
		
	}

	@Override
	public Texture getTexture() {
		// TODO Auto-generated method stub
		return null;
	}

}
