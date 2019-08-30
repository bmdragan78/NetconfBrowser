package com.yangui.main.services.model;

import com.yangui.main.services.model.SchemaItem.SchemaItemType;
import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangExtensionHeader extends YangDataSchemaNode{
	

	public YangExtensionHeader(ModuleIdentifier moduleId) {
		super("EXTENSIONS", moduleId);
		
		typeEnum = SchemaItemType.HEADER;
		//itemIcon = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.FOLDER);
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_E);
		itemIcon.setFill(Paint.valueOf("#e74856"));
	}
	
}