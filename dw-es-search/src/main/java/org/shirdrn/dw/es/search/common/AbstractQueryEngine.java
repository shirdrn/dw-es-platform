package org.shirdrn.dw.es.search.common;

import java.util.List;

import org.shirdrn.dw.es.common.ServerHost;

import com.google.common.collect.Lists;


public abstract class AbstractQueryEngine<CLIENT, REQUEST, RESPONSE> implements QueryEngine<CLIENT, REQUEST, RESPONSE> {
	
	protected final String clusterName;
	protected final List<ServerHost> serverHosts;
	protected CLIENT client;
	
	public AbstractQueryEngine(String clusterName, String... esNodes) {
		this.clusterName = clusterName;
		this.serverHosts = parse(esNodes);
	}
	
	private List<ServerHost> parse(String[] esNodes) {
		List<ServerHost> list = Lists.newArrayList();
		for(String url : esNodes) {
			String[] a = url.split(":");
			list.add(new ServerHost(a[0], Integer.parseInt(a[1])));
		}
		return list;
	}
	
	@Override
	public CLIENT getClient() {
		final CLIENT c = client;
		return c;
	}
	
}
