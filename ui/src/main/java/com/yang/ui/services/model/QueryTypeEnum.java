package com.yang.ui.services.model;




/** This enum is used only on GUI to manage Query type which is transformed into QueryTypeEnum on the client */
public enum QueryTypeEnum {
	
//	if (rpcContent.startsWith("<"))
//        rpcContent = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">" + rpcContent + "</rpc>"; 
//    else
//        rpcContent = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">" + "<" + rpcContent + "/>" + "</rpc>"; 
	
	NOTIFICATION  ("notification", "<notification xmlns=\"urn:ietf:params:xml:ns:netconf:notification:1.0\" message-id=\"1\">\n" + 
			"  <paused xmlns=\"http://example.net/turing-machine\">\n" + 
			"    <state>0</state>    \n" + 
			"  </paused>\n" + 
			"</notification>"),
	
	RPC   	("rpc", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"1\">\n" + 
			"  <insert-food xmlns=\"urn:sysrepo:oven\">\n" + 
			"    <time>now</time>\n" + 
			"  </insert-food>\n" + 
			"</rpc>"),
	
	GET 			("get", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"1\">  \n" + 
			"  <get> \n" + 
//			"    <filter type=\"subtree\"> \n" + 
//			"      <oven xmlns=\"urn:sysrepo:oven\"></oven> \n" + 
//			"    </filter> \n" + 
			"  </get> \n" + 
			"</rpc>"),
	
	GET_CONFIG   	("get-config", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"1\" >\n" + 
			"  <get-config> \n" + 
			"    <source> \n" + 
			"      <candidate/> \n" + 
			"    </source>  \n" + 
//			"    <filter type=\"subtree\"> \n" + 
//			"      <oven xmlns=\"urn:sysrepo:oven\"></oven> \n" + 
//			"    </filter> \n" + 
			"  </get-config>\n" + 
			"</rpc>"),
	
	EDIT_CONFIG   	("edit-config", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"1\">\n" + 
			"  <edit-config> \n" + 
			"    <target> \n" + 
			"      <candidate/> \n" + 
			"    </target>  \n" + 
			"    <config xmlns:xc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">  \n" + 
			"      <oven xmlns=\"urn:sysrepo:oven\" xc:operation=\"replace\">  \n" + 
			"        <turned-on>false</turned-on>  \n" + 
			"        <temperature>0</temperature> \n" + 
			"      </oven> \n" + 
			"    </config> \n" + 
			"  </edit-config>\n" + 
			"</rpc>"),
	
    COPY_CONFIG   	("copy-config", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"1\">\n" + 
    		"  <copy-config> \n" + 
    		"    <target> \n" + 
    		"      <running/> \n" + 
    		"    </target>  \n" + 
    		"    <source> \n" + 
    		"      <candidate/> \n" + 
    		"    </source> \n" + 
    		"  </copy-config>\n" + 
    		"</rpc>"),
    
    DELETE_CONFIG   ("delete-config", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"1\">\n" + 
    		"  <delete-config> \n" + 
    		"    <target>\n" + 
    		"      <startup/>\n" + 
    		"    </target> \n" + 
    		"  </delete-config>\n" + 
    		"</rpc>"),
    
    LOCK 			("lock", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"1\">\n" + 
    		"  <lock> \n" + 
    		"    <target>\n" + 
    		"      <candidate/>\n" + 
    		"    </target> \n" + 
    		"  </lock>\n" + 
    		"</rpc>"),
    
    UNLOCK 			("unlock", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"1\">\n" + 
    		"  <unlock> \n" + 
    		"    <target>\n" + 
    		"      <candidate/>\n" + 
    		"    </target> \n" + 
    		"  </unlock>\n" + 
    		"</rpc>"),
    
	CLOSE_SESSION 	("close-session", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"1\">\n" + 
			"  <close-session></close-session>\n" + 
			"</rpc>"),
	
	KILL_SESSION    ("kill-session", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"1\">\n" + 
			"  <kill-session> \n" + 
			"    <session-id>10</session-id>\n" + 
			"  </kill-session>\n" + 
			"</rpc>");
	
	
    private final String text;
    
    private final String defaultContent;
    
	QueryTypeEnum(String text, String defaultContent) {
        this.text = text;
        this.defaultContent = defaultContent;
    }
	
	
    public String text() { 
    	return text; 
    }
    
    
    public String getDefaultContent() {
		return defaultContent;
	}
    
    
    public static final QueryTypeEnum getInstance(String type) {
    	type = type.toLowerCase();
        for (QueryTypeEnum typeValue : QueryTypeEnum.values()) {
            if (typeValue.text().equals(type)) {
                return typeValue;
            }
        }
        return null; 
    }
    
    
}