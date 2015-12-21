package org.shirdrn.dw.es.indexing.common;

import java.io.Closeable;
import java.util.Iterator;

import org.elasticsearch.action.index.IndexRequest;

public interface IndexingDataSource<R> extends Closeable {

	Iterator<IndexRequest> createIterator();
	ContentBuilder<R> getContentBuilder();
	IndexingDocMetric getIndexingDocMetric();
}
