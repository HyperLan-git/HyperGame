package com.hyper.net;

import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerInfo {
	private String host, serverName;
	private int port;

	public ServerInfo(String host, int port) {
		this.host = host;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public String getServerName() {
		return serverName;
	}

	public InetSocketAddress getAddress() {
		return new InetSocketAddress(host, port);
	}

	public Socket connect() {
		return null;
	}
}
