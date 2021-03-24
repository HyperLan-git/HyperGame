package com.hyper.collision;

import org.joml.Vector2f;

import com.hyper.Utils;
import com.hyper.entity.Entity;
import com.hyper.entity.LivingEntity;

public final class CollisionHandler {
	public static final float pushPrecision = 0.0005f;

	public static boolean collides(Hitbox hitbox, Hitbox other) {
		if(hitbox instanceof AABB && other instanceof AABB)
			return collides((AABB)hitbox, (AABB)other);
		else if(hitbox instanceof CircularHitbox && other instanceof CircularHitbox)
			return collides((CircularHitbox)hitbox, (CircularHitbox)other);
		else if(hitbox instanceof CircularHitbox && other instanceof AABB)
			return collides((AABB) other, (CircularHitbox) hitbox);
		else if(hitbox instanceof AABB && other instanceof CircularHitbox)
			return collides((AABB) hitbox, (CircularHitbox) other);
		else if(hitbox instanceof RectangularHitbox && other instanceof CircularHitbox)
			return collides((CircularHitbox)other, (RectangularHitbox)hitbox);
		else if(hitbox instanceof CircularHitbox && other instanceof RectangularHitbox)
			return collides((CircularHitbox)hitbox, (RectangularHitbox)other);
		return false;
	}

	private static boolean collides(CircularHitbox hitbox, CircularHitbox other) {
		return hitbox.getPosition().distance(other.getPosition()) <= hitbox.getRadius() + other.getRadius();
	}

	private static boolean collides(AABB hitbox, AABB other) {
		return other.isInside(new Vector2f(hitbox.getPosition()).add(-hitbox.getWidth()/2, hitbox.getHeight()/2)) || other.isInside(new Vector2f(hitbox.getPosition()).add(hitbox.getWidth()/2, hitbox.getHeight()/2)) || 
				other.isInside(new Vector2f(hitbox.getPosition()).add(-hitbox.getWidth()/2, -hitbox.getHeight()/2)) || other.isInside(new Vector2f(hitbox.getPosition()).add(hitbox.getWidth()/2, -hitbox.getHeight()/2));
	}

	private static boolean collides(AABB aabb, CircularHitbox ch) {
		Vector2f center = aabb.getPosition();
		float dx = Math.max(Math.max(center.x-aabb.getWidth()/2-ch.getPosition().x, 0), ch.getPosition().x-center.x-aabb.getWidth()/2),
				dy = Math.max(Math.max(center.y-aabb.getHeight()/2-ch.getPosition().y, 0), ch.getPosition().y-center.y-aabb.getHeight()/2);
		return dx*dx+dy*dy <= ch.getRadius()*ch.getRadius();
	}

	public static boolean collides(CircularHitbox ch, RectangularHitbox rect) {
		return ch.getRadius() + rect.getWidth() >=
				Utils.getDistanceFromPointToLine(ch.getPosition(), rect.getPosition(), rect.getDirection());
	}

	public static void push(Hitbox source, Hitbox toPush) {
		int i = 0;
		if(source.getPosition().distance(toPush.getPosition()) == 0)
			source.translate(new Vector2f((float)Math.random()*0.001f, (float)Math.random()*0.001f));
		while(collides(source, toPush)) {
			Vector2f v = new Vector2f(source.getPosition()).sub(toPush.getPosition()).normalize();
			if(source instanceof AABB)
				v = Math.abs(v.x) > Math.abs(v.y)?new Vector2f(v.x>0?1:-1, 0):new Vector2f(0, v.y>0?1:-1);
				if(Float.isNaN(v.x) || Float.isNaN(v.y))
					source.getPosition().normalize(v);
				v.mul(-pushPrecision);
				toPush.translate(v);
				i++;
				if(i > 10000)
					throw new IllegalStateException("Too many collision calculations ! \nEnded up putting a hitbox at x = " + toPush.getPosition().x + ", y = " + toPush.getPosition().y);
		}
	}

	public static void handleCollisions(Entity e) {
		if(e.canBePushed()) {
			for(Entity e2 : e.getWorld().getEntities()) if(collides(e.getHitbox(), e2.getHitbox()) && e2.shouldPush(e))
				push(e2.getHitbox(), e.getHitbox());
			for(Hitbox box : e.getWorld().getObstacles())  if(collides(box, e.getHitbox())) {
				push(box, e.getHitbox());
				if(e instanceof LivingEntity)
					((LivingEntity)e).setSliding(false);
				e.setMotion(new Vector2f());
				e.onHitWall();
			}
			e.getHitbox().correctPosition(e.getWorld());
		}
	}
}
