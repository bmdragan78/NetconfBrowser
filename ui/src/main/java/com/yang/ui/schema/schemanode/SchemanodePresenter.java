package com.yang.ui.schema.schemanode;

import java.net.URL;
import java.util.ResourceBundle;

import com.yang.ui.services.model.SchemaItem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class SchemanodePresenter  implements Initializable {

	@FXML
	GridPane schemanodePane;
	
	@FXML
	TextField schemanodeLocalName;
	
	@FXML
	TextField schemanodePrefixName;
	
	@FXML
	TextField schemanodeQName;
	
	@FXML
	TextField schemanodeTypeEnum;
	
	@FXML
	TextArea schemanodeDataType;
	
	@FXML
	CheckBox schemanodeConfig;
	
	@FXML
	CheckBox schemanodePresence;
	
	@FXML
	TextField schemanodeKey;
	
	@FXML
	CheckBox schemanodeMandatory;
	
	@FXML
	TextField schemanodeDefaultValue;
	
	@FXML
	TextArea schemanodeLocalPath;
	
	@FXML
	TextArea schemanodePrefixPath;
	
	@FXML
	TextArea schemanodeQPath;
	
	@FXML
	TextArea schemanodeDescription;
	
	/** Pass this parameter from parent controller to populate this view -> selected SchemaItem in parent controller */
	private SchemaItem schemaItem;
	
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	schemanodePane.getStyleClass().add("gridPane");
    }
    
    
    /** Populates this view with model */
    public void updateSchemanodeItem(SchemaItem selectedSchemaItem) {
    	schemaItem = selectedSchemaItem;
		updateView(schemaItem);
	}
	
	private void updateView(SchemaItem selSchemaItem) {
		schemanodeLocalName.setText(selSchemaItem.getSchemaNode().getLocalName());
//		schemanodePrefixName.setText(selSchemaItem.getSchemaNode().getPrefixName());
//    	schemanodeQName.setText(selSchemaItem.getSchemaNode().getqName());
//    	
//    	schemanodeTypeEnum.setText(selSchemaItem.getSchemaNode().getTypeEnum().name());
//    	schemanodeDataType.setText(selSchemaItem.getSchemaNode().getDataType());
//    	schemanodeConfig.setSelected(selSchemaItem.getSchemaNode().isConfig());
//    	schemanodePresence.setSelected(selSchemaItem.getSchemaNode().isPresence());
//    	schemanodeKey.setText(selSchemaItem.getSchemaNode().getListKeys());
//    	schemanodeMandatory.setSelected(selSchemaItem.getSchemaNode().isMandatory());
//    	schemanodeDefaultValue.setText(selSchemaItem.getSchemaNode().getDefaultValue());
    	schemanodeLocalPath.setText(selSchemaItem.getSchemaNode().getXpath());
//    	schemanodePrefixPath.setText(selSchemaItem.getSchemaNode().getPrefixPath());
//    	schemanodeQPath.setText(selSchemaItem.getSchemaNode().getQpath());
    	
    	schemanodeDescription.setText(selSchemaItem.getSchemaNode().getDescription());
	}
    
}
