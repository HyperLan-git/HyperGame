package com.hyper;

import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;

import com.hyper.io.Window;
import com.hyper.io.sound.SoundHandler;
import com.hyper.world.World;

public class Client {
	public static void main(String[] args) {
		if (!glfwInit()){
			System.err.println("Error initializing GLFW");
			System.exit(1);
		}

		glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

		SoundHandler.getInstance().init();

		Game game = new Game(new World(64, 32), 60);

		Window.setCallback();

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glfwSwapInterval(1);

		Thread updateThread = new Thread() {
			@Override
			public void run() {
				while(!game.window.shouldClose()) {
					game.update();
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						System.out.println("this is fine");
						e.printStackTrace();
					}
				}
			}
		};
		updateThread.setName("update thread");
		updateThread.setDaemon(true);
		updateThread.setPriority(Thread.MIN_PRIORITY);
		updateThread.start();
		//TODO make it work with multithreading

		while(!game.window.shouldClose()) {
			//game.update();
			game.render();
		}

		updateThread.interrupt();

		game.handler.serializeKeyBindings();

		SoundHandler.getInstance().stop();

		glfwTerminate();
	}
}
