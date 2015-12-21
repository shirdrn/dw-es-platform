package org.shirdrn.dw.es.indexing;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.shirdrn.dw.es.indexing.common.ESIndexingClient;
import org.shirdrn.dw.es.indexing.common.IndexRequestCreator;
import org.shirdrn.dw.es.indexing.constants.ESConfigKeys;
import org.shirdrn.dw.es.indexing.creators.UserInfoIndexRequestCreator;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public class UserInfoESIndexingClient extends ESIndexingClient {

	private static final Log LOG = LogFactory.getLog(UserInfoESIndexingClient.class);
	
	public UserInfoESIndexingClient(String index, String type, Class<? extends IndexRequestCreator> clazz) {
		super(index, type, clazz);
	}
	
	@Override
	public void indexing() throws Exception {
		Preconditions.checkArgument(!super.getInputFiles().isEmpty(), 
				"inputFiles.isEmpty(), plz invoke addInputFiles() or addInputFile()!");
		checkInputFiles();
		
		for(File file : super.getInputFiles()) {
			final UserInfoIndexRequestCreator creator = new UserInfoIndexRequestCreator(index, type, file.getAbsolutePath());
			final BulkProcessor bulkProcessor = buildBulkProcessor();
			Iterator<IndexRequest> iter = creator.iterator();
			try {
				while(iter.hasNext()) {
					IndexRequest request = iter.next();
					if(request != null) {
						bulkProcessor.add(request);
					}
				}
			} catch (Exception e) {
				Throwables.propagate(e);
			} finally {
				// close bulk processor
				bulkProcessor.awaitClose(
						config.getLong(ESConfigKeys.ES_INDEX_BULK_PROCESSOR_MAX_TIMEOUT_SECS, 600), 
						TimeUnit.SECONDS);
				creator.close();
				LOG.info("STAT: totalInputDocCount=" + creator.getIndexingDocMetric().totalInputDocCount() + 
						", indexedDocCount=" + creator.getIndexingDocMetric().indexedDocCount() + 
						", badDocCount=" + creator.getIndexingDocMetric().badDocCount());
				creator.reset();
			}
			bulkProcessor.close();
		}
		
	}
	
	// java -jar dw-es-indexing-0.0.1-SNAPSHOT-jar-with-dependencies.jar user_info user /home/shirdrn/es/user_info/000000_0 /home/shirdrn/es/user_info/000001_0 /home/shirdrn/es/user_info/000002_0 /home/shirdrn/es/user_info/000003_0 /home/shirdrn/es/user_info/000004_0 /home/shirdrn/es/user_info/000005_0 /home/shirdrn/es/user_info/000006_0 /home/shirdrn/es/user_info/000007_0 /home/shirdrn/es/user_info/000008_0 /home/shirdrn/es/user_info/000009_0
	public static void main(String[] args) throws Exception {
		if(args.length < 3) {
			System.err.println("Usage:\n" + 
								"    java -jar dw-es-indexing-<VERSION>-jar-with-dependencies.jar <index> <type> <file1>[ <file2> ...<fileN> ]\n" +
								"Example:\n " + 
								"    java -jar dw-es-indexing-0.0.1-SNAPSHOT-jar-with-dependencies.jar user_info user /home/shirdrn/es/user_info/input/000000_0");
			System.exit(-1);
		}
		String index = args[0];
		String type = args[1];
		LOG.info("Args: index=" + index + ", type=" + type);
		
		final UserInfoESIndexingClient indexingClient = 
				new UserInfoESIndexingClient(index, type, UserInfoIndexRequestCreator.class);
		for (int i = 2; i < args.length; i++) {
			indexingClient.addInputFile(args[i]);
			LOG.info("Input file: " + args[i]);
		}
		
		try {
			indexingClient.indexing();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indexingClient.close();
		}
		
	}

}
