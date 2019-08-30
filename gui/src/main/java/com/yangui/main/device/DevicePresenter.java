package com.yangui.main.device;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRadioButton;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yangui.gui.App;
import com.yangui.main.services.DeviceService;
import com.yangui.main.services.FileService;
import com.yangui.main.services.LogService;
import com.yangui.main.services.SchemaService;
import com.yangui.main.services.model.CapabilityItem;
import com.yangui.main.services.model.DeviceItem;
import com.yangui.main.services.model.ErrorItem;



public class DevicePresenter implements Initializable {
	
	private static final Logger LOG = LoggerFactory.getLogger(DevicePresenter.class);
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////Device List Pane
    @FXML
    SplitPane devicePane;
    
    @FXML
    TableView<DeviceItem> deviceTableView;
    
    @FXML
    JFXButton toggleConnectBtn;
    
    @FXML
    JFXButton disconnectBtn;
    
    @FXML
    JFXButton addBtn;
    
    @FXML
    JFXButton deleteBtn;
    
    @FXML
    JFXButton refreshBtn;
    
//    @FXML
//    TextField filterText;
//    
//    @FXML
//	JFXButton sortBtn;
    
    @FXML
	JFXButton deviceSaveBtn;
    
    
    private BooleanProperty connectedProperty = new SimpleBooleanProperty(false);
	public BooleanProperty getConnectedProperty() {
		return connectedProperty;
	}
    /////////////////////////////////////////////////////////////////////////////////////////////////////Device Detail Pane
    @FXML
    GridPane deviceDetailPane;
    
    @FXML
   	TextField nameTxt;
    
    @FXML
	TextField hostTxt;
    
    @FXML
	TextField portTxt;
    
    @FXML
	TextField usernameTxt;
    
    private ToggleGroup group = new ToggleGroup();
     
    @FXML
    JFXRadioButton loginPassRadio;
    
    @FXML
    JFXRadioButton loginKeyRadio;
 
    
    @FXML
    PasswordField passwordTxt;
    
    @FXML
	TextField keyTxt;
    
    @FXML
    JFXButton browseBtn;
    
    @FXML
    HBox capabilitiesPane;
    
    @FXML
    Label capLabel;
    
    
    @FXML
    JFXListView<CapabilityItem> clientCapList;
    
    @FXML
    JFXListView<CapabilityItem> serverCapList;
    
    @Inject
    DeviceService deviceService;
    
    @Inject
    FileService fileService;
    
    @Inject
    SchemaService schemaService;
    
    @Inject
    LogService logService;
    
    private DeviceListService deviceListSrv;
    
    private DeviceUpdateService deviceUpdateSrv;
    
    private DeviceDeleteService deviceDeleteSrv;
    
    private DeviceConnectService deviceConnectSrv;
    
    private DeviceDisconnectService deviceDisconnectSrv;
    
    private final FileChooser fileChooser = new FileChooser();
    
    private DeviceItem lastSelectedDevice;
    
    //error styling
    private FilteredList<ErrorItem> nameTxtErrors;
    private IntegerBinding nameTxtErrorsSize;
    
    private FilteredList<ErrorItem> hostTxtErrors;
    private IntegerBinding hostTxtErrorsSize;
    
    private FilteredList<ErrorItem> portTxtErrors;
    private IntegerBinding portTxtErrorsSize;
    
    private FilteredList<ErrorItem> usernameTxtErrors;
    private IntegerBinding usernameTxtErrorsSize;
    
    private FilteredList<ErrorItem> keyTxtErrors;
    private IntegerBinding keyTxtErrorsSize;
    
    private FilteredList<ErrorItem> passwordTxtErrors;
    private IntegerBinding passwordTxtErrorsSize;
    
    
    
    static class CapabilityCell extends ListCell<CapabilityItem> {
        @Override
        public void updateItem(CapabilityItem item, boolean empty) {
            super.updateItem(item, empty);
            
            if (item == null || empty) {
                setText(null);
                setStyle("-fx-control-inner-background: #BDE5EA;");
            } else {
                setText(item.getNamespace());
                if(item.getIsMatch()) {
                	setStyle("-fx-control-inner-background: #BDE5EA;");
                } else {
                	setStyle("-fx-control-inner-background: #F0E5C3;");
                }
            }
        }
    }
    

