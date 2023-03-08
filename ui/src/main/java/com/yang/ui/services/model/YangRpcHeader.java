package com.yang.ui.services.model;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangRpcHeader extends YangDataSchemaNode{
	

	public YangRpcHeader(ModuleIdentifier moduleId) {
		super("RPC", moduleId);
		
		typeEnum = SchemaItemType.HEADER;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_R);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}
	
}