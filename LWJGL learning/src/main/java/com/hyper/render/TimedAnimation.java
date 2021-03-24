package com.hyper.render;

import java.util.ArrayList;

import com.hyper.Game;

public class TimedAnimation extends Texture {
	
	//FIXME MULTITHREADING
	public static final class Timer {
		private static Timer instance = new Timer();
		
		public static final Timer getInstance() {
			return instance;
		}
		
		private ArrayList<TimedAnimation> animations = new ArrayList<>();
		
		private Timer() {	}
		
		private void register(TimedAnimation animation) {
			this.animations.add(animation);
		}

		private void delete(TimedAnimation animation) {
			this.animations.remove(animation);
		}

		public void update() {
			for(TimedAnimation animation : animations)
				animation.update();
		}
	}
	private int frames = 0, maxFrames, tex = 0;
	
	protected Texture[] textures;
	
	public TimedAnimation(float time, Texture[] textures) {
		this.maxFrames = (int) (time/Game.getInstance().frame_cap);
		this.textures = textures;
		Timer.getInstance().register(this);
	}
	
	@Override
	public void reset() {
		this.frames = 0;
		this.tex = 0;
		for(Texture tex : textures)
			tex.reset();
	}
	
	protected void update() {
		this.frames++;
		if(frames >= maxFrames) {
			this.frames = 0;
			this.tex++;
			if(tex == textures.length)
				this.tex = 0;
		}
	}
	
	@Override
	public void bind(int sampler) {
		this.textures[tex].bind(sampler);
	}
	
	@Override
	protected void finalize() throws Throwable {
		Timer.instance.delete(this);
		super.finalize();
	}
}
