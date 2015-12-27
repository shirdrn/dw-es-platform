package org.shirdrn.dw.es.indexing.api;

public interface DocContent<CONTENT> {

	String getId();
	
	CONTENT getContent();
	void setContent(CONTENT content);
}
