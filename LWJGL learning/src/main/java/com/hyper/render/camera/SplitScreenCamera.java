package com.hyper.render.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.hyper.Game;
import com.hyper.io.KeyBinding.InputSource;
import com.hyper.io.Window;
import com.hyper.world.World;

public class SplitScreenCamera implements ICamera {
	private int renderPhase = 0;

	private float width, height;

	private Vector3f[] positions = new Vector3f[4];

	public SplitScreenCamera(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public Vector3f getPosition() {
		return positions[renderPhase];
	}

	@Override
	public Matrix4f getProjectionMatrix() {
		float y = renderPhase <= 1? 0:height/2,
				x = -width*(renderPhase%2)/2;
		Matrix4f pos = new Matrix4f().translate(positions[renderPhase]),
				target = new Matrix4f().ortho2D(x-width/2, x+width/2, y-height/2, y+height/2).mul(pos);
		return target;
	}

	@Override
	public Vector4f getBounds() {
		int players = 0;
		for(int i = 0; i < 4; i++) if(positions[i] != null)
			players++;
		float x = (renderPhase%2)-1,
				y = renderPhase <= 1 ?0:-1;
		switch(players) {
		case 1:
			return new Vector4f(-1, -1, 1, 1);
		case 2:
			return new Vector4f(x, -1, x+1, 1);
		case 3:
		case 4:
			return new Vector4f(x, y, x+1, y+1);
		default:
			return null;
		}
	}

	@Override
	public void update() {
		World world = Game.getInstance().theWorld;

		int players = 0;
		for(int i = 0; i < 4; i++) if(positions[i] != null)
			players++;
		for(int i = 0; i < 4; i++) if(Game.getInstance().thePlayers[i] != null) {
			this.positions[i] = new Vector3f(Game.getInstance().thePlayers[i].getPosition().add(players>1?world.getScale()/2:0, players>2?-world.getScale()/4:0), 0);
			this.positions[i].mul(-world.getScale());

			int w = (int) (-world.getWidth() * world.getScale() * 2);
			int h = (int) (world.getHeight() * world.getScale() * 2);
			Window window = Game.getClientInstance().window;

			if(positions[i].x > -window.getWidth()/2 + world.getScale())
				positions[i].x = -window.getWidth()/2 + world.getScale();
			if(positions[i].x < w + (players > 1?0:window.getWidth()/2) + world.getScale())
				positions[i].x = w + (players > 1?0:window.getWidth()/2) + world.getScale();

			if(positions[i].y < window.getHeight()/2 - world.getScale())
				positions[i].y = window.getHeight()/2 - world.getScale();
			if(positions[i].y > h - (players > 2?0:window.getHeight()/2) - world.getScale())
				positions[i].y = h - (players > 2?0:window.getHeight()/2) - world.getScale();
			if(Game.getClientInstance().playerShootInputs[i] == InputSource.MOUSE) {
				this.renderPhase = i;
				Game.getClientInstance().window.setMouseBounds(getBounds());
			}
		} else positions[i] = null;
	}

	public void setRenderPhase(int renderPhase) {
		this.renderPhase = renderPhase;
	}
}
