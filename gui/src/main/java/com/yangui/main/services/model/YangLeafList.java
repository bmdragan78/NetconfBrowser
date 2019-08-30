package com.yangui.main.services.model;

import java.util.List;

import org.opendaylight.yangtools.yang.model.api.LeafListSchemaNode;
import org.opendaylight.yangtools.yang.model.api.TypeDefinition;

import com.yangui.main.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.utils.MaterialDesignIconFactory;
import javafx.scene.paint.Paint;



public class YangLeafList extends YangDataSchemaNode{
	
	@Override
	public String toString() {
	   return getLocalName() + (isConfig() ? " _c" : " _s");
	}
	
	private String type;
	
	private boolean isUserOrdered;
	
	private List<TypeDefinition<? extends TypeDefinition<?>>> typeList;
	
	
	public YangLeafList(LeafListSchemaNode leafListOD, String xpath, ModuleIdentifier moduleId){
		super(leafListOD, xpath, moduleId);
		type = TypeDefinitions.getDefinition(leafListOD.getType());
		isUserOrdered = leafListOD.isUserOrdered();
		typeList = TypeDefinitions.getTypeList(leafListOD.getType());
		
		typeEnum = SchemaItemType.LEAF_LIST;
		itemIcon =  MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.VIEW_AGENDA);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}
	
	@Override
	public List<TypeDefinition<? extends TypeDefinition<?>>> getTypeList() {
		return typeList;
	}
	
	public String getType() {
		return type;
	}


	public boolean isUserOrdered() {
		return isUserOrdered;
	}
	
}
