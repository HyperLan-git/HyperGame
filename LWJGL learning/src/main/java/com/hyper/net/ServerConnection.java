package com.hyper.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ServerConnection extends Thread {
	private ServerHandler instance;

	private Socket connection;

	public ServerConnection(Socket socket, ServerHandler instance) {
		this.connection = socket;
		this.instance = instance;
	}
	
	public InetAddress getAddress() {
		return connection.getInetAddress();
	}

	public BufferedOutputStream getOutputStream() throws IOException {
		return new BufferedOutputStream(connection.getOutputStream());
	}

	@Override
	public void run() {
		try {
			BufferedInputStream stream = new BufferedInputStream(connection.getInputStream());
			while(!connection.isClosed()) if(stream.available() != 0)
				instance.handle(stream, getAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