    @Override
    public void initialize(URL location, ResourceBundle rb) {
    	try {
	    	capLabel.setText("Client/Server"+ System.lineSeparator() + " Capabilities");
	    	deviceDetailPane.getStyleClass().add("gridPane");
	    	//deviceDetailPane.setGridLinesVisible(true);
	    		
	    	connectedProperty.addListener((observable, oldIsConnected, newIsConnected) -> {
	    			
	    		Circle graphic = (Circle)toggleConnectBtn.getGraphic();
	    			
	            if (newIsConnected) {
	                toggleConnectBtn.setText("DISCONNECT");
	                graphic.setFill(Color.valueOf("#e74856"));
	                toggleConnectBtn.setStyle("-fx-border-color: #e74856;");
	            }else {
	                toggleConnectBtn.setText("  CONNECT   ");
	                graphic.setFill(Color.valueOf("#00b7c3"));
	                toggleConnectBtn.setStyle("-fx-border-color: #00b7c3;");
	            }
	    	});
	    	connectedProperty.set(true);
	    	connectedProperty.set(false);
	    		
	    	//login type control
	    	loginPassRadio.setToggleGroup(group);
	    	loginPassRadio.selectedProperty().addListener(new InvalidationListener() {
	    		public void invalidated(Observable ov) {
	    			if (loginPassRadio.isSelected()) { 
	    	            keyTxt.setDisable(true);
	    	            browseBtn.setDisable(true);
	    	           	passwordTxt.setDisable(false);
	    	           		
	    	           	deviceTableView.getSelectionModel().getSelectedItem().setType(false);
	    	        }
	    	    }
	    	});
	    	loginKeyRadio.setToggleGroup(group);
	    	loginKeyRadio.selectedProperty().addListener(new InvalidationListener() {
	    		public void invalidated(Observable ov) {
	    			if (loginKeyRadio.isSelected()) {
	    	            keyTxt.setDisable(false);
	    	            browseBtn.setDisable(false);
	    	           	passwordTxt.setDisable(true);
	    	           		
	    	           	deviceTableView.getSelectionModel().getSelectedItem().setType(true);
	    	         }
	    	    }
	    	});
	    		
	    	//add device list columns
	    	TableColumn<DeviceItem, Integer> idCol = new TableColumn<DeviceItem, Integer>("Id");
	    	idCol.setPrefWidth(90);
	    	TableColumn<DeviceItem, String> nameCol = new TableColumn<DeviceItem, String>("Name");
		    nameCol.setPrefWidth(180);
	        TableColumn<DeviceItem, String> ipCol = new TableColumn<DeviceItem, String>("Host");
	        ipCol.setPrefWidth(180);
	        TableColumn<DeviceItem, Integer> portCol = new TableColumn<DeviceItem, Integer>("Port");
	        portCol.setPrefWidth(100);
	        TableColumn<DeviceItem, String> usernameCol = new TableColumn<DeviceItem, String>("Username");
	        usernameCol.setPrefWidth(180);
	        TableColumn<DeviceItem, Boolean> connectedCol = new TableColumn<DeviceItem, Boolean>("Status");
	        connectedCol.setPrefWidth(100);
	            
//	            TableColumn<DeviceItem, String> errorCol = new TableColumn<DeviceItem, String>("");
//	            errorCol.setPrefWidth(320);
	            
	        idCol.setCellValueFactory(new PropertyValueFactory<DeviceItem, Integer>("id"));
	        nameCol.setCellValueFactory(new PropertyValueFactory<DeviceItem, String>("name"));
	        ipCol.setCellValueFactory(new PropertyValueFactory<DeviceItem, String>("host"));
	        usernameCol.setCellValueFactory(new PropertyValueFactory<DeviceItem, String>("username"));
	        portCol.setCellValueFactory(new PropertyValueFactory<DeviceItem, Integer>("port"));
	        connectedCol.setCellValueFactory(new PropertyValueFactory<DeviceItem, Boolean>("isConnected"));
	        //errorCol.setCellValueFactory(new PropertyValueFactory<DeviceItem, String>("error"));
	            
	        connectedCol.setCellFactory(column -> {
	        	return new TableCell<DeviceItem, Boolean>() {
	        		@Override
	                protected void updateItem(Boolean isConnected, boolean empty) {
	        			super.updateItem(isConnected, empty);
	
	                    setText(null);
	                    if (isConnected == null || empty) {
	                    	setStyle("");
	                        setGraphic(null);
	                    } else {
	                    	Circle statusCircle = new Circle();
	                        statusCircle.setRadius(5);
	                        setGraphic(statusCircle);
	                        if(isConnected == true) {
	                        	statusCircle.setFill(Color.valueOf("#00b7c3"));
	                        }else {
	                        	statusCircle.setFill(Color.valueOf("#e74856"));
	                        }
	                    }
	                }
	            };
	        });
	        deviceTableView.getColumns().addAll(idCol, nameCol, ipCol, portCol, usernameCol, connectedCol);
	        deviceTableView.setEditable(true);
	            
	        //populate device detail from device list selection
			deviceTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//			deviceTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection)// IT DOESN'T REALLY WORK
	        deviceTableView.setRowFactory(new Callback<TableView<DeviceItem>, TableRow<DeviceItem>>() {
	        	@Override
	            public TableRow<DeviceItem> call(TableView<DeviceItem> tableView) {
	        		final TableRow<DeviceItem> row = new TableRow<DeviceItem>() {
	        			@Override
	                    protected void updateItem(DeviceItem device, boolean empty){
	        				super.updateItem(device, empty);
	                        if(device != null && device.getError() != null && device.getError().trim().length() > 0)
	                        	getStyleClass().add("highlightedRow");
	        	            else
	        	            	getStyleClass().remove("highlightedRow");
	                    }
	                };
	                row.setOnMouseClicked(event -> {
	                	if (!row.isEmpty() && event.getButton()==MouseButton.PRIMARY  && event.getClickCount() == 1) {
		                    DeviceItem deviceItem = row.getItem();
		                    updateSelection(deviceItem);
		                }
		            });
	                return row;
	        	}
	        });

			//All the connections share the same set of client capabilities read from local SchemaService
	    	clientCapList.setItems(schemaService.clientCapObsList());
	    	clientCapList.refresh();
	    		
	    	clientCapList.setCellFactory(c -> new CapabilityCell());
	    	serverCapList.setCellFactory(c -> new CapabilityCell());
	    		
	    	//device connect service
	        deviceConnectSrv = new DeviceConnectService();
	        deviceConnectSrv.setOnSucceeded((WorkerStateEvent t) ->{
	        	DeviceItem device = (DeviceItem) t.getSource().getValue();
	        	//LOG.debug("DeviceConnectService Succesfuly ix = " + device.getHost());
	    		clientCapList.refresh();
	        });
	        deviceConnectSrv.setOnFailed((WorkerStateEvent t) -> {
	    		LOG.error("DeviceConnectService Failed: " + deviceUpdateSrv.getException());
	        });
	            
	        //device disconnect service
	        deviceDisconnectSrv = new DeviceDisconnectService();
	        deviceDisconnectSrv.setOnSucceeded((WorkerStateEvent t) ->{
	    		DeviceItem device = (DeviceItem) t.getSource().getValue();
	    		//LOG.debug("DeviceDisconnectService Succesfuly ix = " + device.getHost());
	        });
	        deviceDisconnectSrv.setOnFailed((WorkerStateEvent t) -> {
	    		LOG.error("DeviceDisconnectService Failed: " + deviceUpdateSrv.getException());
	        });
				
	    	//device update service
	        deviceUpdateSrv = new DeviceUpdateService();
	        deviceUpdateSrv.setOnSucceeded((WorkerStateEvent t) ->{
	        	//LOG.debug("DeviceUpdateService Succesfuly ix = " + (Integer) t.getSource().getValue());
	        	deviceTableView.getSelectionModel().clearAndSelect((Integer) t.getSource().getValue());//reselect updated device
	    		deviceTableView.requestFocus();
	    		//updateSelection(deviceTableView.getSelectionModel().getSelectedItem());
	        });
	        deviceUpdateSrv.setOnFailed((WorkerStateEvent t) -> {
	    		LOG.error("DeviceUpdateService Failed: " + deviceUpdateSrv.getException());
	        });
	            
	        //device delete service
	        deviceDeleteSrv = new DeviceDeleteService();
	        deviceDeleteSrv.setOnSucceeded((WorkerStateEvent t) ->{
	        	//LOG.debug("DeviceDeleteService Succesfuly ix = " + (Integer) t.getSource().getValue());
	        });
	        deviceDeleteSrv.setOnFailed((WorkerStateEvent t) -> {
	    		LOG.error("DeviceDeleteService Failed: " + deviceDeleteSrv.getException());
	        });
	            
	        //device loader service
	        deviceListSrv = new DeviceListService();
	        deviceListSrv.setOnSucceeded((WorkerStateEvent t) ->{
	        	//LOG.debug("DeviceListService Succesfuly " + deviceService.deviceObsList().size());
	            deviceTableView.setItems(deviceService.deviceObsList());
	            	
	    		if(deviceService.deviceObsList().size() == 0)
	    			addDeviceAction(null);
	    		else {
	    			deviceTableView.getSelectionModel().clearAndSelect(0);
	    			updateSelection(deviceTableView.getSelectionModel().getSelectedItem());
	    		}
	        });
	        deviceListSrv.setOnFailed((WorkerStateEvent t) -> {
	    		LOG.error("DeviceListService Failed: " + deviceListSrv.getException());
	        });
	        deviceListSrv.restart();
	            
	        Platform.runLater(() -> {
		    	//disable buttons until service is ready
	        	BooleanBinding isRunning = deviceListSrv.runningProperty().or(deviceUpdateSrv.runningProperty()).or(deviceDeleteSrv.runningProperty()).or(deviceConnectSrv.runningProperty()).or(deviceDisconnectSrv.runningProperty()); 
		    	
				toggleConnectBtn.disableProperty().bind(isRunning);
				addBtn.disableProperty().bind(isRunning);
				deleteBtn.disableProperty().bind(isRunning);
				refreshBtn.disableProperty().bind(isRunning);
	        });
	        
	        //nameTxt
			nameTxtErrors = new FilteredList<ErrorItem>(logService.errorObsList());
			nameTxtErrorsSize = Bindings.size(nameTxtErrors);
			nameTxtErrorsSize.addListener((o, oldValue, newValue) -> {
	        	int size = (Integer)newValue.intValue();
	        	if(size > 0) 
	        		nameTxt.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
	        	else 
	        		nameTxt.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;");
	        });
			//hostTxt
			hostTxtErrors = new FilteredList<ErrorItem>(logService.errorObsList());
			hostTxtErrorsSize = Bindings.size(hostTxtErrors);
			hostTxtErrorsSize.addListener((o, oldValue, newValue) -> {
	        	int size = (Integer)newValue.intValue();
	        	if(size > 0) 
	        		hostTxt.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
	        	else 
	        		hostTxt.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;");
	        });
			//portTxt
			portTxtErrors = new FilteredList<ErrorItem>(logService.errorObsList());
			portTxtErrorsSize = Bindings.size(portTxtErrors);
			portTxtErrorsSize.addListener((o, oldValue, newValue) -> {
	        	int size = (Integer)newValue.intValue();
	        	if(size > 0) 
	        		portTxt.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
	        	else 
	        		portTxt.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;");
	        });
			//usernameTxt
			usernameTxtErrors = new FilteredList<ErrorItem>(logService.errorObsList());
			usernameTxtErrorsSize = Bindings.size(usernameTxtErrors);
			usernameTxtErrorsSize.addListener((o, oldValue, newValue) -> {
	        	int size = (Integer)newValue.intValue();
	        	if(size > 0) 
	        		usernameTxt.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
	        	else 
	        		usernameTxt.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;");
	        });
			//keyTxt
			keyTxtErrors = new FilteredList<ErrorItem>(logService.errorObsList());
			keyTxtErrorsSize = Bindings.size(keyTxtErrors);
			keyTxtErrorsSize.addListener((o, oldValue, newValue) -> {
	        	int size = (Integer)newValue.intValue();
	        	if(size > 0) 
	        		keyTxt.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
	        	else 
	        		keyTxt.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;");
	        });
			//passwordTxt
			passwordTxtErrors = new FilteredList<ErrorItem>(logService.errorObsList());
			passwordTxtErrorsSize = Bindings.size(passwordTxtErrors);
			passwordTxtErrorsSize.addListener((o, oldValue, newValue) -> {
	        	int size = (Integer)newValue.intValue();
	        	if(size > 0) 
	        		passwordTxt.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
	        	else 
	        		passwordTxt.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;");
	        });
	        
    	}catch(Exception ex) {
    		LOG.error("Device initialization exception : ", ex);
    	}
    }
    
    
    private void updateSelection(DeviceItem selectedDevice) {//called from selectError() & addDeviceAction() & row.setOnMouseClicked()
	    if(lastSelectedDevice != null) {
			nameTxt.textProperty().unbindBidirectional(lastSelectedDevice.nameProperty());
	    	hostTxt.textProperty().unbindBidirectional(lastSelectedDevice.hostProperty());
	    	portTxt.textProperty().unbindBidirectional(lastSelectedDevice.portProperty());
	    	usernameTxt.textProperty().unbindBidirectional(lastSelectedDevice.usernameProperty());
	    	passwordTxt.textProperty().unbindBidirectional(lastSelectedDevice.passwordProperty());
	    	keyTxt.textProperty().unbindBidirectional(lastSelectedDevice.keyProperty());
	    	//toggleConnectBtn.disableProperty().unbind();
	    	connectedProperty.unbind();
	    	
		}
		lastSelectedDevice = selectedDevice;
		if(selectedDevice != null) {
			nameTxt.textProperty().bindBidirectional(selectedDevice.nameProperty());
			hostTxt.textProperty().bindBidirectional(selectedDevice.hostProperty());
			portTxt.textProperty().bindBidirectional(selectedDevice.portProperty());
			usernameTxt.textProperty().bindBidirectional(selectedDevice.usernameProperty());
			passwordTxt.textProperty().bindBidirectional(selectedDevice.passwordProperty());
			keyTxt.textProperty().bindBidirectional(selectedDevice.keyProperty());
			if(selectedDevice.getType()) {//key
				loginPassRadio.setSelected(false);
				loginKeyRadio.setSelected(true);
			}else {//pass
				loginPassRadio.setSelected(true);
				loginKeyRadio.setSelected(false);
			}
			//toggleConnectBtn.disableProperty().bind(selectedDevice.idProperty().isEqualTo(0));
			connectedProperty.bind(selectedDevice.isConnectedProperty());
			//recompute isMatched on both lists
			serverCapList.setItems(selectedDevice.serverCapObsList());
			serverCapList.refresh();
			deviceService.updateCapabilitiesMatch(schemaService.clientCapObsList(), selectedDevice.serverCapObsList());
			clientCapList.setItems(schemaService.clientCapObsList());
			clientCapList.refresh();

			nameTxtErrors.setPredicate(errorItem -> errorItem.getSource().equals(selectedDevice.getIdString()) && errorItem.getIdCss().equals("nameTxt"));
			hostTxtErrors.setPredicate(errorItem -> errorItem.getSource().equals(selectedDevice.getIdString()) && errorItem.getIdCss().equals("hostTxt"));
			portTxtErrors.setPredicate(errorItem -> errorItem.getSource().equals(selectedDevice.getIdString()) && errorItem.getIdCss().equals("portTxt"));
			usernameTxtErrors.setPredicate(errorItem -> errorItem.getSource().equals(selectedDevice.getIdString()) && errorItem.getIdCss().equals("usernameTxt"));
			keyTxtErrors.setPredicate(errorItem -> errorItem.getSource().equals(selectedDevice.getIdString()) && errorItem.getIdCss().equals("keyTxt"));
			passwordTxtErrors.setPredicate(errorItem -> errorItem.getSource().equals(selectedDevice.getIdString()) && errorItem.getIdCss().equals("passwordTxt"));
		}
		nameTxt.requestFocus();
    }
    
    
    public void selectError(ErrorItem errorItem) {//called from MainPresenter.selectedItemProperty()
    	String deviceId = errorItem.getSource();
    	
    	//select device row
    	List<DeviceItem> deviceItemList = deviceService.deviceObsList().stream().filter(device -> device.getIdString().equals(deviceId)).collect(Collectors.toList());  
        if(deviceItemList.size() == 1) {
        	DeviceItem targetDevice = deviceItemList.get(0);
        	deviceTableView.getSelectionModel().select(targetDevice);
        	updateSelection(deviceTableView.getSelectionModel().getSelectedItem());
//        	String idCss = errorItem.getIdCss();//request focus
//        	Node targetNode = App.MAIN_STAGE.getScene().lookup("#" + idCss);
//        	if(targetNode != null)
//        		targetNode.requestFocus();
        }
    }
	
    
    @FXML
    protected void browseAction(ActionEvent event) {
    	File file = fileChooser.showOpenDialog((Stage) App.MAIN_STAGE);
        if (file != null) {
            //openFile(file);
        	keyTxt.setText(file.getAbsolutePath());
        }
    }
    
