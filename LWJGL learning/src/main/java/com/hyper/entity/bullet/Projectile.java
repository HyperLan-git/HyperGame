package com.hyper.entity.bullet;

import java.awt.Color;

import org.joml.Vector2fc;

import com.hyper.Game;
import com.hyper.collision.Hitbox;
import com.hyper.entity.Entity;
import com.hyper.entity.LivingEntity;
import com.hyper.world.World;

public abstract class Projectile extends Entity {
	private LivingEntity shooter = null;

	private Color color;
	
	private float damage;
	
	private float hitstun;
	
	public Projectile(LivingEntity shooter, Hitbox hitbox, Color color, float damage, float stun) {
		super(shooter.getWorld(), shooter.getPosition(), hitbox);
		this.damage = damage;
		this.color = color;
		this.shooter = shooter;
		this.hitstun = stun;
	}

	public Projectile(World world, Vector2fc position, float hitboxRadius, Color color, float damage, float stun) {
		super(world, position, hitboxRadius);
		this.damage = damage;
		this.color = color;
		this.hitstun = stun;
	}

	public Projectile(LivingEntity shooter, float hitboxRadius, Color color, float damage, float stun) {
		super(shooter.getWorld(), shooter.getPosition(), hitboxRadius);
		this.damage = damage;
		this.shooter = shooter;
		this.color = color;
		this.hitstun = stun;
	}

	@Override
	public void render() {
		Game game = (Game) this.game;
		game.bulletShader.bind();

		this.getCurrentTexture().bind(1);

		float scale = game.theWorld.getScale();

		game.bulletShader.setUniform("sampler", 1);
		game.bulletShader.setUniform("rotation", this.rotation);
		game.bulletShader.setUniform("projection", game.camera.getProjectionMatrix().translate(getPosition().x * scale, getPosition().y * scale, 0).scale(scale));
		game.bulletShader.setUniform("color", color);
		game.bulletShader.setUniform("bounds", game.camera.getBounds());

		game.modelHandler.getModel(getModelName()).render();
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public boolean shouldPush(Entity e) {
		return false;
	}

	@Override
	public void onHitWall() {
		this.dead = true;
	}

	public float getDamage() {
		return this.damage;
	}

	public LivingEntity getShooter() {
		return this.shooter;
	}
	
	public float getHitstun() {
		return this.hitstun;
	}
}
