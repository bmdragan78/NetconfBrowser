package com.yang.ui.services.model;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.RandomStringUtils;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.model.api.AnyxmlSchemaNode;
import org.opendaylight.yangtools.yang.model.api.CaseSchemaNode;
import org.opendaylight.yangtools.yang.model.api.ChoiceSchemaNode;
import org.opendaylight.yangtools.yang.model.api.ContainerSchemaNode;
import org.opendaylight.yangtools.yang.model.api.Deviation;
import org.opendaylight.yangtools.yang.model.api.ExtensionDefinition;
import org.opendaylight.yangtools.yang.model.api.FeatureDefinition;
import org.opendaylight.yangtools.yang.model.api.LeafListSchemaNode;
import org.opendaylight.yangtools.yang.model.api.LeafSchemaNode;
import org.opendaylight.yangtools.yang.model.api.ListSchemaNode;
import org.opendaylight.yangtools.yang.model.api.SchemaNode;
import org.opendaylight.yangtools.yang.model.api.SchemaPath;
import org.opendaylight.yangtools.yang.model.api.TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.stmt.SchemaNodeIdentifier.Absolute;
import org.opendaylight.yangtools.yang.model.api.type.BooleanTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.DecimalTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.EnumTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.IdentityrefTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Int16TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Int32TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Int64TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Int8TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.StringTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Uint16TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Uint32TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Uint64TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Uint8TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.EnumTypeDefinition.EnumPair;



//Remove this class !!!

public class YangNode implements Comparable<YangNode> {//NOT USED ANYMORE !!! -> Remove references
	
	@Override
	public String toString() {
		switch (typeEnum)
	    {
	      case CONTAINER:
	      case LIST:
	      case LEAF_LIST:
	      case LEAF:  
	      case CHOICE:  
	      case CHOICE_CASE:  
	      case ANY_XML: 
	      case EXTENSION:  
	      case AUGMENTATION:  
	      case FEATURE: 
	      case GROUPING:  
	      case IDENTITY:  
	      case NOTIFICATION:  
	      case RPC:  
	      case TYPEDEF:  
	    	  return getLocalName() + (config ? " _c" : " _s");
	      case DEVIATION: 
	    	  return getLocalName();
	     
	      default:        
	    	  return 			"Unknown Yang Schema Item";
	    }
	}

	@Override
	public int compareTo(YangNode o) {
		int result = qName.compareTo(o.qName);
		return result;
	}

	public static enum YangTypeEnum {

		CONTAINER("CONTAINER"), LIST("LIST"), LEAF_LIST("LEAF_LIST"), LEAF("LEAF"), CHOICE("CHOICE"), CHOICE_CASE("CHOICE_CASE"), ANY_XML("ANY_XML"),
		EXTENSION("EXTENSION"), AUGMENTATION("AUGMENTATION"), DEVIATION("DEVIATION"), FEATURE("FEATURE"), GROUPING("EXTENSION"), IDENTITY("IDENTITY"), NOTIFICATION("NOTIFICATION"), RPC("RPC"), TYPEDEF("TYPEDEF"), 
		UNKNOWN("UNKNOWN");

		private final String value;

		YangTypeEnum(String value) {
			this.value = value;
		}

		public String text() {
			return value;
		}

		public static final YangTypeEnum getInstance(String action) {
			for (YangTypeEnum actionValue : YangTypeEnum.values()) {
				if (actionValue.text().equals(action)) {
					return actionValue;
				}
			}
			return null;
		}
	}
	
	public static final String NS_REV_FILE_SEP = "@";
	
	public static final String YANG_EXT = ".yang";
	
	//Symbol used to concatenate namespace & revision together when displaying any qualified name
	public static final String QNAME_REVISION_DELIMITER = "?revision=";

	//Symbol used to represent separator between prefix and local name for an XML element
	public static final String PREFIX_NAME_SEP = ":";

	//Symbol used to represent the path of the root node from yang schema 
	public static final String MODULE_PATH = "/*";
	
