package org.shirdrn.dw.es.indexing.datasource;

import java.io.Closeable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.shirdrn.dw.es.indexing.api.ContentBuilder;
import org.shirdrn.dw.es.utils.IOUtils;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public class DirectoryIndexingDataSource extends FileIndexingDataSource {

	private final File rootDir;
	private File[] dataFiles;
	
	public DirectoryIndexingDataSource(String index, String type, ContentBuilder<String, XContentBuilder> contentBuilder, 
			String directory, FilenameFilter filenameFilter) {
		super(index, type, contentBuilder, directory);
		this.rootDir = new File(directory);
		if(filenameFilter != null) {
			dataFiles = rootDir.listFiles(filenameFilter);
		}
	}
	
	@Override
	public Iterator<IndexRequest> createIterator() {
		return new DirectoryIterator(dataFiles);
	}
	
	protected final class DirectoryIterator implements Iterator<IndexRequest>, Closeable {
		
		private final List<FileIterator> iterators = Lists.newArrayList();
		private FileIterator currentIterator;
		private int index = 0;
		
		public DirectoryIterator(File[] files) {
			for(File file : files) {
				iterators.add(new FileIterator(file.getAbsolutePath()));
			}
		}

		@Override
		public boolean hasNext() {
			boolean continueTo = false;
			if(currentIterator == null) {
				currentIterator = iterators.get(index);
			}
			if(currentIterator.hasNext()) {
				continueTo = true;
			} else {
				++index;
				if(index < iterators.size()) {
					IOUtils.closeQuietly(currentIterator);
					currentIterator = iterators.get(index);
					continueTo = true;
				} else {
					continueTo = false;
				}
			}
			return continueTo;
		}

		@Override
		public IndexRequest next() {
			return currentIterator.next();
		}

		@Override
		public void remove() {
			Throwables.propagate(new UnsupportedOperationException("Unsupported Operation!"));				
		}
		
		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub
			
		}
	}

}
