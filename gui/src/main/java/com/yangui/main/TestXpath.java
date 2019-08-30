package com.yangui.main;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import com.yangui.main.services.MyNamespaceContext;



public class TestXpath {
	
	
	private  static List<String> getTokens(String str, String delim) {
		return Collections.list(new StringTokenizer(str, delim)).stream().map(token -> (String) token).collect(Collectors.toList());
    }	
	
	public static void main(String[] args) {
		
		//String targetPath = "./pre:abc/pre:cde";
		String targetPath = "./sd:abc/sd:cde";
//		String targetPath = "/pre:abc/pre:cde";
//		String targetPath = "/:abc/:cde";
		StringBuilder pathBuf = new StringBuilder();
		
		List<String> stepList = getTokens(targetPath, "/");
		for(String step:stepList) {
			System.out.println("step " + step);
			if(step.contains(":"))
				pathBuf.append("/" + step.substring(step.indexOf(":") + 1, step.length()));
		}
		String path = pathBuf.toString();
		System.out.println("path " + path);
		
		int slashIndex = targetPath.lastIndexOf("/");
		int sepIndex = targetPath.lastIndexOf(":");
		String prefix = targetPath.substring(slashIndex+1, sepIndex);
		System.out.println("prefix " + prefix + " prefixLength " + prefix.length());
		
		
	}			

