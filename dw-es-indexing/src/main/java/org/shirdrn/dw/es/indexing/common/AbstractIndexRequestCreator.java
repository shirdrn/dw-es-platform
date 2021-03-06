package org.shirdrn.dw.es.indexing.common;

import com.google.common.base.Preconditions;

public abstract class AbstractIndexRequestCreator implements IndexRequestCreator {

	protected final String index;
	protected final String type;
	private final IndexingDocMetric indexingDocMetric = new DefaultIndexingDocMetric();
	protected IndexingDataSource<?> indexingDataSource;
	
	public AbstractIndexRequestCreator(String index, String type) {
		super();
		Preconditions.checkArgument(index != null, "index == null");
		Preconditions.checkArgument(type != null, "type == null");
		this.index = index;
		this.type = type;
	}

	@Override
	public String getIndex() {
		return index;
	}

	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public void reset() {
		indexingDocMetric.reset();
	}
	
	@Override
	public IndexingDocMetric getIndexingDocMetric() {
		return indexingDocMetric;
	}
	
	@Override
	public void setIndexingDataSource(IndexingDataSource<?> indexingDataSource) {
		this.indexingDataSource = indexingDataSource;		
	}
	
	@Override
	public IndexingDataSource<?> getIndexingDataSource() {
		return indexingDataSource;
	}

}
