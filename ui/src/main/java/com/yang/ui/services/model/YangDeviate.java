package com.yang.ui.services.model;

import org.opendaylight.yangtools.yang.model.api.DeviateDefinition;

import com.yang.ui.services.SchemaService;
import com.yang.ui.services.model.SchemaItem.SchemaItemType;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.utils.MaterialDesignIconFactory;
import javafx.scene.paint.Paint;



public class YangDeviate extends YangDataSchemaNode{//equals ????
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((moduleIdentifier == null) ? 0 : moduleIdentifier.hashCode());
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
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
		YangDeviate other = (YangDeviate) obj;
		if (moduleIdentifier == null) {
			if (other.moduleIdentifier != null)
				return false;
		} else if (!moduleIdentifier.equals(other.moduleIdentifier))
			return false;
		if (kind == null) {
			if (other.kind != null)
				return false;
		} else if (!kind.equals(other.kind))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(YangSchemaNode o) {
		int result = kind.compareTo(((YangDeviate)o).getKind());
		if(result == 0)
			result = moduleIdentifier.compareTo(((YangDeviate)o).getModuleIdentifier());
		return result;
	}
	
	@Override
	public String toString() {
	   return kind;
	}
	
	@Override
	public String getLocalName() {
		return kind;
	}
	
	private String kind;
	
	private String defaultValue;
	
	private String units;
	
	private String type;
	
	private String must;

	private String uniques;
	
	
	public YangDeviate(DeviateDefinition deviateOD, ModuleIdentifier moduleId) {
		super(deviateOD, moduleId);

		kind = deviateOD.getDeviateType().getKeyword();// NOT_SUPPORTED("not-supported"), ADD("add"), REPLACE("replace"), DELETE("delete");
		defaultValue = deviateOD.getDeviatedDefault();
		units = deviateOD.getDeviatedUnits();
		if(deviateOD.getDeviatedType() != null)
			type = deviateOD.getDeviatedType().toString();
//		must = SchemaService.getMust(deviateOD.getDeviatedMusts());
//		uniques = SchemaService.getUniques(deviateOD.getDeviatedUniques());
		must = SchemaService.getMust(deviateOD.getDeviatedMusts());
		uniques = SchemaService.getUniques(deviateOD.getDeviatedUniques());

		typeEnum = SchemaItemType.DEVIATE;
		itemIcon = MaterialDesignIconFactory.get().createIcon(MaterialDesignIcon.DISQUS_OUTLINE);
		itemIcon.setFill(Paint.valueOf("#e74856"));
	}
	
	
	public String getKind() {
		return kind;
	}


	public String getDefaultValue() {
		return defaultValue;
	}


	public String getUnits() {
		return units;
	}


	public String getType() {
		return type;
	}


	public String getMust() {
		return must;
	}


	public String getUniques() {
		return uniques;
	}
	
	
}