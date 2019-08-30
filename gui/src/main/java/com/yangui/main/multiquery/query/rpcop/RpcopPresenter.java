package com.yangui.main.multiquery.query.rpcop;

import java.net.URL;
import java.util.ResourceBundle;

import com.yangui.main.multiquery.query.base.BaseopPresenter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;



public class RpcopPresenter extends BaseopPresenter implements Initializable {
	
	@FXML
	HBox getopPane;
	
	@FXML
    Button rpcBtn;
	
	@FXML
    Button filterBtn;
	
	@FXML
    Button configBtn;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		getopPane.getStyleClass().add("getopPane");
		
	}

}
