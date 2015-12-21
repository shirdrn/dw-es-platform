package org.shirdrn.dw.es.indexing.common;

public interface ContentBuilder<R> {

	DocContent build(R record);
}
