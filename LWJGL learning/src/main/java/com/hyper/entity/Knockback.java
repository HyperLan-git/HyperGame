package com.hyper.entity;

import org.joml.Vector2fc;

public class Knockback {
	private float stun;
	private Vector2fc kb;
	
	public Knockback(float hitstun, Vector2fc displacement) {
		this.stun = hitstun;
		this.kb = displacement;
	}

	public float getStun() {
		return this.stun;
	}

	public Vector2fc getKb() {
		return this.kb;
	}
}
