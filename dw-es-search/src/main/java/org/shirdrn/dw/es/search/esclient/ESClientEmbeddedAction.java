package org.shirdrn.dw.es.search.esclient;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchResponse;
import org.shirdrn.dw.es.search.common.ClientEmbeddedAction;
import org.shirdrn.dw.es.search.constants.ServletConfigKeys;

import com.google.common.base.Preconditions;

public abstract class ESClientEmbeddedAction extends ClientEmbeddedAction<SearchResponse> {

	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(ESClientEmbeddedAction.class);
	private String addresses;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		addresses = config.getInitParameter(ServletConfigKeys.ES_TCP_ADDRESS);
		Preconditions.checkArgument(addresses != null, "address == null");
		LOG.info("ElasticSearch cluster TCP addresses: addresses=" + addresses);
		
		queryEngine = new ElasticSearchQueryEngine(clusterName, addresses.split("\\s*,\\s*"));
		queryEngine.createClient();
		LOG.info("Search engine client instance created: " + queryEngine);
	}
	
	protected ElasticSearchQueryEngine newEngine() {
		return super.newQueryEngine(ElasticSearchQueryEngine.class);
	}
}
