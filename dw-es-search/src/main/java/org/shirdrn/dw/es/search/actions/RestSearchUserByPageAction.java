package org.shirdrn.dw.es.search.actions;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.dw.es.search.rest.RESTClientEmbeddedAction;
import org.shirdrn.dw.es.search.rest.RESTQueryEngine;
import org.shirdrn.dw.es.search.rest.RESTRequest;

/**
 * Servlet implementation class SearchUserDeviceInfoByPage
 */
public class RestSearchUserByPageAction extends RESTClientEmbeddedAction {
	
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(RestSearchUserByPageAction.class);
	
    public RestSearchUserByPageAction() {
        super();
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    }

    @Override
	protected JSONObject processRequest(HttpServletRequest request) throws Exception {
    	RESTRequest req = new RESTRequest();
    	
    	String index = request.getParameter("index");
    	String type = request.getParameter("type");
    	req.setIndex(index);
    	req.setType(type);
    	
    	String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		String from = request.getParameter("from");
		String size = request.getParameter("size");
		req.getUrlParameters().put("size", size == null ? String.valueOf(pageSize) : size);
		req.getUrlParameters().put("from", from);
		
		JSONObject reqBody = new JSONObject();
		
		JSONObject timeVal = new JSONObject();
		timeVal.put("from", fromDate + " 00:00:00");
		timeVal.put("to", toDate + " 23:59:59");
		
		String timeField = "create_time";
		JSONObject field = new JSONObject();
		field.put(timeField, timeVal);
		
		JSONObject range = new JSONObject();
		range.put("range", field);
		
		JSONObject matchAll = new JSONObject();
		matchAll.put("match_all", new JSONObject());
		
		JSONObject filtered = new JSONObject();
		filtered.put("query", matchAll);
		filtered.put("filter", range);
		
		JSONObject query = new JSONObject();
		query.put("filtered", filtered);
		
		reqBody.put("query", query);
		
		LOG.info("[Req] reqBody=" + reqBody);
		
		// reqUrl:
		// 		http://localhost:8080/dw-es-platform/searchUserByPage.rest?index=basis_device_info&type=user&from=20&fromDate=2015-01-16&toDate=2015-01-18
		// reqBody: 
		// 		{"query":{"filtered":{"query":{"match_all":{}},"filter":{"range":{"create_time":{"from":"1015-01-16 00:00:00","to":"2015-01-18 23:59:59"}}}}}}

		final RESTQueryEngine engine = super.newEngine();
		JSONObject res = engine.query(req);
		if(!res.isEmpty()) {
			return res;
		} else {
			return null;
		}
	}

}
