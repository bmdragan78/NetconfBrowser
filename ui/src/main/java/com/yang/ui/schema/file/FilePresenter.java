package com.yang.ui.schema.file;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXButton;
import com.yang.ui.codearea.CodeareaView;
import com.yang.ui.codearea.Language;
import com.yang.ui.services.FileService;
import com.yang.ui.services.SchemaService;
import com.yang.ui.services.model.SchemaItem;
import com.yang.ui.services.model.SchemaValidationStatus;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;



public class FilePresenter  implements Initializable {
	
	private static final Logger LOG = LoggerFactory.getLogger(FilePresenter.class);

	@FXML
	GridPane filePane;
	
	@FXML
	TextField fileName;
	
	@FXML
	TextField fileSize;
	
	@FXML
	TextField fileLastModified;
	
	@FXML
	JFXButton fileSaveBtn;
	
	@FXML
	StackPane  codeAreaPane;
	
	@Inject
	FileService fileService;
	
	@Inject
	SchemaService schemaService;
	
	private CodeareaView codeareaView;
	
    private UpdateFileService updateFileSrv;
	
	//Pass this parameter from parent controller to populate this view -> selected SchemaItem in parent controller 
	private SchemaItem fileItem;
	
	//Pass this parameter back to parent controller to update parent view 
	private ObjectProperty<SchemaItem> updatedFileProperty;
	public ReadOnlyObjectProperty<SchemaItem> updatedFileProperty() {
        return updatedFileProperty;
    } 

	
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	filePane.getStyleClass().add("gridPane");
    	
    	//add CodeArea to grid
    	codeareaView = new CodeareaView();
    	codeAreaPane.getChildren().add(codeareaView.getView());
    	codeareaView.getCodeArea().initYang(false);
    	
        //init update file service
        updatedFileProperty = new SimpleObjectProperty<SchemaItem>();
    	updateFileSrv = new UpdateFileService();
    	updateFileSrv.setOnSucceeded((WorkerStateEvent t) -> {
    		SchemaItem updatedFile = (SchemaItem) t.getSource().getValue();
    		
    		switch (updatedFile.getStatusEnum()) {
	            case OK:
	            	fileName.setStyle("-fx-border-color: red; -fx-border-width: 0px;");
	            	codeareaView.getCodeArea().setStyle("-fx-border-color: red; -fx-border-width: 0px;");
	                break;
	            case ERR_NO_FILE_NAME:
	            case ERR_FILE_DUPLICATE:
	            	fileName.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
	            	break;
	            case ERR_NO_FILE_CONTENT:
	            case ERR_NO_MODULE:
	            case ERR_NO_NAMESPACE:
	            case ERR_NO_MOD_REVISION://never happens
	            case ERR_MODULE_DUPLICATE:
	            case ERR_IO:
	            case ERR_YANG:
	            	codeareaView.getCodeArea().setStyle("-fx-border-color: red; -fx-border-width: 2px;");
	           	 	break;
	            default:
	           	 	LOG.error("FileUpdate returned N/A Errors ");
	       }
    		//refresh local view
    	    updateView(updatedFile);
    		//refresh parent view
    	    updatedFileProperty.set(null);
    	    updatedFileProperty.set(updatedFile);
    	});
    	updateFileSrv.setOnFailed((WorkerStateEvent t) -> {
			LOG.error("UpdateFileService ------------------- Failed: " + updateFileSrv.getException().getMessage());
    	});
    	
