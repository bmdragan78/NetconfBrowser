package com.yang.ui.services.model;

import org.opendaylight.yangtools.yang.model.api.ChoiceSchemaNode;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangChoice extends YangDataSchemaNode{
	
	@Override
	public String toString() {
	   return getLocalName() + (isConfig() ? " _c" : " _s");
	}
	
	private String defaultCase;
	
	public YangChoice(ChoiceSchemaNode choiceOD, String xpath, ModuleIdentifier moduleId) {
		super(choiceOD, xpath, moduleId);
		defaultCase = choiceOD.getDefaultCase().isPresent() ? choiceOD.getDefaultCase().get().toString() : "";
		
		typeEnum = SchemaItemType.CHOICE;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.LEFT_RIGHT_ARROW);	
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}
	
	public String getDefaultCase() {
		return defaultCase;
	}

}