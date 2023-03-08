package com.yang.ui.services.model;

import org.opendaylight.yangtools.yang.model.api.IdentitySchemaNode;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangIdentity extends YangDataSchemaNode{
	
	private String typeHierarchy;

	public YangIdentity(IdentitySchemaNode identityOD, String xpath, ModuleIdentifier moduleId) {
		super(identityOD, xpath, moduleId);
		
		typeHierarchy = TypeDefinitions.getDefinition(identityOD);
		
		typeEnum = SchemaItemType.IDENTITY;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_I);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}
	
	
	public String getTypeHierarchy() {
		return typeHierarchy;
	}
	
}
