package org.shirdrn.dw.es.indexing.common;

import org.shirdrn.dw.es.indexing.api.DocContent;

public class GenericDocContent<T> implements DocContent<T> {

	private final  String id;
	private T content;
	
	public GenericDocContent(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setContent(T content) {
		this.content = content;		
	}
	
	@Override
	public T getContent() {
		return content;
	}
}
