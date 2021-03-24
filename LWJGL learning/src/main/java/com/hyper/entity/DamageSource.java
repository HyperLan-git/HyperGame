package com.hyper.entity;

public class DamageSource {
	public enum DamageType {
		BULLET,
		LASER,
		ENVIRONEMENT;

		public boolean equalsAny(DamageType... types) {
			for(DamageType type : types) if(this.equals(type))
				return true;
			return false;
		}
	}

	private final DamageType type;
	private final float amount;
	private float stun;
	private final Entity source;

	public DamageSource(DamageType type, float amount, Entity source, float stun) {
		this.type = type;
		this.amount = amount;
		this.source = source;
		this.stun = stun;
	}

	public DamageSource(DamageType type, float amount, float stun) {
		this(type, amount, null, stun);
	}

	/**
	 * Supposed to be used only for debugging
	 * @param amount
	 */
	@Deprecated
	public DamageSource(float amount) {
		this(DamageType.ENVIRONEMENT, amount, 5);
	}

	public DamageType getType() {
		return type;
	}

	public float getAmount() {
		return amount;
	}

	public Entity getSource() {
		return source;
	}
	
	public float getHitstun() {
		return stun;
	}
}
