package com.yangui.main.services.model;

import java.util.ArrayList;
import java.util.List;
import org.opendaylight.yangtools.yang.model.api.AugmentationSchema;
import org.opendaylight.yangtools.yang.model.api.DataSchemaNode;
import org.opendaylight.yangtools.yang.model.api.DeviateDefinition;
import org.opendaylight.yangtools.yang.model.api.Deviation;
import org.opendaylight.yangtools.yang.model.api.ExtensionDefinition;
import org.opendaylight.yangtools.yang.model.api.FeatureDefinition;
import org.opendaylight.yangtools.yang.model.api.GroupingDefinition;
import org.opendaylight.yangtools.yang.model.api.IdentitySchemaNode;
import org.opendaylight.yangtools.yang.model.api.NotificationDefinition;
import org.opendaylight.yangtools.yang.model.api.RpcDefinition;
import org.opendaylight.yangtools.yang.model.api.TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.UsesNode;

import com.yangui.main.services.model.SchemaItem.SchemaItemType;

import javafx.scene.text.Text;



public class YangSchemaNode implements Comparable<YangSchemaNode> {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((xpath == null) ? 0 : xpath.hashCode());
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
		YangSchemaNode other = (YangSchemaNode) obj;
		if (xpath == null) {
			if (other.xpath != null)
				return false;
		} else if (!xpath.equals(other.xpath))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return getLocalName();
	}
	
	@Override
	public int compareTo(YangSchemaNode o) {
		int result = xpath.compareTo(o.xpath);
		return result;
	}

	public boolean isRootNode() {
		String localPath = getLocalPath();
		String[] pathSegments = localPath.split("\\/");
		return pathSegments.length == 2;
	}
	
	
	public String getLocalPath() {
		return "/" + (xpath.substring(xpath.indexOf(":") + 1));
	}
	
