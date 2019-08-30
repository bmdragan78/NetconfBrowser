package com.yangui.main.schema.module;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXCheckBox;
import com.yangui.main.services.SchemaService;
import com.yangui.main.services.model.FeatureItem;
import com.yangui.main.services.model.SchemaItem;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;



public class ModulePresenter  implements Initializable {

	private static final Logger LOG = LoggerFactory.getLogger(ModulePresenter.class);
	
	@FXML
	GridPane modulePane;
	
	@FXML
	TextField moduleName;
	
	@FXML
	TextField moduleNamespace;
	
	@FXML
	TextField modulePrefix;
	
	@FXML
	TextField moduleRevision;
	
	@FXML
	TextArea moduleOrganization;
	
	@FXML
	TextArea moduleContact;
	
	@FXML
	TextArea moduleDescription;
	
	@FXML
	FlowPane featuresPane;
	
    @Inject
    SchemaService schemaSrv;
	
	
	private List<JFXCheckBox> featureChecks = new ArrayList<JFXCheckBox>();
	
	private UpdateFeaturesService featureService;
	
	/** Pass this parameter from parent controller to populate this view -> selected SchemaItem in parent controller */
	private SchemaItem moduleItem;
	
	//It is watched by SchemaPresenter to reload schema
	private BooleanProperty updateFeatureProperty;
    public BooleanProperty updateFeatureProperty() {
		return updateFeatureProperty;
	}


	@Override
    public void initialize(URL location, ResourceBundle rb) {
    	modulePane.getStyleClass().add("gridPane");
    	
    	updateFeatureProperty = new SimpleBooleanProperty(false);
    	featureService = new UpdateFeaturesService();
    	featureService.setOnSucceeded((WorkerStateEvent t) -> {
    		updateFeatureProperty.set(!updateFeatureProperty.get());
    	});
    	featureService.setOnFailed((WorkerStateEvent t) -> {
			LOG.error("UpdateFeaturesService ------------------- Failed: " + featureService.getException().getMessage());
    	});
    }
    
    
    // Populates this view with model 
	public void updateModuleItem(SchemaItem selectedModuleItem) {
		moduleItem = selectedModuleItem;
		updateView(moduleItem);
	}
	
	
	private void updateView(SchemaItem selSchemaItem) {
    	moduleName.setText(selSchemaItem.getModule().getName());
    	moduleNamespace.setText(selSchemaItem.getModule().getNamespace());
    	modulePrefix.setText(selSchemaItem.getModule().getPrefix());
    	
    	moduleRevision.setText(selSchemaItem.getModule().getRevision());
    	moduleOrganization.setText(selSchemaItem.getModule().getOrganization());
    	moduleContact.setText(selSchemaItem.getModule().getContact());
    	moduleDescription.setText(selSchemaItem.getModule().getDescription());
    	
    	//remove all checks
    	featureChecks.clear();
		featuresPane.getChildren().clear();
    	List<FeatureItem> featureItemList = selSchemaItem.getModule().getFeatureItemList();
    	for(FeatureItem featureItem : featureItemList) {
    		JFXCheckBox featureCheck = new JFXCheckBox(featureItem.getFeature().getLocalName().toString());
    		featureCheck.selectedProperty().bindBidirectional(featureItem.isEnabledProperty());
    		featureCheck.selectedProperty().addListener(
    				(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
    					List<FeatureItem> updatedFeatureItemList = moduleItem.getModule().getFeatureItemList();//updated FeatureItem list
    					featureService.setFeatureItemList(updatedFeatureItemList);
    					featureService.restart();
    		});
    		//add new checks
    		featureChecks.add(featureCheck);
    		featuresPane.getChildren().add(featureCheck);
    	}
    	featuresPane.disableProperty().bind(featureService.runningProperty());
	}
	
	
    private final class UpdateFeaturesService extends Service<Void> {
    	
    	private List<FeatureItem> featureItemList;
	      
		public UpdateFeaturesService() {
	  		super();
	  	}	
	
	    @Override
	    protected Task<Void> createTask() {
	    	return new Task<Void>() {
	    		@Override
	            protected Void call() throws Exception {
	    			schemaSrv.updateFeatures(featureItemList);
	    			return null;
	    		}
	       };
	    }
	    
		public void setFeatureItemList(List<FeatureItem> featureItemList) {
			this.featureItemList = featureItemList;
		}
    }
}
