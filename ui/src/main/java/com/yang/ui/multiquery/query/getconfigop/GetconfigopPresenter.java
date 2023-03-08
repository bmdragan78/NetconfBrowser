package com.yang.ui.multiquery.query.getconfigop;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;
import com.jfoenix.controls.JFXComboBox;
import com.yang.ui.multiquery.query.base.BaseopPresenter;
import com.yang.ui.services.XmlService;
import com.yang.ui.services.model.QueryTypeEnum;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;



public class GetconfigopPresenter extends BaseopPresenter implements Initializable {
	
	@FXML
	HBox getopPane;
	
	@FXML
    Button filterBtn;
	
	@FXML
	JFXComboBox<String> sourceCmb;
    
	@Inject
    XmlService xmlSrv;
	
	private SourceService sourceService;
    
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		sourceCmb.setItems(getSourceList());
		sourceCmb.getSelectionModel().select(1);
		sourceService = new SourceService();
		sourceService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	    	@Override
	    	public void handle(WorkerStateEvent t) {
	    		queryPresenter.updateQueryInput(t.getSource().getValue().toString());
	    	}
	    });
		getopPane.getStyleClass().add("getopPane");
	}
	
	
	@FXML
	protected void selectSourceAction(ActionEvent event) {
	    String source = sourceCmb.getSelectionModel().getSelectedItem().toString();
		String input = queryPresenter.getQueryInput();
		QueryTypeEnum queryTypeEnum = queryPresenter.getQueryType();
		String queryName = queryPresenter.getQueryName();
		
		if(queryTypeEnum.equals(QueryTypeEnum.GET_CONFIG)) {
			sourceService.setData(source, input, queryName);
			sourceService.restart();
		}
	}

	
	private final class SourceService extends Service<String> {
	    	
	    String source;
	    String input;
	    String queryName;

		public SourceService() {
			super();
		}
			
		public void setData(String source, String input, String queryName) {
			this.source = source;
			this.input = input;
			this.queryName = queryName;
		}
			
		@Override
		protected Task<String> createTask() {
			return new Task<String>() {
				@Override
			    protected String call() {
					return xmlSrv.updateQuerySource(input, source, queryName);
			    }
		    };
		}
	 }

}
