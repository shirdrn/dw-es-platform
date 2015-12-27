package org.shirdrn.dw.es.indexing.api;

import java.io.Closeable;

public interface IndexingClient<RECORD, CONTENT> extends Closeable {

	String getName();
	void indexing() throws Exception;
	
	IndexRequestCreator<RECORD, CONTENT> getIndexRequestCreator();
}
