package com.yangui.main.schema.deviation;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;

import com.yangui.main.services.SchemaService;
import com.yangui.main.services.model.SchemaItem;
import com.yangui.main.services.model.YangDeviation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class DeviationPresenter  implements Initializable {

	@FXML
	GridPane deviationPane;
	
	@FXML
	TextField nodeTypeTxt;
	
	@FXML
	TextField statusTxt;
	
	@FXML
	TextArea descriptionTxt;
	
	@FXML
	TextField referenceTxt;
	
//	@FXML
//	TextArea targetXpathTxt;
	
	@FXML
	Hyperlink targetPathLink;
	
	@Inject
    SchemaService schemaSrv;
	
	
	@FXML
    protected void navigateTarget(ActionEvent event) {
		schemaSrv.getLinkProperty().set(null);
		schemaSrv.getLinkProperty().set(targetPathLink.getText());
    }
	
	
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	deviationPane.getStyleClass().add("gridPane");
    }
    
	
	public void updateView(SchemaItem selSchemaItem) {
		YangDeviation yangNode = (YangDeviation) selSchemaItem.getSchemaNode();
		
		nodeTypeTxt.setText(yangNode.getTypeEnum().text());
		statusTxt.setText(yangNode.getStatus());
		descriptionTxt.setText(yangNode.getDescription());
		referenceTxt.setText(yangNode.getReference());
		
		targetPathLink.setText(yangNode.getTargetPath());
	}
    
}
