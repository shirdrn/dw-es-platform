package org.shirdrn.dw.es.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public class EsClientUtils {

	public static InetSocketTransportAddress newAddress(String host, int port) throws UnknownHostException {
		return new InetSocketTransportAddress(InetAddress.getByName(host), port);
	}
	
	public static Client buildClient(String clusterName, List<String> hosts, Map<String, String> settings) {
		Preconditions.checkArgument(hosts != null && !hosts.isEmpty(), "hosts == null || hosts.isEmpty()");
		
		Builder builder = Settings.settingsBuilder();
		builder.put("cluster.name", clusterName);
		if(settings != null) {
			builder.put(settings);
		}
		
		Settings esSettings = builder.build();
		TransportClient transportClient = TransportClient.builder().settings(esSettings).build();
		for(String host : hosts) {
			try {
				transportClient.addTransportAddress(EsClientUtils.newAddress(host, 9300));
			} catch (UnknownHostException e) {
				Throwables.propagate(e);
			}
		}
		
		final Client client = transportClient;
		return client;
	}
}
