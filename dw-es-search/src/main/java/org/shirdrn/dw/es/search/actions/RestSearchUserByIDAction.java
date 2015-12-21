package org.shirdrn.dw.es.search.actions;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.shirdrn.dw.es.search.rest.RESTClientEmbeddedAction;
import org.shirdrn.dw.es.search.rest.RESTQueryEngine;
import org.shirdrn.dw.es.search.rest.RESTRequest;

/**
 * Servlet implementation class SearchUserDeviceByUdidAction
 */
public class RestSearchUserByIDAction extends RESTClientEmbeddedAction {
	
	private static final long serialVersionUID = 1L;
       
    public RestSearchUserByIDAction() {
        super();
    }

	@Override
	protected JSONObject processRequest(HttpServletRequest request) throws Exception {
		RESTRequest req = new RESTRequest();
		
		String index = request.getParameter("index");
    	String type = request.getParameter("type");
    	req.setIndex(index);
    	req.setType(type);
    	
		String udid = request.getParameter("udid");
		req.getUrlParameters().put("udid", udid);
		
		// http://localhost:8080/dw-es-platform/searchUserByID.rest?index=basis_device_info&type=user&udid=37ce41a62d5d085f0d8aacf242fcf608
		
		final RESTQueryEngine engine = super.newEngine();
		JSONObject res = engine.query(req);
		if(!res.isEmpty()) {
			return res;
		} else {
			return null;
		}		
	}

}
