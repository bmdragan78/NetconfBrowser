package com.yang.ui.services.model;


public enum ImportFileStatus {
    ERR_NO_NAMESPACE   			("Module Namespace Cannot Be Found !"),								//action=Leave -> All Errors
    ERR_NO_NAME   				("Module Name Cannot Be Found !"),
    ERR_NO_REVISION    			("Module Revision Cannot Be Found !"),
    ERR_EMPTY  					("Source Yang File Is Empty !"), 
    ERR_DUPLICATE_IMPORT 		("Source Yang Files Contain The Same (Module,Rev) !"),		 
    
    VALID_UNIQUE 				("Source Yang File Is Valid."),										//action=Copy 		OR Leave
    VALID_DUPLICATE				("Source Yang File Is Valid But It Is Already Present In Schema."),	//action=Overwrite  OR Leave

    REPORT_LEAVE 				("Destination Yang File Not Imported."),							//final result of the import process
    REPORT_COPY 				("Destination Yang File Created."),	
    REPORT_OVERWRITE			("Destination Yang File Updated."),
    ERR_FILE_IO 				("Destination Yang File Could Not Be Written !");

    private final String text;
    
    ImportFileStatus(String text) {
        this.text = text;
    }
    public final String text() { return text; }
    
    
    public static final ImportFileStatus getInstance(String status) {
        for (ImportFileStatus statusValue : ImportFileStatus.values()) {
            if (statusValue.text().equals(status)) {
                return statusValue;
            }
        }
        return null; 
    }
}
