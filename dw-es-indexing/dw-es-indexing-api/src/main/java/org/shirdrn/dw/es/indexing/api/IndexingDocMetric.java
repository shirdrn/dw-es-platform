package org.shirdrn.dw.es.indexing.api;

public interface IndexingDocMetric {

	int totalInputDocCount();
	int indexedDocCount();
	int badDocCount();

	void reset();
	
	void incrTotalInputDocCount();
	void incrIndexedDocCount();
	void incrBadDocCount();
	
}
