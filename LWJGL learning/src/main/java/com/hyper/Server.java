package com.hyper;

import java.io.IOException;

import com.hyper.net.ServerHandler;
import com.hyper.world.World;

public class Server extends AbstractGame {
	public ServerHandler handler;

	public Server(World world, int fps, int port) {
		super(world, fps);
		try {
			this.handler = new ServerHandler(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void renderGame() {	}
}
