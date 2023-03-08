package com.yang.ui.schema.deviate;

import java.net.URL;
import java.util.ResourceBundle;

import com.yang.ui.services.model.SchemaItem;
import com.yang.ui.services.model.YangDeviate;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class DeviatePresenter  implements Initializable {

	@FXML
	GridPane deviatePane;
	
	@FXML
	TextField kindTxt;
	
	@FXML
	TextField defaultValueTxt;
	
	@FXML
	TextField unitsTxt;
	
	@FXML
	TextArea typeTxt;
	
	@FXML
	TextArea mustTxt;
	
	@FXML
	TextField uniquesTxt;
	
	
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	deviatePane.getStyleClass().add("gridPane");
    }
    
	
	public void updateView(SchemaItem selSchemaItem) {
		YangDeviate yangNode = (YangDeviate) selSchemaItem.getSchemaNode();
		
		kindTxt.setText(yangNode.getKind());
		defaultValueTxt.setText(yangNode.getDefaultValue());
		unitsTxt.setText(yangNode.getUnits());
		typeTxt.setText(yangNode.getType());
		mustTxt.setText(yangNode.getMust());
		uniquesTxt.setText(yangNode.getUniques());
	}
    
}
