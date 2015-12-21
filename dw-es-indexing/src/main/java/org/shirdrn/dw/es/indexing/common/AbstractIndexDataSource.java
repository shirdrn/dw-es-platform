package org.shirdrn.dw.es.indexing.common;

public abstract class AbstractIndexDataSource<R> implements IndexingDataSource<R> {

	protected final ESIndex esIndex;
	private final ContentBuilder<R> contentBuiller;
	private final IndexingDocMetric indexingDocMetric;
	
	public AbstractIndexDataSource(ESIndex esIndex, ContentBuilder<R> contentBuiller) {
		super();
		this.esIndex = esIndex;
		this.contentBuiller = contentBuiller;
		indexingDocMetric = new DefaultIndexingDocMetric();
	}
	
	@Override
	public ContentBuilder<R> getContentBuilder() {
		return contentBuiller;
	}
	
	@Override
	public IndexingDocMetric getIndexingDocMetric() {
		return indexingDocMetric;
	}
	
}
