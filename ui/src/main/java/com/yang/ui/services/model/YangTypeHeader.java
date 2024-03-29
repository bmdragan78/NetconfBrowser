package com.yang.ui.services.model;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangTypeHeader extends YangDataSchemaNode{
	

	public YangTypeHeader(ModuleIdentifier moduleId) {
		super("TYPES", moduleId);
		
		typeEnum = SchemaItemType.HEADER;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_T);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}
	
}