	public static void main1(String[] args) {
		try {
			
			String payload = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">" +
					"<edit-config> \n" + 
					"  <target>\n" + 
					"    <candidate/>\n" + 
					"  </target>  \n" + 
					"  <config xmlns:xc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">  \n" + 
					"    <dat:interfaces xmlns:dat=\"urn:ietf:params:xml:ns:yang:ietf-interfaces\" xc:operation=\"replace\">  \n" + 
					"      <dat:interface> \n" + 
					"        <dat:name>eth0</dat:name>  \n" + 
					"        <dat:description>Ethernet 0</dat:description>  \n" + 
					"        <dat:type xmlns:ianaift=\"urn:ietf:params:xml:ns:yang:iana-if-type\">ianaift:ethernetCsmacd</dat:type>  \n" + 
					"        <dat:enabled>true</dat:enabled> \n" + 
					"      </dat:interface>  \n" + 
					"      <dat:interface> \n" + 
					"        <dat:name>eth1</dat:name>  \n" + 
					"        <dat:description>Ethernet 1</dat:description>  \n" + 
					"        <dat:type xmlns:ianaift=\"urn:ietf:params:xml:ns:yang:iana-if-type\">ianaift:ethernetCsmacd</dat:type>  \n" + 
					"        <dat:enabled>true</dat:enabled> \n" + 
					"      </dat:interface> \n" + 
					"    </dat:interfaces> \n" + 
					"  </config> \n" + 
					"</edit-config></rpc>";

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream input = new ByteArrayInputStream(payload.getBytes("UTF-8"));
			Document doc = builder.parse(input);
			doc.getDocumentElement().normalize();
	        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	        
	        MyNamespaceContext nsCtx = new MyNamespaceContext();
	       
			Map<String, Stack<String>> namespacePathMap = new HashMap<String, Stack<String>>();//namespace -> pathStack
	        Element rootElement = doc.getDocumentElement();
	        parseTreeRec(rootElement, namespacePathMap, null, nsCtx);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void parseTreeRec(Element element, Map<String, Stack<String>> namespacePathMap, String currentNamespace, MyNamespaceContext nsCtx) {
		
		String declaredNamespace = getNamespace(element);//might be NULL
		String declaredPrefix = getPrefix(element);//might be ""
		nsCtx.addPrefixMapping(declaredPrefix, declaredNamespace);
		
		String namespace;
		String prefix = element.getPrefix();//might be NULL
		if(prefix != null) {
			namespace = nsCtx.getNamespaceURI(prefix);
		}else {
			namespace = nsCtx.getNamespaceURI("");
		}
		
		Stack<String> pathStack = namespacePathMap.get(namespace);
		if(pathStack == null) {
			pathStack = new Stack<String>();
			namespacePathMap.put(namespace, pathStack);
		}
		String path = "";
		for(String item : pathStack) {
			path += "/" + item;
		}
		path += "/" + element.getLocalName();
		System.out.println("element = " + element.getLocalName()
		//+ " 	declaredPrefix = " + declaredPrefix + " 	declaredNamespace = " + declaredNamespace
		+ " 	prefix = " + prefix + " 	namespace = " + namespace + " 	path = " + path );
		pathStack.push(element.getLocalName());
		
		NodeList childList = element.getChildNodes();
		int length = childList.getLength();
		if(length == 1) {
	        Node valueNode = childList.item(0);
	        if (Node.TEXT_NODE == valueNode.getNodeType()) {
	            Text value = (Text) valueNode;
	            System.out.println("value :" + value.getNodeValue());
	        }
		}
        for (int i = 0; i < length; i++) {
            Node child = childList.item(i);
            if (Node.ELEMENT_NODE == child.getNodeType()) {
               Element childElem = (Element) child;
               parseTreeRec(childElem, namespacePathMap, currentNamespace, nsCtx);
            }
        }
        
        if(pathStack.size() > 0) 
			pathStack.pop();
        nsCtx.removePrefixMapping(declaredPrefix, declaredNamespace);
	}
	
	
	public static String getNamespace(Element element) {
		String namespace = null;
		try {
			NamedNodeMap attMap = element.getAttributes();
		    if (attMap != null) { 
		        int length = attMap.getLength();
		        for (int i = 0; i < length; i++) {
		            Node attr = attMap.item(i);
		            String attName = attr.getNodeName();
		            String attValue = attr.getNodeValue();
		            if(attName.contains("xmlns")) {
		            	namespace = attValue;
		            }
		        }
		    }
		}catch(Exception ex) {
		}
	    return namespace;
    }
	
	public static String getPrefix(Element element) {
		String prefix = "";
		try {
			NamedNodeMap attMap = element.getAttributes();
		    if (attMap != null) { 
		        int length = attMap.getLength();
		        for (int i = 0; i < length; i++) {
		            Node attr = attMap.item(i);
		            String attName = attr.getNodeName();
		            String attValue = attr.getNodeValue();
		            if(attName.contains("xmlns:")) {
		            	prefix = attName.substring("xmlns:".length(), attName.length());
		            }
		        }
		    }
		}catch(Exception ex) {
			prefix = "";
		}
	    return prefix;
    }
	
	
	
	public static void main2(String[] args) {
		try {
			
//			String payload = "\n" + 
//					"<xc:rpc xmlns:xc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" + 
//					"  <xc:edit-config> \n" + 
//					"    <xc:target> \n" + 
//					"      <xc:candidate/> \n" + 
//					"    </xc:target>  \n" + 
//					"    <xc:config>  \n" + 
//					"      <c1 xmlns=\"urn:ietf:params:xml:ns:yang:mymodule\" xc:operation=\"replace\">  \n" + 
//					"        <attrib-type>1</attrib-type>  \n" + 
//					"        <attrib-val-int>0</attrib-val-int>  \n" + 
//					"        <attrib-val-string>G</attrib-val-string> \n" + 
//					"      </c1> \n" + 
//					"    </xc:config> \n" + 
//					"  </xc:edit-config>\n" + 
//					"</xc:rpc>\n" + 
//					"";
			
			
			String payload = "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" + 
					"  <edit-config> \n" + 
					"    <target> \n" + 
					"      <candidate/> \n" + 
					"    </target>  \n" + 
					"    <config xmlns:xc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">  \n" + 
					"      <dat:c1 xmlns:dat=\"urn:ietf:params:xml:ns:yang:mymodule\" xc:operation=\"replace\">  \n" + 
					"        <dat:attrib-type>2</dat:attrib-type>  \n" + 
					"        <dat:attrib-val-int>0</dat:attrib-val-int> \n" + 
					"        <dat:attrib-val-string>e</dat:attrib-val-string>\n" + 
					"      </dat:c1> \n" + 
					"    </config> \n" + 
					"  </edit-config>\n" + 
					"</rpc>";
			
			NamespaceContext ctx = new NamespaceContext() {
				
			    public String getNamespaceURI(String prefix) {
			        if(prefix.equals("xc"))
			        	return "urn:ietf:params:xml:ns:netconf:base:1.0";
			        else if(prefix.equals(""))
			        	return "urn:ietf:params:xml:ns:yang:mymodule";
			        else
			        	return null;
			    }
			    public Iterator getPrefixes(String val) {
			        return null;
			    }
			    
			    public String getPrefix(String uri) {
			        return null;
			    }
			};
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setNamespaceAware(true);
			DocumentBuilder dbBuilder= dbFactory.newDocumentBuilder();
			//InputStream queryStreamXpath = new BufferedInputStream(new ReaderInputStream(new StringReader(payload)));
			ByteArrayInputStream queryStreamXpath = new ByteArrayInputStream(payload.getBytes("UTF-8"));
			org.w3c.dom.Document doc = dbBuilder.parse(queryStreamXpath);
	        doc.getDocumentElement().normalize();
	        
	        XPath xPathObj =  XPathFactory.newInstance().newXPath();
	        xPathObj.setNamespaceContext(ctx);
//	        Node attribValIntNode = (Node) xPathObj.compile("/rpc/edit-config/config/c1/attrib-val-int").evaluate(doc, XPathConstants.NODE);
//	        Node attribValIntNode = (Node) xPathObj.compile("/urn:ResponseStatus/urn:statusCode/:c1").evaluate(doc, XPathConstants.NODE);
	        Node attribValIntNode = (Node) xPathObj.compile("/xc:rpc/xc:edit-config/xc:config/:c1/:attrib-val-int").evaluate(doc, XPathConstants.NODE);
	        
	        System.out.println("attribValIntNode " + attribValIntNode.getLocalName());
	        
	        String when = "../:attrib-type = 1";
//	        String when = "/rpc/edit-config/config/c1/attrib-type = 1";
	        Boolean isTrue = (Boolean) xPathObj.compile(when).evaluate(attribValIntNode, XPathConstants.BOOLEAN);
	        
	        System.out.println("isTrue " + isTrue);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
