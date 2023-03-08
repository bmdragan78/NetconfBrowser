package com.yang.ui.connection;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;

import com.yang.ui.services.DeviceService;
import com.yang.ui.services.QueryService;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;



public class ConnectionPresenter implements Initializable {
	
	@FXML
    HBox connectionPane;
    
    @FXML
    Circle lightCircle;
    
    @FXML
    Label connectedCount;
    
    @FXML
    Label totalCount;
    
    @Inject
    DeviceService deviceSrv;
    
    @Inject
    QueryService querySrv;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	
    	IntegerBinding totalProperty = Bindings.size(deviceSrv.deviceObsList());
    	totalCount.textProperty().bind(totalProperty.asString());
    	
    	IntegerBinding connectedProperty = Bindings.size(deviceSrv.connectedDeviceObsList());
    	connectedCount.textProperty().bind(connectedProperty.asString());
    	
    	lightCircle.fillProperty().bind(Bindings.createObjectBinding(() -> {
	    	if(connectedProperty.get() > 0)
	    		return Color.valueOf("#00b7c3");
	    	else
	    		return Color.valueOf("#e74856");
	    	}, connectedProperty));
    }
    
}