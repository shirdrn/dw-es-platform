package org.shirdrn.dw.es.search.rest;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.shirdrn.dw.es.common.ServerHost;

public class PoolingRESTQueryEngine extends AbstractRESTQueryEngine {

	private final PoolingHttpClientConnectionManager connectionManager;
	private int maxTotal = 20;
	private int maxPerRoute = 20;
	
	public PoolingRESTQueryEngine(String clusterName, String... esNodes) {
		super(clusterName, esNodes);
		this.connectionManager = new PoolingHttpClientConnectionManager();
	}
	
	@Override
	public synchronized void createClient() {
		if(httpClient == null) {
			connectionManager.setMaxTotal(maxTotal);
			for(ServerHost serverHost : serverHosts) {
				HttpHost host = new HttpHost(serverHost.getHost(), serverHost.getPort());
				connectionManager.setMaxPerRoute(new HttpRoute(host), maxPerRoute);
			}
			httpClient = HttpClients.custom()
					.setConnectionManager(connectionManager)
					.build();
		}
	}
	
	@Override
	public void close() throws IOException {
		super.close();
	}

}
