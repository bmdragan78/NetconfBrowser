package com.yangui.main.services.model;

import com.yangui.main.services.model.SchemaItem.SchemaItemType;
import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangAugmentationHeader extends YangDataSchemaNode{
	

	public YangAugmentationHeader(ModuleIdentifier moduleId) {
		super("AUGMENTATIONS", moduleId);
		
		typeEnum = SchemaItemType.HEADER;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_A);
		itemIcon.setFill(Paint.valueOf("#ffb900"));
	}
	
}