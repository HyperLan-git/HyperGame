package com.hyper.world;

import org.joml.Matrix4f;

import com.hyper.Game;
import com.hyper.render.Shader;
import com.hyper.render.camera.ICamera;

public class TileRenderer {

	public TileRenderer() {}

	public void renderTile(Tile tile, int x, int y, Shader shader, float scale, ICamera camera) {
		shader.bind();
		if(tile != null)
			tile.getTexture().bind(0);
		Matrix4f tilePos = new Matrix4f().translate(x*2, y*2, 0);
		Matrix4f target = new Matrix4f();

		camera.getProjectionMatrix().scale(scale, target);
		target.mul(tilePos);

		shader.setUniform("sampler", 0);
		shader.setUniform("rotation", 0f);
		shader.setUniform("size", 1f);
		shader.setUniform("projection", target);
		shader.setUniform("bounds", camera.getBounds());

		Game.getClientInstance().modelHandler.getModel("square2x2").render();
	}
}
