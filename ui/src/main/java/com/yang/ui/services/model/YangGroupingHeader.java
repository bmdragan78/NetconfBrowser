package com.yang.ui.services.model;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangGroupingHeader extends YangDataSchemaNode{
	

	public YangGroupingHeader(ModuleIdentifier moduleId) {
		super("GROUPINGS", moduleId);
		
		typeEnum = SchemaItemType.HEADER;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_G);
		itemIcon.setFill(Paint.valueOf("#ffb900"));
	}
	
}