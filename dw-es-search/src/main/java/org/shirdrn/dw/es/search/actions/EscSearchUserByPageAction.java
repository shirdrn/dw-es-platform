package org.shirdrn.dw.es.search.actions;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.shirdrn.dw.es.search.constants.ServletConfigKeys;
import org.shirdrn.dw.es.search.esclient.ElasticSearchQueryEngine;
import org.shirdrn.dw.es.search.esclient.ESClientEmbeddedAction;

/**
 * Servlet implementation class SearchUserDeviceInfoByPage
 */
public class EscSearchUserByPageAction extends ESClientEmbeddedAction {
	
	private static final long serialVersionUID = 1L;
//	private static final Log LOG = LogFactory.getLog(SearchUserDeviceInfoByPageAction2.class);
    private int pageSize = 10;
	
    public EscSearchUserByPageAction() {
        super();
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	String configPageSize = config.getInitParameter(ServletConfigKeys.ES_PAGE_SIZE);
    	try {
			pageSize = Integer.parseInt(configPageSize);
		} catch (Exception e) { }
    }

    @Override
	protected SearchResponse processRequest(HttpServletRequest request) throws Exception {
		final ElasticSearchQueryEngine engine = super.newEngine();
		String index = request.getParameter("index");
		String type = request.getParameter("type");
		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		String from = request.getParameter("from");
		String size = request.getParameter("size");
		String rangeField = request.getParameter("rangeField");
		
		
		
		SearchRequestBuilder searchRequestBuilder = engine.getClient()
				.prepareSearch(index)
				.setTypes(type)
				.setQuery(QueryBuilders.matchAllQuery())
				.setPostFilter(QueryBuilders.rangeQuery(rangeField).from(fromDate + " 00:00:00").to(toDate + " 23:59:59"))
				.setFrom(Integer.parseInt(from))
				.setSize(size == null ? pageSize : Integer.parseInt(size));
		SearchResponse searchResponse = engine.query(searchRequestBuilder);
		
		// http://localhost:8080/dw-es-platform/searchUserByPage.esc?rangeField=create_time&index=basis_device_info&type=user&from=20&size=10&fromDate=2015-01-16&toDate=2015-01-18

		if(searchResponse.status() == RestStatus.OK) {
			return searchResponse;
		} else {
			return null;
		}
	}

}
