package org.shirdrn.dw.es.indexing.constants;

public interface ESConfigKeys {

	String ES_INDEX_BULK_BATCH_COUNT = "es.index.bulk.batch.count";
	String ES_INDEX_BULK_FLUSH_SIZE_MB = "es.index.bulk.flush.size.mb";
	String ES_INDEX_BULK_FLUSH_INTERVAL_SECS = "es.index.bulk.flush.interval.secs";
	String ES_INDEX_BULK_CONCURRENT_REQUESTS = "es.index.bulk.concurrent.requests";
	String ES_INDEX_BULK_PROCESSOR_MAX_TIMEOUT_SECS = "es.index.bulk.processor.max.timeout.secs";
	String ES_SERVER_HOST_NAMES = "es.server.host.names";
}