	public static final String formatDate(Date inputDate) {
		return new SimpleDateFormat("yyyy-MM-dd").format(inputDate);
	}
	
	public boolean isRootNode() {//yanglib:(urn:ietf:params:xml:ns:yang:ietf-yang-library?revision=2018-01-17)/yang-library/checksum
		String[] pathSegments = simplePath.split("\\/");
		return pathSegments.length == 2;
	}
	
	public static String getSimplePathFromQueryPath(String xPath) {
		int startPath = xPath.indexOf("/");
		return xPath.substring(startPath);
	}

	private static String getSimplePath(SchemaNode dataSchemaNode) {
		StringBuilder xpathBuf = new StringBuilder();
//		Iterable<QName> pathSegments = dataSchemaNode.getPath().getPathFromRoot();
		Iterable<QName> pathSegments = 	SchemaPath.of( Absolute.of(dataSchemaNode.getQName())).getPathFromRoot();

		
		for (QName segment : pathSegments) 
			xpathBuf.append("/" + segment.getLocalName());
		return xpathBuf.toString();
	}
	
	private static String getPrefixPath(SchemaNode dataSchemaNode, String prefix) {
		StringBuilder xpathBuf = new StringBuilder();
		Iterable<QName> pathSegments = SchemaPath.of( Absolute.of(dataSchemaNode.getQName())).getPathFromRoot();
		for (QName segment : pathSegments) 
			xpathBuf.append("/" + prefix + PREFIX_NAME_SEP + segment.getLocalName());
		return xpathBuf.toString();
	}

	private static String getQPath(SchemaNode dataSchemaNode, String prefix) {
		StringBuilder xpathBuf = new StringBuilder();		
//		Iterable<QName> pathSegments = dataSchemaNode.getPath().getPathFromRoot();
//		for (QName segment : pathSegments) 
//			xpathBuf.append("/" + ModuleIdentifier.fromQName(segment) + segment.getLocalName());
		return xpathBuf.toString();
	}

	private static String getQueryPath(SchemaNode dataSchemaNode) {
		StringBuilder xpathBuf = new StringBuilder();
		
//		ModuleContext moduleCtx = new ModuleContext(dataSchemaNode.getQName().getNamespace().toString(), formatDate(dataSchemaNode.getQName().getRevision()));
//		xpathBuf.append(moduleCtx.toString() + YangNode.getSimplePath(dataSchemaNode));
		return xpathBuf.toString();
	}

	private String ns;

	private String revision;

	private String prefix;

	private String qName;

	private String localName;

	private String prefixName;

	private YangTypeEnum typeEnum;

	private String dataType;

	private boolean config = false;

	private boolean presence = false;

	private String listKeys;

	private boolean mandatory = false;

	private String defaultValue;

	private String simplePath;

	private String prefixPath;

	private String queryPath;
	
	private String qPath;

	private int pathLength;

	//private String description;
	
	private ModuleIdentifier moduleIdentifier;
	
	private ModuleContext moduleCtx;
	
	private boolean isIdentityRef = false;
	
	private String identityRefNs;
	
	private String identityRefPrefix;


	public static int getQPathLength(SchemaNode dataSchemaNode) {
		int length = 0;
		//Iterable<QName> pathSegments = dataSchemaNode.getPath().getPathFromRoot();
		Iterable<QName> pathSegments = 	SchemaPath.of( Absolute.of(dataSchemaNode.getQName())).getPathFromRoot();
		for (QName segment : pathSegments)
			++length;
		return length;
	}

	
	public static int getQPathLength(String queryPath) {
		String[] pathSegments = queryPath.split("\\/");
		return pathSegments.length -1;
	}
	

	public YangNode() {
		super();
	}
	
	
	
