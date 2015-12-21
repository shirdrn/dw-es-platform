package org.shirdrn.dw.es.common;

public class ServerHost {
	
	private final String host;
	private final int port;

	public ServerHost(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}
