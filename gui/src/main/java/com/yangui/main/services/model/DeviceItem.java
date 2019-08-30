package com.yangui.main.services.model;

import com.yangui.client.NetconfClient;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



public class DeviceItem {
	
	@Override
	public String toString() {
		return "id " + id.get() + "		name " + name.get() + "		host " + host.get() + "  error " + error.get();
	}


	
	private final SimpleIntegerProperty id;//set by the DB >0 after save
	
	private final SimpleStringProperty name;
	
	private final SimpleStringProperty host;
	
	private final SimpleStringProperty port;
	
	private final SimpleStringProperty username;
	
	private final SimpleBooleanProperty type;//key=true or pass=false
	
	private final SimpleStringProperty password;
	
	private final SimpleStringProperty key;//content of .pem file; 	if (key != null) keyBasedAuthentication = true;
	
	private final SimpleBooleanProperty isConnected;
	
	private SimpleStringProperty error;
	
//	private final SimpleStringProperty clientCapabilities;//might be different for the same server it you tweak advertised client capabilities from .ch2 API
//	
//	private final SimpleStringProperty serverCapabilities;
	
	//server capabilities
	private final ObservableList<CapabilityItem> serverCapObsList = FXCollections.observableArrayList();//set on each connect
	public ObservableList<CapabilityItem> serverCapObsList() {
		return serverCapObsList;
	}
	
	//transient
	private NetconfClient netconfConn;
	
	public DeviceItem(int id, String name, String host, int port, String username, boolean type, String password, String key, boolean isConnected) {//called only once from DevicePresenter.addDeviceAction()
		super();
		this.id = new SimpleIntegerProperty(id);
		this.name = new SimpleStringProperty(name);
		this.host = new SimpleStringProperty(host);
		this.port = new SimpleStringProperty(port > 0 ? port+"" : "");
		this.username = new SimpleStringProperty(username);
		this.type = new SimpleBooleanProperty(type);
		this.password = new SimpleStringProperty(password);
		this.key = new SimpleStringProperty(key);
		this.isConnected = new SimpleBooleanProperty(isConnected);
		this.error = new SimpleStringProperty("");;
		
		this.netconfConn = new NetconfClient();
	}
	
	
	public NetconfClient getNetconfConn() {
		return netconfConn;
	}

	public SimpleIntegerProperty idProperty() {
		return id;
	}
	public int getId() {
		return id.get();
	}
	public String getIdString() {
		return String.valueOf(id.get());
	}
	public void setId(int id) {
		this.id.set(id);
	}
	
	public SimpleStringProperty errorProperty() {
		return error;
	}
	public String getError() {
		return error.get();
	}
	public void setError(String error) {
		this.error.set(null);
		this.error.set(error);
	}
	
	public SimpleStringProperty nameProperty() {
		return name;
	}
	public String getName() {
		return name.get();
	}
	public void setName(String name) {
		this.name.set(name);
	}
	
	public SimpleStringProperty hostProperty() {
		return host;
	}
	public String getHost() {
		return host.get();
	}
	public void setHost(String host) {
		this.host.set(host);
	}
	
	public SimpleStringProperty portProperty() {
		return port;
	}
	public String getPort() {
		return port.get();
	}
	public void setPort(String port) {
		this.port.set(port);
	}
	
	public SimpleStringProperty usernameProperty() {
		return username;
	}
	public String getUsername() {
		return username.get();
	}
	public void setUsername(String username) {
		this.username.set(username);
	}
	
	public SimpleBooleanProperty typeProperty() {
		return type;
	}
	public boolean getType() {
		return type.get();
	}
	public void setType(boolean type) {
		this.type.set(type);
	}
	
	public SimpleStringProperty passwordProperty() {
		return password;
	}
	public String getPassword() {
		return password.get();
	}
	public void setPassword(String password) {
		this.password.set(password);
	}
	
	public SimpleStringProperty keyProperty() {
		return key;
	}
	public String getKey() {
		return key.get();
	}
	public void setKey(String key) {
		this.key.set(key);
	}
	
	public SimpleBooleanProperty isConnectedProperty() {
		return isConnected;
	}
	public boolean getIsConnected() {
		return isConnected.get();
	}
	public void setIsConnected(boolean isConnected) {
		this.isConnected.set(isConnected);
	}
	
//	public SimpleStringProperty clientCapabilitiesPorperty() {
//		return clientCapabilities;
//	}
//	public String getClientCapabilities() {
//		return clientCapabilities.get();
//	}
//	public void setClientCapabilities(String clientCapabilities) {
//		this.clientCapabilities.set(clientCapabilities);
//	}
//
//	public SimpleStringProperty serverCapabilitiesProperty() {
//		return serverCapabilities;
//	}
//	public String getServerCapabilities() {
//		return serverCapabilities.get();
//	}
//	public void setServerCapabilities(String serverCapabilities) {
//		this.serverCapabilities.set(serverCapabilities);
//	}
}
