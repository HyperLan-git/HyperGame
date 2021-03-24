package com.hyper.entity.player;

import com.hyper.Game;
import com.hyper.entity.DamageSource;
import com.hyper.entity.DamageSource.DamageType;
import com.hyper.entity.Player;

public class PlayerScientist extends Player {
	private float shieldTimer = 0, shieldTime = 3f, shieldInvul = 1.0f/6.0f, shieldInvulTimer = 0,
			counterAttackTimer = 0, counterAttackTime = 1;

	@Override
	public float getMaxHealth() {
		return 3.0f;
	}
	
	@Override
	public void update() {
		super.update();
		float frameTime = (float) Game.getInstance().frame_cap;
		if(shieldTimer > 0)
			shieldTimer -= frameTime;
		if(shieldInvulTimer > 0)
			shieldInvulTimer -= frameTime;
		if(counterAttackTimer > 0)
			counterAttackTime -= frameTime;
	}

	@Override
	public void defend() {
		if(shieldTimer <= 0) {
			shieldTimer = shieldTime;
			shieldInvulTimer = shieldInvul;
		}
	}

	@Override
	public boolean getAttacked(DamageSource source) {
		DamageType type = source.getType();
		if(shieldInvulTimer > 0 && type.equalsAny(DamageType.BULLET, DamageType.LASER)) {
			this.counterAttackTimer = counterAttackTime;
			return false;
		}
		this.health -= source.getAmount();
		return true;
	}

	public boolean canCounterAttack() {
		return counterAttackTimer > 0 && hitstunTimer <= 0;
	}

}
