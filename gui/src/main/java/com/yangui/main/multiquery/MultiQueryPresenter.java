package com.yangui.main.multiquery;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfoenix.controls.JFXButton;
import com.yangui.main.multiquery.query.QueryPresenter;
import com.yangui.main.multiquery.query.QueryView;
import com.yangui.main.services.LogService;
import com.yangui.main.services.QueryService;
import com.yangui.main.services.model.ErrorItem;



public class MultiQueryPresenter implements Initializable {
	
	private static final Logger LOG = LoggerFactory.getLogger(MultiQueryPresenter.class);
	
	@FXML
    AnchorPane multiQueryPane;
    
    @FXML
    TabPane multiQueryTabPane;
    
    @FXML
    JFXButton addTabButton;
    
    @Inject
    QueryService querySrv;
    
    @Inject
    LogService logSrv;
    
    private static int QUERY_LIMIT = 9;//allow only 10 query windows
    
    private List<QueryPresenter> queryPresenterList = new ArrayList<QueryPresenter>();
    
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	try {
    		String queryName = "Query 1";
    		addQueryTab(queryName);
    	}catch(Exception ex) {
    		LOG.error("MultiQuery initialization exception : ", ex);
    	}
    }
    
    
    @FXML
    protected void addQueryAction(ActionEvent event) throws Exception {
    	
    	int tabCount = multiQueryTabPane.getTabs().size();
    	String lastTabTitle = multiQueryTabPane.getTabs().get(tabCount-1).getText();
    	int tabIndex = Integer.parseInt(lastTabTitle.substring(lastTabTitle.length() - 2).trim());//"Query tabIndex"
    	String queryName = "Query " + (++tabIndex);
    	
    	addQueryTab(queryName);
    }
    
    
    private void addQueryTab(String queryName) throws Exception {
    	Tab tab = new Tab(queryName);
    	tab.setOnClosed(e -> {
    		addTabButton.setDisable(false);
    		//purge queryPresenter list
    		String title = tab.getText();
    		Iterator<QueryPresenter> itr = queryPresenterList.iterator();
    		while (itr.hasNext()){
    			QueryPresenter t = itr.next();
    			if ( t.getQueryName().equals(title)) 
    				itr.remove();
    		}
    		//purge errorList 
    		logSrv.clearQueryErrors(title);
    	});
    	QueryView queryView = new QueryView();
    	QueryPresenter queryPresenter = (QueryPresenter) queryView.getPresenter();
    	queryPresenter.setQueryName(queryName);
    	Parent queryPane = queryView.getView();
    	tab.setContent(queryPane);
    	multiQueryTabPane.getTabs().add(tab);
    	queryPresenterList.add(queryPresenter);
    	
    	//select the new query tab
    	SingleSelectionModel<Tab> selectionModel = multiQueryTabPane.getSelectionModel();
    	selectionModel.select(tab); 
    	multiQueryPane.getStyleClass().add("multiQueryPane");
    	multiQueryTabPane.getStyleClass().add("multiQueryTabPane");
    	//limit no of open tabs
    	int tabCount = multiQueryTabPane.getTabs().size();
    	if(tabCount == QUERY_LIMIT) {
    		addTabButton.setDisable(true);
    	}else
    		addTabButton.setDisable(false);
    }
    
    
    public void selectError(ErrorItem errorItem) {//called from MainPresenter.selectedItemProperty()
    	String queryName = errorItem.getSource();
    	//select queryName tab
    	ObservableList<Tab> tabList = multiQueryTabPane.getTabs();
    	for(Tab tab:tabList) {
    		String title = tab.getText();
    		if(title.equals(queryName)) {
    			SingleSelectionModel<Tab> selectionModel = multiQueryTabPane.getSelectionModel();
    			selectionModel.select(tab);
    		}
    	}
    	//find query presenter
    	QueryPresenter queryPresenter = queryPresenterList.stream().filter(p -> p.getQueryName().equals(queryName)).findAny().orElse(null);
    	if(queryPresenter != null) {
    		queryPresenter.selectError(errorItem);
    	}
    }
    
}

