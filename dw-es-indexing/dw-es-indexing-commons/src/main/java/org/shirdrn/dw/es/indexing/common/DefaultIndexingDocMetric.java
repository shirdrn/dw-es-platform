package org.shirdrn.dw.es.indexing.common;

import org.shirdrn.dw.es.indexing.api.IndexingDocMetric;

public class DefaultIndexingDocMetric implements IndexingDocMetric {

	protected int totalInputDocCount;
	protected int indexedDocCount;
	protected int badDocCount;
	
	@Override
	public int totalInputDocCount() {
		return totalInputDocCount;
	}

	@Override
	public int indexedDocCount() {
		return indexedDocCount;
	}

	@Override
	public int badDocCount() {
		return badDocCount;
	}

	@Override
	public void reset() {
		totalInputDocCount = 0;
		indexedDocCount = 0;
		badDocCount = 0;
	}

	@Override
	public void incrTotalInputDocCount() {
		++totalInputDocCount;		
	}

	@Override
	public void incrIndexedDocCount() {
		++indexedDocCount;		
	}

	@Override
	public void incrBadDocCount() {
		++badDocCount;		
	}

}
