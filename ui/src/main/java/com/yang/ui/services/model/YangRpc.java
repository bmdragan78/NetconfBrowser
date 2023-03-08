package com.yang.ui.services.model;

import org.opendaylight.yangtools.yang.model.api.RpcDefinition;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangRpc extends YangDataSchemaNode{
	
	
	public YangRpc(RpcDefinition rpcOD, String xpath, ModuleIdentifier moduleId) {
		super(rpcOD, xpath, moduleId);
		
		typeEnum = SchemaItemType.RPC;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_R);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}

}