package org.shirdrn.dw.es.indexing.simple.clients;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.shirdrn.dw.es.indexing.api.IndexRequestCreator;
import org.shirdrn.dw.es.indexing.common.FileESIndexingClient;
import org.shirdrn.dw.es.indexing.simple.creators.UserInfoIndexRequestCreator;

public class TestUserInfoESIndexingClient {

	private static final Log LOG = LogFactory.getLog(TestUserInfoESIndexingClient.class);
	private FileESIndexingClient<String, XContentBuilder> indexingClient;
	
	@Before
	public void setUp() {
		final String index = "user_info";
		final String type = "user";
		LOG.info("Args: index=" + index + ", type=" + type);
		
		final IndexRequestCreator<String, XContentBuilder> indexRequestCreator = new UserInfoIndexRequestCreator(index, type);
		assertNotNull(indexRequestCreator);
		
		indexingClient = new UserInfoESIndexingClient(indexRequestCreator);
	}
	
	@Test
	public void indexing() throws Exception {
		String file = "E:\\Develop\\eclipse-jee-kepler-SR2\\workspace\\dw-es-platform\\dw-es-indexing\\dw-es-indexing-clients\\dw-es-indexing-simple\\src\\test\\resources\\user_info.txt";
		assertNotNull(indexingClient);
		indexingClient.addInputFile(file);
		indexingClient.indexing();
	}
	
	@After
	public void tearDown() throws IOException {
		indexingClient.close();
	}
}
