package com.yang.ui.multiquery.query.getop;

import java.net.URL;
import java.util.ResourceBundle;

import com.yang.ui.multiquery.query.base.BaseopPresenter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;



public class GetopPresenter extends BaseopPresenter implements Initializable {
	
	@FXML
	HBox getopPane;
	
	@FXML
    Button filterBtn;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		getopPane.getStyleClass().add("getopPane");
	}

}
