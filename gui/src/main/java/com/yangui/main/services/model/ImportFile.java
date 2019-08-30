package com.yangui.main.services.model;

import javafx.beans.property.SimpleStringProperty;



public class ImportFile implements Comparable<ImportFile>{
	
	
	/** Copy constructor */
	public ImportFile copy (){
		ImportFile dst = new ImportFile();
		dst.setSrcName(this.getSrcName());
		dst.setDstName(this.getDstName());
		dst.setStatus(this.getStatus());
		dst.setAction(this.getAction());
		dst.setContent(this.getContent());
		return dst;
	}
	
	
	@Override
	public String toString() {
		return getDstName() + " " + getSrcName();
	}
	
	
	@Override
	public int compareTo(ImportFile o) {
		int result = dstName.getValue().compareTo(o.dstName.getValue());
		if(result == 0)
			result = srcName.getValue().compareTo(o.srcName.getValue());
		return result;
	}
	
	private final SimpleStringProperty srcName = new SimpleStringProperty("");	//original file name
	private SimpleStringProperty dstName = new SimpleStringProperty("");		//moduleName+moduleRevision
	private SimpleStringProperty status = new SimpleStringProperty("");			//ImportFileStatus: OK, ERR_NO_NAMESPACE, ERR_NO_NAME, ERR_NO_REVISION, ERR_EMPTY, ERR_DUPLICATE_REPO, ERR_DUPLICATE_IMPORT
	private SimpleStringProperty action = new SimpleStringProperty("");			//(Copy & Leave || Overwrite & Leave) ImportFileAction: LEAVE, COPY, OVERWRITE
	
	private String content = "";	
	
	public ImportFile() {
		this("", "", "", "");
	}
	
	public ImportFile(String srcName, String dstName, String status, String action) {
		super();
		setSrcName(srcName);
		setDstName(dstName);
		setStatus(status);
		setAction(action);
		setContent("");
	}
	
	
	public String getSrcName() {
		return srcName.getValue();
	}

	public void setSrcName(String srcName) {
		this.srcName.set(srcName);
	}

	public String getDstName() {
		return dstName.getValue();
	}

	public void setDstName(String dstName) {
		this.dstName.set(dstName);
	}

	public String getStatus() {
		return status.getValue();
	}

	public void setStatus(String status) {
		this.status.set(status);
	}

	public String getAction() {
		return action.getValue();
	}

	public void setAction(String action) {
		this.action.set(action);
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
