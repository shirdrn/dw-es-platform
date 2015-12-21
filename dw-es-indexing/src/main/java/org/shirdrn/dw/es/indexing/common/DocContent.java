package org.shirdrn.dw.es.indexing.common;

import org.elasticsearch.common.xcontent.XContentBuilder;

public class DocContent {

	private String id;
	private XContentBuilder content;
	
	public DocContent() {
		super();
	}
	
	public DocContent(String id, XContentBuilder content) {
		super();
		this.id = id;
		this.content = content;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public XContentBuilder getContent() {
		return content;
	}
	
	public void setContent(XContentBuilder content) {
		this.content = content;
	}
}
