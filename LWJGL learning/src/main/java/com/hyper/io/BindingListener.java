package com.hyper.io;

public interface BindingListener {
	public void onPressed(KeyBinding binding);
	
	public void onDown(KeyBinding binding);

	public void onReleased(KeyBinding binding);
}
