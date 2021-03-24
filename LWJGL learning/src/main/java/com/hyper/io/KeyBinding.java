package com.hyper.io;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFW;

public class KeyBinding {
	public static final KeyBinding NULL = new KeyBinding() {
		@Override
		public InputSource getInputType() {return null;}
		
		@Override
		public int getKeyID() {return -1;}

		@Override
		public float getSensibility() {return Float.MIN_NORMAL;}

		@Override
		public float howMuchDown() {return 0;}

		@Override
		public void update() {}
		
		@Override
		public boolean changeBinding() {return false;}
	};

	private final String name;

	private transient final Window window;

	private int keyID;

	private InputSource type;

	private float sensibility = 0.2f;

	private transient boolean lastState = false;
	
	private KeyBinding() {
		this.name = "";
		this.window = null;
	}

	/**
	 * @param name the key binding unlocalized name
	 * @param window teh window
	 * @param defaultSource the default binding
	 * @param defaultID the default button id (for a controller must be 100*controller + buttonID)
	 */
	public KeyBinding(String name, Window window, InputSource defaultSource, int defaultID) {
		this.name = name;
		this.window = window;
		this.type = defaultSource;
		this.keyID = defaultID;
	}

	public boolean changeBinding() {
		for(int i = 0; i < GLFW_KEY_LAST+1; i++)
			if(glfwGetKey(window.getID(), i) == GLFW_PRESS) {
				this.keyID = i;
				this.type = InputSource.KEYBOARD;
				return true;
			}
		for(int i = 0; i < GLFW_MOUSE_BUTTON_LAST+1; i++)
			if(glfwGetMouseButton(window.getID(), i) == GLFW_PRESS) {
				this.keyID = i;
				this.type = InputSource.MOUSE;
				return true;
			}
		for(int controller = 0; controller < 18; controller++) if(GLFW.glfwJoystickPresent(controller)) {
			FloatBuffer fb = glfwGetJoystickAxes(controller);
			for(int j = 0; j < 6; j++) if(fb.get(j) == 1 || fb.get(j) == -1) {
				this.keyID = fb.get(j) == 1?j+100*controller:j+6+100*controller;
				this.type = InputSource.CONTROLLER_JOYSTICKS;
				return true;
			}
			ByteBuffer bb = glfwGetJoystickButtons(controller);
			for(int button = 0; button < 17; button++)
				if(bb.get(button) == GLFW_PRESS) {
					this.keyID = button + 100*controller;
					this.type = InputSource.CONTROLLER_BUTTONS;
					return true;
				}
		}
		return false;
	}

	public void update() {
		this.lastState = isDown();
	}

	public boolean wasDownLastTick() {
		return this.lastState;
	}

	public final boolean isDown() {
		return howMuchDown() > 0;
	}

	public float howMuchDown() {
		int buttonID = this.keyID-(this.keyID/100)*100;
		float result;
		switch(type) {
		case KEYBOARD:
			return GLFW.glfwGetKey(this.window.getID(), this.keyID);
		case MOUSE:
			if(keyID == -1)
				return 0;
			return GLFW.glfwGetMouseButton(this.window.getID(), this.keyID);
		case CONTROLLER_BUTTONS:
			if(!glfwJoystickPresent(keyID/100) || buttonID < 0 || buttonID > 17)
				return 0;
			ByteBuffer bb = glfwGetJoystickButtons(this.keyID/100);
			if(bb.limit() <= buttonID)
				return 0;
			result = bb.get(buttonID);
			return result < this.sensibility?0:result;
		case CONTROLLER_JOYSTICKS:
			if(!glfwJoystickPresent(keyID/100) || buttonID < 0 || buttonID > 11)
				return 0;
			FloatBuffer fb = glfwGetJoystickAxes(this.keyID/100);
			if(fb.limit() <= (buttonID%6))
				return 0;
			result = Math.max(0, buttonID < 6?fb.get(buttonID):-fb.get(buttonID-6));
			return result < this.sensibility?0:result;
		default:
			return 0;
		}
	}
	public boolean isPressed() {
		return this.isDown() && !this.wasDownLastTick();
	}

	public boolean isReleased() {
		return !this.isDown() && this.wasDownLastTick();
	}

	public float getSensibility() {
		return sensibility;
	}

	public void setSensibility(float sensibility) {
		this.sensibility = sensibility;
	}

	public final String getName() {
		return this.name;
	}

	public InputSource getInputType() {
		return this.type;
	}

	public int getKeyID() {
		return this.keyID;
	}

	public static enum InputSource {
		KEYBOARD,
		MOUSE,
		/**
		 * The value for this input type has special syntax :
		 * value = (controller number)*100 + v
		 * v can be :
		 * 0 = SQUARE
		 * 1 = CROSS
		 * 2 = CIRCLE
		 * 3 = TRIANGLE
		 * 4 = L1
		 * 5 = R1
		 * 6 = L2
		 * 7 = R2
		 * 8 = SHARE
		 * 9 = PAUSE
		 * 10 = L3
		 * 11 = R3
		 * 12 = PS
		 * 13 = TOUCHPAD
		 * 14 = UP
		 * 15 = RIGHT
		 * 16 = DOWN
		 * 17 = LEFT
		 */
		CONTROLLER_BUTTONS,
		/**
		 * The value for this input type has special syntax :
		 * value = (controller number)*100 + v
		 * v can be :
		 * 0 = L-right
		 * 1 = L-down
		 * 2 = R-right
		 * 3 = L2
		 * 4 = R2
		 * 5 = R-down
		 * 6 = L-left
		 * 7 = L-up
		 * 8 = R-left
		 * 9 = -L2
		 * 10 = -R2
		 * 11 = R-up
		 */
		CONTROLLER_JOYSTICKS;
	}
}
