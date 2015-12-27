package org.shirdrn.dw.es.indexing.api;

public interface ContentBuilder<RECORD, CONTENT> {

	DocContent<CONTENT> build(RECORD record);
}