	public YangNode(Deviation deviationNode, String prefix) {
		super();
		typeEnum = YangTypeEnum.DEVIATION;
		localName = deviationNode.getDescription().orElse(""); 
	}
	
	
	public YangNode(SchemaNode schemaNode, String prefix) {
		//this.children = new ArrayList<YangNode>(10);
		this.ns = schemaNode.getQName().getNamespace().toString();
		this.revision = new SimpleDateFormat("yyyy-MM-dd").format(schemaNode.getQName().getRevision());
		this.prefix = prefix;
		//this.moduleIdentifier = new ModuleIdentifier(ns, revision);
		this.moduleCtx = new ModuleContext(ns, revision);

		localName = schemaNode.getQName().getLocalName();
		prefixName = prefix + PREFIX_NAME_SEP + localName;
		qName = schemaNode.getQName().toString();
		
		simplePath = getSimplePath(schemaNode);
		prefixPath = getPrefixPath(schemaNode, prefix);
		qPath = getQPath(schemaNode, prefix);
		pathLength = getQPathLength(schemaNode);
		//queryPath = getQueryPath(schemaNode, prefix);
		
//		config = dataSchemaNode.isConfiguration();
//		mandatory = dataSchemaNode.getConstraints().isMandatory();
		//description = schemaNode.getDescription();
		
		if (schemaNode instanceof ContainerSchemaNode) {
			typeEnum = YangTypeEnum.CONTAINER;
			presence = ((ContainerSchemaNode) schemaNode).isPresenceContainer();
		} else if (schemaNode instanceof ListSchemaNode) {
			this.typeEnum = YangTypeEnum.LIST;
			Collection<QName> keyDefs = ((ListSchemaNode) schemaNode).getKeyDefinition();
			StringBuilder keys = new StringBuilder("");
			for (QName keyDef : keyDefs)
				keys.append(keyDef.getLocalName() + ",");
			keys.replace(keys.length() - 1, keys.length(), "");// remove last comma
			listKeys = keys.toString();
		} else if (schemaNode instanceof LeafSchemaNode) {
			typeEnum = YangTypeEnum.LEAF;
			dataType = ((LeafSchemaNode) schemaNode).getType().toString();
			
			TypeDefinition type = ((LeafSchemaNode) schemaNode).getType();
			if(type instanceof IdentityrefTypeDefinition) {
//				IdentitySchemaNode firstElement = ((IdentityrefTypeDefinition) type).getIdentities().stream().findFirst().get();
//				IdentitySchemaNode secondElement =  firstElement.getDerivedIdentities().stream().findFirst().get();
//				if(secondElement.getDerivedIdentities() != null && secondElement.getDerivedIdentities().size() > 0) {
//					IdentitySchemaNode thirdElement =  secondElement.getDerivedIdentities().stream().findFirst().get();
//					QName qname = thirdElement.getQName();
//					
//					isIdentityRef = true;
//					defaultValue = qname.getLocalName();
//					identityRefNs = qname.getNamespace().toString();
//					identityRefPrefix = generateRndString();
//				}
			}else if(type instanceof EnumTypeDefinition) {
				List<EnumPair> valuesList = ((EnumTypeDefinition) type).getValues();
				EnumPair pair = valuesList.get(0);
				defaultValue = Integer.toString(pair.getValue());
			}else if(type instanceof BooleanTypeDefinition) {
				defaultValue = "false";
			}else if(type instanceof DecimalTypeDefinition || type instanceof Int8TypeDefinition || type instanceof Int16TypeDefinition || type instanceof Int32TypeDefinition || type instanceof Int64TypeDefinition 
					|| type instanceof Uint8TypeDefinition || type instanceof Uint16TypeDefinition || type instanceof Uint32TypeDefinition || type instanceof Uint64TypeDefinition ) {
				defaultValue = generateRndInteger();
			}else if(type instanceof StringTypeDefinition) {
				defaultValue = generateRndString();
			}else {
				//defaultValue = ((LeafSchemaNode) schemaNode).getDefault() != null ? ((LeafSchemaNode) schemaNode).getDefault() : "defaultValue";
				defaultValue = ((LeafSchemaNode) schemaNode).getType().getDefaultValue().isPresent() ?  ((LeafSchemaNode) schemaNode).getType().getDefaultValue().get().toString() : "defaultValue";
			}
			
			if(defaultValue == null)
				defaultValue = ((LeafSchemaNode) schemaNode).getType().getDefaultValue().isPresent() ?  ((LeafSchemaNode) schemaNode).getType().getDefaultValue().get().toString() : "defaultValue";
			
		} else if (schemaNode instanceof LeafListSchemaNode) {
			this.typeEnum = YangTypeEnum.LEAF_LIST;
			this.defaultValue = "defaultValue";
		}else if (schemaNode instanceof ChoiceSchemaNode)
			this.typeEnum = YangTypeEnum.CHOICE;
		
		else if (schemaNode instanceof CaseSchemaNode)
			this.typeEnum = YangTypeEnum.CHOICE_CASE;
		
		else if (schemaNode instanceof AnyxmlSchemaNode)
			this.typeEnum = YangTypeEnum.ANY_XML;
		
		else if (schemaNode instanceof ExtensionDefinition)
			this.typeEnum = YangTypeEnum.EXTENSION;
		
		else if (schemaNode instanceof FeatureDefinition)
			this.typeEnum = YangTypeEnum.FEATURE;
		
		else if (schemaNode instanceof Deviation)
			this.typeEnum = YangTypeEnum.DEVIATION;
		
		else
			this.typeEnum = YangTypeEnum.UNKNOWN;
	}
	
	
	
//	public YangNode(DataSchemaNode dataSchemaNode, String prefix) {
//		this.children = new ArrayList<YangNode>(10);
//		this.ns = dataSchemaNode.getQName().getNamespace().toString();
//		this.revision = new SimpleDateFormat("yyyy-MM-dd").format(dataSchemaNode.getQName().getRevision());
//		this.prefix = prefix;
//		this.moduleIdentifier = new ModuleIdentifier(ns, revision);
//		this.moduleCtx = new ModuleContext(this.prefix, ns, revision);
//
//		localName = dataSchemaNode.getQName().getLocalName();
//		prefixName = prefix + PREFIX_NAME_SEP + localName;
//		qName = dataSchemaNode.getQName().toString();
//		
//		simplePath = getSimplePath(dataSchemaNode);
//		prefixPath = getPrefixPath(dataSchemaNode, prefix);
//		qPath = getQPath(dataSchemaNode, prefix);
//		pathLength = getQPathLength(dataSchemaNode);
//		queryPath = getQueryPath(dataSchemaNode, prefix);
//		
//		config = dataSchemaNode.isConfiguration();
//		mandatory = dataSchemaNode.getConstraints().isMandatory();
//		description = dataSchemaNode.getDescription();
//		
//		if (dataSchemaNode instanceof ContainerSchemaNode) {
//			typeEnum = YangTypeEnum.CONTAINER;
//			presence = ((ContainerSchemaNode) dataSchemaNode).isPresenceContainer();
//		} else if (dataSchemaNode instanceof ListSchemaNode) {
//			this.typeEnum = YangTypeEnum.LIST;
//			Collection<QName> keyDefs = ((ListSchemaNode) dataSchemaNode).getKeyDefinition();
//			StringBuilder keys = new StringBuilder("");
//			for (QName keyDef : keyDefs)
//				keys.append(keyDef.getLocalName() + ",");
//			keys.replace(keys.length() - 1, keys.length(), "");// remove last comma
//			listKeys = keys.toString();
//		} else if (dataSchemaNode instanceof LeafSchemaNode) {
//			typeEnum = YangTypeEnum.LEAF;
//			dataType = ((LeafSchemaNode) dataSchemaNode).getType().toString();
//			
//			TypeDefinition type = ((LeafSchemaNode) dataSchemaNode).getType();
//			if(type instanceof IdentityrefTypeDefinition) {
////				IdentitySchemaNode firstElement = ((IdentityrefTypeDefinition) type).getIdentities().stream().findFirst().get();
////				IdentitySchemaNode secondElement =  firstElement.getDerivedIdentities().stream().findFirst().get();
////				if(secondElement.getDerivedIdentities() != null && secondElement.getDerivedIdentities().size() > 0) {
////					IdentitySchemaNode thirdElement =  secondElement.getDerivedIdentities().stream().findFirst().get();
////					QName qname = thirdElement.getQName();
////					
////					isIdentityRef = true;
////					defaultValue = qname.getLocalName();
////					identityRefNs = qname.getNamespace().toString();
////					identityRefPrefix = generateRndString();
////				}
//			}else if(type instanceof EnumTypeDefinition) {
//				List<EnumPair> valuesList = ((EnumTypeDefinition) type).getValues();
//				EnumPair pair = valuesList.get(0);
//				defaultValue = Integer.toString(pair.getValue());
//			}else if(type instanceof BooleanTypeDefinition) {
//				defaultValue = "false";
//			}else if(type instanceof DecimalTypeDefinition || type instanceof IntegerTypeDefinition ) {
//				defaultValue = generateRndInteger();
//			}else if(type instanceof StringTypeDefinition) {
//				defaultValue = generateRndString();
//			}else {
//				defaultValue = ((LeafSchemaNode) dataSchemaNode).getDefault() != null ? ((LeafSchemaNode) dataSchemaNode).getDefault() : "defaultValue";
//			}
//			
//			if(defaultValue == null)
//				defaultValue = ((LeafSchemaNode) dataSchemaNode).getDefault() != null ? ((LeafSchemaNode) dataSchemaNode).getDefault() : "defaultValue";
//			
//		} else if (dataSchemaNode instanceof LeafListSchemaNode) {
//			this.typeEnum = YangTypeEnum.LEAF_LIST;
//			this.defaultValue = "defaultValue";
//		}else if (dataSchemaNode instanceof ChoiceSchemaNode)
//			this.typeEnum = YangTypeEnum.CHOICE;
//		else if (dataSchemaNode instanceof ChoiceCaseNode)
//			this.typeEnum = YangTypeEnum.CHOICE_CASE;
//		else if (dataSchemaNode instanceof AnyXmlSchemaNode)
//			this.typeEnum = YangTypeEnum.ANY_XML;
//		else
//			this.typeEnum = YangTypeEnum.UNKNOWN;
//	}
	
	
	private String generateRndString() {
		return RandomStringUtils.random(5, true, false);//length, useLetters, useNumbers
	}
	
