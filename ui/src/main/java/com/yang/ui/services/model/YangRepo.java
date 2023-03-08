package com.yang.ui.services.model;



public class YangRepo implements Comparable<YangRepo>{

	@Override
	public String toString() {
		return schemaFolder;
	}
	
	
	@Override
	public int compareTo(YangRepo o) {
		int result = schemaFolder.compareTo(o.schemaFolder);
		if(result == 0)
			result = schemaFolder.compareTo(o.schemaFolder);
			
		return result;
	}
	
	private String schemaFolder;

	private int fileCount;
	
	private long fileSize;
	

	public YangRepo() {
		super();
		this.schemaFolder = "";
		this.fileCount = 0;
		this.fileSize = 0L;
	}
	
	
	public YangRepo(String schemaFolder, int fileCount, long fileSize) {
		super();
		this.schemaFolder = schemaFolder;
		this.fileCount = fileCount;
		this.fileSize = fileSize;
	}
	
	
	public String getSchemaFolder() {
		return schemaFolder;
	}


	public void setSchemaFolder(String schemaFolder) {
		this.schemaFolder = schemaFolder;
	}


	public int getFileCount() {
		return fileCount;
	}


	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}


	public long getFileSize() {
		return fileSize;
	}


	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
}