    	//update line number
    	schemaService.getSourceIndexProperty().addListener( ( ov, oldv, newv ) -> {
    		Integer index = (Integer)newv;
    		if(index > 1) 
    			codeareaView.getCodeArea().moveTo(index-1);
	    	else
	    		codeareaView.getCodeArea().moveTo(0);
    		codeareaView.getCodeArea().requestFollowCaret();
    	});
    }

    
    @FXML
    protected void saveAction(ActionEvent event) {
        updateFileSrv.setData(fileItem, fileName.getText(), codeareaView.getCodeArea().getText());
    	updateFileSrv.restart();
    }
    
    // Populates this view with model
	public void updateFileItem(SchemaItem selectedFileItem) {
		fileItem = selectedFileItem;
		updateView(fileItem);
	}
	
	
    private void updateView(SchemaItem selSchemaItem) {//called from updateFileSrv.OnAction & SchemaPresenter.selectedItemChange event
    	try {
    		//LOG.debug("updateView staus " + selSchemaItem.getStatusEnum().text());
    		
	    	fileName.setText(selSchemaItem.getFile().getName());
	    	fileSize.setText(selSchemaItem.getFile().getSize()+"");
	    	fileLastModified.setText(selSchemaItem.getFile().getLastModifiedTime().toString());
	    	codeareaView.getCodeArea().replaceText(selSchemaItem.getFile().getContent());
	    	
	    	switch (selSchemaItem.getStatusEnum()) {
	            case OK:
	            	fileName.setStyle("-fx-border-color: red; -fx-border-width: 0px;");
	            	//codeareaView.getCodeArea().setStyle("-fx-border-color: red; -fx-border-width: 0px;");
	                break;
	            case ERR_NO_FILE_NAME:
	            case ERR_FILE_DUPLICATE:
	            	fileName.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
	            	break;
	            case ERR_NO_FILE_CONTENT:
	            case ERR_NO_MODULE:
	            case ERR_NO_NAMESPACE:
	            case ERR_NO_MOD_REVISION://never happens
	            case ERR_MODULE_DUPLICATE:
	            case ERR_IO:
	            case ERR_YANG:
	            	codeareaView.getCodeArea().setStyle("-fx-border-color: red; -fx-border-width: 2px;");
	           	 	break;
	            default:
	           	 	LOG.error("FileUpdate returned N/A Errors ");
	       }
	    	
	    	//in case of errors present on this SchemaItem go to line in FileEditor and update logText from MainPresenter
	    	if(SchemaValidationStatus.OK != selSchemaItem.getStatusEnum()) {
	    		
	    		try {
		    		int errorLineNumber = selSchemaItem.getErrorLineNumber();
			    	if(errorLineNumber > 1) 
			    		codeareaView.getCodeArea().moveTo(errorLineNumber-1, 0);
			    	else
			    		codeareaView.getCodeArea().moveTo(0, 0);
			    	codeareaView.getCodeArea().requestFollowCaret();
	    		}catch(IndexOutOfBoundsException iex) {
	    			
	    		}
	    	}else {//in case of no errors present on this SchemaItem go to sourceMsg in FileEditor; sourceMsg is set on SchemaPresenter.selectedItem event
//	    		String sourceMsg = selSchemaItem.getSourceMsg();
//	    		if(sourceMsg != null) {
//	    		}else {//if sourceMsg is not set go to line 0 in FileEditor
//		    		fileContent.moveTo(0, 0);
//		    		fileService.getErrorMsgProperty().set(null);
//	    		}
	    	}
    	}catch(Exception ex) {
    		ex.printStackTrace();
    		
    	}
    }
    

	//Updates local file in the schema repository
    private final class UpdateFileService extends Service<SchemaItem> {
    	
    	private SchemaItem schemaItem;
		  
		private String newName;
		  
	    private String newContent;
	      
	    public UpdateFileService() {
	  		super();
	  	}	
	
	    @Override
	    protected Task<SchemaItem> createTask() {
	      	
	    	return new Task<SchemaItem>() {
	    		@Override
	            protected SchemaItem call() throws Exception {
	    			return fileService.updateCreateFile(schemaItem, newName, newContent);
	    		}
	       };
	    }
	    
		public void setData(SchemaItem schemaItem, String newName, String newContent) {
			this.schemaItem = schemaItem;
			this.newName = newName;
			this.newContent = newContent;
		}

    }
    
}
