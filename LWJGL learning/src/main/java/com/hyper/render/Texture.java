package com.hyper.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.stb.STBImage.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import com.hyper.io.ResourceLocation;

public class Texture {
	private int id, width, height;

	public Texture(String file) throws IOException {
		this(new ResourceLocation(file));
	}

	protected Texture() {	}

	public Texture(ResourceLocation resourceLocation) throws IOException {
		IntBuffer width = BufferUtils.createIntBuffer(1), height = BufferUtils.createIntBuffer(1),
				comp = BufferUtils.createIntBuffer(1);

		ByteBuffer buffer = stbi_load(resourceLocation.getPath(), width, height, comp, 4);
		if(buffer == null) throw new IOException("Could not load texture in : " + resourceLocation.getPath());

		this.width = width.get();
		this.height = height.get();
		this.id = glGenTextures();

		glBindTexture(GL_TEXTURE_2D, id);

		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		stbi_image_free(buffer);
	}

	public void reset() {	}

	public void bind(int sampler) {
		if(sampler < 0 || sampler > 31)
			throw new IllegalArgumentException("Invalid sampler id !");
		glActiveTexture(GL_TEXTURE0 + sampler);
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public Texture clone() {
		Texture tex = new Texture();
		tex.id = this.id;
		tex.height = this.height;
		tex.width = this.width;
		return tex;
	}
}
