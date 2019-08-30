package com.yangui.main.services.model;

import java.util.ArrayList;
import java.util.List;

public enum ImportFileAction {
		
		LEAVE 					("Leave"),
	    COPY   					("Copy"),
	    OVERWRITE   		    ("Overwrite");

	    private final String text;
	    
	    ImportFileAction(String text) {
	        this.text = text;
	    }
	    public String text() { return text; }
	    
	    
	    public static final ImportFileAction getInstance(String action) {
	        for (ImportFileAction actionValue : ImportFileAction.values()) {
	            if (actionValue.text().equals(action)) {
	                return actionValue;
	            }
	        }
	        return null; 
	    }
	    
	    public static List<String> getErrorOptions(){
	    	List<String> errorOptions = new ArrayList<String>(1);
	    	errorOptions.add(LEAVE.text());
	    	return errorOptions;
	    }
	    
	    public static List<String> getCopyOptions(){
	    	List<String> copyOptions = new ArrayList<String>(2);
	    	copyOptions.add(COPY.text());
	    	copyOptions.add(LEAVE.text());
	    	return copyOptions;
	    }
	    
	    public static List<String> getOverwriteOptions(){
	    	List<String> overwriteOptions = new ArrayList<String>(2);
	    	overwriteOptions.add(OVERWRITE.text());
	    	overwriteOptions.add(LEAVE.text());
	    	return overwriteOptions;
	    }
	}
