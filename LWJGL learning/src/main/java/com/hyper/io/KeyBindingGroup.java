package com.hyper.io;

public class KeyBindingGroup {
	public static final KeyBindingGroup ROOT = new KeyBindingGroup();

	private final String groupName;

	private KeyBindingGroup parent = ROOT;

	private KeyBindingGroup() {
		this.groupName = "";
		this.parent = null;
	}

	public KeyBindingGroup(String name, KeyBindingGroup parent) {
		this.groupName = name;
		this.parent = (parent == null)?ROOT:parent;
	}

	public String getFullPath() {
		return (this.parent == ROOT)?groupName:this.parent.getFullPath()+"_"+this.groupName;
	}

	public String getName() {
		return this.groupName;
	}

	public KeyBindingGroup getParent() {
		return (this.parent == null)?this:this.parent;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof KeyBindingGroup))
			return false;
		KeyBindingGroup group = (KeyBindingGroup) obj;
		return this.getFullPath().equals(group.getFullPath());
	}
}
