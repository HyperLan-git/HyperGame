package com.hyper.world;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import com.hyper.Game;
import com.hyper.collision.AABB;
import com.hyper.collision.CollisionHandler;
import com.hyper.collision.Hitbox;
import com.hyper.entity.Entity;
import com.hyper.entity.LivingEntity;
import com.hyper.io.Window;
import com.hyper.particle.Particle;
import com.hyper.render.Shader;
import com.hyper.render.camera.ICamera;

public class World {
	private byte[] tiles;
	private int width, height, viewX, viewY;
	private float scale, power = 1, powerGrowth = 0.04f, maxPower = 1.5f;
	private Hitbox[] hitboxes = new Hitbox[0];

	private ArrayList<Entity> entities = new ArrayList<>(), toSpawn = new ArrayList<>();

	private ArrayList<Particle> particles = new ArrayList<>();

	public World(int width, int height) {
		this.width = width;
		this.height = height;
		scale = 32;

		tiles = new byte[width * height];
		for(int i = 0; i < tiles.length; i++)
			tiles[i] = 0x00;
	}

	public void render(TileRenderer renderer, Shader shader, ICamera camera, Window window) {
		int posX = (int) (camera.getPosition().x / (scale * 2));
		int posY = (int) (camera.getPosition().y / (scale * 2));

		for (int i = 0; i < viewX; i++) 
			for (int j = 0; j < viewY; j++) {
				Tile t = getTile(i - posX - (viewX / 2) + 1, j + posY - (viewY / 2));
				if (t != null) renderer.renderTile(t, i - posX - (viewX / 2) + 1, -j - posY + (viewY / 2), shader, this.scale, camera);
			}

		synchronized(entities) {
			for(Entity e : entities)
				e.render();
		}

		for(int i = particles.size()-1; i >= 0; i--) {
			Particle p = particles.get(i);
			if(p.isDead()) particles.remove(i);
		}

		synchronized(particles) {
			for(Particle p : particles) 
				p.render();
		}
	}

	public void calculateView(Window window) {
		viewX = (int) (8 + window.getWidth() / (scale));
		viewY = (int) (8 + window.getHeight() / (scale));
	}

	public void setTile(Tile tile, int x, int y) {
		this.tiles[x + y*width] = tile.getID();
		updateHitboxes();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public Tile getTile(int x, int y) {
		if(x < 0 || y < 0 || x >= this.width || y >= this.height)
			return null;
		return Tile.tiles[this.tiles[x + y*width]];
	}

	public float getScale() {
		return scale;
	}

	public Hitbox[] getObstacles() {
		return hitboxes;
	}

	public void setScale(float scale) {
		this.scale = scale;
		calculateView(Game.getClientInstance().window);
	}

	public <T extends Entity> T addEntity(T e) {
		this.toSpawn.add(e);
		e.setWorld(this);
		return e;
	}

	public void update() {
		this.power += powerGrowth*Game.getInstance().frame_cap;
		if(power > maxPower)
			power = maxPower;

		synchronized(entities) {
			for(int i = this.entities.size()-1; i >= 0; i--) {
				Entity e = entities.get(i);
				if(e.dead())
					entities.remove(i);
			}

			for(int i = this.toSpawn.size()-1; i >= 0; i--) {
				Entity e = toSpawn.get(i);
				entities.add(e);
				toSpawn.remove(i);
			}

			for(Entity e : entities) {
				e.update();
				Tile t = this.getTile(Math.round(e.getPosition().x/2), Math.round((-e.getPosition().y+0.25f)/2));
				if(t != null && e instanceof LivingEntity) t.onWalkOn((LivingEntity) e);
				CollisionHandler.handleCollisions(e);
			}
		}
	}

	private void updateHitboxes() {
		ArrayList<AABB> hitboxes = new ArrayList<>();
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++) {
				Tile t = getTile(x, y);
				if(t.getHitboxes() != null) for(AABB box : t.getHitboxes()) {
					AABB aabb = new AABB(new Vector2f(box.getPosition()).add(x*2, -y*2), box.getWidth(), box.getHeight()); 
					hitboxes.add(aabb);
				}
			}
		this.hitboxes = hitboxes.toArray(new AABB[hitboxes.size()]);
	}

	public void kill(Entity e) {
		e.setDead();
	}

	public float getPower() {
		return power;
	}

	public void setPower(float power) {
		this.power = power;
		if(power > maxPower)
			power = maxPower;
	}

	public void addParticle(Particle particle) {
		synchronized(particles) {
			this.particles.add(particle);
		}
	}
}
