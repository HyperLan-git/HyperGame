package com.hyper.net;

public interface PacketHandler<T extends Packet> {
	/**
	 * @param packet The packet recieved
	 * @return A response or null
	 */
	public Packet handlePacket(T packet, PacketInfo info);
}
