package org.shirdrn.dw.es.indexing.simple.clients;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.shirdrn.dw.es.indexing.api.ContentBuilder;
import org.shirdrn.dw.es.indexing.api.IndexRequestCreator;
import org.shirdrn.dw.es.indexing.api.IndexingDataSource;
import org.shirdrn.dw.es.indexing.common.FileESIndexingClient;
import org.shirdrn.dw.es.indexing.constants.ESConfigKeys;
import org.shirdrn.dw.es.indexing.datasource.FileIndexingDataSource;
import org.shirdrn.dw.es.indexing.simple.creators.UserInfoIndexRequestCreator;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public class UserInfoESIndexingClient extends FileESIndexingClient<String, XContentBuilder> {

	private static final Log LOG = LogFactory.getLog(UserInfoESIndexingClient.class);
	
	public UserInfoESIndexingClient(IndexRequestCreator<String, XContentBuilder> indexRequestCreator) {
		super(indexRequestCreator);
	}
	
	@Override
	public void indexing() throws Exception {
		Preconditions.checkArgument(!super.getInputFiles().isEmpty(), 
				"inputFiles.isEmpty(), plz invoke addInputFiles() or addInputFile()!");
		checkInputFiles();
		
		final IndexRequestCreator<String, XContentBuilder> creator = indexRequestCreator;
		final ContentBuilder<String, XContentBuilder> contentBuilder = creator.newContentBuilder();
		final String index = indexRequestCreator.getIndex();
		final String type = indexRequestCreator.getType();
		for(File file : super.getInputFiles()) {
			IndexingDataSource<String, XContentBuilder> indexingDataSource = new FileIndexingDataSource(index, type, contentBuilder, file.getAbsolutePath());
			creator.setIndexingDataSource(indexingDataSource);
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
		
		final IndexRequestCreator<String, XContentBuilder> indexRequestCreator = new UserInfoIndexRequestCreator(index, type);
		final UserInfoESIndexingClient indexingClient = new UserInfoESIndexingClient(indexRequestCreator);
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
