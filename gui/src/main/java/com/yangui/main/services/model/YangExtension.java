package com.yangui.main.services.model;

import org.opendaylight.yangtools.yang.model.api.ExtensionDefinition;
import com.yangui.main.services.model.SchemaItem.SchemaItemType;
import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangExtension extends YangDataSchemaNode{
	
	private String argument;
	
	private boolean isYin;
	

	public YangExtension(ExtensionDefinition extensionOD, String xpath, ModuleIdentifier moduleId) {
		super(extensionOD, xpath, moduleId);
		
		argument = extensionOD.getArgument();
		isYin = extensionOD.isYinElement();
		
		typeEnum = SchemaItemType.EXTENSION;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_E);
		itemIcon.setFill(Paint.valueOf("#e74856"));
	}
	
	public String getArgument() {
		return argument;
	}


	public boolean isYin() {
		return isYin;
	}
}
