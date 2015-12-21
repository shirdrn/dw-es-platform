package org.shirdrn.dw.es.search.rest;

import java.util.concurrent.TimeUnit;

import net.sf.json.JSONObject;

import org.apache.http.impl.client.HttpClients;

public class SimpleRESTQueryEngine extends AbstractRESTQueryEngine {

	public SimpleRESTQueryEngine(String clusterName, String[] esNodes) {
		super(clusterName, esNodes);
	}

	@Override
	public synchronized void createClient() {
		if(httpClient == null) {
			httpClient = HttpClients.createDefault();
		}
	}

	@Override
	public JSONObject query(RESTRequest request) {
		return super.query(request);
	}
	
	@Override
	public JSONObject query(RESTRequest request, long timeout, TimeUnit timeUnit) {
		throw new UnsupportedOperationException("Unsupported Operation!");	
	}

}
