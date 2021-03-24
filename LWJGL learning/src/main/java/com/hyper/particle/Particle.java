package com.hyper.particle;

import org.joml.Vector2f;

import com.hyper.AbstractGame;
import com.hyper.Game;
import com.hyper.render.Model;
import com.hyper.render.Texture;
import com.hyper.world.World;

public abstract class Particle {
	protected World world;
	protected final AbstractGame game = AbstractGame.getInstance();

	protected boolean dead = false;
	protected Model model;

	protected float size;
	
	protected float rotation = 0;
	
	public Particle(World world) {
		this.world = world;
	}

	public void render() {
		Game game = (Game) this.game;
		game.shader.bind();
		if(this.getCurrentTexture() != null)
			this.getCurrentTexture().bind(1);

		float scale = game.theWorld.getScale();

		game.shader.setUniform("sampler", 1);
		game.shader.setUniform("rotation", this.rotation);
		game.shader.setUniform("size", size);
		game.shader.setUniform("projection", game.camera.getProjectionMatrix().translate(getPosition().x * scale, getPosition().y * scale, 0).scale(scale));
		game.shader.setUniform("bounds", game.camera.getBounds());

		game.modelHandler.getModel(this.getModelName()).render();
	}

	protected abstract String getModelName();

	protected abstract Vector2f getPosition();

	protected abstract Texture getCurrentTexture();
	
	public final boolean isDead() {
		return dead;
	}
	
	public final void kill() {
		this.dead = true;
	}
}
