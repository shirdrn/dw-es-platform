package org.shirdrn.dw.es.search.rest;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.shirdrn.dw.es.utils.IOUtils;

import com.google.common.base.Preconditions;

public abstract class AbstractRESTQueryEngine extends RESTQueryEngine {
	
	protected CloseableHttpClient httpClient;

	public AbstractRESTQueryEngine(String clusterName, String... esNodes) {
		super(clusterName, esNodes);
	}
	
	@Override
	public void close() throws IOException {
		Preconditions.checkArgument(httpClient != null, "httpClient == null !");
		httpClient.close();
	}
	
	@Override
	public JSONObject httpGet(String reqUrl) {
		HttpGet get = new HttpGet(reqUrl);
		CloseableHttpResponse response = null;
		JSONObject res = null;
		try {
			response = httpClient.execute(get);
			res = processResponse(response);
			if(res == null) {
				res = new JSONObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(response);
		}
		
		return res;
	}

	private JSONObject processResponse(CloseableHttpResponse response) throws ParseException, IOException {
		JSONObject res = null;
		if(response != null) {
			StatusLine sl = response.getStatusLine();
			if(sl != null) {
				if(sl.getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					if(entity != null) {
						res = JSONObject.fromObject(EntityUtils.toString(entity));
					}
				}
			}
		}
		return res;
	}
	
	@Override
	public JSONObject httpPost(String reqUrl, JSONObject body) {
		StringEntity entity = new StringEntity(body.toString(),
		        ContentType.create("text/json", Consts.UTF_8));
		HttpPost post = new HttpPost(reqUrl);
		post.setEntity(entity);
		
		CloseableHttpResponse response = null;
		JSONObject res = null;
		try {
			response = httpClient.execute(post);
			res = processResponse(response);
			if(res == null) {
				res = new JSONObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(response);
		}
		return res;
	}

}
