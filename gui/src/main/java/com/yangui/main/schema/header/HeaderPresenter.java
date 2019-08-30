package com.yangui.main.schema.header;


import java.net.URL;
import java.util.ResourceBundle;
import com.yangui.main.services.model.SchemaItem;
import com.yangui.main.services.model.YangSchemaNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class HeaderPresenter  implements Initializable {

	@FXML
	GridPane headerPane;
	
	@FXML
	TextField localNameTxt;
	
	@FXML
	TextField nodeTypeTxt;
	
	
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	headerPane.getStyleClass().add("gridPane");
    }
    
	
	public void updateView(SchemaItem selSchemaItem) {
		YangSchemaNode yangNode = (YangSchemaNode) selSchemaItem.getSchemaNode();
		
		localNameTxt.setText(yangNode.getLocalName());
		nodeTypeTxt.setText("Virtual Node");
	}
    
}
