package com.yang.ui.services.model;

import org.opendaylight.yangtools.yang.model.api.NotificationDefinition;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangNotification extends YangDataSchemaNode{
	

	public YangNotification(NotificationDefinition notifOD, String xpath, ModuleIdentifier moduleId) {
		super(notifOD, xpath, moduleId);
		
		typeEnum = SchemaItemType.NOTIFICATION;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_N);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}
	
}
