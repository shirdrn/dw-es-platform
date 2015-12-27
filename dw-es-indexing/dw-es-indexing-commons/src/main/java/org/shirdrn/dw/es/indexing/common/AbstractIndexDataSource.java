package org.shirdrn.dw.es.indexing.common;

import org.shirdrn.dw.es.indexing.api.ContentBuilder;
import org.shirdrn.dw.es.indexing.api.IndexingDataSource;
import org.shirdrn.dw.es.indexing.api.IndexingDocMetric;

public abstract class AbstractIndexDataSource<RECORD, CONTENT> implements IndexingDataSource<RECORD, CONTENT> {

	private final ContentBuilder<RECORD, CONTENT> contentBuilder;
	private final IndexingDocMetric indexingDocMetric;
	protected final String index;
	protected final String type;
	
	public AbstractIndexDataSource(String index, String type, ContentBuilder<RECORD, CONTENT> contentBuilder) {
		super();
		this.index = index;
		this.type = type;
		this.contentBuilder = contentBuilder;
		indexingDocMetric = new DefaultIndexingDocMetric();
	}
	
	@Override
	public ContentBuilder<RECORD, CONTENT> getContentBuilder() {
		return contentBuilder;
	}
	
	@Override
	public IndexingDocMetric getIndexingDocMetric() {
		return indexingDocMetric;
	}
	
	@Override
	public String getIndex() {
		return index;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
}
