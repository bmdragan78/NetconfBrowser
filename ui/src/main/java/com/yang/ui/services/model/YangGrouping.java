package com.yang.ui.services.model;

import org.opendaylight.yangtools.yang.model.api.GroupingDefinition;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;


public class YangGrouping extends YangDataSchemaNode{
	
	@Override
	public String toString() {
	   return getLocalName();
	}
	
	private boolean isAddedByUses;
	
	
	public YangGrouping(GroupingDefinition groupOD, String xpath, ModuleIdentifier moduleId) {
		super(groupOD, xpath, moduleId);
		isAddedByUses = groupOD.isAddedByUses();
		
		typeEnum = SchemaItemType.GROUPING;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_G);
		itemIcon.setFill(Paint.valueOf("#ffb900"));
	}
	
	
	public boolean isAddedByUses() {
		return isAddedByUses;
	}
}