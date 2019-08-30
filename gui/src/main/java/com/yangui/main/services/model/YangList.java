package com.yangui.main.services.model;

import java.util.Collection;
import java.util.List;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.model.api.ListSchemaNode;
import org.opendaylight.yangtools.yang.model.api.UniqueConstraint;
import org.opendaylight.yangtools.yang.model.api.stmt.SchemaNodeIdentifier.Relative;

import com.yangui.main.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.utils.MaterialDesignIconFactory;
import javafx.scene.paint.Paint;



public class YangList extends YangDataSchemaNode{
	
	@Override
	public String toString() {
	   return getLocalName() + (isConfig() ? " _c" : " _s");
	}
	
	private String key;
	
	private boolean isUserOrdered;
	
	private int elementCount;//used only for validation
	
	private List<QName> keyDef;
	
	private Collection<UniqueConstraint> uniqueDef;
	

	public YangList(ListSchemaNode listOD, String xpath, ModuleIdentifier moduleId){
		super(listOD, xpath, moduleId);
		isUserOrdered = listOD.isUserOrdered();
		keyDef = listOD.getKeyDefinition();
		if(keyDef != null)
			key = getKey(keyDef);
		
		uniqueDef = listOD.getUniqueConstraints();
		for(UniqueConstraint unique : uniqueDef) {
			Collection<Relative> relList = unique.getTag();
			relList.size();
		}
		
		typeEnum = SchemaItemType.LIST;
		itemIcon = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.VIEW_GRID);
		itemIcon.setFill(Paint.valueOf("#00b7c3"));
	}
	
	
	private String getKey(List<QName> keyDefs) {
		try {
			StringBuilder keys = new StringBuilder("");
			for (QName keyDef : keyDefs)
				keys.append(keyDef.getLocalName() + ",");
			keys.replace(keys.length() - 1, keys.length(), "");// remove last comma
			return  keys.toString();
		}catch(Exception ex) {
			return "";
		}
	}
	
	public String getKey() {
		return key;
	}

	public boolean isUserOrdered() {
		return isUserOrdered;
	}
	
	public int getElementCount() {
		return elementCount;
	}

	public void setElementCount(int elementCount) {
		this.elementCount = elementCount;
	}

	public List<QName> getKeyDef() {
		return keyDef;
	}

	public Collection<UniqueConstraint> getUniqueDef() {
		return uniqueDef;
	}
}
