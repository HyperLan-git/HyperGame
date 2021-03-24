package com.hyper.entity;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import com.hyper.AbstractGame;
import com.hyper.Game;
import com.hyper.collision.CircularHitbox;
import com.hyper.collision.Hitbox;
import com.hyper.render.Texture;
import com.hyper.world.World;

public abstract class Entity {
	protected Hitbox hitbox;
	protected World world;
	protected final AbstractGame game = AbstractGame.getInstance();

	protected Vector2f motion = new Vector2f();

	protected boolean dead = false;

	protected float rotation = 0;
	
	protected float entitySize = 1;

	public Entity(World world, Vector2fc position, float hitboxRadius) {
		this.world = world;
		this.hitbox = new CircularHitbox(position, hitboxRadius);
	}

	public Entity(World world, Vector2f position, Hitbox hitbox) {
		this.world = world;
		this.hitbox = hitbox;
		this.setPosition(position);
	}

	public void update() {
		this.hitbox.translate(this.motion);
		if(this.hitbox.correctPosition(world))
			onHitWall();
	}

	public boolean canBePushed() {
		return true;
	}

	public abstract Texture getCurrentTexture();

	public final Vector2f getPosition() {
		return new Vector2f(this.hitbox.getPosition());
	}

	public final Entity setPosition(Vector2f position) {
		this.hitbox.setPosition(position);
		return this;
	}

	public void render() {
		Game game = (Game) this.game;
		game.shader.bind();
		if(this.getCurrentTexture() != null)
			this.getCurrentTexture().bind(1);

		float scale = game.theWorld.getScale();

		game.shader.setUniform("sampler", 1);
		game.shader.setUniform("rotation", this.rotation);
		game.shader.setUniform("projection", game.camera.getProjectionMatrix().translate(getPosition().x * scale, getPosition().y * scale, 0).scale(scale));
		game.shader.setUniform("size", entitySize);
		game.shader.setUniform("bounds", game.camera.getBounds());

		game.modelHandler.getModel(getModelName()).render();
	}

	public void setMotion(Vector2f motion) {
		this.motion = motion;
	}

	public Hitbox getHitbox() {
		return hitbox;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Vector2fc getMotion() {
		return new Vector2f(this.motion);
	}

	public boolean shouldPush(Entity e) {
		return e.canBePushed() && e != this;
	}

	public void onHitWall() {}
	
	public abstract String getModelName();

	public void setDead() {
		this.dead = true;
	}

	public boolean dead() {
		return dead;
	}
}
