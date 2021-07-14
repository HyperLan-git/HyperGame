package com.hyper.world;

import java.io.IOException;

import org.joml.Vector2f;

import com.hyper.collision.AABB;
import com.hyper.entity.LivingEntity;
import com.hyper.io.ResourceLocation;
import com.hyper.render.Texture;

public class Tile {

	public static Tile[] tiles = new Tile[Byte.MAX_VALUE + 1];
	private static byte lastID = 0;

	public static Tile grass, checker, ice, rock;

	static {
		Tile.grass = new Tile("grass");
		Tile.checker = new Tile("checker");
		Tile.ice = new Tile("ice") {
			@Override
			public void onWalkOn(LivingEntity e) {
				e.setSliding(true);
			}
		};
		Tile.rock = new Tile("rock") {
			@Override
			public void onWalkOn(LivingEntity e) {
				e.onHitWall();
			}
			@Override
			public AABB[] getHitboxes() {
				return new AABB[]{new AABB(new Vector2f(), 2f, 2f)};
			}
		};
	}

	private byte id;
	private Texture texture;
	private String name;

	public Tile(String name, Texture texture) {
		this.name = name;
		this.texture = texture;
	}

	private Tile(String name) {
		this.name = name;
		try {
			this.texture = new Texture(new ResourceLocation("textures/tiles/" + name + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		registerTile(this);
	}

	public static void registerTile(Tile t) {
		t.id = lastID;
		lastID++;
		tiles[t.id] = t;
	}

	public byte getID() {
		return id;
	}

	public Texture getTexture() {
		return texture;
	}

	public String getName() {
		return name;
	}

	public AABB[] getHitboxes() {
		return null;
	}

	public void onWalkOn(LivingEntity e) {
		e.setMotion(e.getMotion().mul(0.3f, new Vector2f()));
		e.setSliding(false);
	}
}