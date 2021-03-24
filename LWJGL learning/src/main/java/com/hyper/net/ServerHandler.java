package com.hyper.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;

public class ServerHandler extends NetHandler {
	private HashMap<Class<? extends Packet>, Integer> ids = new HashMap<>();
	private HashMap<Class<? extends Packet>, PacketHandler<?>> handlers = new HashMap<>();

	private ArrayList<ServerConnection> openedConnections = new ArrayList<>();

	private final ServerSocket socket;

	public ServerHandler(int port) throws IOException {
		this(port, 5);
	}

	public ServerHandler(int port, int maxPackets) throws IOException {
		this.socket = new ServerSocket(port, maxPackets, InetAddress.getLocalHost());
	}

	@Override
	public void run() {
		while(true) try {
			Socket recieved = socket.accept();
			ServerConnection newConnection = new ServerConnection(recieved, this);
			newConnection.start();
			this.openedConnections.add(newConnection);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void sendTo(Packet packet, int player) {
		sendTo(packet, openedConnections.get(player).getAddress());
	}

	private int getPlayerID(InetAddress address) {
		for(int i = 0; i < this.openedConnections.size(); i++) if(this.openedConnections.get(i).getAddress() == address)
			return i;
		return -1;
	}

	private void sendTo(Packet packet, InetAddress target) {
		for(ServerConnection connection : openedConnections) if(connection.getAddress() == target) try {
			BufferedOutputStream stream = connection.getOutputStream();
			stream.write(this.ids.get(packet.getClass()));
			byte[] data = packet.write().array();
			stream.write(data.length);
			stream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handle(BufferedInputStream stream, InetAddress inetAddress) {
		try {
			int packetID = stream.read();
			for(Entry<Class<? extends Packet>, Integer> entry : this.ids.entrySet())
				if(entry.getValue() == packetID) {
					Class<? extends Packet> packetType = entry.getKey();
					int length = stream.read();
					byte[] data = new byte[length];
					stream.read(data);

					ByteBuffer buffer = BufferUtils.createByteBuffer(length);
					buffer.put(data);
					buffer.flip();

					Packet packet = packetType.newInstance();
					packet.read(buffer);

					PacketHandler<? extends Packet> handler = this.handlers.get(packetType);
					handler.getClass().getMethod("handlePacket", packetType, PacketInfo.class)
					.invoke(handler, packet, new PacketInfo(Side.SERVER, this.getPlayerID(inetAddress)));
					return;
				}
		} catch (IOException | ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		socket.close();
		super.finalize();
	}
}
