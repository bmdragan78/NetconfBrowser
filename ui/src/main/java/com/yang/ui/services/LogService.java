package com.yang.ui.services;

import java.util.Iterator;
import java.util.List;
import javax.inject.Singleton;

import com.yang.ui.services.model.ErrorItem;
import com.yang.ui.services.model.ErrorTypeEnum;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



@Singleton
public class LogService {
	
	private ObservableList<ErrorItem> errorList = FXCollections.observableArrayList();//total list of devices
	
	
	public ObservableList<ErrorItem> errorObsList() {
		return errorList;
	}
	
	
	public void updateSchemaErrors(List<ErrorItem> schemaErrorList) {//add file Name NOT NEEDED because replaceAll behaviour for schema reload
		clearSchemaErrors();
		errorList.addAll(schemaErrorList);
	}
	
	
	public void clearSchemaErrors() {
		Iterator<ErrorItem> itr = errorList.iterator();
		while (itr.hasNext()){
			ErrorItem t = itr.next();
			if ( t.getTypeEnum().equals(ErrorTypeEnum.SCHEMA)) {
				itr.remove();
			}
		}
	}
	
	
	public void updateNetworkErrors(List<ErrorItem> networkErrorList, String deviceId) {
		clearNetworkErrors(deviceId);
		errorList.addAll(networkErrorList);
	}
	
	public void updateAllNetworkErrors(List<ErrorItem> networkErrorList) {
		clearAllNetworkErrors();
		errorList.addAll(networkErrorList);
	}
	
	
	public void clearNetworkErrors(String deviceId) {
		Iterator<ErrorItem> itr = errorList.iterator();
		while (itr.hasNext()){
			ErrorItem t = itr.next();
			if ( t.getTypeEnum().equals(ErrorTypeEnum.NETWORK) && t.getSource().equals(deviceId)) {
				itr.remove();
			}
		}
	}
	
	public void clearAllNetworkErrors() {
		Iterator<ErrorItem> itr = errorList.iterator();
		while (itr.hasNext()){
			ErrorItem t = itr.next();
			if ( t.getTypeEnum().equals(ErrorTypeEnum.NETWORK)) {
				itr.remove();
			}
		}
	}
	
	
	public void updateQueryErrors(List<ErrorItem> queryErrorList, String queryName) {
		clearQueryErrors(queryName);
		errorList.addAll(queryErrorList);
	}
	
	
	public void clearQueryErrors(String queryName) {//add queryName
		Iterator<ErrorItem> itr = errorList.iterator();
		while (itr.hasNext()){
			ErrorItem t = itr.next();
			if ( t.getTypeEnum().equals(ErrorTypeEnum.QUERY) && t.getSource().equals(queryName)) {
				itr.remove();
			}
		}
	}
}

