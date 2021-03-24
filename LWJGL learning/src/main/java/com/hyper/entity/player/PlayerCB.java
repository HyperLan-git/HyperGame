package com.hyper.entity.player;

import org.joml.Vector2f;

import com.hyper.Game;
import com.hyper.entity.DamageSource;
import com.hyper.entity.Player;
import com.hyper.io.Resource;
import com.hyper.render.Texture;

public class PlayerCB extends Player {
	private float invulTimer = 0, invulTime = 0.5f, recoveryTimer = 0, recoveryTime = 0.2f;

	private Vector2f dodgeVec = new Vector2f();

	@Resource(path={"textures/entities/player/dodge_0_0.png"})
	public static final Texture DODGE = null;

	@Override
	public void update() {
		float frameTime = (float) Game.getInstance().frame_cap;
		if(recoveryTimer > 0) {
			recoveryTimer -= frameTime;
			this.motion = new Vector2f();
			dodgeVec.mul(0.2f, motion);
			this.sliding = false;
		}
		if(invulTimer > 0) {
			invulTimer -= frameTime;
			this.motion = new Vector2f(dodgeVec);
			this.sliding = false;
			if(invulTimer < 0)
				recoveryTimer = recoveryTime;
		}
		super.update();
	}

	@Override
	public boolean getAttacked(DamageSource source) {
		if(this.invulTimer > 0)
			return false;
		this.health -= source.getAmount();
		return true;
	}

	@Override
	public void defend() {
		if(this.hitstunTimer > 0) return;
		if(invulTimer <= 0 && recoveryTimer <= 0 && motion.lengthSquared() > 0.15*0.15f) {
			this.dodgeVec = this.motion.mul(1.05f);
			this.invulTimer = invulTime;
		}
	}
	
	@Override
	public void shoot() {
		if(invulTimer > 0 || recoveryTimer > 0 || hitstunTimer > 0) return;
		super.shoot();
	}
	
	@Override
	public Texture getCurrentTexture() {
		if(invulTimer > 0)
			return DODGE;
		return super.getCurrentTexture();
	}

}
