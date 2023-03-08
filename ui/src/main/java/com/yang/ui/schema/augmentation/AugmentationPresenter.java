package com.yang.ui.schema.augmentation;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;

import com.yang.ui.services.SchemaService;
import com.yang.ui.services.model.SchemaItem;
import com.yang.ui.services.model.YangAugmentation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class AugmentationPresenter  implements Initializable {

	@FXML
	GridPane augmentationPane;
	
	@FXML
	TextField nodeTypeTxt;
	
//	@FXML
//	TextArea targetPathTxt;
	
	@FXML
	Hyperlink targetPathLink;
	
	@FXML
	TextField statusTxt;
	
	@FXML
	TextArea descriptionTxt;
	
	@FXML
	TextField referenceTxt;
	
	@FXML
	TextField whenTxt;
	
	@Inject
    SchemaService schemaSrv;
	
	@FXML
    protected void navigateTarget(ActionEvent event) {
		
		//set module xpath on SchemService.linkProperty ->on listen->selectTree(item where item.xpath=link.xpath)
		//start service to search
		schemaSrv.getLinkProperty().set(null);
		schemaSrv.getLinkProperty().set(targetPathLink.getText());
    }
	
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	augmentationPane.getStyleClass().add("gridPane");
    }
    
	
	public void updateView(SchemaItem selSchemaItem) {
		YangAugmentation yangNode = (YangAugmentation) selSchemaItem.getSchemaNode();
		
		targetPathLink.setText(yangNode.getTargetPath());
		nodeTypeTxt.setText(yangNode.getTypeEnum().text());
		statusTxt.setText(yangNode.getStatus());
		descriptionTxt.setText(yangNode.getDescription());
		referenceTxt.setText(yangNode.getReference());
		
		whenTxt.setText(yangNode.getWhen());
	}
    
}
