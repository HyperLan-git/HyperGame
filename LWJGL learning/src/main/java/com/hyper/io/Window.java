package com.hyper.io;

import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

public class Window {
	private long id, monitor;
	private boolean fullScreen, hasResized;
	private int width, height;
	private GLFWWindowSizeCallback windowSizeCallback;

	private Vector2f boundsMin = new Vector2f(0, 0), boundsMax;

	private HashMap<KeyBinding, ArrayList<BindingListener>> bindings = new HashMap<>();
	private AtomicBoolean shouldClose;

	public static void setCallback() {
		glfwSetErrorCallback(new GLFWErrorCallbackI() {
			@Override
			public void invoke(int error, long description) {
				throw new IllegalStateException(GLFWErrorCallback.getDescription(description));
			}
		});
	}

	private void setLocalCallbacks() {
		windowSizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long argWindow, int argWidth, int argHeight) {
				width = argWidth;
				height = argHeight;
				hasResized = true;
			}
		};

		glfwSetWindowSizeCallback(id, windowSizeCallback);
	}

	public Window(int width, int height, String title, boolean fullScreen, long monitor) {
		this.fullScreen = fullScreen;
		id = glfwCreateWindow(width, height, title, fullScreen?monitor:0l, 0l);
		if(id == 0)
			throw new IllegalStateException("Failed to create a window !");

		this.width = width;
		this.height = height;
		this.boundsMax = new Vector2f(width, height);
		this.monitor = monitor;
		this.shouldClose = new AtomicBoolean();
		GLFWVidMode videoMode = glfwGetVideoMode(monitor);
		if(monitor != glfwGetPrimaryMonitor()) {
			GLFWVidMode primaryMonitor = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(id, primaryMonitor.width()+(videoMode.width() - this.getWidth())/2, (videoMode.height() - this.getHeight())/2);
		} else
			glfwSetWindowPos(id, (videoMode.width() - this.getWidth())/2, (videoMode.height() - this.getHeight())/2);

		glfwShowWindow(id);

		glfwMakeContextCurrent(id);

		setLocalCallbacks();
	}

	public long getID(){
		return id;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isFullScreen() {
		return fullScreen;
	}

	public boolean shouldClose() {
		return shouldClose.get();
	}

	public Vector2f getMousePos() {
		DoubleBuffer xPos = BufferUtils.createDoubleBuffer(1),
				yPos = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(id, xPos, yPos);
		return new Vector2f((float)xPos.get(), (float)yPos.get());
	}

	public long getMonitor() {
		return monitor;
	}

	public void update() {
		for(KeyBinding binding : this.bindings.keySet()) {
			for(BindingListener listener : this.bindings.get(binding)) {
				if(binding.isPressed())
					listener.onPressed(binding);
				if(binding.isDown())
					listener.onDown(binding);
				if(binding.isReleased())
					listener.onReleased(binding);
			}
			binding.update();
		}
	}
	
	public void handle() {
		glfwSwapBuffers(id);
		Vector2f mousePos = getMousePos();
		if(mousePos.x < boundsMin.x)
			glfwSetCursorPos(id, boundsMin.x, mousePos.y);
		mousePos = getMousePos();
		if(mousePos.x > boundsMax.x)
			glfwSetCursorPos(id, boundsMax.x, mousePos.y);
		mousePos = getMousePos();
		if(mousePos.y < boundsMin.y)
			glfwSetCursorPos(id, mousePos.x, boundsMin.y);
		mousePos = getMousePos();
		if(mousePos.y > boundsMax.y)
			glfwSetCursorPos(id, mousePos.x, boundsMax.y);
		mousePos = getMousePos();
		this.hasResized = false;
		glfwPollEvents();
		this.shouldClose.set(glfwWindowShouldClose(id));
	}

	public boolean hasResized() {
		return hasResized;
	}

	public void registerKeyBinding(KeyBinding keyBinding) {
		if(keyBinding == KeyBinding.NULL) return;
		this.bindings.put(keyBinding, new ArrayList<>(1));
	}

	public KeyBinding getBinding(String name) {
		for(KeyBinding binding : bindings.keySet())
			if(binding.getName().contentEquals(name))
				return binding;
		return KeyBinding.NULL;
	}

	public void addBindingListener(String bindingName, BindingListener listener) {
		if(bindingName == "") return;
		bindings.get(getBinding(bindingName)).add(listener);
	}

	public void addToBindingsListener(BindingListener listener, String... bindingNames) {
		for(String str : bindingNames) {
			if(str == "") continue;
			addBindingListener(str, listener);
		}
	}

	public void setMouseBounds(Vector4f glBounds) {
		this.boundsMin = new Vector2f(glBounds.x+1, -glBounds.w+1).mul(width/2, height/2);
		this.boundsMax = new Vector2f(glBounds.z+1, -glBounds.y+1).mul(width/2, height/2);
	}

	KeyBinding[] getKeyBindings() {
		return bindings.keySet().toArray(new KeyBinding[bindings.keySet().size()]);
	}
}
