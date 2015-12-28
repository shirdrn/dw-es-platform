package org.shirdrn.dw.es.indexing.common;

import java.io.File;
import java.util.List;

import org.shirdrn.dw.es.indexing.api.IndexRequestCreator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public abstract class FileESIndexingClient<RECORD, CONTENT> extends AbstractESIndexingClient<RECORD, CONTENT> {

	public FileESIndexingClient(IndexRequestCreator<RECORD, CONTENT> indexRequestCreator) {
		super(indexRequestCreator);
	}

	private final List<File> inputFiles = Lists.newArrayList();
	
	public void addInputFiles(String... files) {
		Preconditions.checkArgument(files != null, "files == null ");
		for(String file : files) {
			addInputFile(file);
		}
	}
	
	public void addInputFile(String file) {
		inputFiles.add(new File(file));
	}
	
	protected void checkInputFiles() {
		for(File file : inputFiles) {
			Preconditions.checkArgument(file.exists(), "File doesn't exist: " + file);
		}
	}
	
	public List<File> getInputFiles() {
		return inputFiles;
	}
}
