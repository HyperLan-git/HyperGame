package com.hyper.entity;

import java.io.IOException;

import org.joml.Vector2f;

import com.hyper.Game;
import com.hyper.io.Loader;
import com.hyper.render.FacingSprite;
import com.hyper.render.Texture;
import com.hyper.render.TimedAnimation;
import com.hyper.weapon.Pistol;

public abstract class Player extends LivingEntity {
	public static FacingSprite IDLE, WALKING;

	//FIXME SHARED WALKING FOR BOTH PLAYERS MEANING ONE AFFECTS THE OTHER

	@Loader
	public static final void loadResources() throws IOException {
		Texture[] walking = new TimedAnimation[4];
		for(int i = 0; i < 4; i++) {
			Texture[] tex = new Texture[2];
			for(int j = 0; j < 2; j++)
				tex[j] = new Texture("textures/entities/player/walk_" + String.valueOf(i+j*4) + ".png");
			walking[i] = new TimedAnimation(0.5f, tex);
		}

		IDLE = new FacingSprite("textures/entities/player", "", new String[] {"0", "1", "2", "3"}, "png");
		WALKING = new FacingSprite(walking);
	}

	protected FacingSprite texturesIdle = IDLE.clone(), texturesWalking = WALKING.clone();

	private boolean moving = false;

	public Player() {
		super(Game.getInstance().theWorld, new Vector2f(), 0.3f);

		this.weapon = new Pistol();
	}

	public void move(Vector2f heading) {
		if(this.hitstunTimer > 0) return;
		this.moving = true;
		this.heading = Direction.getDirection(heading);
		double frameCap = Game.getInstance().frame_cap;
		if(this.sliding) {
			this.motion.add(this.heading.move((float) (frameCap * this.movespeed/40)));
			if(this.motion.length() > frameCap * this.movespeed*2.5)
				this.motion.normalize().mul((float) (frameCap * this.movespeed*2.5));
		} else
			this.motion.add(this.heading.move((float) (frameCap * this.movespeed)));
	}

	public final void move(Direction heading, float multiplier) {
		if(this.hitstunTimer > 0) return;
		this.heading = heading;
		this.moving = true;
		double frameCap = Game.getInstance().frame_cap;
		if(this.sliding) {
			this.motion.add(this.heading.move((float) (multiplier * frameCap * this.movespeed/40)));
			if(this.motion.length() > frameCap * this.movespeed*2.5)
				this.motion.normalize().mul((float) (frameCap * this.movespeed*2.5));
		} else
			this.motion.add(this.heading.move((float) (multiplier * frameCap * this.movespeed)));
	}

	@Override
	public float getMaxHealth() {
		return 5;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public boolean isMoving() {
		return this.moving;
	}

	@Override
	public void update() {
		super.update();
		if(this.motion.lengthSquared() <= 0.01f)
			this.setMoving(false);
		//FIXME multithrading timed animation
		if(!this.isMoving())
			texturesWalking.reset();
	}

	@Override
	public Texture getCurrentTexture() {
		this.texturesIdle.setDirection(heading);
		this.texturesWalking.setDirection(heading);
		if(this.moving)
			return this.texturesWalking;
		else
			return this.texturesIdle;
	}

	@Override
	public String getModelName() {
		return "square2x2";
	}

	public abstract void defend();
}
