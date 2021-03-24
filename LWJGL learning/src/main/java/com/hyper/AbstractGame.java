package com.hyper;

import java.util.concurrent.atomic.AtomicBoolean;

import com.hyper.entity.Player;
import com.hyper.world.World;

public abstract class AbstractGame {
	private static AbstractGame instance;

	public static final AbstractGame getInstance() {
		return instance;
	}

	public Player[] thePlayers = new Player[4];

	public final double frame_cap;
	public double curr_cap;

	public boolean debug = true;

	private double time = Utils.getTimeSeconds(), unprocessed = 0, frameTime = 0;

	private int UPS = 0;

	public World theWorld;
	
	private AtomicBoolean shouldRender = new AtomicBoolean();

	public AbstractGame(World theWorld, int fps) {
		instance = this;
		this.theWorld = theWorld;
		frame_cap = 1.0f/fps;
	}

	public final void update() {
		double time2 = Utils.getTimeSeconds();
		double passed = time2 - time;
		curr_cap = frame_cap/theWorld.getPower();
		unprocessed += passed;
		frameTime += passed;
		time = time2;
		if(unprocessed >= curr_cap*10) unprocessed = curr_cap*10;

		while(unprocessed >= curr_cap) {
			UPS++;
			unprocessed -= curr_cap;

			updateLogic();
			shouldRender.set(true);

			if(frameTime >= 1.0) {
				frameTime = 0;
				if(debug) System.out.println("UPS = " + UPS);
				UPS = 0;
			}
		}
	}

	public void updateLogic() {
		this.theWorld.update();
	}

	public void render() {
		if(shouldRender.get()) {
			renderGame();
			shouldRender.set(false);
		}
	}
	
	protected abstract void renderGame();
}
