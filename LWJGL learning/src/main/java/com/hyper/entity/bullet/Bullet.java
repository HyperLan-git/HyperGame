package com.hyper.entity.bullet;

import java.awt.Color;

import org.joml.Vector2fc;

import com.hyper.collision.CollisionHandler;
import com.hyper.entity.DamageSource;
import com.hyper.entity.DamageSource.DamageType;
import com.hyper.entity.Entity;
import com.hyper.entity.LivingEntity;
import com.hyper.io.Resource;
import com.hyper.render.Texture;
import com.hyper.world.World;

public class Bullet extends Projectile {
	public static float size = 0.2f;

	@Resource(path={"textures/entities/bullets/normal.png"})
	public static final Texture TEXTURE = null;

	public Bullet(World world, Vector2fc position, Color color, float damage, float stun) {
		super(world, position, size, color, damage, stun);
	}

	public Bullet(LivingEntity shooter, Color color, float speed, float damage, float stun) {
		super(shooter, size, color, damage, stun);
		this.motion = shooter.getAim().normalize().mul(speed);
	}

	@Override
	public Texture getCurrentTexture() {
		return TEXTURE;
	}

	@Override
	public String getModelName() {
		return "square1x1";
	}

	@Override
	public void update() {
		super.update();
		for(Entity e : this.world.getEntities()) if(e instanceof LivingEntity && e != this.getShooter())
			if(CollisionHandler.collides(this.hitbox, e.getHitbox())) {
				((LivingEntity)e).damage(new DamageSource(
						DamageType.BULLET, this.getDamage(), this.getShooter(), this.getHitstun()
						));
				this.setDead();
				return;
			}
	}
}