//	public String getDefaultValue() {//remove it !!!
//		return "someValue";
//	}
	
	protected Text itemIcon;
	
	//DocumentedNode
	private String description;
	
	private String status;
	
	private String reference;
	
	//SchemaNode
	protected String localName;
	
	private String xpath;//like "/moduleName:a/b/c" ---> "/earth:c1_when/vpn-id"
	
	//use this field for equals and comparing
	private String qpath;//like "/(namespace?revision=date)a/(namespace?revision=date)b/(namespace?revision=date)c"  ---> "/(urn:ietf:params:xml:ns:yang:earth?revision=2018-02-10)c1_when/(urn:ietf:params:xml:ns:yang:earth?revision=2018-02-10)vpn-id"
	
	//all node can have children
	private List<YangSchemaNode> children = new ArrayList<YangSchemaNode>();
	
	protected SchemaItemType typeEnum;//ALWAYS initialized by children classes
	
	protected ModuleIdentifier moduleIdentifier;// name, namespace, revision, prefix
	
	
	public void add(YangSchemaNode yangNode) {
		children.add(yangNode);
	}
	
	
	public List<YangSchemaNode> getChildren() {
		return children;
	}
	
	
	public YangSchemaNode(String localName, ModuleIdentifier moduleIdentifier) {//HEADER
		super();
		this.localName = localName;
		this.moduleIdentifier = moduleIdentifier;
	}
	
	
	public YangSchemaNode(AugmentationSchema augOD, ModuleIdentifier moduleIdentifier) {
		super();
		if(augOD.getStatus() != null)
			status = augOD.getStatus().name().toString();
		description = augOD.getDescription();
		reference = augOD.getReference();
		this.moduleIdentifier = moduleIdentifier;
	}
	
	public YangSchemaNode(UsesNode usesOD, ModuleIdentifier moduleIdentifier) {
		super();
		if(usesOD.getStatus() != null)
			status = usesOD.getStatus().name().toString();
		description = usesOD.getDescription();
		reference = usesOD.getReference();
		this.moduleIdentifier = moduleIdentifier;
	}
	
	public YangSchemaNode(Deviation devationOD, ModuleIdentifier moduleIdentifier) {
		super();
		description = devationOD.getDescription();
		reference = devationOD.getReference();
		this.moduleIdentifier = moduleIdentifier;
	}
	
	public YangSchemaNode(DeviateDefinition deviateOD, ModuleIdentifier moduleIdentifier) {
		super();
		this.moduleIdentifier = moduleIdentifier;
	}
	
	public YangSchemaNode(RpcDefinition rpcOD, String xpath, ModuleIdentifier moduleIdentifier) {
		super();
		if(rpcOD.getStatus() != null)
			status = rpcOD.getStatus().name().toString();
		description = rpcOD.getDescription();
		reference = rpcOD.getReference();
		localName = rpcOD.getQName().getLocalName();
		this.xpath = xpath;
		this.moduleIdentifier = moduleIdentifier;
	}	
	
	public YangSchemaNode(NotificationDefinition notifOD, String xpath, ModuleIdentifier moduleIdentifier) {
		super();
		if(notifOD.getStatus() != null)
			status = notifOD.getStatus().name().toString();
		description = notifOD.getDescription();
		reference = notifOD.getReference();
		localName = notifOD.getQName().getLocalName();
		this.xpath = xpath;
		this.moduleIdentifier = moduleIdentifier;
	}	
	
	public YangSchemaNode(IdentitySchemaNode identityOD, String xpath, ModuleIdentifier moduleIdentifier) {
		super();
		if(identityOD.getStatus() != null)
			status = identityOD.getStatus().name().toString();
		description = identityOD.getDescription();
		reference = identityOD.getReference();
		localName = identityOD.getQName().getLocalName();
		this.xpath = xpath;
		this.moduleIdentifier = moduleIdentifier;
	}
	
	public YangSchemaNode(FeatureDefinition featureOD, String xpath, ModuleIdentifier moduleIdentifier) {
		super();
		if(featureOD.getStatus() != null)
			status = featureOD.getStatus().name().toString();
		description = featureOD.getDescription();
		reference = featureOD.getReference();
		localName = featureOD.getQName().getLocalName();
		this.xpath = xpath;
		this.moduleIdentifier = moduleIdentifier;
	}
	
	public YangSchemaNode(ExtensionDefinition extensionOD, String xpath, ModuleIdentifier moduleIdentifier) {
		super();
		if(extensionOD.getStatus() != null)
			status = extensionOD.getStatus().name().toString();
		description = extensionOD.getDescription();
		reference = extensionOD.getReference();
		localName = extensionOD.getQName().getLocalName();
		this.xpath = xpath;
		this.moduleIdentifier = moduleIdentifier;
	}
	
	public YangSchemaNode(GroupingDefinition groupOD, String xpath, ModuleIdentifier moduleIdentifier) {
		super();
		if(groupOD.getStatus() != null)
			status = groupOD.getStatus().name().toString();
		description = groupOD.getDescription();
		reference = groupOD.getReference();
		localName = groupOD.getQName().getLocalName();
		this.xpath = xpath;
		this.moduleIdentifier = moduleIdentifier;
	}
	
	public YangSchemaNode(TypeDefinition typeOD, String xpath, ModuleIdentifier moduleIdentifier) {
		super();
		if(typeOD.getStatus() != null)
			status = typeOD.getStatus().name().toString();
		description = typeOD.getDescription();
		reference = typeOD.getReference();
		localName = typeOD.getQName().getLocalName();
		this.xpath = xpath;
		this.moduleIdentifier = moduleIdentifier;
	}
	
	public YangSchemaNode(DataSchemaNode dataSchemaNode, String xpath, ModuleIdentifier moduleIdentifier) {
		super();
		if(dataSchemaNode.getStatus() != null)
			status = dataSchemaNode.getStatus().name().toString();
		description = dataSchemaNode.getDescription();
		reference = dataSchemaNode.getReference();
		localName = dataSchemaNode.getQName().getLocalName();
		this.xpath = xpath;
		this.moduleIdentifier = moduleIdentifier;
	}
	
	public ModuleIdentifier getModuleIdentifier() {
		return moduleIdentifier;
	}
	
	public SchemaItemType getTypeEnum() {
		return typeEnum;
	}
	
	public Text getItemIcon() {
		return itemIcon;
	}

	public String getDescription() {
		return description;
	}


	public String getStatus() {
		return status;
	}


	public String getReference() {
		return reference;
	}


	public String getLocalName() {
		return localName;
	}


	public String getXpath() {
		return xpath;
	}
	
	public List<TypeDefinition<? extends TypeDefinition<?>>> getTypeList() {
		return null;
	}

}
