package com.hyper.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;

public final class ClientHandler extends NetHandler {
	private HashMap<Class<? extends Packet>, Integer> ids = new HashMap<>();
	private HashMap<Class<? extends Packet>, PacketHandler<?>> handlers = new HashMap<>();

	private final Socket socket;

	private final ServerInfo serverInfo;

	public ClientHandler(String host, int port) throws IOException {
		this.serverInfo = new ServerInfo(host, port);
		this.socket = serverInfo.connect();
	}

	@Override
	public void run() {
		while(true) try {
			BufferedInputStream stream = new BufferedInputStream(socket.getInputStream());
			if(stream.available() != 0)
				handle(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void handle(InputStream stream) throws IOException {
		int i = stream.read();
		Class<? extends Packet> packetType = null;
		for(Entry<Class<? extends Packet>, Integer> entry : ids.entrySet())	if(entry.getValue() == i)
			packetType = entry.getKey();

		if(packetType == null)	throw new IOException("Recieved a message from the server but could not identify any packet !");

		ByteBuffer data = ByteBuffer.allocate(stream.available());
		while(stream.available() != 0)
			data.put((byte) stream.read());

		try {
			Packet packet = packetType.newInstance();
			packet.read(data);
			PacketHandler<? extends Packet> handler = this.handlers.get(packetType);
			Packet answer = (Packet) handler.getClass().getMethod("handlePacket", packetType, Side.class).invoke(handler, packetType.cast(packet), Side.CLIENT);
			if(answer != null) sendPacket(answer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public <T extends Packet> void register(T packetType, PacketHandler<T> packetHandler, int packetID) {
		this.ids.put(packetType.getClass(), packetID);
		this.handlers.put(packetType.getClass(), packetHandler);
	}

	public void sendPacket(Packet packet) throws IOException {
		BufferedOutputStream stream = new BufferedOutputStream(socket.getOutputStream());
		stream.write(ids.get(packet.getClass()));
		byte[] data = packet.write().array();
		stream.write(data.length);
		stream.write(data);
	}

	@Override
	protected void finalize() throws Throwable {
		socket.close();
		super.finalize();
	}
}