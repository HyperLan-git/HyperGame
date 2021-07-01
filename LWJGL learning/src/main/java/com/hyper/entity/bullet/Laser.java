package com.hyper.entity.bullet;

import java.awt.Color;

import org.joml.Vector2f;

import com.hyper.Game;
import com.hyper.collision.CollisionHandler;
import com.hyper.collision.RectangularHitbox;
import com.hyper.entity.DamageSource;
import com.hyper.entity.DamageSource.DamageType;
import com.hyper.entity.Entity;
import com.hyper.entity.Knockback;
import com.hyper.entity.LivingEntity;
import com.hyper.io.Resource;
import com.hyper.render.Texture;

public class Laser extends Projectile {
	public static final int LASER_LIFETIME = 10;
	@Resource(path={"textures/entities/laser/laser.png"})
	public static final Texture TEXTURE = null;
	
	private int lived = 0;

	public Laser(LivingEntity shooter, float width, Color color, float damage, float stun) {
		super(shooter, new RectangularHitbox(shooter.getPosition(), shooter.getAim(), width), color, damage, stun);

		this.entitySize = width;
		this.rotation = -shooter.getAimAngle();
	}

	@Override
	public void update() {
		super.update();
		
		lived++;
		if(lived > LASER_LIFETIME)
			this.dead = true;
		for(Entity e : this.world.getEntities()) if(e instanceof LivingEntity && e != this.getShooter())
			if(CollisionHandler.collides(e.getHitbox(), this.hitbox)) {
				((LivingEntity)e).damage(new DamageSource(DamageType.LASER, this.getDamage(), this.getShooter(),
						new Knockback(this.getHitstun(), new Vector2f((float)Math.cos(this.rotation), (float)Math.sin(this.rotation)))));
				this.setDead();
				return;
			}
	}

	@Override
	public void onHitWall() {}

	@Override
	public boolean canBePushed() {
		return false;
	}
	
	@Override
	public String getModelName() {
		return "square2x2";
	}
	
	public String getCastModelName() {
		return "lasercast";
	}

	@Override
	public void render() {
		super.render();

		Game.getClientInstance().modelHandler.getModel(getCastModelName()).render();
	}

	@Override
	public Texture getCurrentTexture() {
		return TEXTURE;
	}

}
