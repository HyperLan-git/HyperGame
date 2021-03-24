package com.hyper.render;

import java.io.File;
import java.io.IOException;

import com.hyper.entity.Direction;

public class FacingSprite extends Texture {
	public static final String[] DIRECTION_SUFFIX = new String[] {
			"_DOWN", "_UP", "_LEFT", "_RIGHT"
	};
	
	private Texture[] textures = new Texture[4];
	
	private Direction direction = Direction.DOWN;

	public FacingSprite(File textureDirectory, String baseName, String extension) throws IOException {
		for(int i = 0; i < 4; i++)
			this.textures[i] = new Texture(textureDirectory.getPath() + "/" + baseName + DIRECTION_SUFFIX[i] + "." + extension);
	}
	
	public FacingSprite(String textureDirectory, String baseName, String[] direction_suffix, String extension) throws IOException {
		for(int i = 0; i < 4; i++)
			this.textures[i] = new Texture(textureDirectory + "/" + baseName + direction_suffix[i] + "." + extension);
	}
	
	public FacingSprite(Texture[] textures) {
		this.textures = textures;
	}
	
	public void setDirection(Direction dir) {
		this.direction = dir;
	}
	
	@Override
	public void reset() {
		for(Texture tex : textures)
			tex.reset();
	}

	@Override
	public void bind(int sampler) {
		this.textures[direction.ordinal()].bind(sampler);
	}
}
