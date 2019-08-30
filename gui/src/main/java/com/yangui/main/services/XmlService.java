package com.yangui.main.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang.RandomStringUtils;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.model.api.IdentitySchemaNode;
import org.opendaylight.yangtools.yang.model.api.MustDefinition;
import org.opendaylight.yangtools.yang.model.api.RevisionAwareXPath;
import org.opendaylight.yangtools.yang.model.api.SchemaPath;
import org.opendaylight.yangtools.yang.model.api.TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.UniqueConstraint;
import org.opendaylight.yangtools.yang.model.api.stmt.SchemaNodeIdentifier.Relative;
import org.opendaylight.yangtools.yang.model.api.type.BinaryTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.BitsTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.BitsTypeDefinition.Bit;
import org.opendaylight.yangtools.yang.model.api.type.EnumTypeDefinition.EnumPair;
import org.opendaylight.yangtools.yang.model.api.type.BooleanTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.DecimalTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.EmptyTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.EnumTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.IdentityrefTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.InstanceIdentifierTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.IntegerTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.LeafrefTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.LengthConstraint;
import org.opendaylight.yangtools.yang.model.api.type.PatternConstraint;
import org.opendaylight.yangtools.yang.model.api.type.RangeConstraint;
import org.opendaylight.yangtools.yang.model.api.type.StringTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.UnionTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.UnsignedIntegerTypeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.yangui.main.services.model.ErrorItem;
import com.yangui.main.services.model.ErrorTypeEnum;
import com.yangui.main.services.model.ModuleIdentifier;
import com.yangui.main.services.model.QueryTypeEnum;
import com.yangui.main.services.model.SchemaItem;
import com.yangui.main.services.model.YangChoice;
import com.yangui.main.services.model.YangContainer;
import com.yangui.main.services.model.YangDataSchemaNode;
import com.yangui.main.services.model.YangLeaf;
import com.yangui.main.services.model.YangLeafList;
import com.yangui.main.services.model.YangList;
import com.yangui.main.services.model.YangModule;
import com.yangui.main.services.model.YangSchemaNode;
import com.yangui.main.services.model.SchemaItem.SchemaItemType;
import com.yangui.main.services.xml.LocationAnnotator;
import com.yangui.main.services.xml.LocationData;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;



@Singleton
public class XmlService {
	
	private static final Logger LOG = LoggerFactory.getLogger(XmlService.class);
	
	@Inject
	private RegexService regexSrv;
	
	@Inject
	SchemaService schemaSrv;
	
	@Inject
    FileService fileService;
	
