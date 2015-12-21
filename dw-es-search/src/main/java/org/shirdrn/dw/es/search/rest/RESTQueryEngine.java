package org.shirdrn.dw.es.search.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONObject;

import org.apache.http.impl.client.CloseableHttpClient;
import org.shirdrn.dw.es.common.ServerHost;
import org.shirdrn.dw.es.search.common.AbstractQueryEngine;

public abstract class RESTQueryEngine extends AbstractQueryEngine<CloseableHttpClient, RESTRequest, JSONObject> {

	private int maxTotal = 10;
	private int maxPerRoute = 20;
	private final Random random = new Random();
	private final List<String> esKeywords = Arrays.asList(new String[] {
			"size", "from"
	});
	
	public RESTQueryEngine(String clusterName, String... esNodes) {
		super(clusterName, esNodes);
	}
	
	protected abstract JSONObject httpGet(String reqUrl);
	protected abstract JSONObject httpPost(String reqUrl, JSONObject body);
	
	private ServerHost selectHost() {
		return serverHosts.get(random.nextInt(serverHosts.size()));
	}
	
	@Override
	public JSONObject query(RESTRequest request) {
		ServerHost serverHost = selectHost();
		StringBuffer reqUrl = new StringBuffer("http://")
			.append(serverHost.getHost() + ":")
			.append(serverHost.getPort() + "/")
			.append(request.getIndex() + "/")
			.append(request.getType() + "/_search?");
		Iterator<Entry<String, String>> iter = request.getUrlParameters().entrySet().iterator();
		while(iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			if(esKeywords.contains(entry.getKey())) {
				reqUrl.append(entry.getKey() + "=" + entry.getValue());
			} else {
				reqUrl.append("q=" + entry.getKey() + ":" + entry.getValue());
			}
			if(iter.hasNext()) {
				reqUrl.append("&");
			}
		}
		if(request.getBody() == null) {
			return httpGet(reqUrl.toString());
		} else {
			return httpPost(reqUrl.toString(), request.getBody());
		}
	}
	
	@Override
	public JSONObject query(RESTRequest request, long timeout, TimeUnit timeUnit) {
		throw new UnsupportedOperationException("Unsupported operation!");
	}
	
	@Override
	public void close() throws IOException {
		if(client != null) {
			client.close();
		}
		
	}

	public int getMaxPerRoute() {
		return maxPerRoute;
	}

	public void setMaxPerRoute(int maxPerRoute) {
		this.maxPerRoute = maxPerRoute;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}
	
}
