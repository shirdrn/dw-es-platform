package org.shirdrn.dw.es.indexing.api;

import java.io.Closeable;

import org.elasticsearch.action.index.IndexRequest;

public interface IndexRequestCreator<RECORD, CONTENT> extends Iterable<IndexRequest>, ESIndex, Closeable {

	IndexingDataSource<RECORD, CONTENT> getIndexingDataSource();
	void setIndexingDataSource(IndexingDataSource<RECORD, CONTENT> indexingDataSource);
	
	IndexingDocMetric getIndexingDocMetric();
	void reset();
	
	ContentBuilder<RECORD, CONTENT> newContentBuilder();
	
}
