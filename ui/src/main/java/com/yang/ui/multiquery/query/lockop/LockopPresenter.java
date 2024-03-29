package com.yang.ui.multiquery.query.lockop;

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
import javafx.scene.layout.HBox;



public class LockopPresenter extends BaseopPresenter implements Initializable {

	@FXML
	HBox getopPane;
	
	@FXML
	JFXComboBox<String> targetCmb;
	
    
	@Inject
    XmlService xmlSrv;
	
	private TargetService targetService;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		targetCmb.setItems(getTargetList());
		targetCmb.getSelectionModel().select(1);
		
		targetService = new TargetService();
		targetService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	    	@Override
	    	public void handle(WorkerStateEvent t) {
	    		queryPresenter.updateQueryInput(t.getSource().getValue().toString());
	    	}
	    });
		getopPane.getStyleClass().add("getopPane");
		
	}
	
	@FXML
	protected void selectTargetAction(ActionEvent event) {
	    String target = targetCmb.getSelectionModel().getSelectedItem().toString();
		String input = queryPresenter.getQueryInput();
		QueryTypeEnum queryTypeEnum = queryPresenter.getQueryType();
		String queryName = queryPresenter.getQueryName();
		
		if(queryTypeEnum.equals(QueryTypeEnum.LOCK)) {
			targetService.setData(target, input, queryName);
			targetService.restart();
		}
	}


	private final class TargetService extends Service<String> {
    	
	    String target;
	    String input;
	    String queryName;

		public TargetService() {
			super();
		}
			
		public void setData(String target, String input, String queryName) {
			this.target = target;
			this.input = input;
			this.queryName = queryName;
		}
			
		@Override
		protected Task<String> createTask() {
			return new Task<String>() {
				@Override
			    protected String call() {
					return xmlSrv.updateQueryTarget(input, target, queryName);
			    }
		    };
		}
	 }
}