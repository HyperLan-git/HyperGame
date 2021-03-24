package com.hyper.net;

import java.nio.ByteBuffer;

public abstract class Packet {
	public Packet() {}

	public abstract void read(ByteBuffer buffer);

	public abstract ByteBuffer write();
}
