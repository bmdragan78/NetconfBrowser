package com.yang.ui.schema.extension;

import java.net.URL;
import java.util.ResourceBundle;

import com.yang.ui.services.model.SchemaItem;
import com.yang.ui.services.model.YangExtension;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class ExtensionPresenter  implements Initializable {

	@FXML
	GridPane extensionPane;
	
	@FXML
	TextField localNameTxt;
	
	@FXML
	TextField nodeTypeTxt;
	
	@FXML
	TextArea xpathTxt;
	
	@FXML
	TextField statusTxt;
	
	@FXML
	TextArea descriptionTxt;
	
	@FXML
	TextField referenceTxt;
	
	@FXML
	CheckBox isYinCheck;
	
	@FXML
	TextField argumentTxt;
	
	
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	extensionPane.getStyleClass().add("gridPane");
    }
    
	
	public void updateView(SchemaItem selSchemaItem) {
		YangExtension yangNode = (YangExtension) selSchemaItem.getSchemaNode();
		
		localNameTxt.setText(yangNode.getLocalName());
		nodeTypeTxt.setText(yangNode.getTypeEnum().text());
		xpathTxt.setText(yangNode.getXpath());
		statusTxt.setText(yangNode.getStatus());
		descriptionTxt.setText(yangNode.getDescription());
		referenceTxt.setText(yangNode.getReference());
		
		isYinCheck.setSelected(yangNode.isYin());
		
		argumentTxt.setText(yangNode.getArgument());
	}
    
}
