package org.shirdrn.dw.es.indexing.common;

public interface IndexingDocMetric {

	int totalInputDocCount();
	int indexedDocCount();
	int badDocCount();

	void reset();
	
	void incrTotalInputDocCount();
	void incrIndexedDocCount();
	void incrBadDocCount();
	
}
