package com.yangui.main.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yangui.main.dao.DeviceDao;
import com.yangui.main.services.model.CapabilityItem;
import com.yangui.main.services.model.DeviceItem;
import com.yangui.main.services.model.ErrorItem;
import com.yangui.main.services.model.ErrorTypeEnum;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



@Singleton
public class DeviceService {
	
	private static final Logger LOG = LoggerFactory.getLogger(DeviceService.class);
	
	@Inject
	private RegexService regexService;
	
	@Inject
	private SchemaService schemaService;
	
	@Inject
	private DeviceDao deviceDao;
	
	@Inject
    LogService logService;
	
	
	private final ObservableList<DeviceItem> deviceList = FXCollections.observableArrayList();//total list of devices
	
	public ObservableList<DeviceItem> deviceObsList() {
		return deviceList;
	}
	
	private final ObservableList<DeviceItem> connectedDeviceList = FXCollections.observableArrayList();//list of connected devices; used to populate ConnectionCombo from QueryPresenter
	
	public ObservableList<DeviceItem> connectedDeviceObsList() {
		return connectedDeviceList;
	}
	
	@PostConstruct
	private void init() throws Exception{
	}
	
	//ErrorTypeEnum typeEnum, String source, String lineNumber, String idCss, String message
	public void refreshDeviceList(){//called from DeviceListService
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();
		LOG.debug("Refreshing device list");
		try {
			//disconnect all devices
			for(DeviceItem device : connectedDeviceList) {
				device.getNetconfConn().close();
			}
			List<DeviceItem> deviceListDb = deviceDao.getDevices();
			Platform.runLater(() -> {
				connectedDeviceList.clear();
				
				deviceList.clear();
				deviceList.addAll(deviceListDb);
				
				deviceList.stream().forEach( device -> device.setError("")); 
			});
			
		}catch (Exception e) {
			LOG.error("", e);
    		errorList.clear();
    		errorList.add(new ErrorItem(ErrorTypeEnum.NETWORK, "Device List", "0", "", "Check Error Window"));
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateAllNetworkErrors(errorList);
				LOG.error("Refreshing device list returned errors");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
			}else {
				logService.clearAllNetworkErrors();
				LOG.debug("Refreshing device list finished ok");
			}
		});
	}
	
	
	public Integer deleteDevice(int ix){
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();
		
		final DeviceItem device = deviceList.get(ix);
		LOG.debug("Deleting device " + device.getIdString());
		try {
			if(device.getId() > 0) {
				deviceDao.deleteDevice(device);
			}
			Platform.runLater(() -> {
				deviceList.remove(ix);
			});
		}catch (Exception e) {
			LOG.error("", e);
    		errorList.clear();
    		errorList.add(new ErrorItem(ErrorTypeEnum.NETWORK, device.getIdString(), "0", "", "Check Error Window"));
		}
		
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateNetworkErrors(errorList, device.getIdString());
				LOG.error("Deleting device " + device.getIdString() + " returned errors");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
				device.setError("Check Error Window");
				deviceList.set(ix, null); // required for refresh
				deviceList.set(ix, device);
			}else {
				logService.clearNetworkErrors(device.getIdString());
				LOG.debug("Deleting device " + device.getIdString() + " finished ok");
				device.setError("");
			}
		});
		return ix;
	}
	
	
	public Integer updateDevice(int ix){
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();
		
		final DeviceItem device = deviceList.get(ix);
		LOG.debug("Updating device " + device.getIdString());
		try {
			//check name non null
			if(device.getName() == null || device.getName().trim().length() == 0) 
	       	    errorList.add(new ErrorItem(ErrorTypeEnum.NETWORK, device.getIdString(), "0", "nameTxt", "Name is empty"));
			//name is unique checked by db
			
			//check host non null
			if(device.getHost() == null || device.getHost().trim().length() == 0) 
				errorList.add(new ErrorItem(ErrorTypeEnum.NETWORK, device.getIdString(), "0", "hostTxt", "Host is empty"));
			
			//port non null
			if(device.getPort() == null || device.getPort().trim().length() == 0) 
				errorList.add(new ErrorItem(ErrorTypeEnum.NETWORK, device.getIdString(), "0", "portTxt", "Port is empty"));
			//username non null
			if(device.getUsername() == null || device.getUsername().trim().length() == 0) 
				errorList.add(new ErrorItem(ErrorTypeEnum.NETWORK, device.getIdString(), "0", "usernameTxt", "Username is empty"));
			
			if(device.getType()) {//key non null based on loginType
				if(device.getKey() == null || device.getKey().trim().length() == 0) 
					errorList.add(new ErrorItem(ErrorTypeEnum.NETWORK, device.getIdString(), "0", "keyTxt", "Key is empty"));
	    	}else {//pass non null
	    		if(device.getPassword() == null || device.getPassword().trim().length() == 0) 
	    			errorList.add(new ErrorItem(ErrorTypeEnum.NETWORK, device.getIdString(), "0", "passwordTxt", "Password is empty"));
	    	}
			
			//update DB
			if(errorList.size() == 0) {
				deviceDao.updateDevice(device);
			}
		}catch (Exception e) {
			LOG.error("", e);
    		errorList.clear();
    		errorList.add(new ErrorItem(ErrorTypeEnum.NETWORK, device.getIdString(), "0", "", "Check Error Window"));
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateNetworkErrors(errorList, device.getIdString());
				LOG.error("Updating device " + device.getIdString() + " returned errors");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
				device.setError("Check Error Window");
			}else {
				logService.clearNetworkErrors(device.getIdString());
				LOG.debug("Updating device " + device.getIdString() + " finished ok");
				device.setError("");
			}
			deviceList.set(ix, null); // required for refresh
			deviceList.set(ix, device);
		});
		return ix;
	}
	
	
	public DeviceItem connectDevice(int ix){
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();
		final DeviceItem device = deviceList.get(ix);
		LOG.debug("Connecting device " + device.getIdString());
		try {
			boolean isConnected = device.getIsConnected();
			if(!isConnected) {
				String host = device.getHost();
				int port = Integer.parseInt(device.getPort());
				String username = device.getUsername();
				boolean isKeyLogin = device.getType();
				String key = null;
				String keyContent = null;
				String password = null;
				if(isKeyLogin) {
					key = device.getKey();
//					Path keyFile = Paths.get(key);
//					keyContent = new String(Files.readAllBytes(keyFile));

				}else
					password = device.getPassword();
					
				device.getNetconfConn().connect(host, username, password, key, port);
				
				String serverCapString = device.getNetconfConn().getServerCapabilities();
				List<CapabilityItem> serverCapList = regexService.parseServerCapabilities(serverCapString);
				serverCapList.sort(Comparator.comparing(CapabilityItem::getNamespace));
				
				Platform.runLater(() -> {
					device.isConnectedProperty().set(true);
					connectedDeviceList.add(device);//update connected
					device.serverCapObsList().clear();
					device.serverCapObsList().addAll(serverCapList);
					updateCapabilitiesMatch(schemaService.clientCapObsList(), device.serverCapObsList());
				});
			}
		} catch (Exception e) {
			LOG.error("", e);
    		errorList.clear();
    		errorList.add(new ErrorItem(ErrorTypeEnum.NETWORK, device.getIdString(), "0", "", "Check Error Window"));
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateNetworkErrors(errorList, device.getIdString());
				LOG.error("Connecting device " + device.getIdString() + " returned errors");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
				device.setError("Check Error Window");
				device.isConnectedProperty().set(false);
				connectedDeviceList.remove(device);//update connected
			}else {
				logService.clearNetworkErrors(device.getIdString());
				LOG.debug("Connecting device " + device.getIdString() + " finished ok");
				device.setError("");
			}
			deviceList.set(ix, null); // required for refresh
			deviceList.set(ix, device);
		});
		return device;
	}
	
	
	public DeviceItem disconnectDevice(int ix){
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();
		final DeviceItem device = deviceList.get(ix);
		LOG.debug("Disconnecting device " + device.getIdString());
		try {
			boolean isConnected = device.getIsConnected();
			if(isConnected) {
				device.getNetconfConn().close();
				Platform.runLater(() -> {
					LOG.debug("Disconnected From Device " + device.getHost());
					device.isConnectedProperty().set(false);
					
					connectedDeviceList.remove(device);//update connected
					//device.serverCapObsList().clear();
					//recompute isMatched on both lists is done in DevicePresenter.ConnectionService.OnAction()
				});
			}
		} catch (Exception e) {
			LOG.error("", e);
    		errorList.clear();
    		errorList.add(new ErrorItem(ErrorTypeEnum.NETWORK, device.getIdString(), "0", "", "Check Error Window"));
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateNetworkErrors(errorList, device.getIdString());
				LOG.error("Disconnecting device " + device.getIdString() + " returned errors");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
				device.setError("Check Error Window");
			}else {
				device.isConnectedProperty().set(false);
				connectedDeviceList.remove(device);//update connected
				logService.clearNetworkErrors(device.getIdString());
				LOG.debug("Disconnecting device " + device.getIdString() + " finished ok");
				device.setError("");
			}
			deviceList.set(ix, null); // required for refresh
			deviceList.set(ix, device);
		});
		return device;
	}
	
	
	 public void updateCapabilitiesMatch(ObservableList<CapabilityItem> clientCapObsList, ObservableList<CapabilityItem> serverCapObsList) {//called from DevicePresente.updateSelection() & this.connectDevice()
			
			for(CapabilityItem capability : serverCapObsList) {
				if(clientCapObsList.contains(capability))
					capability.setIsMatch(true);
				else
					capability.setIsMatch(false);
			}
			for(CapabilityItem capability : clientCapObsList) {
				if(serverCapObsList.contains(capability))
					capability.setIsMatch(true);
				else
					capability.setIsMatch(false);
			}
	    }
	
	
	//-------------------------------------------------------
	public boolean connectAllDevice(){
		//iterate deviceList and call connectDevice(deviceItem)
		return false;
	}
	
	public boolean disconnectAllDevice(){
		return false;
	}
}