package com.yangui.main.multiquery.query.notop;

import java.net.URL;
import java.util.ResourceBundle;

import com.yangui.main.multiquery.query.base.BaseopPresenter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;



public class NotopPresenter extends BaseopPresenter implements Initializable {
	
	@FXML
	HBox getopPane;
	
	@FXML
    Button notifBtn;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		getopPane.getStyleClass().add("getopPane");
		
	}

}
