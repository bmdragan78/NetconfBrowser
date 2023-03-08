package com.yang.ui.schema.container;

import java.net.URL;
import java.util.ResourceBundle;

import com.yang.ui.services.model.SchemaItem;
import com.yang.ui.services.model.YangContainer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class ContainerPresenter  implements Initializable {

	@FXML
	GridPane containerPane;
	
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
	CheckBox isAugmentingCheck;
	
	@FXML
	CheckBox isAddedByUsesCheck;
	
	@FXML
	CheckBox isMandatoryCheck;
	
	@FXML
	CheckBox isConfigCheck;
	
	@FXML
	CheckBox isPresenceCheck;
	
	@FXML
	TextField whenTxt;
	
	
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	containerPane.getStyleClass().add("gridPane");
    }
    
	
	public void updateView(SchemaItem selSchemaItem) {
		YangContainer yangNode = (YangContainer) selSchemaItem.getSchemaNode();
		
		localNameTxt.setText(yangNode.getLocalName());
		nodeTypeTxt.setText(yangNode.getTypeEnum().text());
		xpathTxt.setText(yangNode.getXpath());
		statusTxt.setText(yangNode.getStatus());
		descriptionTxt.setText(yangNode.getDescription());
		referenceTxt.setText(yangNode.getReference());
		
		isAugmentingCheck.setSelected(yangNode.isConfig());
		isAddedByUsesCheck.setSelected(yangNode.isAddedByUses());
		isMandatoryCheck.setSelected(yangNode.isMandatory());
		isConfigCheck.setSelected(yangNode.isConfig());
		
		isPresenceCheck.setSelected(yangNode.isPresence());
		
		whenTxt.setText(yangNode.getWhen());
	}
    
}
