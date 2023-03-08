package com.yang.ui.services.model;

import org.opendaylight.yangtools.yang.model.api.ContainerSchemaNode;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.utils.MaterialDesignIconFactory;
import javafx.scene.paint.Paint;
import org.opendaylight.yangtools.yang.model.api.InputSchemaNode;
import org.opendaylight.yangtools.yang.model.api.OutputSchemaNode;


public class YangContainer extends YangDataSchemaNode{
	
	@Override
	public String toString() {
	   return getLocalName() + (isConfig() ? " _c" : " _s");
	}
	
	boolean isPresence;
	
	public YangContainer(ContainerSchemaNode containerOD, String xpath, ModuleIdentifier moduleId) {
		super(containerOD, xpath, moduleId);
		isPresence = containerOD.isPresenceContainer();

		typeEnum = SchemaItemType.CONTAINER;
		itemIcon = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.SQUARE_INC);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}

	public YangContainer(InputSchemaNode containerOD, String xpath, ModuleIdentifier moduleId) {
		super(containerOD, xpath, moduleId);
		//isPresence = containerOD.isPresenceContainer();

		typeEnum = SchemaItemType.CONTAINER;
		itemIcon = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.SQUARE_INC);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}

	public YangContainer(OutputSchemaNode containerOD, String xpath, ModuleIdentifier moduleId) {
		super(containerOD, xpath, moduleId);
		//isPresence = containerOD.isPresenceContainer();

		typeEnum = SchemaItemType.CONTAINER;
		itemIcon = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.SQUARE_INC);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}

	public boolean isPresence() {
		return isPresence;
	}
}
