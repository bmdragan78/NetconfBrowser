package com.yang.ui.services.model;

import org.opendaylight.yangtools.yang.model.api.TypeDefinition;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangType extends YangDataSchemaNode{
	
	
	private String defaultValue;
	
	private String units;
	
	private String typeHierarchy;
	
	
	public YangType(TypeDefinition typeOD, String xpath, ModuleIdentifier moduleId) {
		super(typeOD, xpath, moduleId);
		
		typeHierarchy = TypeDefinitions.getDefinition(typeOD);
		
		if(typeOD.getDefaultValue() != null)
			defaultValue = typeOD.getDefaultValue().toString();
		units = typeOD.getUnits().isPresent() ? typeOD.getUnits().get().toString() : "";
		
		typeEnum = SchemaItemType.TYPEDEF;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_T);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}


	public String getUnits() {
		return units;
	}


	public String getTypeHierarchy() {
		return typeHierarchy;
	}
}
