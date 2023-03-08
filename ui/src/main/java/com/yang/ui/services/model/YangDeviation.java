package com.yang.ui.services.model;

import org.opendaylight.yangtools.yang.model.api.Deviation;

import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import javafx.scene.paint.Paint;
import de.jensd.fx.glyphs.emojione.EmojiOne;
import de.jensd.fx.glyphs.emojione.utils.EmojiOneFactory;



public class YangDeviation extends YangDataSchemaNode{
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((moduleIdentifier == null) ? 0 : moduleIdentifier.hashCode());
		result = prime * result + ((targetPath == null) ? 0 : targetPath.hashCode());
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
		YangDeviation other = (YangDeviation) obj;
		if (moduleIdentifier == null) {
			if (other.moduleIdentifier != null)
				return false;
		} else if (!moduleIdentifier.equals(other.moduleIdentifier))
			return false;
		if (targetPath == null) {
			if (other.targetPath != null)
				return false;
		} else if (!targetPath.equals(other.targetPath))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return targetPath;
	}
	
	@Override
	public int compareTo(YangSchemaNode o) {
		int result = targetPath.compareTo(((YangDeviation)o).getTargetPath());
		if(result == 0)
			result = moduleIdentifier.compareTo(((YangDeviation)o).getModuleIdentifier());
		return result;
	}
	
	@Override
	public String getLocalName() {//used only for regex exp
		return targetPathPrefix;
	}
	
	private String targetPath;
	
	private String targetPathPrefix;
	
	//private ModuleIdentifier moduleId;
	

	public YangDeviation(Deviation devationOD, String targetPath, String targetPathPrefix, ModuleIdentifier moduleId) {
		super(devationOD, moduleId);
		
		this.targetPath = targetPath;
		this.targetPathPrefix = targetPathPrefix;
		
		typeEnum = SchemaItemType.DEVIATION;
		itemIcon = EmojiOneFactory.get().createIcon(EmojiOne.REGIONAL_INDICATOR_D);
		itemIcon.setFill(Paint.valueOf("#e74856"));
	}
	
	
	public String getTargetPathPrefix() {
		return targetPathPrefix;
	}
	
	public String getTargetPath() {
		return targetPath;
	}
	
}
