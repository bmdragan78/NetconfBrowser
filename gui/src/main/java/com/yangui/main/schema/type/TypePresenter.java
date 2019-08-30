package com.yangui.main.schema.type;

import java.net.URL;
import java.util.ResourceBundle;

import com.yangui.main.services.model.SchemaItem;
import com.yangui.main.services.model.YangType;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class TypePresenter  implements Initializable {

	@FXML
	GridPane typePane;
	
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
	TextField defaultValueTxt;
	
	@FXML
	TextField unitsTxt;
	
	@FXML
	TextArea typeHierarchyTxt;
	
	
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	typePane.getStyleClass().add("gridPane");
    }
    
	
	public void updateView(SchemaItem selSchemaItem) {
		YangType yangNode = (YangType) selSchemaItem.getSchemaNode();
		
		localNameTxt.setText(yangNode.getLocalName());
		nodeTypeTxt.setText(yangNode.getTypeEnum().text());
		xpathTxt.setText(yangNode.getXpath());
		statusTxt.setText(yangNode.getStatus());
		descriptionTxt.setText(yangNode.getDescription());
		referenceTxt.setText(yangNode.getReference());
		
		typeHierarchyTxt.setText(yangNode.getTypeHierarchy());
		unitsTxt.setText(yangNode.getUnits());
		defaultValueTxt.setText(yangNode.getDefaultValue());
	}
    
}
