package com.yang.ui.services.model;



public enum SchemaValidationStatus {
	
	OK 							("Valid Schema"),
	
	ERR_NO_FILE_NAME   			("File name is empty. Name not saved!"),
	ERR_FILE_DUPLICATE   		("Duplicate file name. Name not saved!"),
	
	ERR_NO_FILE_CONTENT   		("File content is empty. Content not saved!"),
	
	ERR_NO_MODULE   			("Module name not found. Content not saved!"),
	ERR_MODULE_DUPLICATE 		("Duplicate module name. Content not saved!"),
	
    ERR_NO_NAMESPACE   			("Module namespace not found. Content not saved!"),
    ERR_NAMESPACE_DUPLICATE 	("Duplicate namespace. Content not saved!"),
    
    ERR_NO_MOD_REVISION    		("Module revision not found. Content not saved!"),
    
    ERR_NO_MOD_IMPORT    		("Module imports not found. Content saved!"),
    
    ERR_IO  					("Filesystem error. Content not saved!"),
    
    ERR_YANG 					("Yang schema not valid. Content saved!");

    private final String text;
    
    SchemaValidationStatus(String text) {
        this.text = text;
    }
    public String text() { return text; }
}
