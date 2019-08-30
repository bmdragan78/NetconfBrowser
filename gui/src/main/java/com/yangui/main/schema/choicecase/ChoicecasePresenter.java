package com.yangui.main.schema.choicecase;

import java.net.URL;
import java.util.ResourceBundle;
import com.yangui.main.services.model.SchemaItem;
import com.yangui.main.services.model.YangChoiceCase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class ChoicecasePresenter  implements Initializable {

	@FXML
	GridPane choicecasePane;
	
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
	
	
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	choicecasePane.getStyleClass().add("gridPane");
    }
    
	
	public void updateView(SchemaItem selSchemaItem) {
		YangChoiceCase yangNode = (YangChoiceCase) selSchemaItem.getSchemaNode();
		
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
		
		whenTxt.setText(yangNode.getWhen());
		maxElementsTxt.setText("" + yangNode.getMaxElements());
		minElementsTxt.setText("" + yangNode.getMinElements());
	}
    
}