	private String generateRndInteger() {
		return RandomStringUtils.random(5, false, true);//length, useLetters, useNumbers
	}

	public boolean isIdentityRef() {
		return isIdentityRef;
	}

	public String getIdentityRefNs() {
		return identityRefNs;
	}
	
	public String getIdentityRefPrefix() {
		return identityRefPrefix;
	}
	
	public String getNs() {
		return ns;
	}

	public String getRevision() {
		return revision;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getPrefixName() {
		return prefixName;
	}

	public String getLocalName() {
		return localName;
	}

	public String getqName() {
		return qName;
	}

	public YangTypeEnum getTypeEnum() {
		return typeEnum;
	}

	public String getDataType() {
		return dataType;
	}

	public boolean isConfig() {
		return config;
	}

	public boolean isPresence() {
		return presence;
	}

	public String getListKeys() {
		return listKeys;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getSimplePath() {
		return simplePath;
	}

	public String getPrefixPath() {
		return prefixPath;
	}

	public String getQpath() {
		return qPath;
	}

	public String getQueryPath() {
		return queryPath;
	}
	
	public void setQueryPath(String queryPath) {
		this.queryPath = queryPath;
	}

	public int getPathLength() {
		return pathLength;
	}

	public ModuleIdentifier getModuleIdentifier() {
		return moduleIdentifier;
	}

	public ModuleContext getModuleCtx() {
		return moduleCtx;
	}
}