package com.yangui.main.services.model;

import org.opendaylight.yangtools.yang.model.api.UsesNode;
import com.yangui.main.services.model.SchemaItem.SchemaItemType;
import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;
import javafx.scene.paint.Paint;



public class YangUses extends YangDataSchemaNode{
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((moduleIdentifier == null) ? 0 : moduleIdentifier.hashCode());
		result = prime * result + ((groupingPath == null) ? 0 : groupingPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		YangUses other = (YangUses) obj;
		if (moduleIdentifier == null) {
			if (other.moduleIdentifier != null)
				return false;
		} else if (!moduleIdentifier.equals(other.moduleIdentifier))
			return false;
		if (groupingPath == null) {
			if (other.groupingPath != null)
				return false;
		} else if (!groupingPath.equals(other.groupingPath))
			return false;
		return true;
	}

	@Override
	public int compareTo(YangSchemaNode o) {
		int result = groupingPath.compareTo(((YangUses)o).getGroupingPath());
		if(result == 0)
			result = moduleIdentifier.compareTo(((YangUses)o).getModuleIdentifier());
		return result;
	}
	
	@Override
	public String toString() {
		return groupingPath;
	}
	
	@Override
	public String getLocalName() {//used only for regex exp
		return groupingPathPrefix;
	}
	
	private String groupingPath;
	
	private String groupingPathPrefix;
	
	//@Nonnull Map<SchemaPath, SchemaNode> getRefines(); ADD ???
	//read children augmentations !!!!
	
	public YangUses(UsesNode usesOD, String groupingPath, String groupingPathPrefix, ModuleIdentifier moduleId) {
		super(usesOD, moduleId);
		this.groupingPath = groupingPath;
		this.groupingPathPrefix = groupingPathPrefix;
		
		typeEnum = SchemaItemType.USES;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_U);
		itemIcon.setFill(Paint.valueOf("#ffb900"));
	}
	
	
	public String getGroupingPath() {
		return groupingPath;
	}
	
	public String getGroupingPathPrefix() {
		return groupingPathPrefix;
	}

}
