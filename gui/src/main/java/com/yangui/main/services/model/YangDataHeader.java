package com.yangui.main.services.model;


import com.yangui.main.services.model.SchemaItem.SchemaItemType;
import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangDataHeader extends YangDataSchemaNode{
	

	public YangDataHeader(ModuleIdentifier moduleId) {
		super("DATA", moduleId);
		
		typeEnum = SchemaItemType.HEADER;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.ENVELOPE);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}
	
}