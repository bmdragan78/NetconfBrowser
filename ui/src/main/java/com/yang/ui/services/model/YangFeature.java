package com.yang.ui.services.model;

import org.opendaylight.yangtools.yang.model.api.FeatureDefinition;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangFeature extends YangDataSchemaNode{
	
	@Override
	public String toString() {
		return getLocalName() + (isActive ? "" : " (inactive)");
	}
	
	private boolean isActive;
	

	public YangFeature(FeatureDefinition featureOD, String xpath, ModuleIdentifier moduleId, boolean isActive) {
		super(featureOD, xpath, moduleId);
		
		this.isActive = isActive;
		
		typeEnum = SchemaItemType.FEATURE;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_F);	
		itemIcon.setFill(Paint.valueOf("#e74856"));
	}
	
}
