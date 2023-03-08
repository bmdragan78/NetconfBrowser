package com.yang.ui.services.model;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangIdentityHeader extends YangDataSchemaNode{
	

	public YangIdentityHeader(ModuleIdentifier moduleId) {
		super("IDENTITIES", moduleId);
		
		typeEnum = SchemaItemType.HEADER;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_I);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}
	
}