package com.yang.ui.services.model;

import org.opendaylight.yangtools.yang.model.api.CaseSchemaNode;
import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;


public class YangChoiceCase extends YangDataSchemaNode{
	
	@Override
	public String toString() {
	   return getLocalName() + (isConfig() ? " _c" : " _s");
	}
	
	public YangChoiceCase(CaseSchemaNode choiceOD, String xpath, ModuleIdentifier moduleId) {
		super(choiceOD, xpath, moduleId);
		
		typeEnum = SchemaItemType.CHOICE_CASE;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.ARROW_RIGHT_HOOK);	
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}
}