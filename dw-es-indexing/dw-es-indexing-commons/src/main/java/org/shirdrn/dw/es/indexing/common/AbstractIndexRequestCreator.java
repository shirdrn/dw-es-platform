package org.shirdrn.dw.es.indexing.common;

import org.shirdrn.dw.es.indexing.api.IndexRequestCreator;
import org.shirdrn.dw.es.indexing.api.IndexingDataSource;
import org.shirdrn.dw.es.indexing.api.IndexingDocMetric;

import com.google.common.base.Preconditions;

public abstract class AbstractIndexRequestCreator<RECORD, CONTENT> implements IndexRequestCreator<RECORD, CONTENT> {

	protected final String index;
	protected final String type;
	private final IndexingDocMetric indexingDocMetric = new DefaultIndexingDocMetric();
	protected IndexingDataSource<RECORD, CONTENT> indexingDataSource;
	
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
	public void setIndexingDataSource(IndexingDataSource<RECORD, CONTENT> indexingDataSource) {
		this.indexingDataSource = indexingDataSource;		
	}
	
	@Override
	public IndexingDataSource<RECORD, CONTENT> getIndexingDataSource() {
		return indexingDataSource;
	}

}
