package org.shirdrn.dw.es.indexing.datasource;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.index.IndexRequest;
import org.shirdrn.dw.es.indexing.common.AbstractIndexDataSource;
import org.shirdrn.dw.es.indexing.common.ContentBuilder;
import org.shirdrn.dw.es.indexing.common.DocContent;
import org.shirdrn.dw.es.indexing.common.ESIndex;
import org.shirdrn.dw.es.utils.IOUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public class FileIndexingDataSource extends AbstractIndexDataSource<String> {

	private static final Log LOG = LogFactory.getLog(FileIndexingDataSource.class);
	private final String inputFile;
	private FileIterator iterator;
	
	public FileIndexingDataSource(ESIndex esIndex, ContentBuilder<String> contentBuiller, String inputFile) {
		super(esIndex, contentBuiller);
		Preconditions.checkArgument(inputFile != null, "inputFile == null");
		Preconditions.checkArgument(new File(inputFile).exists(), "inputFile DOES NOT exist!");
		this.inputFile = inputFile;
	}
	
	@Override
	public Iterator<IndexRequest> createIterator() {
		iterator = new FileIterator();
		return iterator;
	}
	
	private final class FileIterator implements Iterator<IndexRequest>, Closeable {

		private BufferedReader reader = null;
		private String line = null;
		
		public FileIterator() {
			InputStreamReader inputStreamReader = null;
			try {
				inputStreamReader = new InputStreamReader(new FileInputStream(inputFile), "UTF-8");
				reader = new BufferedReader(inputStreamReader);
				line = reader.readLine();
			} catch (IOException e) {
				IOUtils.closeQuietly(inputStreamReader, reader);
				Throwables.propagate(e);
			}
		}
		
		@Override
		public boolean hasNext() {
			return line != null;
		}

		@Override
		public IndexRequest next() {
			IndexRequest indexRequest = null;
			try {
				while((line = reader.readLine()) != null) {
					getIndexingDocMetric().incrTotalInputDocCount();
					final DocContent content = getContentBuilder().build(line);
					if(content == null) {
						getIndexingDocMetric().incrBadDocCount();
					} else {
						try {
							indexRequest = new IndexRequest(esIndex.getIndex(), esIndex.getType(), content.getId());
							indexRequest.source(content.getContent());
							getIndexingDocMetric().incrIndexedDocCount();
							break;
						} catch (Exception e) {
							getIndexingDocMetric().incrBadDocCount();
						}
					}
				}
			} catch (IOException e) {
				Throwables.propagate(e);
			}
			reportProgress(false);
			return indexRequest;
		}

		protected void reportProgress(boolean force) {
			final int modFactor = 1000;
			if(force) {
				LOG.info("Progress: totalInputDocCount=" + getIndexingDocMetric().totalInputDocCount() + 
						", indexedDocCount=" + getIndexingDocMetric().indexedDocCount() + 
						", badDocCount=" + getIndexingDocMetric().badDocCount());
			} else {
				if(getIndexingDocMetric().totalInputDocCount() % modFactor == 0) {
					LOG.info("Progress: totalInputDocCount=" + getIndexingDocMetric().totalInputDocCount() + 
							", indexedDocCount=" + getIndexingDocMetric().indexedDocCount() + 
							", badDocCount=" + getIndexingDocMetric().badDocCount());
				}
			}
		}
		
		@Override
		public void remove() {
			Throwables.propagate(new UnsupportedOperationException("Unsupported Operation!"));			
		}

		@Override
		public void close() throws IOException {
			reportProgress(true);
			IOUtils.closeQuietly(reader);	
		}
		
	}
	
	@Override
	public void close() throws IOException {
		IOUtils.closeQuietly(iterator);		
	}

}
