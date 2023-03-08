package com.yang.ui.services.model;


import org.opendaylight.yangtools.yang.model.api.AnyxmlSchemaNode;
import com.yang.ui.services.model.SchemaItem.SchemaItemType;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.utils.MaterialIconFactory;
import javafx.scene.paint.Paint;



public class YangAny extends YangDataSchemaNode{
	
	@Override
	public String toString() {
	   return getLocalName() + (isConfig() ? " _c" : " _s");
	}
	
	public YangAny(AnyxmlSchemaNode anyOD, String xpath, ModuleIdentifier moduleId){
		super(anyOD, xpath, moduleId);
		
		typeEnum = SchemaItemType.ANY_XML;
		itemIcon = MaterialIconFactory.get().createIcon(MaterialIcon.FORMAT_COLOR_TEXT);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}
}