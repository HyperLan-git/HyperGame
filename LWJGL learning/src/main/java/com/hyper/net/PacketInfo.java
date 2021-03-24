package com.hyper.net;

public class PacketInfo {
	private Side side;

	private int player;

	public PacketInfo(Side side, int player) {
		this.side = side;
		this.player = player;
	}

	public Side getSide() {
		return side;
	}

	public int getPlayerID() {
		return player;
	}
}
