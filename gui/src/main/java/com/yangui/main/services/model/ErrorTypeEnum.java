package com.yangui.main.services.model;



public enum ErrorTypeEnum {
	
	SCHEMA  ("SCHEMA"),
	NETWORK  ("NETWORK"),
	QUERY  ("QUERY");
	
	private final String text;
	    
	 private ErrorTypeEnum(String text) {
		 this.text = text;
	 }
		
		
	 public String text() { 
		 return text; 
	 }
	    
	    
	 public static final ErrorTypeEnum getInstance(String type) {
		 type = type.toLowerCase();
	     for (ErrorTypeEnum typeValue : ErrorTypeEnum.values()) {
	         if (typeValue.text().equals(type)) {
	              return typeValue;
	         }
	      }
	      return null; 
	    }

}
