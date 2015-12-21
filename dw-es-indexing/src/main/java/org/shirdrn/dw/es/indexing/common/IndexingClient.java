package org.shirdrn.dw.es.indexing.common;

import java.io.Closeable;

public interface IndexingClient extends Closeable {

	String getName();
	void indexing() throws Exception;
}
