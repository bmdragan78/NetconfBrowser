package com.yangui.main.services.model;

import com.yangui.main.services.xml.LocationData;

import javafx.beans.property.SimpleStringProperty;



public class ErrorItem {
	
	@Override
	public String toString() {
		return "ErrorItem [type=" + type.get() + ", source=" + source.get() + ", idCss=" + idCss.get() + ", message=" + message.get() + "]";
	}

	private final ErrorTypeEnum typeEnum;
	
	private final SimpleStringProperty type;
	
	private final SimpleStringProperty source;//Schema->FileId, Network->DeviceId, Query->QueryName
	
	private final SimpleStringProperty idCss;//javafx node id is present only for Schema & Network
	
	private final SimpleStringProperty lineNumber;//is present only for Schema & Query
	
	private final SimpleStringProperty message;
	
	private final LocationData locationData;
	
	
//	public ErrorItem(ErrorTypeEnum typeEnum, String source, String lineNumber, String message) {//SCHEMA
//		this.typeEnum = typeEnum;
//		this.type = new SimpleStringProperty(typeEnum.text());
//		this.source = new SimpleStringProperty(source);
//		this.locationData = null;
//		this.lineNumber = new SimpleStringProperty(lineNumber);
//		this.idCss = new SimpleStringProperty("");
//		this.message = new SimpleStringProperty(message);
//	}
	
	public ErrorItem(ErrorTypeEnum typeEnum, String source, String lineNumber, String message) {//QUERY
		this.typeEnum = typeEnum;
		this.type = new SimpleStringProperty(typeEnum.text());
		this.source = new SimpleStringProperty(source);
		this.locationData = null;
		this.lineNumber = new SimpleStringProperty(lineNumber);
		this.idCss = new SimpleStringProperty("");
		this.message = new SimpleStringProperty(message);
	}
	
	public ErrorItem(ErrorTypeEnum typeEnum, String source, LocationData locationData, String message) {//QUERY
		this.typeEnum = typeEnum;
		this.type = new SimpleStringProperty(typeEnum.text());
		this.source = new SimpleStringProperty(source);
		this.locationData = locationData;
		this.lineNumber = new SimpleStringProperty(locationData.getStartLine()+"");
		this.idCss = new SimpleStringProperty("");
		this.message = new SimpleStringProperty(message);
	}
	
	
	public ErrorItem(ErrorTypeEnum typeEnum, String source, String lineNumber, String idCss, String message) {//Network
		this.typeEnum = typeEnum;
		this.type = new SimpleStringProperty(typeEnum.text());
		this.source = new SimpleStringProperty(source);
		this.locationData = null;
		this.lineNumber = new SimpleStringProperty(lineNumber);
		this.idCss = new SimpleStringProperty(idCss);
		this.message = new SimpleStringProperty(message);
	}
	

	public LocationData getLocationData() {
		return locationData;
	}
	
	public ErrorTypeEnum getTypeEnum() {
		return typeEnum;
	}
	
	public SimpleStringProperty lineNumberProperty() {
		return lineNumber;
	}
	
	public String getLineNumber() {
		return lineNumber.get();
	}
	
	public void setLineNumber(String lineNumber) {
		this.lineNumber.set(lineNumber);
	}
	
	public SimpleStringProperty messageProperty() {
		return message;
	}
	
	public String getMessage() {
		return message.get();
	}
	
	public void setMessage(String message) {
		this.message.set(message);
	}
	
	public SimpleStringProperty typeProperty() {
		return type;
	}
	
	public String getType() {
		return type.get();
	}
	
	public void setType(String type) {
		this.type.set(type);
	}
	
	public SimpleStringProperty sourceProperty() {
		return source;
	}
	
	public String getSource() {
		return source.get();
	}
	
	public void setSource(String source) {
		this.source.set(source);
	}
	
	
	public SimpleStringProperty idCssProperty() {
		return idCss;
	}
	
	public String getIdCss() {
		return idCss.get();
	}
	
	public void setIdCss(String idCss) {
		this.idCss.set(idCss);
	}

}
