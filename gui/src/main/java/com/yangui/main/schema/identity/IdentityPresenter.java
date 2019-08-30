package com.yangui.main.schema.identity;


import java.net.URL;
import java.util.ResourceBundle;

import com.yangui.main.services.model.SchemaItem;
import com.yangui.main.services.model.YangIdentity;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class IdentityPresenter  implements Initializable {

	@FXML
	GridPane identityPane;
	
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
	TextArea typeHierarchyTxt;
	
	
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	identityPane.getStyleClass().add("gridPane");
    }
    
	
	public void updateView(SchemaItem selSchemaItem) {
		YangIdentity yangNode = (YangIdentity) selSchemaItem.getSchemaNode();
		
		localNameTxt.setText(yangNode.getLocalName());
		nodeTypeTxt.setText(yangNode.getTypeEnum().text());
		xpathTxt.setText(yangNode.getXpath());
		statusTxt.setText(yangNode.getStatus());
		descriptionTxt.setText(yangNode.getDescription());
		referenceTxt.setText(yangNode.getReference());
		
		typeHierarchyTxt.setText(yangNode.getTypeHierarchy());
	}
    
}