	@Inject
    LogService logService;
	
	
	@PostConstruct
    public void init() throws RuntimeException {
    }
	
	
	public String prettyPrint(String input) {
		try {
			SAXReader reader = new SAXReader();
			org.dom4j.Document doc = reader.read(new StringReader(input));
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setSuppressDeclaration(true);
			format.setNewLineAfterDeclaration(false);
			XMLWriter writer = new XMLWriter( stream, format );
			writer.write( doc );
			
			return new String(stream.toByteArray());
		}catch (Exception ex) {
			LOG.error("", ex);
			return input;
		}
	}
	
	
	public String updateQueryTarget(String input, String target, String queryName) {//called from UI
		LOG.debug("Generating Target for " + queryName);
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();//they can come either from Schema errors or from Exceptions
		String output = null;
		try {
			String operation = regexSrv.getOperType(input);
			QueryTypeEnum operationEnum = QueryTypeEnum.getInstance(operation);
			if(operationEnum.equals(QueryTypeEnum.EDIT_CONFIG)) {
				output = regexSrv.replaceEditConfigTarget(input, target);
			}else if(operationEnum.equals(QueryTypeEnum.COPY_CONFIG)) {
				output = regexSrv.replaceCopyConfigTarget(input, target);
			}else if(operationEnum.equals(QueryTypeEnum.DELETE_CONFIG)) {
				output = regexSrv.replaceDeleteConfigTarget(input, target);
			}else if(operationEnum.equals(QueryTypeEnum.LOCK)) {
				output = regexSrv.replaceLockTarget(input, target);
			}else if(operationEnum.equals(QueryTypeEnum.UNLOCK)) {
				output = regexSrv.replaceUnlockTarget(input, target);
			}else {
				output = input;
			}
			output = prettyPrint(regexSrv.removeEmptyLines(output));
			
		}catch(Exception ex) {
			LOG.error("", ex);
			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, "0", "Check Error Window"));
			output = input;
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateQueryErrors(errorList, queryName);
				LOG.error("Generating Target for " + queryName + " returned errors");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
			}else {
				logService.clearQueryErrors(queryName);
				LOG.debug("Generating Target for " + queryName + " finished ok");
			}
		});
		return output;
	}
	
	
	public String updateQuerySource(String input, String source, String queryName) {//called from UI
		LOG.debug("Generating Source for " + queryName);
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();//they can come either from Schema errors or from Exceptions
		String output = null;
		try {
			String operation = regexSrv.getOperType(input);
			QueryTypeEnum operationEnum = QueryTypeEnum.getInstance(operation);
			if(operationEnum.equals(QueryTypeEnum.GET_CONFIG)) {
				output = regexSrv.replaceGetConfigSource(input, source);
			}else if(operationEnum.equals(QueryTypeEnum.COPY_CONFIG)) {
				output = regexSrv.replaceCopyConfigSource(input, source);
			}else {
				output = input;
			}
			output = prettyPrint(regexSrv.removeEmptyLines(output));
			
		}catch(Exception ex) {
			LOG.error("", ex);
			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, "0", "Check Error Window"));
			output = input;
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateQueryErrors(errorList, queryName);
				LOG.error("Generating Source for " + queryName + " returned errors");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
			}else {
				logService.clearQueryErrors(queryName);
				LOG.debug("Generating Source for " + queryName + " finished ok");
			}
		});
		return output;
	}
		
	
	//generate filter and replace it in input
	public String updateQueryFilter(String input, TreeItem<SchemaItem> selTreeItem, TreeItem<SchemaItem> rootTreeItem, String queryName) {//called from UI
		LOG.debug("Generating Filter for " + queryName);
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();//they can come either from Schema errors or from Exceptions
		String output = null;
		try {
			String filter = generateFilter(selTreeItem);
			String operation = regexSrv.getOperType(input);
			QueryTypeEnum operationEnum = QueryTypeEnum.getInstance(operation);
			if(operationEnum.equals(QueryTypeEnum.GET)) {
				output = regexSrv.replaceGetFilter(input, filter);
			}else if(operationEnum.equals(QueryTypeEnum.GET_CONFIG)) {
				output = regexSrv.replaceGetConfigFilter(input, filter);
			}else {
				output = input;
			}
			output = prettyPrint(regexSrv.removeEmptyLines(output));
			
		}catch(Exception ex) {
			LOG.error("", ex);
			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, "0", "Check Error Window"));
			output = input;
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateQueryErrors(errorList, queryName);
				LOG.error("Generating Filter for " + queryName + " returned errors");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
			}else {
				logService.clearQueryErrors(queryName);
				LOG.debug("Generating Filter for " + queryName + " finished ok");
			}
		});
		return output;
	}
	
	
	//generate config and replace it in input
	public String updateQueryConfig(String input, TreeItem<SchemaItem> selTreeItem, TreeItem<SchemaItem> rootTreeItem, String queryName) {//called from UI
		LOG.debug("Generating Config For " + queryName);
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();//they can come either from Schema errors or from Exceptions
		String output = null;
		try {
			String config = generateConfig(selTreeItem, rootTreeItem);
			String operation = regexSrv.getOperType(input);
			QueryTypeEnum operationEnum = QueryTypeEnum.getInstance(operation);
			if(operationEnum.equals(QueryTypeEnum.EDIT_CONFIG)) {
				output = regexSrv.replaceEditConfigConfig(input, config);
			}else {
				output = input;
			}
			output = prettyPrint(regexSrv.removeEmptyLines(output));
			
		}catch(Exception ex) {
			LOG.error("", ex);
			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, "0", "Check Error Window"));
			output = input;
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateQueryErrors(errorList, queryName);
				LOG.error("Generating Config for " + queryName + " returned errors");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
			}else {
				logService.clearQueryErrors(queryName);
				LOG.debug("Generating Config for " + queryName + " finished ok");
			}
		});
		return output;
	}

	
	private  List<String> getTokens(String str, String delim) {
		return Collections.list(new StringTokenizer(str, delim)).stream().map(token -> (String) token).collect(Collectors.toList());
    }	
	
	
	private String generateFilter(TreeItem<SchemaItem> treeItem) throws Exception {
		try {
			DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			
			SchemaItem schemaItem = treeItem.getValue();
		    YangSchemaNode yangNode = schemaItem.getSchemaNode();
		    String namespace  = yangNode.getModuleIdentifier().getNamespace();
			String localPath = yangNode.getLocalPath();
			
			Element filterElement = doc.createElement("filter");
	        doc.appendChild(filterElement);
	        Attr nsAttr = doc.createAttribute("type");
			nsAttr.setValue("subtree");
			filterElement.setAttributeNode(nsAttr);
	        
			Element lastElement = filterElement;
			int index = 0;
			List<String> stepList = getTokens(localPath, "/");
			for(String step:stepList) {
				Element stepElement = doc.createElement(step);
				lastElement.appendChild(stepElement);
				lastElement = stepElement;
		        if(index == 0) {
		        	nsAttr = doc.createAttribute("xmlns");
					nsAttr.setValue(namespace);
					stepElement.setAttributeNode(nsAttr);
		        }
		        index++;
			}
	        //DOM -> string
	        DOMSource source = new DOMSource(doc);
	        Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        StringWriter stringWriter = new StringWriter();
	        Result dest = new StreamResult(stringWriter);
	        transformer.transform(source, dest);
	        String xmlString = stringWriter.toString();
			
	        return prettyPrint(xmlString);
		}catch(Exception ex) {
			LOG.error("", ex);
		}
		return "";
	}

	
	public String updateNotificationInput(String input, TreeItem<SchemaItem> selTreeItem, TreeItem<SchemaItem> rootTreeItem, String queryName) {//called from UI
		LOG.debug("Generating Notification for " + queryName);
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();
		String output = null;
		try {
			DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			
			SchemaItem schemaItem = selTreeItem.getValue();//Notification
		    YangSchemaNode yangNode = schemaItem.getSchemaNode();
			
			SchemaItemType type = schemaItem.getSchemaNode().getTypeEnum();
			if(SchemaItemType.NOTIFICATION != type)
				throw new Exception("Selected schema item is not a Notification node");//it is prevented from UI
			
			ModuleIdentifier moduleId = selTreeItem.getValue().getSchemaNode().getModuleIdentifier();
			Element rootElement = doc.createElement("notification");
			Attr nsAttr = doc.createAttribute("xmlns");
			nsAttr.setValue("urn:ietf:params:xml:ns:netconf:notification:1.0");
	        rootElement.setAttributeNode(nsAttr);
	        doc.appendChild(rootElement);
	        
	        //SchemaItem rpcItem = selTreeItem.getParent().getValue();//RPC
	        Element notificationElement = doc.createElement(yangNode.getLocalName());
	        rootElement.appendChild(notificationElement);
	        nsAttr = doc.createAttribute("xmlns");
			nsAttr.setValue(moduleId.getNamespace());
			notificationElement.setAttributeNode(nsAttr);
	        
	        YangModule yangModule = schemaSrv.getModuleTreeItem(selTreeItem).getValue().getModule();
	        generateSimpleTreeRec( notificationElement, yangNode.getChildren(), rootTreeItem, yangModule);
	        
	        //DOM -> string
	        DOMSource source = new DOMSource(doc);
	        Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        StringWriter stringWriter = new StringWriter();
	        Result dest = new StreamResult(stringWriter);
	        transformer.transform(source, dest);
	        String xmlString = stringWriter.toString();
			
	        output = prettyPrint(xmlString);
		}catch(Exception ex) {
			LOG.error("", ex);
			errorList.clear();
			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, "0", "Check Error Window"));
			output = input;
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateQueryErrors(errorList, queryName);
				LOG.error("Generating Notification for " + queryName + " returned errors");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
			}else {
				logService.clearQueryErrors(queryName);
				LOG.debug("Generating Notification for " + queryName + " finished ok");
			}
		});
		return output;
	}
	
	
	public String updateRpcInput(String input, TreeItem<SchemaItem> selTreeItem, TreeItem<SchemaItem> rootTreeItem, String queryName) {//called from UI
		LOG.debug("Generating Rpc Input for " + queryName);
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();
		String output = null;
		try {
			DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			
			SchemaItem schemaItem = selTreeItem.getValue();//RPC Input
		    YangSchemaNode yangNode = schemaItem.getSchemaNode();
			
			SchemaItemType type = schemaItem.getSchemaNode().getTypeEnum();
			if(SchemaItemType.CONTAINER != type)
				throw new Exception("Selected schema item is not an RPC input node");//it is prevented from UI
			
			ModuleIdentifier moduleId = selTreeItem.getValue().getSchemaNode().getModuleIdentifier();
			Element rootElement = doc.createElement("rpc");
			doc.appendChild(rootElement);
			Attr nsAttr = doc.createAttribute("xmlns");
			nsAttr.setValue("urn:ietf:params:xml:ns:netconf:base:1.0");
	        rootElement.setAttributeNode(nsAttr);
	        
	        SchemaItem rpcItem = selTreeItem.getParent().getValue();//RPC
	        Element rpcElement = doc.createElement(rpcItem.getSchemaNode().getLocalName());
	        rootElement.appendChild(rpcElement);
	        nsAttr = doc.createAttribute("xmlns");
			nsAttr.setValue(moduleId.getNamespace());
			rpcElement.setAttributeNode(nsAttr);
	        
	        YangModule yangModule = schemaSrv.getModuleTreeItem(selTreeItem).getValue().getModule();
	        generateSimpleTreeRec( rpcElement, yangNode.getChildren(), rootTreeItem, yangModule);
	        
	        //DOM -> string
	        DOMSource source = new DOMSource(doc);
	        Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        StringWriter stringWriter = new StringWriter();
	        Result dest = new StreamResult(stringWriter);
	        transformer.transform(source, dest);
	        String xmlString = stringWriter.toString();
	        
	        output = prettyPrint(xmlString);
			
		}catch(Exception ex) {
			LOG.error("", ex);
			errorList.clear();
			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, "0", "Check Error Window"));
			output = input;
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateQueryErrors(errorList, queryName);
				LOG.error("Generating Rpc Input for " + queryName + " returned errors");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
			}else {
				logService.clearQueryErrors(queryName);
				LOG.debug("Generating Rpc Input for " + queryName + " finished ok");
			}
		});
		return output;
	}
	
	
	public String updateRpcFilter(String input, TreeItem<SchemaItem> selTreeItem, TreeItem<SchemaItem> rootTreeItem, String queryName) {//called from UI
		LOG.debug("Generating Rpc Filter for " + queryName);
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();
		String output = null;
		try {
			TreeItem<SchemaItem> treeItem = schemaSrv.getModulesTreeRoot();
			
			Document doc = createDOM(input);
	        Element rootElement = doc.getDocumentElement();
	        
	        MyNamespaceContext nsCtx = new MyNamespaceContext();
	        Map<String, Stack<SchemaItem>> namespacePathMap = new HashMap<String, Stack<SchemaItem>>();
	        
	        Element anyXmlElement = searchTreeItemRec(rootElement, namespacePathMap, treeItem, nsCtx, doc);	//filterElement	
	        if(anyXmlElement == null) {
	        	output = input;
	        	throw new Exception("No AnyXml Nodes could be found in the xml");
	        }else {
	        	//remove all children of anyXmlElement & type attribute
	        	while (anyXmlElement.hasChildNodes())
	        		anyXmlElement.removeChild(anyXmlElement.getFirstChild());
	        	while (anyXmlElement.getAttributes().getLength() > 0) {
	        	    Node att = anyXmlElement.getAttributes().item(0);
	        	    anyXmlElement.getAttributes().removeNamedItem(att.getNodeName());
	        	}
	        	SchemaItem schemaItem = selTreeItem.getValue();
	    	    YangSchemaNode yangNode = schemaItem.getSchemaNode();
	    	    ModuleIdentifier moduleId = yangNode.getModuleIdentifier();
	    	    String namespace  = moduleId.getNamespace();
	    	    String localPath = yangNode.getLocalPath();
	    	    
		        Attr nsAttr = doc.createAttribute("type");
				nsAttr.setValue("subtree");
				anyXmlElement.setAttributeNode(nsAttr);
		        
				Element lastElement = anyXmlElement;
				int index = 0;
				List<String> stepList = getTokens(localPath, "/");
				for(String step:stepList) {
					Element stepElement = doc.createElement(step);
					lastElement.appendChild(stepElement);
					lastElement = stepElement;
			        if(index == 0) {
			        	nsAttr = doc.createAttribute("xmlns");
						nsAttr.setValue(namespace);
						stepElement.setAttributeNode(nsAttr);
			        }
			        index++;
				}
	        	//DOM -> string
	            DOMSource source = new DOMSource(doc);
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
	            StringWriter stringWriter = new StringWriter();
	            Result dest = new StreamResult(stringWriter);
	            transformer.transform(source, dest);
	            String xmlString = stringWriter.toString();
	            
	            output = prettyPrint(xmlString);
	        }
			
		}catch(Exception ex) {
			LOG.error("", ex);
			errorList.clear();
			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, "0", "Check Error Window"));
			output = input;
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateQueryErrors(errorList, queryName);
				LOG.error("Generating Rpc Filter for " + queryName + " returned errors");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
			}else {
				logService.clearQueryErrors(queryName);
				LOG.debug("Generating Rpc Filter for " + queryName + " finished ok");
			}
		});
		return output;
	}
	
	
	public String updateRpcConfig(String input, TreeItem<SchemaItem> selTreeItem, TreeItem<SchemaItem> rootTreeItem, String queryName) {//called from UI
		LOG.debug("Generating Rpc Config for " + queryName);
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();
		String output = null;
		try {
			//read schema tree
			TreeItem<SchemaItem> treeItem = schemaSrv.getModulesTreeRoot();
			
			//get line number info while parsing DOM
			Document doc = createDOM(input);
	        Element rootElement = doc.getDocumentElement();
	        
	        MyNamespaceContext nsCtx = new MyNamespaceContext();
	        Map<String, Stack<SchemaItem>> namespacePathMap = new HashMap<String, Stack<SchemaItem>>();//namespace -> pathStack
	        
	        //find first Anyxml node and update it; if no Anyxml node could be found do nothing
	        Element anyXmlElement = searchTreeItemRec(rootElement, namespacePathMap, treeItem, nsCtx, doc);		
	        if(anyXmlElement == null) {
	        	output = input;
	        	throw new Exception("No AnyXml Nodes could be found in the xml");
	        }else {
	        	//remove all children/attributes of anyXmlElement
	        	while (anyXmlElement.hasChildNodes())
	        		anyXmlElement.removeChild(anyXmlElement.getFirstChild());
	        	while (anyXmlElement.getAttributes().getLength() > 0) {
	        	    Node att = anyXmlElement.getAttributes().item(0);
	        	    anyXmlElement.getAttributes().removeNamedItem(att.getNodeName());
	        	}
	        	
	        	SchemaItem schemaItem = selTreeItem.getValue();
	    	    YangSchemaNode yangNode = schemaItem.getSchemaNode();
	    	    ModuleIdentifier moduleId = yangNode.getModuleIdentifier();
	        	
	        	 YangModule yangModule = schemaSrv.getModuleTreeItem(selTreeItem).getValue().getModule();
	             switch(yangNode.getTypeEnum()){//only container and list can be root nodes in a yang module ?
	     			case CONTAINER:
	     			case LIST:
	     				Element selElement = doc.createElement(yangNode.getLocalName());
	     				Attr nsAttr = doc.createAttribute("xmlns");
	     				nsAttr.setValue(moduleId.getNamespace());
	     				selElement.setAttributeNode(nsAttr);
	     				anyXmlElement.appendChild(selElement);
	     				
	     				generateSimpleTreeRec( selElement, yangNode.getChildren(), rootTreeItem, yangModule);
	     				break;
	     			default:
	     		}
	             
	        	//DOM -> string
	            DOMSource source = new DOMSource(doc);
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
	            StringWriter stringWriter = new StringWriter();
	            Result dest = new StreamResult(stringWriter);
	            transformer.transform(source, dest);
	            String xmlString = stringWriter.toString();
	            
	            output = prettyPrint(xmlString);
	        }
		}catch(Exception ex) {
			LOG.error("", ex);
			errorList.clear();
			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, "0", "Check Error Window"));
			output = input;
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateQueryErrors(errorList, queryName);
				LOG.error("Generating Rpc Config for " + queryName + " returned errors");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
			}else {
				logService.clearQueryErrors(queryName);
				LOG.debug("Generating Rpc Config for " + queryName + " finished ok");
			}
		});
		return output;
	}
	
	
	private String generateConfig(TreeItem<SchemaItem> selTreeItem, TreeItem<SchemaItem> rootTreeItem) throws Exception {//add operation attribute for edit-config, filter attribute for get/get-config
		
		DocumentBuilderFactory dbFactory =DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.newDocument();
		
		SchemaItem schemaItem = selTreeItem.getValue();
	    YangSchemaNode yangNode = schemaItem.getSchemaNode();//should only be root nodes since edit-config is always a merge(You cannot filter it like get e.g.)
		
		Element configElement = doc.createElement("config");
		Attr nsAttr = doc.createAttribute("xmlns:xc");
		ModuleIdentifier moduleId = yangNode.getModuleIdentifier();
		nsAttr.setValue("urn:ietf:params:xml:ns:netconf:base:1.0");
        configElement.setAttributeNode(nsAttr);
        doc.appendChild(configElement);
        
        YangModule yangModule = schemaSrv.getModuleTreeItem(selTreeItem).getValue().getModule();
        switch(yangNode.getTypeEnum()){//only container and list can be root nodes in a yang module ?
			case CONTAINER:
			case LIST:
				Element rootElement = doc.createElement(yangNode.getLocalName());
				configElement.appendChild(rootElement);
				nsAttr = doc.createAttribute("xmlns");
				nsAttr.setValue(moduleId.getNamespace());
				rootElement.setAttributeNode(nsAttr);
				nsAttr = doc.createAttribute("xc:operation");
				nsAttr.setValue("replace");
				rootElement.setAttributeNode(nsAttr);
				
				generateSimpleTreeRec( rootElement, yangNode.getChildren(), rootTreeItem, yangModule);
				break;
			default:
		}
		
	    //DOM -> string
        DOMSource source = new DOMSource(doc);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter stringWriter = new StringWriter();
        Result dest = new StreamResult(stringWriter);
        transformer.transform(source, dest);
        String xmlString = stringWriter.toString();
        
		return prettyPrint(xmlString);
	}
	
	
	private Element searchTreeItemRec(Element element, Map<String, Stack<SchemaItem>> namespacePathMap, TreeItem<SchemaItem> treeItem, MyNamespaceContext nsCtx, Document doc) throws Exception {
//		if(contor == 0 && pathStack.size() == 0 && !element.equals("rpc") && !element.equals("notification"))//root nodes missing
//			throw new Exception("Root element can only be <rpc> or <notification>");
		SchemaItem schemaItem = null;
		TreeItem<SchemaItem> elementTreeItem = null;
		//update MyNamespaceContext
		String declaredNamespace = getNamespace(element);//might be NULL
		String declaredPrefix = getPrefix(element);//might be ""
		nsCtx.addPrefixMapping(declaredPrefix, declaredNamespace);
		
		XPath xPathObj =  XPathFactory.newInstance().newXPath();
		xPathObj.setNamespaceContext(nsCtx);
		
		LocationData locationData = (LocationData) element.getUserData(LocationData.LOCATION_DATA_KEY);
		
		//get element namespace
		String namespace;
		String prefix = element.getPrefix();//might be NULL
		if(prefix == null) 
			prefix = "";
		namespace = nsCtx.getNamespaceURI(prefix);
		
		//get pathStack for namespace
		Stack<SchemaItem> pathStack = namespacePathMap.get(namespace);
		if(pathStack == null) {
			pathStack = new Stack<SchemaItem>();
			namespacePathMap.put(namespace, pathStack);
		}
		
		if(pathStack.size() == 0 && (element.getLocalName().equals("rpc") || element.getLocalName().equals("notification"))) { //ignore root nodes
			schemaItem = null;
		}else {
			//build path
			String path = "";
			for(SchemaItem item : pathStack) 
				path += "/" + item.getSchemaNode().getLocalName();
			path += "/" + element.getLocalName();
			String xpath = schemaSrv.getXpath(path, namespace);
			//LOG.debug("locationData " + (locationData != null ? locationData.toString() : "") + " element = " + element.getLocalName() + " 	namespace = " + namespace + " 	xpath = " + xpath );
			
			//search element in schema tree
			elementTreeItem = schemaSrv.searchTreeItemRec( treeItem, (SchemaItem p) -> (p.getSchemaNode() != null && p.getSchemaNode().getXpath() != null ?  (p.getSchemaNode().getXpath().equals(xpath)) : false) );
	    	if(elementTreeItem != null) {
	    		schemaItem = elementTreeItem.getValue();
	    		pathStack.push(schemaItem);
	    		
	    		switch(schemaItem.getTypeEnum()) {
		   	      case RPC:		//augment path by adding "/input"
		   	    	  String inputXpath = schemaItem.getSchemaNode().getXpath() + "/input";
		   	    	  TreeItem<SchemaItem> inputTreeItem = schemaSrv.searchTreeItemRec( treeItem, (SchemaItem p) -> (p.getSchemaNode() != null && p.getSchemaNode().getXpath() != null ?  (p.getSchemaNode().getXpath().equals(inputXpath)) : false) );
		   	    	  pathStack.push(inputTreeItem.getValue());
		   	    	  break;
		   	    	  
		   	      case ANY_XML:
		   	    	LOG.debug("Augmenting ANY_XML element found at  " + xpath);
		   	    	return element;
		   	    	  
		   	      default:        
		   	    	  break; 
	    		}
	    	}else
	    		throw new Exception("Element " + xpath + " cannot be found in schema tree");
		}
		//parse children recursively
		NodeList childList = element.getChildNodes();
		int length = childList.getLength();
        for (int i = 0; i < length; i++) {
            Node child = childList.item(i);
            if (Node.ELEMENT_NODE == child.getNodeType()) {
               Element childElem = (Element) child;
               Element foundElem = searchTreeItemRec(childElem, namespacePathMap, treeItem, nsCtx, doc);
               if(foundElem != null)
            	   return foundElem;
            }
        }
        if(pathStack.size() > 0) 
			pathStack.pop();
        return null;
	}
	
	
	private void generateSimpleTreeRec(Node parentNode, List<YangSchemaNode> children, TreeItem<SchemaItem> rootTreeItem, YangModule yangModule) throws Exception {
		
		for (YangSchemaNode childSchemaNode : children) {
			
			String localName = childSchemaNode.getLocalName();
			switch(childSchemaNode.getTypeEnum()){
				case CONTAINER:
					Element childElement = parentNode.getOwnerDocument().createElement(localName);
					parentNode.appendChild(childElement);
		         	generateSimpleTreeRec(childElement, childSchemaNode.getChildren(), rootTreeItem, yangModule);
					break;
					
				case LIST:
					int listSize = ((YangDataSchemaNode)childSchemaNode).getMinElements();
					if(listSize == 0)
						listSize++;
					for(int i=0; i< listSize; i++) {
						childElement = parentNode.getOwnerDocument().createElement(localName);
						parentNode.appendChild(childElement);
			         	generateSimpleTreeRec(childElement, childSchemaNode.getChildren(), rootTreeItem, yangModule);
					}
					break;
					
				case LEAF_LIST:
					generateLeafListValue(parentNode, childSchemaNode, rootTreeItem, yangModule);
					break;
					
				case LEAF:
					generateLeafValue(parentNode, childSchemaNode, rootTreeItem, yangModule);
					break;
					
				case ANY_XML:
					generateAnyXmlValue(parentNode, childSchemaNode);
					break;
					
				case CHOICE:
					YangSchemaNode defaultCaseNode = null;
					List<YangSchemaNode> childrenList = childSchemaNode.getChildren();
					String defaultCase = ((YangChoice)childSchemaNode).getDefaultCase();
					if(defaultCase != null) {
						List<YangSchemaNode> defaultChildrenList = childrenList.stream().filter(yangNode -> yangNode.getLocalName().equals(defaultCase)).collect(Collectors.toList());//size==1 Always
						defaultCaseNode = defaultChildrenList.get(0);
					}else 
						defaultCaseNode = childrenList.get(0);
					generateSimpleTreeRec(parentNode, defaultCaseNode.getChildren(), rootTreeItem, yangModule);
					break;
				default:
			}
				
		}
	}
	
	
	private void generateLeafListValue(Node parentNode, YangSchemaNode yangNode, TreeItem<SchemaItem> rootTreeItem, YangModule yangModule) throws Exception {
		int leafListSize = ((YangDataSchemaNode)yangNode).getMinElements();
		if(leafListSize == 0)
			leafListSize++;
		for(int i=0; i< leafListSize; i++) {
			Element leafElement = parentNode.getOwnerDocument().createElement(yangNode.getLocalName());
			parentNode.appendChild(leafElement);
			
			String genValue = null;//do not use default value of leaf-list if present; see bellow 'generateLeafValue'
			if(genValue == null) {
				List<TypeDefinition<? extends TypeDefinition<?>>> typeList = ((YangLeafList)yangNode).getTypeList();//size >= 1 Always
				TypeDefinition type = typeList.get(0);
				genValue = buildValue(leafElement, type, yangNode.getModuleIdentifier(), rootTreeItem, yangModule);
			}
			if(genValue != null)
				leafElement.appendChild(leafElement.getOwnerDocument().createTextNode(genValue));
		}
	}
	
	
	private void generateLeafValue(Node parentNode, YangSchemaNode yangNode, TreeItem<SchemaItem> rootTreeItem, YangModule yangModule) throws Exception {
		
		Element leafElement = parentNode.getOwnerDocument().createElement(yangNode.getLocalName());
		parentNode.appendChild(leafElement);
		
		List<TypeDefinition<? extends TypeDefinition<?>>> typeList = ((YangLeaf)yangNode).getTypeList();//size >= 1 Always
		TypeDefinition type = typeList.get(0);
		String genValue = ((YangLeaf)yangNode).getDefaultValue();//use default value of leaf if present
		
		if(genValue == null && !(type instanceof BitsTypeDefinition)) {
			genValue = buildValue(leafElement, type, yangNode.getModuleIdentifier(), rootTreeItem, yangModule);
			if(genValue != null)
				leafElement.appendChild(leafElement.getOwnerDocument().createTextNode(genValue));
			
		}else if(genValue != null && !(type instanceof BitsTypeDefinition)) {
			leafElement.appendChild(leafElement.getOwnerDocument().createTextNode(genValue));
			
		}else if(type instanceof BitsTypeDefinition) {
			genValue = buildBitsValue(type, ((YangLeaf)yangNode).getDefaultValue());
			if(genValue != null)
				leafElement.appendChild(leafElement.getOwnerDocument().createCDATASection(genValue));
		}
	}
	
	
	private void generateAnyXmlValue(Node parentNode, YangSchemaNode yangNode) throws Exception {
		Element leafElement = parentNode.getOwnerDocument().createElement(yangNode.getLocalName());
		parentNode.appendChild(leafElement);
		leafElement.appendChild(leafElement.getOwnerDocument().createTextNode("Any xml"));
	}
	
	
	private String buildBitsValue(TypeDefinition type, String defaultValue) throws Exception {
		String genValue = null;
		//use default value of type if present
		if(defaultValue == null && type.getDefaultValue() != null) 
			defaultValue = type.getDefaultValue().toString();
		if(type instanceof BitsTypeDefinition) {
	    	List<Bit> bitList = ((BitsTypeDefinition) type).getBits();
	    	List<Bit> bitListSorted = bitList.stream().sorted((p1, p2)->new Long(p1.getPosition()).compareTo(p2.getPosition())).collect(Collectors.toList());
	    	genValue = "";
	    	if(defaultValue != null) {
	    		for(Bit bit: bitListSorted) {
	    			if(bit.getName().equals(defaultValue))
	    				genValue += bit.getName();
	    			else
	    				genValue += " ";
		    	}
			}else {
				for(Bit bit: bitListSorted) 
		    		genValue += bit.getName();
			}
	    }
		return genValue;
	}

	
	private String buildValue(Element leafElement, TypeDefinition type, ModuleIdentifier moduleId, TreeItem<SchemaItem> rootTreeItem, YangModule yangModule) throws Exception {
		String genValue = null;
		
		//use default value of type if present
		if(type.getDefaultValue() != null) {
			genValue = type.getDefaultValue().toString();
			return genValue;
		}
		
		if(type instanceof BinaryTypeDefinition) {
			List<LengthConstraint>  lengthConstraint = ((BinaryTypeDefinition) type).getLengthConstraints();//size >= 0 Always
			int minChars = 0;
			if(lengthConstraint.size() > 0) 
				minChars = lengthConstraint.get(0).getMin().intValue();
			if(minChars == 0)
	    		minChars++;
	    	genValue = Stream.generate(() -> String.valueOf(generateRndString(1))).limit(minChars).collect(Collectors.joining());
	    	genValue = Base64.getEncoder().encodeToString(genValue.getBytes());
	    		
	    }
		else if(type instanceof BooleanTypeDefinition) {
	    	genValue = "false";
	    		
	    }else if(type instanceof DecimalTypeDefinition) {
	    	List<RangeConstraint>  rangeConstraint = ((DecimalTypeDefinition) type).getRangeConstraints();//size >=1 Always; includes base constraints && and defaults to [0..maxint]
	    	int minValue = rangeConstraint.get(0).getMin().intValue();
	    	genValue = "" + minValue;
	    	Integer fractionDigits = ((DecimalTypeDefinition) type).getFractionDigits();
	    	if(fractionDigits > 0) 
	    		genValue += "." + generateRndInteger(fractionDigits);
	    	else
	    		genValue += ".0";
	    		
	    }else if(type instanceof EmptyTypeDefinition) {
	    	genValue = null;
	    		
	    }else if(type instanceof EnumTypeDefinition) {
	    	List<EnumPair> valueList = ((EnumTypeDefinition) type).getValues();
	    	genValue = valueList.get(0).getName();
	    		
	    }else if(type instanceof IdentityrefTypeDefinition) {
	    	String identityPrefix = generateRndString(3);
			Attr nsAttr = leafElement.getOwnerDocument().createAttribute("xmlns:" + identityPrefix);
			IdentitySchemaNode identitySchema = ((IdentityrefTypeDefinition) type).getIdentity();
			nsAttr.setValue(identitySchema.getQName().getNamespace().toString());
			leafElement.setAttributeNode(nsAttr);
			
			genValue = identityPrefix + ":" +  identitySchema.getQName().getLocalName();
	        
	    }else if(type instanceof IntegerTypeDefinition) {
	    	List<RangeConstraint>  rangeConstraint = ((IntegerTypeDefinition) type).getRangeConstraints();//size >=1 Always; includes base constraints && and defaults to [0..maxint]
	    	int minValue = rangeConstraint.get(0).getMin().intValue();
	    	genValue = "" + minValue;
	    		
	    }else if(type instanceof StringTypeDefinition) {//ignore Regex Pattern Constraints
	    	List<LengthConstraint>  lengthConstraint = ((StringTypeDefinition) type).getLengthConstraints();//size >=1 Always; includes base constraints && and defaults to [0..maxint]
	    	int minChars = 0;
	    	if(lengthConstraint.size() > 0)
	    		minChars = lengthConstraint.get(0).getMin().intValue();
	    	if(minChars == 0)
	    		minChars++;
	    	genValue = Stream.generate(() -> String.valueOf(generateRndString(1))).limit(minChars).collect(Collectors.joining());
	    		
	    }else if(type instanceof UnsignedIntegerTypeDefinition) {
	    	List<RangeConstraint>  rangeConstraint = ((UnsignedIntegerTypeDefinition) type).getRangeConstraints();//size >=1 Always; includes base constraints && and defaults to [0..maxint]
	    	int minValue = rangeConstraint.get(0).getMin().intValue();
	    	genValue = "" + minValue;
	    		
	    }else if(type instanceof UnionTypeDefinition) {										
	    	TypeDefinition childType = ((UnionTypeDefinition) type).getTypes().get(0);
	    	genValue = buildValue(leafElement, childType, moduleId, rootTreeItem, yangModule);
	    		
	    }else if(type instanceof LeafrefTypeDefinition) {									//only generate value for the target leaf
	    	Map<String, String> prefixMap = yangModule.getPrefixMap();
	    	RevisionAwareXPath targetXPath = ((LeafrefTypeDefinition) type).getPathStatement();
    		String xpath = schemaSrv.getXpath(targetXPath, prefixMap);
    		
    		TreeItem<SchemaItem> targetTreeItem = schemaSrv.searchTreeItemRec( rootTreeItem, (SchemaItem p) -> (p.getSchemaNode() != null && p.getSchemaNode().getXpath() != null ? p.getSchemaNode().getXpath().equals(xpath) : false) );
	    	if(targetTreeItem != null) {
		    	List<TypeDefinition<? extends TypeDefinition<?>>> targetTypeList = ((YangLeaf)targetTreeItem.getValue().getSchemaNode()).getTypeList();//size >= 1 Always
				TypeDefinition targetType = targetTypeList.get(0);
				genValue = buildValue(leafElement, targetType, moduleId, rootTreeItem, yangModule);
	    	}else
	    		genValue = "UnknownPath";
	    	
	    }else if(type instanceof InstanceIdentifierTypeDefinition) {						//?????
	    }
		return genValue;
	}
	

	//----------------------------------------------------------------------------------------VALIDATE--------------------------------------------
	public void validateQuery(String query, String queryName) {
		LOG.debug("Validating " + queryName);
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();//they can come either from Schema errors or from Exceptions
		try {
			TreeItem<SchemaItem> treeItem = schemaSrv.getModulesTreeRoot();
			
			Document doc = createDOM(query);
	        Element rootElement = doc.getDocumentElement();
	        MyNamespaceContext nsCtx = new MyNamespaceContext();
	        Map<String, Stack<SchemaItem>> namespacePathMap = new HashMap<String, Stack<SchemaItem>>();
	        
	        validateTreeRec(rootElement, namespacePathMap, treeItem, nsCtx, doc, errorList, queryName);		
		}catch(TransformerException tex) {
			//tex.printStackTrace();
			LOG.error("", tex);
        	if(tex.getCause() != null && tex.getCause() instanceof SAXParseException) {
        		SAXParseException sex = (SAXParseException) tex.getCause();
        		errorList.clear();
    			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, sex.getLineNumber()+"", sex.getMessage()));
        	}else {
        		errorList.clear();
    			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, "0", "Check Error Window"));
        	}
		}
		catch(Exception ex) {
			//ex.printStackTrace();
			LOG.error("", ex);
			errorList.clear();
			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, "0", "Check Error Window"));
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateQueryErrors(errorList, queryName);
				LOG.error(queryName + " is invalid");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
			}else {
				logService.clearQueryErrors(queryName);
				LOG.debug(queryName + " is valid");
			}
		});
	}
	
	
	private SchemaItem validateTreeRec(Element element, Map<String, Stack<SchemaItem>> namespacePathMap, TreeItem<SchemaItem> treeItem, MyNamespaceContext nsCtx, Document doc, List<ErrorItem> errorList, String queryName) throws Exception {
//		if(contor == 0 && pathStack.size() == 0 && !element.equals("rpc") && !element.equals("notification"))//root nodes missing
//			throw new Exception("Root element can only be <rpc> or <notification>");
		SchemaItem schemaItem = null;
		TreeItem<SchemaItem> elementTreeItem = null;
		//update MyNamespaceContext
		String declaredNamespace = getNamespace(element);//might be NULL
		String declaredPrefix = getPrefix(element);//might be ""
		nsCtx.addPrefixMapping(declaredPrefix, declaredNamespace);
		
		XPath xPathObj =  XPathFactory.newInstance().newXPath();
		xPathObj.setNamespaceContext(nsCtx);
		
		//get element namespace
		String namespace;
		String prefix = element.getPrefix();//might be NULL
		if(prefix == null) 
			prefix = "";
		namespace = nsCtx.getNamespaceURI(prefix);
		
		//get pathStack for namespace
		Stack<SchemaItem> pathStack = namespacePathMap.get(namespace);
		if(pathStack == null) {
			pathStack = new Stack<SchemaItem>();
			namespacePathMap.put(namespace, pathStack);
		}
		LocationData locationData = (LocationData) element.getUserData(LocationData.LOCATION_DATA_KEY);
		
		if(pathStack.size() == 0 && (element.getLocalName().equals("rpc") || element.getLocalName().equals("notification"))) { //ignore root nodes
			schemaItem = null;
		}else {
			//build path
			String path = "";
			for(SchemaItem item : pathStack) {
				path += "/" + item.getSchemaNode().getLocalName();
			}
			path += "/" + element.getLocalName();
			String xpath = schemaSrv.getXpath(path, namespace);
			//LOG.debug("locationData " + (locationData != null ? locationData.toString() : "") + " element = " + element.getLocalName() + " 	namespace = " + namespace + " 	xpath = " + xpath );
			
			//search element in schema tree
			elementTreeItem = schemaSrv.searchTreeItemRec( treeItem, 
								(SchemaItem p) -> (p.getSchemaNode() != null && p.getSchemaNode().getXpath() != null ?  (p.getSchemaNode().getXpath().equals(xpath)) : false) );
	    	if(elementTreeItem != null) {
	    		schemaItem = elementTreeItem.getValue();
	    		pathStack.push(schemaItem);
	    		
	    		switch(schemaItem.getTypeEnum()) {
		   	      case RPC:		//augment path by adding "/input" , ADD output also ???
		   	    	  String inputXpath = schemaItem.getSchemaNode().getXpath() + "/input";
		   	    	  TreeItem<SchemaItem> inputTreeItem = schemaSrv.searchTreeItemRec( treeItem, 
							(SchemaItem p) -> (p.getSchemaNode() != null && p.getSchemaNode().getXpath() != null ?  (p.getSchemaNode().getXpath().equals(inputXpath)) : false) );
		   	    	  pathStack.push(inputTreeItem.getValue());
		   	    	  break;
		   	      default:        
		   	    	  break; 
	    		}
	    		
	    		//validate constraints
	    		YangDataSchemaNode elementItemData = (YangDataSchemaNode)schemaItem.getSchemaNode();
    			//validate when
	    		String when = elementItemData.getWhen();
	    		if(when != null && when.trim().length() > 0) {
		    		Boolean isWhen = false;
		    		try {
		    			when  = when.replaceAll(":", prefix + ":");//when is always written with prefix=""
		    			isWhen = (Boolean) xPathObj.compile(when).evaluate(element, XPathConstants.BOOLEAN);
		    		}catch(Exception ex) {
		    			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " failed when constraint " + when));
	    			}
		    		if(!isWhen) {
		    			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " failed when constraint " + when));
		    		}
	    		}
	    		//validate must
	    		Set<MustDefinition> mustSet = elementItemData.getMustSet();
	    		if(mustSet != null) {
		    		for(MustDefinition mustDef: mustSet) {
		    			String mustXpath = "";
		    			Boolean isMust = false;
		    			try {
			    			mustXpath = mustDef.getXpath().toString();
			    			mustXpath  = mustXpath.replaceAll(":", prefix + ":");//must is always written with prefix=""
			    			isMust = (Boolean) xPathObj.compile(mustXpath).evaluate(element, XPathConstants.BOOLEAN);
		    			}catch(Exception ex) {
		    				errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " failed must constraint " + mustXpath));
		    			}
			    		if(!isMust) {
			    			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " failed must constraint " + mustXpath));
			    		}
					}
	    		}
	    	}else {
	    		errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " cannot be found in schema tree"));
	    	}
		}
		

		NodeList childList = element.getChildNodes();
		int length = childList.getLength();
		//validate text value
		if(length == 1) {
			YangModule yangModule = null;
			if(elementTreeItem != null) 
				yangModule = schemaSrv.getModuleTreeItem(elementTreeItem).getValue().getModule();//ALWAYS found
			
	        Node valueNode = childList.item(0);
	        if (Node.TEXT_NODE == valueNode.getNodeType()) {
	            Text valueObj = (Text) valueNode;
	            String value = valueObj.getNodeValue();
	            
	            if(pathStack.size() > 0) {
		            SchemaItem currentItem = pathStack.peek();
					switch(currentItem.getTypeEnum()) {
						case LEAF:
			   	    	  YangLeaf schemaNodeLeaf = (YangLeaf)currentItem.getSchemaNode();
			   	    	  validateLeafValue(null, schemaNodeLeaf, value, treeItem, nsCtx, yangModule, errorList, queryName, locationData);
			   	    	  break;
			   	      	case LEAF_LIST:
			   	    	  YangLeafList schemaNodeLeafList = (YangLeafList)currentItem.getSchemaNode();
			   	    	  validateLeafValue(null, schemaNodeLeafList, value, treeItem, nsCtx, yangModule, errorList, queryName, locationData);
			   	    	  break;
			   	      	default:        
			   	    	  break; 
		    		}
	            }
	        }else  if (Node.CDATA_SECTION_NODE == valueNode.getNodeType()) {//only Bits type
	        	CDATASection valueObj = (CDATASection) valueNode;
	            String value = valueObj.getNodeValue();
	            
	            if(pathStack.size() > 0) {
		            SchemaItem currentItem = pathStack.peek();
					switch(currentItem.getTypeEnum()) {
						case LEAF:
			   	    	  YangLeaf schemaNodeLeaf = (YangLeaf)currentItem.getSchemaNode();
			   	    	  validateLeafValue(null, schemaNodeLeaf, value, treeItem, nsCtx, yangModule, errorList, queryName, locationData);
			   	    	  break;
			   	      	case LEAF_LIST:
			   	    	  YangLeafList schemaNodeLeafList = (YangLeafList)currentItem.getSchemaNode();
			   	    	  validateLeafValue(null, schemaNodeLeafList, value, treeItem, nsCtx, yangModule, errorList, queryName, locationData);
			   	    	  break;
			   	      	default:        
			   	    	  break; 
		    		}
	            }
	        }
		}
		//validate children recursively
		List<Element> dataElem = new ArrayList<Element>(length);
		List<SchemaItem> dataChildren = new ArrayList<SchemaItem>(length);
        for (int i = 0; i < length; i++) {
            Node child = childList.item(i);
            if (Node.ELEMENT_NODE == child.getNodeType()) {
               Element childElem = (Element) child;
               dataElem.add(childElem);
               SchemaItem childSchemaItem = validateTreeRec(childElem, namespacePathMap, treeItem, nsCtx, doc, errorList, queryName);
               if(childSchemaItem != null)
            	   dataChildren.add(childSchemaItem);
            }
        }
        
        if(schemaItem != null && schemaItem.getSchemaNode() != null) {
        	
        	//validate uniques & keys
        	int index = 0;
        	Map<String, Set<String>> keySetMap = new HashMap<String, Set<String>>();
        	Map<String, Set<String>> uniqueSetMap = new HashMap<String, Set<String>>();
        	for(SchemaItem childItem : dataChildren) {
	          	SchemaItemType type = childItem.getTypeEnum();
	  			switch(type){
	  				case LIST:
	  					YangList childListItem = (YangList) childItem.getSchemaNode();
	  					String xpath = childListItem.getXpath();
	  					List<QName> keyDefs = childListItem.getKeyDef();
	  					
	  					String value = "";
	  					for(QName keyDef: keyDefs) {//name ip
	  						Node childListNode = dataElem.get(index);
	  						String keyPath = "./" + prefix + ":" + keyDef.getLocalName();
		  		    		Node keyNode = (Node) xPathObj.compile(keyPath).evaluate(childListNode, XPathConstants.NODE);
		  		    		if(keyNode != null && keyNode.getFirstChild() != null) {
		  		    			String pkValue = keyNode.getFirstChild().getTextContent();
		  		    			value += pkValue;
		  		    			LOG.debug("pkValue " + pkValue + " value " + value);
		  		    		}else
		  		    			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "List Element " + xpath + " is missing primary key column " + keyDef.getLocalName()));
	  					}
	  					Set<String> keySet = keySetMap.get(xpath);
	  					if(keySet == null) {
	  						keySet = new HashSet<String>();
	  						keySetMap.put(xpath, keySet);
	  					}
	  					if(keySet.contains(value))
	  						errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "List Element " + xpath + " failed primary key column unique constraint " + childListItem.getKey()));
	  					else
	  						keySet.add(value);
	  					
	  					Collection<UniqueConstraint> uniqueDef = childListItem.getUniqueDef();
	  					for(UniqueConstraint unique : uniqueDef) {
	  						value = "";
	  						String uniquePath = "";
	  						Collection<Relative> relList = unique.getTag();
	  						for(Relative rel : relList) {//c1/index c1/name
	  							
	  							Node childListNode = dataElem.get(index);
	  							
	  							SchemaPath  relSchema = rel.asSchemaPath();
	  							StringBuilder uniquePathBuf = new StringBuilder(".");
	  							Iterable<QName> relSegments = relSchema.getPathFromRoot();
	  							for (QName segment : relSegments) 
	  								uniquePathBuf.append("/" + prefix + ":" + segment.getLocalName());
	  							uniquePath = uniquePathBuf.toString();
	  							Node uniqueNode = (Node) xPathObj.compile(uniquePath).evaluate(childListNode, XPathConstants.NODE);
			  		    		if(uniqueNode != null && uniqueNode.getFirstChild() != null) {
			  		    			String uniqueValue = uniqueNode.getFirstChild().getTextContent();
			  		    			value += uniqueValue;
			  		    			LOG.debug("uniqueValue " + uniqueValue + " value " + value);
			  		    		}else
			  		    			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "List Element " + xpath + " is missing unique key column " + uniquePath));
	  						}
	  						Set<String> uniqueSet = uniqueSetMap.get(xpath);
		  					if(uniqueSet == null) {
		  						uniqueSet = new HashSet<String>();
		  						uniqueSetMap.put(xpath, uniqueSet);
		  					}
	  						if(uniqueSet.contains(value))
	  							errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "List Element " + xpath + " failed unique constraint " + uniquePath));
		  					else
		  						uniqueSet.add(value);
	  					}
	  					break;
	  					
	  				case LEAF_LIST://add unique on leaf-list?
	  					break;
	  					
	  				default:
	  			}
	  			index++;
        	}
        	
			//validate min/max elements
	        Map<String, Integer> countersMap = new HashMap<String, Integer>();//xpath -> counter
	        for(SchemaItem childItem : dataChildren) {
	        	SchemaItemType type = childItem.getTypeEnum();
				switch(type){
					case LIST:
					case LEAF_LIST:
						String xpath = childItem.getSchemaNode().getXpath();
						Integer counter = countersMap.get(xpath);
						if(counter == null) {
							counter = new Integer(0);
							countersMap.put(xpath, counter);
						}
						counter = counter + 1;
						countersMap.put(xpath, counter);
						break;
					default:
				}
	        }
	        for (Map.Entry<String, Integer> entry : countersMap.entrySet()) {  
	            String xpath = entry.getKey();
	            int counter = entry.getValue();
	            elementTreeItem = schemaSrv.searchTreeItemRec( treeItem, 
						(SchemaItem p) -> (p.getSchemaNode() != null && p.getSchemaNode().getXpath() != null ?  (p.getSchemaNode().getXpath().equals(xpath)) : false) );
				if(elementTreeItem != null) {
					SchemaItem listItem  = elementTreeItem.getValue();
					int minElements = ((YangDataSchemaNode)listItem.getSchemaNode()).getMinElements();
					int maxElements = ((YangDataSchemaNode)listItem.getSchemaNode()).getMaxElements();
					
					if(minElements == 0 && maxElements == 0)
						continue;
					
					if(counter < minElements || counter > maxElements)
						errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "List Element " + listItem.getSchemaNode().getXpath() + " has an invalid number of items " + counter));
				}
	        }
	        
	        //validate isMandatory
			List<YangSchemaNode> schemaChildren = schemaItem.getSchemaNode().getChildren();//schema children list
			for(YangSchemaNode schemaChild : schemaChildren) {
				SchemaItemType type = schemaChild.getTypeEnum();
				switch(type){
					case CHOICE:
						boolean caseFound = false;//true if there is at least one match
						List<YangSchemaNode> caseList = schemaChild.getChildren();
						for(YangSchemaNode caseChild : caseList) {
							List<YangSchemaNode> caseChildren = caseChild.getChildren();
							for(YangSchemaNode child : caseChildren) {
								boolean isFound = dataChildren.stream().anyMatch(dataChild -> dataChild.getSchemaNode().getXpath().equals(child.getXpath()));
								if(isFound)
									caseFound = true;
								boolean isMandatory = ((YangDataSchemaNode)child).isMandatory();
								if(isMandatory && !isFound) 
									errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Mandatory Schema Element " + child.getXpath() + " cannot be found in data tree"));
							}
						}
						boolean isMandatoryChoice = ((YangDataSchemaNode)schemaChild).isMandatory();//Mandatory Choice
						if(isMandatoryChoice && !caseFound) 
							errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Mandatory Choice Element " + schemaChild.getXpath() + " cannot be found in data tree"));
						break;

					case CONTAINER:
						boolean isFoundCont = dataChildren.stream().anyMatch(dataChild -> dataChild.getSchemaNode().getXpath().equals(schemaChild.getXpath()));
						boolean isPresence = ((YangContainer)schemaChild).isPresence();
						if(!isFoundCont && !isPresence) {
							List<YangSchemaNode> contChildList = schemaChild.getChildren();
							for(YangSchemaNode child : contChildList) {
								boolean isMandatory = ((YangDataSchemaNode)child).isMandatory();
								if(isMandatory) 
									errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Mandatory Container Element " + schemaChild.getXpath() + " cannot be found in data tree"));
							}
						}
						break;
						
					case LIST:
						boolean isFoundList = dataChildren.stream().anyMatch(dataChild -> dataChild.getSchemaNode().getXpath().equals(schemaChild.getXpath()));
						int minElements = ((YangDataSchemaNode)schemaChild).getMinElements();
						if(!isFoundList && minElements > 0) 
							errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Mandatory List Element " + schemaChild.getXpath() + " cannot be found in data tree"));
						break;
						
					case LEAF_LIST:
						boolean isFoundLeafList = dataChildren.stream().anyMatch(dataChild -> dataChild.getSchemaNode().getXpath().equals(schemaChild.getXpath()));
						minElements = ((YangDataSchemaNode)schemaChild).getMinElements();
						if(!isFoundLeafList && minElements > 0) 
							errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Mandatory Leaf List Element " + schemaChild.getXpath() + " cannot be found in data tree"));
						break;
						
					default:
						boolean isMandatory = ((YangDataSchemaNode)schemaChild).isMandatory();
						if(isMandatory) {
							boolean isFound = dataChildren.stream().anyMatch(dataChild -> dataChild.getSchemaNode().getXpath().equals(schemaChild.getXpath()));
							if(!isFound)
								errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Mandatory Schema Element " + schemaChild.getXpath() + " cannot be found in data tree"));
						}
				}
			}
        }
        
        if(pathStack.size() > 0) 
			pathStack.pop();
        
        return schemaItem;
	}
	
	
	private String getNamespace(Element element) {
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
	
	
	private String getPrefix(Element element) {
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
	
	
	private boolean validateLeafValue(TypeDefinition unionType, YangSchemaNode schemaNode, String value, TreeItem<SchemaItem> rootTreeItem, MyNamespaceContext nsCtx, YangModule yangModule, List<ErrorItem> errorList, String queryName, LocationData locationData) throws Exception {
		boolean isValid = false;
		
		List<TypeDefinition<? extends TypeDefinition<?>>> typeList = schemaNode.getTypeList();
	    TypeDefinition type = typeList.get(0);
	    if(unionType != null)
	    	type = unionType;
	    ModuleIdentifier moduleId = schemaNode.getModuleIdentifier();
	    String xpath = schemaNode.getXpath();
	    
	    if(!(type instanceof EmptyTypeDefinition) && (value == null || value.trim().length() == 0))
	    	errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid empty value "));
	    
	    if(type instanceof UnionTypeDefinition) {	
	    	
	    	List<TypeDefinition<?>> unionTypes = ((UnionTypeDefinition) type).getTypes();
	    	for(TypeDefinition uType : unionTypes) {
	    		try {
	    			validateLeafValue(uType, schemaNode, value, rootTreeItem, nsCtx, yangModule, errorList, queryName, locationData);
	    			isValid = true;
	    			break;
	    		}catch(Exception ex) {
	    		}
	    	}
	    	if(!isValid)
	    		errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element union " + xpath + " has an invalid value " + value));
				
		}else if(type instanceof LeafrefTypeDefinition) {									//only generate value for the target leaf
			Map<String, String> prefixMap = yangModule.getPrefixMap();
	    	RevisionAwareXPath targetXPath = ((LeafrefTypeDefinition) type).getPathStatement();
    		String targetPath = schemaSrv.getXpath(targetXPath, prefixMap);
    		
	    	TreeItem<SchemaItem> targetTreeItem = schemaSrv.searchTreeItemRec( rootTreeItem,  (SchemaItem p) -> (p.getSchemaNode() != null && p.getSchemaNode().getXpath() != null ? p.getSchemaNode().getXpath().equals(targetPath) : false) );
	    	if(targetTreeItem != null) 
				validateLeafValue(null, targetTreeItem.getValue().getSchemaNode(), value, rootTreeItem, nsCtx, yangModule, errorList, queryName, locationData);
	    	else
	    		errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element leafref target " + targetXPath + " cannot be found in schema tree"));
	    	
	    }else if(type instanceof BinaryTypeDefinition) {
			try {
				value = new String(Base64.getDecoder().decode(value));
				int charCount = value.trim().length();
				List<LengthConstraint>  lengthConstraints = ((BinaryTypeDefinition) type).getLengthConstraints();//size >= 0 Always
				if(lengthConstraints.size() == 0)
					isValid = true;
				else {
					for(LengthConstraint lengthConstraint : lengthConstraints) {
						int minChars = lengthConstraint.getMin().intValue();
						int maxChars = lengthConstraint.getMax().intValue();
						if(charCount >= minChars && charCount <= maxChars) {
							isValid = true;
							break;
						}
					}
				}
				if(!isValid)
					errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid binary value " + value + " for length constraint"));
			}catch(IllegalArgumentException ex) {
				errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid binary value " + value + " for base64 format"));
			}
	    }else if(type instanceof BitsTypeDefinition) {
	    	try {
		    	List<Bit> bitList = ((BitsTypeDefinition) type).getBits();
		    	List<Bit> bitListSorted = bitList.stream().sorted((p1, p2)->new Long(p1.getPosition()).compareTo(p2.getPosition())).collect(Collectors.toList());
		    	int index = 0;
		    	for(Bit bit: bitListSorted) {
		    		String bitName = bit.getName();
		    		String valueBitName = value.substring(index, index + 1);
		    		if(!valueBitName.equals(" ")) {
		    			valueBitName = value.substring(index, index + bitName.length());
		    			if(!valueBitName.equals(bitName))
		    				errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid bit value " + value));
		    			index = index + bitName.length();
		    		}else
		    			index = index + 1;
		    		
		    	}
	    	}catch(Exception ex) {
	    		errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid bit value " + value));
			}
	    	
	    }else if(type instanceof BooleanTypeDefinition) {
	    	if(!value.equals("true") && !value.equals("false"))
	    		errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid boolean value " + value));
	    	
	    }else if(type instanceof DecimalTypeDefinition) {
		    float floatValue = 0f;
		    try {
		    	floatValue = Float.parseFloat(value);
		    }catch(NumberFormatException e) {
		    	errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid decimal value " + value));
		    }catch(NullPointerException e) {
		    	errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " is missing the decimal value "));
		    }
		    //fraction digits contraint
		    int decimalIndex = value.indexOf(".");
		    if(decimalIndex == -1)
		    	errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid decimal value " + value + " without decimal point"));
		    try {
		    	String decimalString = value.substring(decimalIndex + 1);
		    	Integer fractionDigits = ((DecimalTypeDefinition) type).getFractionDigits();
		    	if(decimalString == null || decimalString.length() != fractionDigits)
		    		errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid decimal value " + value + " with wrong number of digits"));
		    }catch(IndexOutOfBoundsException ex) {
		    	errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid decimal value " + value));
			}
		    //range constraint
		    List<RangeConstraint>  rangeConstraints = ((DecimalTypeDefinition) type).getRangeConstraints();
		    if(rangeConstraints.size() == 0)
				isValid = true;
			else {
				for(RangeConstraint rangeConstraint : rangeConstraints) {
					float minValue = rangeConstraint.getMin().floatValue();
					float maxValue = rangeConstraint.getMax().floatValue();
						
					if(floatValue >= minValue && floatValue <= maxValue) {
						isValid = true;
						break;
					}
				}
			}
			if(!isValid)
				errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid decimal value " + value + " for range constraint"));
	    		
	    }else if(type instanceof IntegerTypeDefinition) {
	    	int intValue = 0;
		    try {
		    	intValue = Integer.parseInt(value);
		    }catch(NumberFormatException e) {
		    	errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid integer value " + value));
		    }catch(NullPointerException e) {
		    	errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " is missing the integer value "));
		    }
		    //range constraint
		    List<RangeConstraint>  rangeConstraints = ((IntegerTypeDefinition) type).getRangeConstraints();
		    if(rangeConstraints.size() == 0)
				isValid = true;
			else {
				for(RangeConstraint rangeConstraint : rangeConstraints) {
					int minValue = rangeConstraint.getMin().intValue();
					int maxValue = rangeConstraint.getMax().intValue();
						
					if(intValue >= minValue && intValue <= maxValue) {
						isValid = true;
						break;
					}
				}
			}
			if(!isValid)
				errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid integer value " + value + " for range constraint"));
	    		
	    }else if(type instanceof UnsignedIntegerTypeDefinition) {
	    	long intValue = 0;
		    try {
		    	intValue = Long.parseLong(value);
		    }catch(NumberFormatException e) {
		    	errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid unsigned integer value " + value));
		    }catch(NullPointerException e) {
		    	errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " is missing the unsigned integer value "));
		    }
		    if(intValue < 0)
		    	errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an negative unsigned integer value " + value));
		    //range constraint
		    List<RangeConstraint>  rangeConstraints = ((UnsignedIntegerTypeDefinition) type).getRangeConstraints();
		    if(rangeConstraints.size() == 0)
				isValid = true;
			else {
				for(RangeConstraint rangeConstraint : rangeConstraints) {
					long minValue = rangeConstraint.getMin().longValue();
					long maxValue = rangeConstraint.getMax().longValue();
						
					if(intValue >= minValue && intValue <= maxValue) {
						isValid = true;
						break;
					}
				}
			}
			if(!isValid)
				errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid unsigned integer value " + value + " for range constraint"));
	    		
	    }else if(type instanceof StringTypeDefinition) {
	    	//length constraint
	    	int charCount = value.trim().length();
			List<LengthConstraint>  lengthConstraints = ((StringTypeDefinition) type).getLengthConstraints();
			if(lengthConstraints.size() == 0)
				isValid = true;
			else {
				for(LengthConstraint lengthConstraint : lengthConstraints) {
					int minChars = lengthConstraint.getMin().intValue();
					int maxChars = lengthConstraint.getMax().intValue();
					if(charCount >= minChars && charCount <= maxChars) {
						isValid = true;
						break;
					}
				}
			}
			if(!isValid)
				errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid string value " + value + " for length constraint"));
			//pattern constraint
			List<PatternConstraint>  patternConstraints = ((StringTypeDefinition) type).getPatternConstraints();
			for(PatternConstraint patternConstraint : patternConstraints) {
				String regex = patternConstraint.getRegularExpression();
				if(!value.matches(regex))
					errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid string value " + value + " for pattern constraint"));
			}
	    		
	    }else if(type instanceof EmptyTypeDefinition) {
	    	if(value.length() > 0)
	    		errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " must have an empty value"));
	    		
	    }else if(type instanceof EnumTypeDefinition) {
	    	List<EnumPair> pairList = ((EnumTypeDefinition) type).getValues();
	    	for(EnumPair pair:pairList) {
	    		if(value.equals(pair.getName())) {
	    			isValid = true;
					break;
	    		}
	    	}
	    	if(!isValid)
	    		errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element " + xpath + " has an invalid enum value " + value));
	    	
	    }else if(type instanceof IdentityrefTypeDefinition) {
	    	String prefix = value.substring(0, value.indexOf(":"));
	    	String namespace = nsCtx.getNamespaceURI(prefix);
	    	String identityName = value.substring(value.indexOf(":") + 1);
	    	String identityPath = schemaSrv.getXpath("/" + identityName, namespace);
	    	TreeItem<SchemaItem> identityTreeItem = schemaSrv.searchTreeItemRec( rootTreeItem, 
						(SchemaItem p) -> (p.getSchemaNode() != null && p.getSchemaNode().getXpath() != null ?  (p.getSchemaNode().getXpath().equals(identityPath)) : false) );
	    	if(identityTreeItem == null)
	    		errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, locationData, "Element identity " + identityPath + " cannot be found in schema tree"));
	    	
	    }else if(type instanceof InstanceIdentifierTypeDefinition) {						//?????
	    }
		
		return isValid;
	}
	
	
	private String generateRndString(int length) {
		return RandomStringUtils.random(length, true, false);//length, useLetters, useNumbers
	}
	
	
	private String generateRndInteger(int length) {
		return RandomStringUtils.random(length, false, true);//length, useLetters, useNumbers
	}
	

	//create DOM with line number info attached to each Node
	private static Document createDOM(String input) throws Exception {
		ByteArrayInputStream inputIs = new ByteArrayInputStream(input.getBytes("UTF-8"));
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer nullTransformer = transformerFactory.newTransformer();
		Document doc = docBuilder.newDocument();
		DOMResult domResult = new DOMResult(doc);
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setNamespaceAware(true);
		
		SAXParser saxParser = saxParserFactory.newSAXParser();
		XMLReader xmlReader = saxParser.getXMLReader();
		LocationAnnotator locationAnnotator = new LocationAnnotator(xmlReader, doc);
		InputSource inputSource = new InputSource(inputIs);
		SAXSource saxSource = new SAXSource(locationAnnotator, inputSource);
		nullTransformer.transform(saxSource, domResult);
		return doc;
	}
	
	public static void main(String[] args) throws Exception {
		String query="<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" + 
				"<edit-config> \n" + 
				"  <target>\n" + 
				"    <candidate/>\n" + 
				"  </target>  \n" + 
				"  <config xmlns:xc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">  \n" + 
				"    <interfaces xmlns=\"urn:ietf:params:xml:ns:yang:ietf-interfaces\" xc:operation=\"replace\">  \n" + 
				"      <interface> \n" + 
				"        <name>eth0</name>  \n" + 
				"        <description>Ethernet 0</description>  \n" + 
				"        <type xmlns:ianaift=\"urn:ietf:params:xml:ns:yang:iana-if-type\">ianaift:ethernetCsmacd</type>  \n" + 
				"        <enabled>true</enabled> \n" + 
				"      </interface>  \n" + 
				"      <interface> \n" + 
				"        <name>eth1</name>  \n" + 
				"        <description>Ethernet 1</description>  \n" + 
				"        <type xmlns:ianaift=\"urn:ietf:params:xml:ns:yang:iana-if-type\">ianaift:ethernetCsmacd</type>  \n" + 
				"        <enabled>true</enabled> \n" + 
				"      </interface> \n" + 
				"    </interfaces> \n" + 
				"  </config> \n" + 
				"</edit-config>\n" + 
				"</rpc>";
		Document doc = createDOM(query);
        Element rootElement = doc.getDocumentElement();
	}
	
}
