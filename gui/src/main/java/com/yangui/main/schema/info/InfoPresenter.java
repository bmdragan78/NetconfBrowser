package com.yangui.main.schema.info;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;

import com.yangui.main.schema.SchemaPresenter;
import com.yangui.main.services.FileService;
import com.yangui.main.services.model.SchemaItem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class InfoPresenter  implements Initializable {

	@FXML
	TextField schemaFolder;
	
	@FXML
	TextField fileCount;
	
	@FXML
	TextField fileSize;
	
	@FXML
	GridPane infoPane;
	
	@Inject
	SchemaPresenter schPresenter;
	
	@Inject
	FileService fileService;
	
	private SchemaItem repoItem;
	
	
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	infoPane.getStyleClass().add("gridPane");
    }
    
    
    public void updateView(SchemaItem schemaItem) {
    	schemaFolder.setText(schemaItem.getRepo().getSchemaFolder());
    	fileCount.setText(Integer.toString(schemaItem.getRepo().getFileCount()));
    	fileSize.setText(Long.toString(schemaItem.getRepo().getFileSize()));
    }
    
    
	public void updateInfoItem(SchemaItem repo) {
		repoItem = repo;
		updateView(repoItem);
	}
    
    
}
