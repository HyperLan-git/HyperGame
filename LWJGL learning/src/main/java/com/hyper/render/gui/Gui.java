package com.hyper.render.gui;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import com.hyper.Game;
import com.hyper.render.Model;
import com.hyper.render.Shader;
import com.hyper.render.Texture;

public class Gui extends Model {
	private Texture texture;

	public Gui(Vector2f posOnScreen, float width, float height, Texture texture) {
		super(new double[] {
				posOnScreen.x, posOnScreen.y,
				posOnScreen.x + width, posOnScreen.y,
				posOnScreen.x + width, posOnScreen.y + height,
				posOnScreen.x, posOnScreen.y + height
		}, new double[] {
				0, 0,
				1, 0,
				1, 1,
				0, 1
		}, new int[] {
				0, 1, 2,
				0, 2, 3
		}, GL11.GL_TRIANGLES);
		this.texture = texture;
	}

	public Gui(Vector2f posOnScreen, float size, Texture texture) {
		super(new double[] {
				posOnScreen.x - size/4, posOnScreen.y + size/2,
				posOnScreen.x + size/4, posOnScreen.y + size/2,
				posOnScreen.x + size/4, posOnScreen.y - size/2,
				posOnScreen.x-size/4, posOnScreen.y - size/2
		}, new double[] {
				0, 0,
				1, 0,
				1, 1,
				0, 1
		}, new int[] {
				0, 1, 2,
				0, 2, 3
		}, GL11.GL_TRIANGLES);
		this.texture = texture;
	}

	@Override
	public void render() {
		Shader s = Game.getClientInstance().guiShader;
		s.bind();
		s.setUniform("sampler", 2);
		this.texture.bind(2);
		super.render();
	}

	public Texture getCurrentTexture() {
		return this.texture;
	}
}
