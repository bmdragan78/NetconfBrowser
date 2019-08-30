package com.yangui.main.schema.list;

import java.net.URL;
import java.util.ResourceBundle;
import com.yangui.main.services.model.SchemaItem;
import com.yangui.main.services.model.YangList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class ListPresenter  implements Initializable {

	@FXML
	GridPane listPane;
	
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
	TextField whenTxt;
	
	@FXML
	TextField maxElementsTxt;
	
	@FXML
	TextField minElementsTxt;
	
	@FXML
	TextField keyTxt;
	
	@FXML
	CheckBox isUserOrderedCheck;
	
	
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	listPane.getStyleClass().add("gridPane");
    }
    
	
	public void updateView(SchemaItem selSchemaItem) {
		YangList yangNode = (YangList) selSchemaItem.getSchemaNode();
		
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
		
		keyTxt.setText(yangNode.getKey());
		isUserOrderedCheck.setSelected(yangNode.isUserOrdered());
		
		whenTxt.setText(yangNode.getWhen());
		maxElementsTxt.setText("" + yangNode.getMaxElements());
		minElementsTxt.setText("" + yangNode.getMinElements());
	}
    
}
