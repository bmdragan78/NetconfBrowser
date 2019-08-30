package com.yangui.main.services.model;

import org.opendaylight.yangtools.yang.model.api.RpcDefinition;

import com.yangui.main.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangRpcOutput extends YangDataSchemaNode{
	

	public YangRpcOutput(RpcDefinition rpcOD, String xpath, ModuleIdentifier moduleId) {
		super(rpcOD, xpath, moduleId);
		
		typeEnum = SchemaItemType.RPC_OUT;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_O);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}
	
}