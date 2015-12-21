package org.shirdrn.dw.es.indexing.common;

import java.io.Closeable;

import org.elasticsearch.action.index.IndexRequest;

public interface IndexRequestCreator extends Iterable<IndexRequest>, ESIndex, Closeable {

	IndexingDataSource<?> getIndexingDataSource();
	void setIndexingDataSource(IndexingDataSource<?> indexingDataSource);
	
	IndexingDocMetric getIndexingDocMetric();
	void reset();
}
