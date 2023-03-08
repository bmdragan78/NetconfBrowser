package com.yang.ui.services.model;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangFeatureHeader extends YangDataSchemaNode{
	

	public YangFeatureHeader(ModuleIdentifier moduleId) {
		super("FEATURES", moduleId);
		
		typeEnum = SchemaItemType.HEADER;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_F);	
		itemIcon.setFill(Paint.valueOf("#e74856"));
	}
	
}