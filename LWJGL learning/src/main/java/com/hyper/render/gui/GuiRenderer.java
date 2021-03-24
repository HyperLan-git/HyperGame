package com.hyper.render.gui;

import java.io.IOException;
import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector4f;

import com.hyper.AbstractGame;
import com.hyper.Game;
import com.hyper.entity.Player;
import com.hyper.io.KeyBinding.InputSource;
import com.hyper.io.ResourceLocation;
import com.hyper.render.Texture;
import com.hyper.render.camera.ICamera;
import com.hyper.render.camera.SplitScreenCamera;

public class GuiRenderer {
	public static Texture life, empty_life;
	public static Texture visor;

	private ArrayList<Gui> guis = new ArrayList<>();

	public GuiRenderer() {
		try {
			life = new Texture(new ResourceLocation("textures/guis/life.png"));
			empty_life = new Texture(new ResourceLocation("textures/guis/empty_life.png"));
			visor = new Texture(new ResourceLocation("textures/misc/visor.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void render() {
		for(int i = 0; i < 4; i++)
			renderPlayerUI(i);

		this.renderPowerBar();

		for(Gui g : guis)
			g.render();
	}

	private void renderPlayerUI(int player) {
		if(Game.getInstance().thePlayers[player] == null) return;
		this.renderVisor(player);
		this.renderLifeBar(player);
		this.renderWeapon(player);
	}

	private void renderLifeBar(int player) {
		Player p = AbstractGame.getInstance().thePlayers[player];
		ICamera c = Game.getClientInstance().camera;
		if(c instanceof SplitScreenCamera)
			((SplitScreenCamera) c).setRenderPhase(player);

		if(GuiRenderer.life != null && GuiRenderer.empty_life != null) for(int i = 0; i <= p.getMaxHealth(); i++) {
			Gui life = new Gui(new Vector2f(player%2==0?-1:0 + i*1/20.0f, (player < 2)?1:0), 1/16.0f, -1/8.0f, GuiRenderer.life),
					empty_life = new Gui(new Vector2f(player%2==0?-1:0 + i*1/20.0f, (player < 2)?1:0), 1/16.0f, -1/8.0f, GuiRenderer.empty_life);
			if(p.getHealth() >= i)
				life.render();
			else
				empty_life.render();
		}
	}

	private void renderPowerBar() {

	}

	private void renderVisor(int player) {
		Game g = Game.getClientInstance();
		Player p = g.thePlayers[player];
		ICamera c = g.camera;
		float scale = Game.getClientInstance().theWorld.getScale();
		if(c instanceof SplitScreenCamera)
			((SplitScreenCamera) c).setRenderPhase(player);
		Vector2f screenPos = null;
		Gui visor = null;
		if(Game.getClientInstance().playerShootInputs[player] == InputSource.MOUSE && g.isMouseLocked()) {
			screenPos = g.window.getMousePos().mul(2.0f/g.window.getWidth(), -2.0f/g.window.getHeight()).sub(1, -1);
		} else {
			Vector4f aimedAt = new Vector4f(p.getPosition().add(p.getAim()), 0, 1).mul(c.getProjectionMatrix().scale(scale));
			screenPos = new Vector2f(aimedAt.x, aimedAt.y);
		}
		visor = new Gui(screenPos, 0.05f, GuiRenderer.visor);
		visor.render();
	}

	private void renderWeapon(int player) {

	}
}