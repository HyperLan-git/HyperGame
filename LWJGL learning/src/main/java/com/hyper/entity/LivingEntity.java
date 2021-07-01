package com.hyper.entity;

import org.joml.Vector2f;

import com.hyper.Game;
import com.hyper.weapon.Weapon;
import com.hyper.world.World;

public abstract class LivingEntity extends Entity {
	public float shadowSize = 0.5f, shadowTransparency = 0.5f;
	public float hitstunTimer = 0;
	public Vector2f shadowCenter = new Vector2f(0, 0.4f);

	/**
	 * Aim angle in radians
	 */
	protected float aim = 0;

	protected float movespeed = 12f, health = getMaxHealth();

	protected boolean sliding = false;

	protected Weapon weapon = null;

	protected Direction heading = Direction.DOWN;

	public LivingEntity(World world, Vector2f position, float hitboxRadius) {
		super(world, position, hitboxRadius);
	}

	@Override
	public void render() {
		Game game = (Game) this.game;
		game.livingShader.bind();
		game.livingShader.setUniform("entity_pos", this.getPosition());
		game.livingShader.setUniform("shadow_size", shadowSize);
		game.livingShader.setUniform("shadow_transparency", shadowTransparency);
		game.livingShader.setUniform("shadow_center", shadowCenter);

		if(this.getCurrentTexture() != null)
			this.getCurrentTexture().bind(1);
		if(this.weapon != null && this.weapon.getTexture() != null)
			this.weapon.getTexture().bind(2);

		float scale = game.theWorld.getScale();

		game.livingShader.setUniform("sampler", 1);
		game.livingShader.setUniform("weapon", 2);
		game.livingShader.setUniform("rotation", this.rotation);
		game.livingShader.setUniform("aim", this.aim);
		game.livingShader.setUniform("size", entitySize);
		game.livingShader.setUniform("projection", game.camera.getProjectionMatrix().translate(getPosition().x * scale, getPosition().y * scale, 0).scale(scale));
		game.livingShader.setUniform("bounds", game.camera.getBounds());

		game.modelHandler.getModel(getModelName()).render();
	}

	public void shoot() {
		this.weapon.weaponShoot(this);
	}

	@Override
	public void update() {
		super.update();
		if(hitstunTimer > 0) hitstunTimer -= game.frame_cap;
		weapon.weaponUpdate(this);
		if(this.health > getMaxHealth()) this.health = getMaxHealth();
		if(this.health < 0) this.dead = true;
	}

	public float getMaxHealth() {
		return 1;
	}

	public final float getHealth() {
		return health;
	}
	
	public boolean damage(DamageSource source) {
		this.health -= source.getAmount();
		this.hitstunTimer = source.getHitstun().getStun();
		this.motion.set(source.getHitstun().getKb());
		return true;
	}

	public Vector2f getAim() {
		return new Vector2f((float)Math.cos(aim), (float)Math.sin(aim));
	}

	public float getAimAngle() {
		return aim;
	}

	public Weapon getWeapon() {
		return this.weapon;
	}

	public Direction getDirection() {
		return this.heading;
	}

	public void setAiming(Vector2f aiming) {
		if(aiming.lengthSquared() == 0) {
			this.aim = 0;
			return;
		}
		aiming.normalize();
		this.aim = (float) Math.atan2(aiming.y, aiming.x);
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public void setSliding(boolean sliding) {
		this.sliding = sliding;
	}

	public boolean isSliding() {
		return this.sliding || hitstunTimer > 0;
	}

	public abstract boolean getAttacked(DamageSource damageSource);
}
