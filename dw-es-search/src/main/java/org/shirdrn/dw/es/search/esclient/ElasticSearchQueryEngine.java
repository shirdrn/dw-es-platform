package org.shirdrn.dw.es.search.esclient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.shirdrn.dw.es.common.ServerHost;
import org.shirdrn.dw.es.search.common.AbstractQueryEngine;
import org.shirdrn.dw.es.utils.EsClientUtils;

import com.google.common.base.Throwables;

public class ElasticSearchQueryEngine extends AbstractQueryEngine<Client, SearchRequestBuilder, SearchResponse> {
	
	public ElasticSearchQueryEngine(String clusterName, String[] esNodes) {
		super(clusterName, esNodes);
	}
	
	@Override
	public void close() throws IOException {
		if(client != null) {
			client.close();
		}
	}

	@Override
	public synchronized void createClient() {
		try {
			if(client == null) {
				Settings settings = Settings.settingsBuilder()
						.put("cluster.name", clusterName)
						.put("client.transport.sniff", true)
						.build();
				TransportClient transportClient = TransportClient.builder().settings(settings).build();
				for(ServerHost pair : serverHosts) {
					InetSocketTransportAddress addr = EsClientUtils.newAddress(pair.getHost(), pair.getPort());
					transportClient.addTransportAddress(addr);
				}
				client = transportClient;
			}
		} catch (Exception e) {
			Throwables.propagate(e);
		}
	}
	
	@Override
	public SearchResponse query(SearchRequestBuilder request, long timeout, TimeUnit timeUnit) {
		return request.execute().actionGet(timeout, timeUnit);
	}
	
	@Override
	public SearchResponse query(SearchRequestBuilder request) {
		return request.execute().actionGet();
	}

}
