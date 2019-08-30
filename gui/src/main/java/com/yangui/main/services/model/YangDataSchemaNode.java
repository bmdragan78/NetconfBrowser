package com.yangui.main.services.model;

import java.util.Set;

import org.opendaylight.yangtools.yang.data.util.EmptyConstraintDefinition;
import org.opendaylight.yangtools.yang.model.api.AugmentationSchema;
import org.opendaylight.yangtools.yang.model.api.ConstraintDefinition;
import org.opendaylight.yangtools.yang.model.api.DataSchemaNode;
import org.opendaylight.yangtools.yang.model.api.DeviateDefinition;
import org.opendaylight.yangtools.yang.model.api.Deviation;
import org.opendaylight.yangtools.yang.model.api.ExtensionDefinition;
import org.opendaylight.yangtools.yang.model.api.FeatureDefinition;
import org.opendaylight.yangtools.yang.model.api.GroupingDefinition;
import org.opendaylight.yangtools.yang.model.api.IdentitySchemaNode;
import org.opendaylight.yangtools.yang.model.api.MustDefinition;
import org.opendaylight.yangtools.yang.model.api.NotificationDefinition;
import org.opendaylight.yangtools.yang.model.api.RpcDefinition;
import org.opendaylight.yangtools.yang.model.api.TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.UsesNode;



public class YangDataSchemaNode extends YangSchemaNode{

	//DataSchemaNode
	private boolean isAugmenting;
	
	private boolean isAddedByUses;
	
	private boolean isConfig;
	
	//ConstraintDefinition
	private int maxElements;
	
	private int minElements;
	
	private String when;
	
	private boolean isMandatory;
	
	private Set<MustDefinition> mustSet;

	private String must;
	
	
	
	public YangDataSchemaNode(String localName, ModuleIdentifier moduleId) {//Virtual Nodes Headers; because headers are added only on module the localName is unique
		super(localName, moduleId);
	}
	
	public YangDataSchemaNode(RpcDefinition rpcOD, String xpath, ModuleIdentifier moduleId) {
		super(rpcOD, xpath, moduleId);
	}
		
		
	public YangDataSchemaNode(NotificationDefinition notifOD, String xpath, ModuleIdentifier moduleId) {
		super(notifOD, xpath, moduleId);
		
		ConstraintDefinition constraintOd = notifOD.getConstraints();
		if(constraintOd != null && !(constraintOd instanceof EmptyConstraintDefinition)) {
			if(constraintOd.getMaxElements() != null) 
				maxElements = constraintOd.getMaxElements();
			if(constraintOd.getMinElements() != null)
				minElements = constraintOd.getMinElements();
			if(constraintOd.getWhenCondition() != null)
				when = constraintOd.getWhenCondition().toString();
			if(constraintOd.getMustConstraints() != null) {
				mustSet = constraintOd.getMustConstraints();
				for(MustDefinition mustDef: mustSet) {
					must += mustDef.getXpath().toString() + System.lineSeparator();
				}
			}
			isMandatory = constraintOd.isMandatory();
		}
	}
		
		
	public YangDataSchemaNode(IdentitySchemaNode identityOD, String xpath, ModuleIdentifier moduleId) {
		super(identityOD, xpath, moduleId);
	}
		
	public YangDataSchemaNode(FeatureDefinition featureOD, String xpath, ModuleIdentifier moduleId) {
		super(featureOD, xpath, moduleId);
	}
		
		
	public YangDataSchemaNode(ExtensionDefinition extensionOD, String xpath, ModuleIdentifier moduleId) {
		super(extensionOD, xpath, moduleId);
	}
	
	public YangDataSchemaNode(AugmentationSchema augOD, ModuleIdentifier moduleId) {
		super(augOD, moduleId);
		if(augOD.getWhenCondition() != null)
			when = augOD.getWhenCondition().toString();
	}
		
	public YangDataSchemaNode(UsesNode usesOD, ModuleIdentifier moduleId) {
		super(usesOD, moduleId);
		isAugmenting = usesOD.isAugmenting();
		isAddedByUses = usesOD.isAddedByUses();
		if(usesOD.getWhenCondition() != null)
			when = usesOD.getWhenCondition().toString();
	}
	
	public YangDataSchemaNode(GroupingDefinition groupOD, String xpath, ModuleIdentifier moduleId) {
		super(groupOD, xpath, moduleId);
	}
	
	
	public YangDataSchemaNode(TypeDefinition typeOD, String xpath, ModuleIdentifier moduleId) {
		super(typeOD, xpath, moduleId);
	}
	
	
	public YangDataSchemaNode(DeviateDefinition deviateOD, ModuleIdentifier moduleId) { 
		super(deviateOD, moduleId);
		if(deviateOD.getDeviatedMaxElements() != null)
			maxElements = deviateOD.getDeviatedMaxElements();
		if(deviateOD.getDeviatedMinElements() != null)
			minElements = deviateOD.getDeviatedMinElements();
		if(deviateOD.getDeviatedConfig() != null)
			isConfig = deviateOD.getDeviatedConfig();
		if(deviateOD.getDeviatedMandatory() != null)
			isMandatory = deviateOD.getDeviatedMandatory();
	}
	
	public YangDataSchemaNode(Deviation deviationOD, ModuleIdentifier moduleId) {
		super(deviationOD, moduleId);
	}
	
	
	public YangDataSchemaNode(DataSchemaNode dataSchemaNode, String xpath, ModuleIdentifier moduleId) {
		super(dataSchemaNode, xpath, moduleId);
		isAugmenting = dataSchemaNode.isAugmenting();
		isAddedByUses = dataSchemaNode.isAddedByUses();
		isConfig = dataSchemaNode.isConfiguration();
		
		ConstraintDefinition constraintOd = dataSchemaNode.getConstraints();
		if(constraintOd != null && !(constraintOd instanceof EmptyConstraintDefinition)) {
			if(constraintOd.getMaxElements() != null) 
				maxElements = constraintOd.getMaxElements();
			if(constraintOd.getMinElements() != null)
				minElements = constraintOd.getMinElements();
			if(constraintOd.getWhenCondition() != null)
				when = constraintOd.getWhenCondition().toString();
			if(constraintOd.getMustConstraints() != null) {
				mustSet = constraintOd.getMustConstraints();
				for(MustDefinition mustDef: mustSet) {
					must += mustDef.getXpath().toString() + System.lineSeparator();
				}
			}
			isMandatory = constraintOd.isMandatory();
		}
	}
	

	public Set<MustDefinition> getMustSet() {
		return mustSet;
	}

	public String getMust() {
		return must;
	}
	
	public boolean isAugmenting() {
		return isAugmenting;
	}


	public boolean isAddedByUses() {
		return isAddedByUses;
	}


	public boolean isConfig() {
		return isConfig;
	}


	public int getMaxElements() {
		return maxElements;
	}


	public int getMinElements() {
		return minElements;
	}


	public String getWhen() {
		return when;
	}


	public boolean isMandatory() {
		return isMandatory;
	}
	
	
}
