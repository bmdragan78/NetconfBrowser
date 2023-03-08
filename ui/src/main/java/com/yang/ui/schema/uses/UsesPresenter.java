package com.yang.ui.schema.uses;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;

import com.yang.ui.services.SchemaService;
import com.yang.ui.services.model.SchemaItem;
import com.yang.ui.services.model.YangUses;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class UsesPresenter  implements Initializable {

	@FXML
	GridPane usesPane;
	
	@FXML
	TextField nodeTypeTxt;
	
//	@FXML
//	TextArea groupingPathTxt;
	
	@FXML
	Hyperlink groupingPathLink;
	
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
	TextField whenTxt;
	
	@Inject
    SchemaService schemaSrv;
	
	
	@FXML
    protected void navigateTarget(ActionEvent event) {
		schemaSrv.getLinkProperty().set(null);
		schemaSrv.getLinkProperty().set(groupingPathLink.getText());
    }
	
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	usesPane.getStyleClass().add("gridPane");
    }
    
	
	public void updateView(SchemaItem selSchemaItem) {
		YangUses yangNode = (YangUses) selSchemaItem.getSchemaNode();
		
		nodeTypeTxt.setText(yangNode.getTypeEnum().text());
		groupingPathLink.setText(yangNode.getGroupingPath());
		statusTxt.setText(yangNode.getStatus());
		descriptionTxt.setText(yangNode.getDescription());
		referenceTxt.setText(yangNode.getReference());
		
		isAugmentingCheck.setSelected(yangNode.isConfig());
		isAddedByUsesCheck.setSelected(yangNode.isAddedByUses());
		
		whenTxt.setText(yangNode.getWhen());
	}
    
}
