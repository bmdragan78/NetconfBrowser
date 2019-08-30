package com.yangui.main.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;



public class MyNamespaceContext implements NamespaceContext {

    private Map<String, String> prefixMap;
    
 
    public MyNamespaceContext() {
        prefixMap= new HashMap<String, String>();
    }
 
    
    public void addPrefixMapping(String prefix, String namespace) {
    	if(namespace != null) {
    		prefixMap.put(prefix, namespace);
    	}
    }
    
    public void removePrefixMapping(String prefix, String namespace) {
    	if(namespace != null) {
    		prefixMap.remove(prefix);
    	}
    }
    
    public String getNamespaceURI(String prefix) {
    	return prefixMap.get(prefix);
    }
 
    
    public String getPrefix(String namespaceURI) {
        for (String prefix   : prefixMap.keySet()) {
            String namespace = prefixMap.get(prefix); 
            if(namespaceURI.equals(namespace))
            	return prefix;
       }  
        return null;
    }
 
    
    public Iterator getPrefixes(String namespaceURI) {
        // not implemented yet
        return null;
    }
 
}