package org.shirdrn.dw.es.indexing.common;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.shirdrn.dw.es.indexing.api.IndexRequestCreator;
import org.shirdrn.dw.es.indexing.constants.ESConfigKeys;
import org.shirdrn.dw.es.utils.EsClientUtils;

public abstract class AbstractESIndexingClient<RECORD, CONTENT> extends RetryIndexingClient<RECORD, CONTENT, Client, IndexRequest> {

	private static final Log LOG = LogFactory.getLog(AbstractESIndexingClient.class);
	
	public AbstractESIndexingClient(IndexRequestCreator<RECORD, CONTENT> indexRequestCreator) {
		super(indexRequestCreator);
	}
	
	@Override
	protected Client buildClient() {
		return EsClientUtils.buildClient(clusterName, super.getHostNames(), super.getSettings());
	}
	
	@Override
	protected boolean retry(IndexRequest req) {
		try {
			ActionFuture<IndexResponse> future = client.index(req);
			future.actionGet(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	protected BulkProcessor buildBulkProcessor() {
		// https://www.elastic.co/guide/en/elasticsearch/client/java-api/2.0/java-docs-bulk-processor.html
		int bulkActions = config.getInt(ESConfigKeys.ES_INDEX_BULK_BATCH_COUNT, 1000);
		int bulkSizeMB = config.getInt(ESConfigKeys.ES_INDEX_BULK_FLUSH_SIZE_MB, 10);
		ByteSizeValue bulkSize = new ByteSizeValue(bulkSizeMB, ByteSizeUnit.MB);
		TimeValue flushInterval = TimeValue.timeValueSeconds(config.getInt(ESConfigKeys.ES_INDEX_BULK_FLUSH_INTERVAL_SECS, 300));
		int concurrentRequests = config.getInt(ESConfigKeys.ES_INDEX_BULK_CONCURRENT_REQUESTS, 8);
		
		// create bulk processor
		final BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {

			public void afterBulk(long id, BulkRequest req, BulkResponse resp) {
			}

			@SuppressWarnings("unchecked")
			public void afterBulk(long id, BulkRequest req, Throwable cause) {
				for(ActionRequest<IndexRequest> ar : req.requests()) {
					IndexRequest ir = (IndexRequest) ar;
					AbstractESIndexingClient.this.addRetryingRequest(ir);
				}
			}

			public void beforeBulk(long id, BulkRequest req) {
			}
			
		})
		.setName(name)
		.setBulkActions(bulkActions)
		.setBulkSize(bulkSize)
		.setFlushInterval(flushInterval)
		.setConcurrentRequests(concurrentRequests)
		.build();
		
		return bulkProcessor;
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		client.close();		
	}
	
	@Override
	protected RetryFailurePolicy<IndexRequest> getRetryFailurePolicy() {
		return new DefaultRetryFailurePolicy();
	}
	
	private final class DefaultRetryFailurePolicy implements RetryFailurePolicy<IndexRequest> {

		@Override
		public void process(IndexRequest req) {
			LOG.info(req.sourceAsMap());			
		}
		
	}

}
