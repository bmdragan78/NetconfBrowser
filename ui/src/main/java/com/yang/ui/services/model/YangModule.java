package com.yang.ui.services.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opendaylight.yangtools.yang.model.api.ModuleImport;

import com.yang.ui.services.model.ModuleIdentifier;
import com.yang.ui.services.model.YangNode;



public class YangModule implements Comparable<YangModule> {
	
	@Override
	public String toString() {
		return " " + name + YangNode.QNAME_REVISION_DELIMITER + revision + " ";
	}
	

	@Override
	public int compareTo(YangModule o) {
		return moduleIdentifier.compareTo(o.moduleIdentifier);
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((moduleIdentifier == null) ? 0 : moduleIdentifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModuleIdentifier other = (ModuleIdentifier) obj;
		return other.equals(obj);
		
//		if (name == null) {
//			if (other.name != null)
//				return false;
//		} else if (!name.equals(other.name))
//			return false;
//		if (namespace == null) {
//			if (other.namespace != null)
//				return false;
//		} else if (!namespace.equals(other.namespace))
//			return false;
////		if (prefix == null) {
////			if (other.prefix != null)
////				return false;
////		} else if (!prefix.equals(other.prefix))
////			return false;
//		if (revision == null) {
//			if (other.revision != null)
//				return false;
//		} else if (!revision.equals(other.revision))
//			return false;
//		return true;
	}
	
	private String name;
	
	private String namespace;
	
	private String prefix;
	
	private String revision;
	
	private String organization;
	
	private String contact;
	
	private String description;
	
	private String queryPath;
	
	private ModuleIdentifier moduleIdentifier;
	
	//private ModuleContext moduleCtx;
	
	private boolean isDataContainer;
	
	
	private List<YangSchemaNode> children;
	
	private List<FeatureItem> featureItemList;
	

	public List<YangSchemaNode> getChildren() {
		return children;
	}
	
	private Map<String, String> prefixMap;//prefix -> module name from imports


	public YangModule() {
		super();
	}
	
	public YangModule(String name, String namespace, String prefix, String revision, String organization, String contact, String description, boolean isDataContainer, Collection<? extends ModuleImport>  moduleImports) {
		super();
		this.name = name;
		this.namespace = namespace;
		this.prefix = prefix;
		this.revision = revision;
		this.organization = organization;
		this.contact = contact;
		this.description = description;
		this.moduleIdentifier = new ModuleIdentifier(name, namespace, revision, prefix);
		//this.moduleCtx = new ModuleContext(namespace, revision);
		//this.queryPath = moduleCtx.toString() + "/*";
		this.queryPath = "";
		this.isDataContainer = isDataContainer;
		this.children = new ArrayList<YangSchemaNode>();
		this.prefixMap = new HashMap<String, String>();
		prefixMap.put("", name);
//		for(ModuleImport moduleImport : moduleImports) {
//			prefixMap.put(moduleImport.getPrefix(), moduleImport.getModuleName());
//		}
		for(ModuleImport moduleImport : moduleImports) {
			prefixMap.put(moduleImport.getPrefix(), moduleImport.getModuleName().getLocalName());
		}
	}
	
	public Map<String, String> getPrefixMap() {
		return prefixMap;
	}
	
	public List<FeatureItem> getFeatureItemList() {
		return featureItemList;
	}


	public void setFeatureItemList(List<FeatureItem> featureItemList) {
		this.featureItemList = featureItemList;
	}
	
	public String getQueryPath() {
		return queryPath;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public ModuleIdentifier getModuleIdentifier() {
		return moduleIdentifier;
	}
	
	public void setModuleIdentifier(ModuleIdentifier moduleIdentifier) {
		this.moduleIdentifier = moduleIdentifier;
	}
	
	public boolean isDataContainer() {
		return isDataContainer;
	}
}
