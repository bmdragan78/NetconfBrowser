package com.yang.ui.services.model;



/** This enum is used only on GUI to manage Query result & errors */
public enum QueryStatusEnum {
	
	OK 					("Ok"),
    ERR_NO_TYPE   		("Type of query not found. Choose one of READ, WRITE, DELETE, SUBSCRIBE, UNSUBSCRIBE"),
    ERR_NO_XPATH   		("Xpath of query not found. Browse Yang schema and select a xpath"),
    ERR_NO_PAYLOAD    	("Payload of query not found. Browse Yang schema and generate sample xml from schema"),
    ERR_IO 				("Communication error"),
    ERR_SERVER 			("Server error");

    private final String text;
    
    QueryStatusEnum(String text) {
        this.text = text;
    }
    public String text() { return text; }
    
    
    public static final QueryStatusEnum getInstance(String status) {
        for (QueryStatusEnum statusValue : QueryStatusEnum.values()) {
            if (statusValue.text().equals(status)) {
                return statusValue;
            }
        }
        return null; 
    }
}
