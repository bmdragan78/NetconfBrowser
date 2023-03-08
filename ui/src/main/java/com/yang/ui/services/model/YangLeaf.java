package com.yang.ui.services.model;

import java.util.List;

import org.opendaylight.yangtools.yang.model.api.LeafSchemaNode;
import org.opendaylight.yangtools.yang.model.api.TypeDefinition;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.utils.MaterialDesignIconFactory;
import javafx.scene.paint.Paint;



public class YangLeaf extends YangDataSchemaNode{
	
	@Override
	public String toString() {
	   return getLocalName() + (isConfig() ? " _c" : " _s");
	}
	
	private String defaultValue;
	
	private String units;
	
	private String type;
	
	private List<TypeDefinition<? extends TypeDefinition<?>>> typeList;
	

	public YangLeaf(LeafSchemaNode leafOD,  String xpath, ModuleIdentifier moduleId) {
		super(leafOD, xpath, moduleId);
		//defaultValue = leafOD.getDefault();
		defaultValue = leafOD.getType().getDefaultValue().isPresent() ? leafOD.getType().getDefaultValue().get().toString() : "";
		//units = leafOD.getUnits();
		units = leafOD.getType().getUnits().orElse("");
		type = TypeDefinitions.getDefinition(leafOD.getType());
		
		typeList = TypeDefinitions.getTypeList(leafOD.getType());
//		if(typeList.get(0) instanceof BitsTypeDefinition)
//			defaultValue = null;
		
		typeEnum = SchemaItemType.LEAF;
		itemIcon = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.LEAF);	
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}
	

	public String getDefaultValue() {
		return defaultValue;
	}


	public String getUnits() {
		return units;
	}

	@Override
	public List<TypeDefinition<? extends TypeDefinition<?>>> getTypeList() {
		return typeList;
	}
	
	public String getType() {
		return type;
	}
	
}
