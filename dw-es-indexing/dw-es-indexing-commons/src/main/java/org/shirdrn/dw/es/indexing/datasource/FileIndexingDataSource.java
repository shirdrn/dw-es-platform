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
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.shirdrn.dw.es.indexing.api.ContentBuilder;
import org.shirdrn.dw.es.indexing.api.DocContent;
import org.shirdrn.dw.es.indexing.common.AbstractIndexDataSource;
import org.shirdrn.dw.es.utils.IOUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public class FileIndexingDataSource extends AbstractIndexDataSource<String, XContentBuilder> {

	private static final Log LOG = LogFactory.getLog(FileIndexingDataSource.class);
	private final String inputPath;
	private FileIterator iterator;
	
	public FileIndexingDataSource(String index, String type, ContentBuilder<String, XContentBuilder> contentBuilder, File inputFile) {
		super(index, type, contentBuilder);
		Preconditions.checkArgument(inputFile != null, "inputFile == null");
		Preconditions.checkArgument(inputFile.exists(), "inputFile DOES NOT exist!");
		this.inputPath = inputFile.getAbsolutePath();
	}
	
	public FileIndexingDataSource(String index, String type, ContentBuilder<String, XContentBuilder> contentBuilder, String inputpath) {
		super(index, type, contentBuilder);
		Preconditions.checkArgument(inputpath != null, "inputpath == null");
		Preconditions.checkArgument(new File(inputpath).exists(), "inputpath DOES NOT exist!");
		this.inputPath = inputpath;
	}
	
	@Override
	public Iterator<IndexRequest> createIterator() {
		iterator = new FileIterator(inputPath);
		return iterator;
	}
	
	protected final class FileIterator implements Iterator<IndexRequest>, Closeable {

		private BufferedReader reader = null;
		private String line = null;
		
		public FileIterator(String file) {
			InputStreamReader inputStreamReader = null;
			try {
				inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
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
					final DocContent<XContentBuilder> content = getContentBuilder().build(line);
					if(content == null) {
						getIndexingDocMetric().incrBadDocCount();
					} else {
						try {
							indexRequest = new IndexRequest(index, type, content.getId());
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
