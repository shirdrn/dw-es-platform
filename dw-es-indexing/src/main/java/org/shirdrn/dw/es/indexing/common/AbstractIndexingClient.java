package org.shirdrn.dw.es.indexing.common;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.dw.es.indexing.constants.ESConfigKeys;
import org.shirdrn.dw.es.utils.ReflectionUtils;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class AbstractIndexingClient implements IndexingClient {

	private static final Log LOG = LogFactory.getLog(AbstractIndexingClient.class);
	protected final Configuration config;
	private final String defaultConfigurationFile = "elasticsearch.properties";
	protected final IndexRequestCreator indexRequestCreator;
	protected final String index;
	protected final String type;
	protected final String clusterName;
	
	protected final String name;
	private final List<String> hostNames = Lists.newArrayList();
	private final Map<String, String> settings = Maps.newHashMap();

	public AbstractIndexingClient(String index, String type, Class<? extends IndexRequestCreator> clazz) {
		super();
		this.index = index;
		this.type = type;
		name = index;
		
		// create configuration object
		Configuration c = null;
		try {
			c = new PropertiesConfiguration(defaultConfigurationFile);
		} catch (ConfigurationException e) {
			Throwables.propagate(e);
		}
		indexRequestCreator = ReflectionUtils.newInstance(clazz, new Object[] {index, type});
		LOG.info("Index request creator created: " + indexRequestCreator);
		
		this.config = c;
		clusterName = config.getString("cluster.name", "elasticsearch");
		
		loadSettings();
		readEsHosts();
	}

	private void readEsHosts() {
		try {
			String[] hosts = config.getStringArray(ESConfigKeys.ES_SERVER_HOST_NAMES);
			for(String host : hosts) {
				hostNames.add(host);
			}
		} catch (Exception e) {
			Throwables.propagate(e);
		}
		LOG.info("Elasticsearch hosts: " + hostNames);
	}

	private void loadSettings() {
		Iterator<String> iter = config.getKeys();
		while(iter.hasNext()) {
			String key = iter.next();
			settings.put(key, config.getProperty(key).toString());
		}
	}
	
	protected List<String> getHostNBames() {
		return hostNames;
	}

	protected Map<String, String> getSettings() {
		return settings;
	}
	
	@Override
	public String getName() {
		return name;
	}

}
