package com.yang.ui.codearea.base;

import com.jfoenix.controls.JFXComboBox;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;


public class FXComboBox<T> extends JFXComboBox<T>{
	
	 public FXComboBox() {
		 super();
		 
		 //show selected item on click
		 this.setOnShowing(event -> {
			 ListView<?> listView = getListView();
	         if (listView != null) {
	        	listView.scrollTo(getSelectionModel().getSelectedIndex());
	         }
	     });
		 
		 //navigate to item on key press
		 this.setOnKeyPressed(e -> {
			 String key = e.getText();
			 System.out.println("TEST " + key);
			 
			 ListView<?> listView = getListView();
			 ObservableList<T> items = FXComboBox.this.getItems();
			 for(int i=0; i< items.size(); i++) {
				T item = items.get(i);

				String val = "";
				if(item instanceof Integer) {
					val = Integer.toString((Integer)item);
				}else  if(item instanceof String) {
					val = (String)item;
				}
				if(val.toLowerCase().startsWith(key.toLowerCase())) {
					if (listView != null) 
						listView.scrollTo(i);	 
					break;
				}
			 }
		});
	 }
	 
	 private ListView<?> getListView() {
		 ComboBoxListViewSkin<?> skin = (ComboBoxListViewSkin<?>)getSkin();
         if (skin != null) {
        	 return ((ListView<?>) skin.getPopupContent());
         }else
        	 return null;
	 }
}

