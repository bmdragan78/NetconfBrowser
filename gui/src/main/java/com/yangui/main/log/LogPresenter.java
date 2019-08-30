package com.yangui.main.log;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;
import org.fxmisc.richtext.InlineCssTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import com.jfoenix.controls.JFXTabPane;
import com.yangui.main.services.FileService;
import com.yangui.main.services.LogService;
import com.yangui.main.services.model.ErrorItem;



public class LogPresenter implements Initializable {
	
	private static final Logger LOG = LoggerFactory.getLogger(LogPresenter.class);

	@FXML
	JFXTabPane logTabPane;
	
	@FXML
	Tab logTab;
	
	@FXML
	InlineCssTextArea logText;
	    
	@FXML
	Tab errorTab;
	
	@FXML
	TableView<ErrorItem> errorTableView;
    
	@Inject
	FileService fileService;
	
	@Inject
	private LogService logService;
	
	private IntegerBinding errorSizeProperty;
	
	private ObjectProperty<ErrorItem> selectedErrorProperty = new SimpleObjectProperty<ErrorItem>();;
	
	
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	//listen for log changes
    	fileService.getLogProperty().addListener( ( ov, oldv, newv ) -> {
    		try {
    			
	    		logText.clear();
	    		logText.appendText(newv);
	    		
	    		logText.moveTo(logText.getParagraphs().size()-1, 0);
	    		logText.requestFollowCaret();
    		}catch(Exception ex) {
    			LOG.error("", ex);
    		}
    	});
    	TableColumn<ErrorItem, String> typeCol = new TableColumn<ErrorItem, String>("Type");
        typeCol.setPrefWidth(90);
        TableColumn<ErrorItem, String> sourceCol = new TableColumn<ErrorItem, String>("Source");
        sourceCol.setPrefWidth(90);
        TableColumn<ErrorItem, String> lineNumberCol = new TableColumn<ErrorItem, String>("Line");
        lineNumberCol.setPrefWidth(90);
        TableColumn<ErrorItem, String> messageCol = new TableColumn<ErrorItem, String>("Message");
        messageCol.setPrefWidth(980);
        
        typeCol.setCellValueFactory(new PropertyValueFactory<ErrorItem, String>("type"));
        sourceCol.setCellValueFactory(new PropertyValueFactory<ErrorItem, String>("source"));
        lineNumberCol.setCellValueFactory(new PropertyValueFactory<ErrorItem, String>("lineNumber"));
        messageCol.setCellValueFactory(new PropertyValueFactory<ErrorItem, String>("message"));
        
        errorTableView.getColumns().addAll(typeCol, sourceCol, lineNumberCol, messageCol);
        errorTableView.setEditable(false);
        
        //add device list data
        errorTableView.setItems(logService.errorObsList());
        errorTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        errorTableView.setRowFactory(new Callback<TableView<ErrorItem>, TableRow<ErrorItem>>() {
        	@Override
            public TableRow<ErrorItem> call(TableView<ErrorItem> tableView) {
        		final TableRow<ErrorItem> row = new TableRow<ErrorItem>() {
        			@Override
                    protected void updateItem(ErrorItem errorItem, boolean empty){
        				super.updateItem(errorItem, empty);
                    }
                };
                row.setOnMouseClicked(event -> {
                	if (!row.isEmpty() && event.getButton()==MouseButton.PRIMARY  && event.getClickCount() == 1) {
                		ErrorItem errorItem = row.getItem();
	                    //updateSelection(errorItem);
	                    selectedErrorProperty.set(null);
	                    selectedErrorProperty.set(errorItem);
	                }
	            });
                return row;
        	}
        });
        
        errorSizeProperty = Bindings.size(logService.errorObsList());
        errorSizeProperty.addListener((o, oldValue, newValue) -> {
        	int size = (Integer)newValue.intValue();
        	Label titleLabel = (Label) logTabPane.lookup(".jfx-tab-pane #errorTab.tab .tab-label");
        	if(titleLabel == null) {
        		LOG.error("Log tab with id=errorTab was not found in LogView");
        		return;
        	}
        	if(size > 0) 
        		titleLabel.setStyle("-fx-text-fill: red;");
        	else 
        		titleLabel.setStyle("-fx-text-fill: white;");
        });
    }
    
    
    public ObjectProperty<ErrorItem> selectedErrorProperty(){//watched by MainPresenter
    	return selectedErrorProperty;
    }
    
}