    @FXML
    protected void toggleConnectAction(ActionEvent event) {
    	int ix = deviceTableView.getSelectionModel().getSelectedIndex();
        if (ix < 0) {
            return;
        }
        if(connectedProperty.get()) {
        	 deviceDisconnectSrv.setIx(ix);
             deviceDisconnectSrv.restart();
        }else {
        	deviceConnectSrv.setIx(ix);
            deviceConnectSrv.restart();
        }
    }
    
    
    @FXML
    protected void refreshAction(ActionEvent event) {
    	if(deviceService.connectedDeviceObsList().size() > 0) {
	    	Alert alert = new Alert(AlertType.CONFIRMATION);
	    	alert.setTitle("Close All Device Connections Confirmation");
	    	alert.setHeaderText("");
	    	alert.setContentText("This will close all active devices.Are you sure you want to proceed ?");
	    	
	    	Optional<ButtonType> result = alert.showAndWait();
	    	if (result.get() == ButtonType.OK){
	    		deviceListSrv.restart();
	    	}
    	}else {
    		deviceListSrv.restart();
    	}
    }
    
    
    @FXML
    protected void addDeviceAction(ActionEvent event) {
    	//prevent add device if there is already a device with id=0; since there is no refresh we cannot have multiple new devices -> only one
    	OptionalInt indexOpt = IntStream.range(0, deviceService.deviceObsList().size())
    		     .filter(i -> deviceService.deviceObsList().get(i).getId() == 0)
    		     .findFirst();
    	if(indexOpt.isPresent()) {
    		deviceTableView.getSelectionModel().clearAndSelect(indexOpt.getAsInt());
    		return;
    	}
    	//DeviceItem device = new DeviceItem(0, "Conn1", "127.0.0.1", 830, "draganb", false, "parola12", "", false);//revert to defaults after DEV !!!
    	DeviceItem device = new DeviceItem(0, "", "", 830, "", false, "", "", false);
    	int ix = 0;
    	
    	 Platform.runLater(() -> {
	    	deviceService.deviceObsList().add(ix, device);
	    	deviceTableView.getSelectionModel().clearAndSelect(ix);
	    	updateSelection(deviceTableView.getSelectionModel().getSelectedItem());
    	 });
    }
    
    
    @FXML
    protected void deleteDeviceAction(ActionEvent event) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Delete Device Confirmation");
    	alert.setHeaderText("");
    	alert.setContentText("Are you sure you want to delete device connection ?");
    	
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		int ix = deviceTableView.getSelectionModel().getSelectedIndex();
            if (ix < 0) {
                return;
            }
            deviceDeleteSrv.setIx(ix);
        	deviceDeleteSrv.restart();
    	} 
    }
    
    
    @FXML
    protected void saveAction(ActionEvent event) {
    	int ix = deviceTableView.getSelectionModel().getSelectedIndex();
        if (ix < 0) {
            return;
        }
        //db uppdate
    	deviceUpdateSrv.setIx(ix);
    	deviceUpdateSrv.restart();
    }
    
    
    private final class DeviceListService extends Service<Void> {
    	
    	public DeviceListService() {
			super();
	    }	
    	
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                	
                	deviceService.refreshDeviceList();
                	return null;
                }
            };
        }
   }
    
    

    private final class DeviceDeleteService extends Service<Integer> {
    	
    	private int ix;
    	
		public void setIx(int ix) {
			this.ix = ix;
		}

		public DeviceDeleteService() {
			super();
		}

		@Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                	
                	return deviceService.deleteDevice(ix);
                }
            };
        }
   }

    
    private final class DeviceUpdateService extends Service<Integer> {
    	
    	private int ix;
    	
		public void setIx(int ix) {
			this.ix = ix;
		}

		public DeviceUpdateService() {
			super();
		}

		@Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                	
                	return deviceService.updateDevice(ix);
                }
            };
        }
   }
    
    private final class DeviceConnectService extends Service<DeviceItem> {
    	
    	private int ix;
    	
		public void setIx(int ix) {
			this.ix = ix;
		}

		public DeviceConnectService() {
			super();
		}

		@Override
        protected Task<DeviceItem> createTask() {
            return new Task<DeviceItem>() {
                @Override
                protected DeviceItem call() throws Exception {
                	return deviceService.connectDevice(ix);
                }
            };
        }
   }
    
    private final class DeviceDisconnectService extends Service<DeviceItem> {
    	
    	private int ix;
    	
		public void setIx(int ix) {
			this.ix = ix;
		}

		public DeviceDisconnectService() {
			super();
		}

		@Override
        protected Task<DeviceItem> createTask() {
            return new Task<DeviceItem>() {
                @Override
                protected DeviceItem call() throws Exception {
                	return deviceService.disconnectDevice(ix);
                }
            };
        }
   }
    

}
