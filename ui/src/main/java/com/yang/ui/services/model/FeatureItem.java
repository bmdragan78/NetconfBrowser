package com.yang.ui.services.model;

import org.opendaylight.yangtools.yang.common.QName;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;



public class FeatureItem {
	
	private QName feature;
	
	private BooleanProperty isEnabledProperty;
	

	public FeatureItem(QName feature, boolean isEnabled) {
		super();
		this.feature = feature;
		this.isEnabledProperty = new SimpleBooleanProperty(isEnabled);
	}
	

	public BooleanProperty isEnabledProperty() {
		return isEnabledProperty;
	}
	
	public boolean isEnabled() {
		return isEnabledProperty.get();
	}

	public void setEnabled(boolean isEnabled) {
		isEnabledProperty.set(isEnabled);
	}

	public QName getFeature() {
		return feature;
	}

}
