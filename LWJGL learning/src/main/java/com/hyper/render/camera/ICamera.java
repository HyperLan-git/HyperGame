package com.hyper.render.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public interface ICamera {
	public Matrix4f getProjectionMatrix();
	
	public void update();

	public Vector3f getPosition();

	public Vector4f getBounds();
}
