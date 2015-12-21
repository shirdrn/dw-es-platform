package org.shirdrn.dw.es.search.common;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.shirdrn.dw.es.search.constants.ServletConfigKeys;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public abstract class ClientEmbeddedAction<RESPONSE> extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(ClientEmbeddedAction.class);
	protected String clusterName;
	protected QueryEngine<?, ?, ?> queryEngine;
	protected int pageSize = 10;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		clusterName = config.getInitParameter(ServletConfigKeys.ES_CLUSTER_NAME);
		Preconditions.checkArgument(clusterName != null, "clusterName == null");
		LOG.info("ElasticSearch cluster: clusterName=" + clusterName);
		
		String configPageSize = config.getInitParameter(ServletConfigKeys.ES_PAGE_SIZE);
    	try {
			pageSize = Integer.parseInt(configPageSize);
		} catch (Exception e) { }
    	LOG.info("ElasticSearch config: pageSize=" + pageSize);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			RESPONSE res = processRequest(request);
			if(res != null) {
				response.getWriter().print(res.toString());
			} else {
				response.sendError(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Process request based on actual business logic.
	 * @param request
	 * @throws Exception
	 */
	protected abstract RESPONSE processRequest(HttpServletRequest request) throws Exception;
	
	@Override
	public void destroy() {
		super.destroy();
		try {
			queryEngine.close();
		} catch (IOException e) {
			Throwables.propagate(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T newQueryEngine(Class<T> engineClazz) {
		return (T) queryEngine;
	}
	
}
