package org.shirdrn.dw.es.search.common;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

public interface QueryEngine<CLIENT, REQUEST, RESPONSE> extends Closeable {

	void createClient();
	CLIENT getClient();
	
	RESPONSE query(REQUEST request);
	RESPONSE query(REQUEST request, long timeout, TimeUnit timeUnit);
}
