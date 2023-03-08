package com.yang.ui.main;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import com.jfoenix.controls.JFXTabPane;
import com.yang.ui.App;
import com.yang.ui.connection.ConnectionView;
import com.yang.ui.device.DevicePresenter;
import com.yang.ui.device.DeviceView;
import com.yang.ui.log.LogPresenter;
import com.yang.ui.log.LogView;
import com.yang.ui.multiquery.MultiQueryPresenter;
import com.yang.ui.multiquery.MultiQueryView;
import com.yang.ui.schema.SchemaPresenter;
import com.yang.ui.schema.SchemaView;
import com.yang.ui.services.FileService;
import com.yang.ui.services.model.ErrorItem;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;



public class MainPresenter implements Initializable {
	
	private static final Logger LOG = LoggerFactory.getLogger(MainPresenter.class);

	@FXML
	Hyperlink companyLink;
	
    @FXML
    SplitPane mainPane;
    
    @FXML
    BorderPane borderPane;

    @FXML
    JFXTabPane mainTabPane;
    
    @FXML
    Tab schemaTab;
    
    @FXML
    Tab queryTab;
    
    @FXML
    Tab deviceTab;
    
    @FXML
    HBox headerPane;
    
    @FXML
    HBox logoPane;
    
    @FXML
    VBox logoVBox;
    
    @FXML
    ImageView logoImage;
    
    @FXML
    VBox logContainer;
    
	
	@FXML
    protected void navigateTarget(ActionEvent event) {
		 try {
			 String url = companyLink.getText();
			 App.WEB_BROWSER.showDocument(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	
    private ConnectionView connectionView; 
    private Parent connectionPane;

    private SchemaView schemaView;
    private SchemaPresenter schemaPresenter;
    private Parent schemaPane;
    
    private DeviceView deviceView;
    private DevicePresenter devicePresenter;
    private Parent devicePane;
    
    private MultiQueryView multiQueryView;
    private MultiQueryPresenter multiQueryPresenter;
    private Parent multiQueryPane;
    
    private LogView logView;
    private LogPresenter logPresenter;
    private Parent logPane;
    
	@Inject
	FileService fileService;
	
	
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	
    	Image image = new Image(getClass().getResourceAsStream("db-logo.png"));
    	logoImage.setImage(image);
    	
    	mainPane.getStyleClass().add("mainPane");
    	borderPane.getStyleClass().add("borderPane");
    	headerPane.getStyleClass().add("headerPane");
    	logoPane.getStyleClass().add("logoPane");
    	logoVBox.getStyleClass().add("logoVBox");

        //Populate schema tab
        schemaView = new SchemaView();
        schemaPane = schemaView.getView();
        schemaTab.setContent(schemaPane);
        schemaPresenter = (SchemaPresenter) schemaView.getPresenter();
        
        //Populate query tab
        multiQueryView = new MultiQueryView();
        multiQueryPane = multiQueryView.getView();
        queryTab.setContent(multiQueryPane);
        multiQueryPresenter = (MultiQueryPresenter) multiQueryView.getPresenter();
        
        //Populate network tab
        deviceView = new DeviceView();
        devicePane = deviceView.getView();
        deviceTab.setContent(devicePane);
        devicePresenter = (DevicePresenter) deviceView.getPresenter();
        
    	//Populate connect widget
    	connectionView = new ConnectionView();
    	connectionPane = connectionView.getView();
    	headerPane.getChildren().add(connectionPane);
    	connectionPane.getStyleClass().add("connectionPane");
    	
    	//Populate log pane
    	logView = new LogView();
    	logPane = logView.getView();
        logContainer.getChildren().add(logPane);
        logPresenter = (LogPresenter) logView.getPresenter();
        
        //error selection
        logPresenter.selectedErrorProperty().addListener((obs, oldSelection, newSelection) -> {
		    if (newSelection != null) {
		    	ErrorItem errorItem = newSelection;
		    	SingleSelectionModel<Tab> selectionModel = mainTabPane.getSelectionModel();
		    	switch(errorItem.getTypeEnum()) {
			    	case SCHEMA:
			    		selectionModel.select(0);
			    		schemaPresenter.selectError(errorItem);
			    		break;
			    	case NETWORK:
			    		selectionModel.select(1);
			    		devicePresenter.selectError(errorItem);
			    		break;
			    	case QUERY:
			    		selectionModel.select(2);
			    		multiQueryPresenter.selectError(errorItem);
			    		break;
			    	default:
		    	}
		    }
		});
    }
    
}
