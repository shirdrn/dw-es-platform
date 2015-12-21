package org.shirdrn.dw.es.indexing.common;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.dw.es.utils.NamedThreadFactory;

public abstract class RetryIndexingClient<CLIENT, T> extends AbstractIndexingClient {

	private static final Log LOG = LogFactory.getLog(RetryIndexingClient.class);
	protected final CLIENT client;
	private final ExecutorService executorService;
	private final BlockingQueue<T> retryingRequestQueue;
	private volatile boolean running = true;
	private final RetryFailurePolicy<T> retryFailurePolicy;
	
	public RetryIndexingClient(String index, String type, Class<? extends IndexRequestCreator> clazz) {
		super(index, type, clazz);
		client = buildClient();
		executorService = Executors.newCachedThreadPool(new NamedThreadFactory("RETRY"));
		retryingRequestQueue = new LinkedBlockingQueue<T>();
		
		executorService.execute(new RetryingWorker());
		LOG.info("RetryingWorker started.");
		
		retryFailurePolicy = getRetryFailurePolicy();
	}
	
	protected abstract CLIENT buildClient();
	protected abstract boolean retry(T req);

	protected void addRetryingRequest(T req) {
		retryingRequestQueue.add(req);
	}
	
	private final class RetryingWorker extends Thread {
		@Override
		public void run() {
			while(running) {
				try {
					while(!retryingRequestQueue.isEmpty()) {
						T req = retryingRequestQueue.poll();
						boolean result = retry(req);
						if(!result) {
							LOG.debug("Process failure request: " + req);
							retryFailurePolicy.process(req);
						}
					}
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		running = false;
		executorService.shutdown();
		LOG.info("RetryingWorker exits.");
	}
	
	protected abstract RetryFailurePolicy<T> getRetryFailurePolicy();
	
	public interface RetryFailurePolicy<T> {
		void process(T req);
	}
	
}
