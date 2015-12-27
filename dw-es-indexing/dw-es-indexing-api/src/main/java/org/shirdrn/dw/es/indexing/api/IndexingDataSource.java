package org.shirdrn.dw.es.indexing.api;

import java.io.Closeable;
import java.util.Iterator;

import org.elasticsearch.action.index.IndexRequest;

public interface IndexingDataSource<RECORD, CONTENT> extends ESIndex, Closeable {

	Iterator<IndexRequest> createIterator();
	ContentBuilder<RECORD, CONTENT> getContentBuilder();
	IndexingDocMetric getIndexingDocMetric();
}
