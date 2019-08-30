package com.yangui.main.multiquery.query.base;

import com.jfoenix.controls.JFXButton;
import com.yangui.gui.App;
import com.yangui.main.multiquery.query.QueryPresenter;
import com.yangui.main.multiquery.query.xpathselector.XpathSelectorPresenter;
import com.yangui.main.multiquery.query.xpathselector.XpathSelectorView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



public class BaseopPresenter {
	
	protected QueryPresenter queryPresenter;
	
	//xPath selector dialog
    private Stage xPathSelectorDlg;
    private XpathSelectorView xPathSelectorView;
	private XpathSelectorPresenter xPathSelectorPresenter;
	private Parent xPathSelectorPane;
	
	public BaseopPresenter() {
		super();
	}

	public void setQueryPresenter(QueryPresenter queryPresenter) {
		this.queryPresenter = queryPresenter;
	}
	
	protected ObservableList<String> getTargetList() {
        ObservableList<String> targetList = FXCollections.observableArrayList();
        targetList.add("<running/>");
        targetList.add("<candidate/>");
        targetList.add("<startup/>");
        return targetList;
    }
	
	protected ObservableList<String> getSourceList() {
        ObservableList<String> sourceList = FXCollections.observableArrayList();
        sourceList.add("<running/>");
        sourceList.add("<candidate/>");
        sourceList.add("<startup/>");
        return sourceList;
    }
	
	@FXML
	protected void filterAction(ActionEvent event) {//called from FXML action from editconfigop.fxml, getconfigop.fxml, getop.fxml, rcpop.fxml, notop.fxml
		
		if(event.getSource() != null && event.getSource() instanceof JFXButton) {
			JFXButton evSource = (JFXButton) event.getSource();
			String sourceBtnId = evSource.getId();
			queryPresenter.setSourceBtnId(sourceBtnId);//rpcBtn, filterBtn, configBtn
		}
		
    	xPathSelectorDlg = new Stage();
    	xPathSelectorDlg.setTitle("XPath Selector");
    	xPathSelectorDlg.initModality(Modality.APPLICATION_MODAL);
    	xPathSelectorDlg.initStyle(StageStyle.UTILITY);
    	xPathSelectorDlg.initOwner((Stage) App.MAIN_STAGE);
    	xPathSelectorDlg.setResizable(true);
        
        xPathSelectorView = new XpathSelectorView();
        xPathSelectorPresenter = (XpathSelectorPresenter) xPathSelectorView.getPresenter();
        xPathSelectorPresenter.setQueryPresenter(queryPresenter);
        xPathSelectorPane = xPathSelectorView.getView();

        Scene dialogScene = new Scene(xPathSelectorPane, App.XPATHSEL_DLG_WIDTH, App.XPATHSEL_DLG_HEIGHT);
        //dialogScene.getStylesheets().add("//style sheet of your choice");
        xPathSelectorDlg.setScene(dialogScene);
        xPathSelectorDlg.show();	
	}
	


}
