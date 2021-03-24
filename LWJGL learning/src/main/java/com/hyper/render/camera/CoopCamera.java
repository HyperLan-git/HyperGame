package com.hyper.render.camera;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.hyper.Game;
import com.hyper.io.Window;
import com.hyper.world.World;

public class CoopCamera implements ICamera {
	public static final float MAX_SIZE = 15;
	
	private Vector3f position = new Vector3f();
	private Matrix4f projection = new Matrix4f();

	public CoopCamera(int width, int height) {
		projection = new Matrix4f().ortho2D(-width/2, width/2, -height/2, height/2);
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public Matrix4f getProjectionMatrix() {
		Matrix4f pos = new Matrix4f().translate(position), target = projection.mul(pos, new Matrix4f());
		return target;
	}

	@Override
	public Vector4f getBounds() {
		return new Vector4f(-1, -1, 1, 1);
	}

	public void setPosition(Vector2f position) {
		this.position = new Vector3f(position.x, position.y, 0);
	}

	public void update() {
		//TODO make scale adaptable and render correctly guis
		World world = Game.getInstance().theWorld;
		ArrayList<Vector2f> positions = new ArrayList<>(4);
		for(int i = 0; i < 4 && Game.getInstance().thePlayers[i] != null; i++)
			positions.add(Game.getInstance().thePlayers[i].getPosition());
		Vector2f finalPos = new Vector2f();
		for(int i = 0; i < positions.size(); i++)
			finalPos.add(positions.get(i).mul(-world.getScale()/positions.size()));
		float dist = 0;
		Vector2f realPos = new Vector2f(finalPos).mul(0.5f);
		for(int i = 0; i < positions.size(); i++) {
			float d = positions.get(i).distance(realPos);
			if(dist < d) dist = d;
		}
		if(dist > 128)
			world.setScale((float) Math.max(33f-dist/128, MAX_SIZE));

		int w = (int) (-world.getWidth() * world.getScale() * 2);
		int h = (int) (world.getHeight() * world.getScale() * 2);
		Window window = Game.getClientInstance().window;

		if(finalPos.x > - window.getWidth()/2 + world.getScale())
			finalPos.x = - window.getWidth()/2 + world.getScale();
		else if(finalPos.x < w + window.getWidth()/2 + world.getScale())
			finalPos.x = w + window.getWidth()/2 + world.getScale();

		if(finalPos.y < window.getHeight()/2 - world.getScale())
			finalPos.y = window.getHeight()/2 - world.getScale();
		else if(finalPos.y > h - window.getHeight()/2 - world.getScale())
			finalPos.y = h - window.getHeight()/2 - world.getScale();
		this.setPosition(finalPos);
	}
}
