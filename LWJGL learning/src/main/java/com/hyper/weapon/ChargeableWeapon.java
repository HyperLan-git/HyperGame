package com.hyper.weapon;

import com.hyper.Game;
import com.hyper.entity.LivingEntity;
import com.hyper.entity.bullet.Projectile;
import com.hyper.entity.player.PlayerScientist;

/**
 * Do not worry about checking for charge time, it is already handled with {@link #weaponShoot(LivingEntity)}
 * @author HyperLan
 */
public abstract class ChargeableWeapon extends Weapon {
	public float chargeTime = 1, chargeTimer = 0.1f;

	private boolean charging = false;

	{
		this.automatic = true;
		this.magazineCapacity = 3;
		this.reloadTime = 0.1f;
	}

	public ChargeableWeapon() {		}

	public ChargeableWeapon(float chargeTime) {
		this.chargeTime = chargeTime;
	}

	@Override
	public void weaponShoot(LivingEntity source) {
		if(ammo == 0) {
			System.out.println("reloading");
			reload();
		}

		if(source instanceof PlayerScientist) {
			PlayerScientist realSource = (PlayerScientist) source;
			if(realSource.canCounterAttack()) {
				Projectile[] bullets = shoot(source);
				if(bullets != null)
					for(Projectile b : bullets)
						source.getWorld().addEntity(b);
			}
		}

		if(!canShoot())
			return;
		charging = true;
		if(chargeTimer < chargeTime)
			chargeTimer += Game.getInstance().frame_cap;
	}

	@Override
	public void weaponUpdate(LivingEntity owner) {
		float frameTime = (float) Game.getInstance().frame_cap;
		update();
		if(!charging && isCharged() && ammo > 0) {
			this.ammo--;
			Projectile[] bullets = shoot(owner);
			if(bullets != null)
				for(Projectile b : bullets)
					owner.getWorld().addEntity(b);
		}
		if(!charging)
			chargeTimer = 0;
		if(reloadTimer > 0) {
			reloadTimer -= frameTime;
			if(reloadTimer <= 0) {
				this.ammo = this.magazineCapacity;
				reloadTimer = 0;
			}
		}

		charging = false;
	}
	
	public final boolean isCharged() {
		return chargeTimer >= chargeTime;
	}
}
