package com.yang.ui.schema.importfiles;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import com.jfoenix.controls.JFXButton;
import com.yang.ui.services.FileService;
import com.yang.ui.services.model.ImportFile;
import com.yang.ui.services.model.ImportFileAction;
import com.yang.ui.services.model.ImportFileStatus;
import com.yang.ui.services.model.YangNode;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;




public class ImportPresenter  implements Initializable {
	
	private static final Logger LOG = LoggerFactory.getLogger(ImportPresenter.class);

	@FXML
	VBox importPane;
	
	@FXML
	TilePane importTile;

	@FXML
	JFXButton browseBtn;
	
	@FXML
	JFXButton importBtn;
	
	@FXML
	JFXButton closeBtn;
	
	@FXML
	TableView<ImportFile> importTable;
	
	@Inject
	FileService fileService;
	
    private ImportFilesPreview importPreviewSrv;
    
    private ImportFilesReport importReportSrv;
	
	
	@FXML
    protected void browseAction(ActionEvent event) {
		
    	LOG.trace("browseAction()");
        FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Select yang files to import");
    	fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*" + YangNode.YANG_EXT, "*" + YangNode.YANG_EXT));
		List<File> inputFiles = fileChooser.showOpenMultipleDialog(((Stage) importPane.getScene().getWindow()));
		
		importPreviewSrv = new ImportFilesPreview(inputFiles);
		importPreviewSrv.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	    	@Override
	    	public void handle(WorkerStateEvent t) {
	    			
	    		LOG.trace("ImportFilesPreview Service ------------------- Done: " + t.getSource().getValue());
	    		List<ImportFile> previewList = (List<ImportFile>) t.getSource().getValue();
	    		final ObservableList<ImportFile> previewListObs = FXCollections.observableArrayList(previewList);
	    		importTable.setItems(previewListObs);
	    		
	    		importBtn.setDisable(false);
	    	}
	    });
		importPreviewSrv.start();
    }

	
	@FXML
    protected void importAction(ActionEvent event) {
		
    	LOG.debug("Importing Yang Files");
    	List<ImportFile> inputFiles = importTable.getItems();
		importReportSrv = new ImportFilesReport(inputFiles);
		importReportSrv.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	    	@Override
	    	public void handle(WorkerStateEvent t) {
	    			
	    		LOG.trace("ImportFilesReport Service ------------------- Done: " + t.getSource().getValue());
	    		List<ImportFile> reportList = (List<ImportFile>) t.getSource().getValue();
	    		
	    		final ObservableList<ImportFile> reportListObs = FXCollections.observableArrayList(reportList);
	    		importTable.setItems(reportListObs);
	    		
	    		importBtn.setDisable(true);
	    	}
	    });
		importReportSrv.start();
    }
	
	
	@FXML
    protected void closeAction(ActionEvent event) {
    	LOG.trace("closeAction()");
    	((Stage) importPane.getScene().getWindow()).close();
    }
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		importPane.getStyleClass().add("importPane");
		importTile.getStyleClass().add("importTile");
		//configure action column
		TableColumn<ImportFile, String> actionColumn = (TableColumn<ImportFile, String>) importTable.getColumns().get(3);
		actionColumn.setOnEditCommit(t -> {
	        String newAction = t.getNewValue();
	        ObservableList<ImportFile> items = t.getTableView().getItems();
	        TablePosition<ImportFile, String> tablePosition = t.getTablePosition();
	        ImportFile importFile = items.get(tablePosition.getRow());
	        importFile.setAction(newAction);
		});
		actionColumn.setCellFactory(t -> new ComboBoxTableCell<ImportFile, String>(){
	         @Override 
	         public void startEdit() {
	        	ImportFile importFile = (ImportFile) getTableRow().getItem();
	        	ImportFileAction actionEnum = ImportFileAction.getInstance(importFile.getAction()); 
	        	switch (actionEnum)
        	    {
        	      case LEAVE:
        	    	  	ImportFileStatus statusEnum = ImportFileStatus.getInstance(importFile.getStatus());
        	    	  	if( statusEnum == ImportFileStatus.VALID_UNIQUE || statusEnum == ImportFileStatus.VALID_DUPLICATE) {}
        	    	  	else
        	    	  		getItems().setAll( ImportFileAction.getErrorOptions() );//Error
        	    	  	break;
        	      case COPY:
        	    	  getItems().setAll( ImportFileAction.getCopyOptions() ); 		//Copy 
        	    	  break;
        	      case OVERWRITE:
        	    	  getItems().setAll( ImportFileAction.getOverwriteOptions() );	//Overwrite
        	    	  break;
        	      default:        
        	    	  break;
        	    }
	            super.startEdit();
	        }
	    });
		//configure row factory
		importTable.setRowFactory(new Callback<TableView<ImportFile>, TableRow<ImportFile>>() {
	        @Override
	        public TableRow<ImportFile> call(TableView<ImportFile> tableView) {
	            final TableRow<ImportFile> row = new TableRow<ImportFile>() {
	                @Override
	                protected void updateItem(ImportFile importFile, boolean empty){
	                    super.updateItem(importFile, empty);
	                    
	                    if(importFile == null)
	                    	return;
	                    
	                    switch (ImportFileStatus.getInstance(importFile.getStatus()))
	            	    {
	            	      case ERR_NO_NAMESPACE:
	            	      case ERR_NO_NAME:
	            	      case ERR_NO_REVISION:
	            	      case ERR_EMPTY:
	            	      case ERR_DUPLICATE_IMPORT:
	            	      case ERR_FILE_IO:
	            	    	  if (! getStyleClass().contains("errorRow")) 
		                            getStyleClass().add("errorRow");
	            	    	  break;
	            	      case VALID_UNIQUE:
	            	      case VALID_DUPLICATE:
	            	    	  if ( getStyleClass().contains("errorRow")) 
	            	    		  getStyleClass().removeAll(Collections.singleton("errorRow"));
	            	    	  break;
	            	      case REPORT_LEAVE:
	            	      case REPORT_COPY:
	            	      case REPORT_OVERWRITE:
	            	    	  if ( getStyleClass().contains("errorRow")) 
	            	    		  getStyleClass().removeAll(Collections.singleton("errorRow"));
	            	    	  break;
	            	      default:        
	            	    	  break;
	            	    }
	                }
	            };
	            return row;
	        }
	    });
	}
	
	
	/** Populates the report import files table */
    private final class ImportFilesReport extends Service<List<ImportFile>> {
    	
	      public ImportFilesReport(List<ImportFile> value) {
				super();
				setInputFiles(value);
		  }	
	
		  private ObjectProperty<List<ImportFile>> inputFilesProperty = new SimpleObjectProperty<List<ImportFile>>();
	      public final void setInputFiles(List<ImportFile> value) {
	    	  inputFilesProperty.set(value);
	      }
	      public final List<ImportFile> getInputFiles() {
	          return inputFilesProperty.get();
	      }
	      public final ObjectProperty<List<ImportFile>> inputFilesProperty() {
	         return inputFilesProperty;
	      }
	
	      @Override
	      protected Task<List<ImportFile>> createTask() {
	      	final List<ImportFile> inputFiles = getInputFiles();
	          return new Task<List<ImportFile>>() {
	              @Override
	              protected List<ImportFile> call() {
	            	  return fileService.createImportReport(inputFiles);
	              }
	          };
	      }
    }
	
	
	 /** Populates the preview import files table */
    private final class ImportFilesPreview extends Service<List<ImportFile>> {
    	
	      public ImportFilesPreview(List<File> value) {
				super();
				setInputFiles(value);
		  }	
	
		  private ObjectProperty<List<File>> inputFilesProperty = new SimpleObjectProperty<List<File>>();
	      public final void setInputFiles(List<File> value) {
	    	  inputFilesProperty.set(value);
	      }
	      public final List<File> getInputFiles() {
	          return inputFilesProperty.get();
	      }
	      public final ObjectProperty<List<File>> inputFilesProperty() {
	         return inputFilesProperty;
	      }
	
	      @Override
	      protected Task<List<ImportFile>> createTask() {
	      	final List<File> inputFiles = getInputFiles();
	          return new Task<List<ImportFile>>() {
	              @Override
	              protected List<ImportFile> call() {
	            	  return fileService.createImportPreview(inputFiles);
	              }
	          };
	      }
    }
}
