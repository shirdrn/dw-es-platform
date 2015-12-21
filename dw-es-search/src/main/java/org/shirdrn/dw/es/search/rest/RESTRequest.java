package org.shirdrn.dw.es.search.rest;

import java.util.Map;

import net.sf.json.JSONObject;

import com.google.common.collect.Maps;

public class RESTRequest {

	private String index;
	private String type;
	private final Map<String, String> urlParameters = Maps.newHashMap();
	private JSONObject body;
	
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public JSONObject getBody() {
		return body;
	}
	public void setBody(JSONObject body) {
		this.body = body;
	}
	public Map<String, String> getUrlParameters() {
		return urlParameters;
	}
	
}
