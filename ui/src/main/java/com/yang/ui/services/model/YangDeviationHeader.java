package com.yang.ui.services.model;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangDeviationHeader extends YangDataSchemaNode{
	

	public YangDeviationHeader(ModuleIdentifier moduleId) {
		super("DEVIATIONS", moduleId);
		
		typeEnum = SchemaItemType.HEADER;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_D);
		itemIcon.setFill(Paint.valueOf("#e74856"));
	}
	
}