package com.sdt.common.file;

import java.io.File;

public class ReadDirectory {

	private String inputDir = "C:/Temp/filesplitter/original/";
	private String outputDir = "C:/Temp/filesplitter/splitted/";
	
	public ReadDirectory() {
		super();
	}

	public ReadDirectory(String originalDir, String spilltedDir) {
		super();
		this.inputDir = originalDir;
		this.outputDir = spilltedDir;
	}

	public File[] getFiles() {
		File dir = new File(this.inputDir);
		if(dir != null && dir.isDirectory())
			return dir.listFiles();
		
		return null;
	}

	public String getInputDir() {
		return inputDir;
	}

	public String getOutputDir() {
		return outputDir;
	}
	
}
