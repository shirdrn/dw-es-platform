package org.shirdrn.dw.es.search.rest;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.dw.es.search.common.ClientEmbeddedAction;
import org.shirdrn.dw.es.search.constants.ServletConfigKeys;

import com.google.common.base.Preconditions;

public abstract class RESTClientEmbeddedAction extends ClientEmbeddedAction<JSONObject> {

	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(RESTClientEmbeddedAction.class);
	private int maxTotal = 10;
	private int maxPerRoute = 20;
	private String addresses;
	
	public RESTClientEmbeddedAction() {
		super();
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		addresses = config.getInitParameter(ServletConfigKeys.ES_REST_ADDRESS);
		Preconditions.checkArgument(addresses != null , "addresses == null");
		LOG.info("ElasticSearch cluster REST addresses: addresses=" + addresses);
		
		String configMaxTotal = config.getInitParameter(ServletConfigKeys.MAX_TOTAL);
		try {
			maxTotal = Integer.parseInt(configMaxTotal);
		} catch (Exception e) { }
		
		String configMaxPerRoute = config.getInitParameter(ServletConfigKeys.MAX_PER_ROUTE);
		try {
			maxPerRoute = Integer.parseInt(configMaxPerRoute);
		} catch (Exception e) { }
		
		LOG.info("Http client config: maxTotal=" + maxTotal + ", maxPerRoute=" + maxPerRoute);
		
		queryEngine = new PoolingRESTQueryEngine(clusterName, addresses.split("\\s*,\\s*"));
		final RESTQueryEngine httpEngine = (RESTQueryEngine) queryEngine;
		httpEngine.setMaxTotal(maxTotal);
		httpEngine.setMaxPerRoute(maxPerRoute);
		httpEngine.createClient();
		LOG.info("Query engine client instance created: " + httpEngine);
	}
	
	protected RESTQueryEngine newEngine() {
		return super.newQueryEngine(RESTQueryEngine.class);
	}
	
}
