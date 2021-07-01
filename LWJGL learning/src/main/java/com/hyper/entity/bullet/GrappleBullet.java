package com.hyper.entity.bullet;

import java.awt.Color;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import com.hyper.collision.CollisionHandler;
import com.hyper.entity.DamageSource;
import com.hyper.entity.DamageSource.DamageType;
import com.hyper.entity.Entity;
import com.hyper.entity.Knockback;
import com.hyper.entity.LivingEntity;
import com.hyper.world.World;

public class GrappleBullet extends Bullet {
	public static final float SPEED_TRANSFER = 5f;

	public GrappleBullet(LivingEntity shooter, Color color, float speed, float damage, float stun) {
		super(shooter, color, speed, damage, stun);
	}
	
	public GrappleBullet(World world, Vector2fc position, Color color, float damage, float stun) {
		super(world, position, color, damage, stun);
	}
	
	@Override
	public void update() {
		super.update();
		for(Entity e : this.world.getEntities()) if(e instanceof LivingEntity && e != this.getShooter())
			if(CollisionHandler.collides(this.hitbox, e.getHitbox())) {
				if(((LivingEntity)e).damage(new DamageSource(DamageType.BULLET, this.getDamage(), this.getShooter(),
						new Knockback(this.getHitstun(), new Vector2f(this.getMotion()).mul(-SPEED_TRANSFER)))))
				this.setDead();
				return;
			}
	}
}
