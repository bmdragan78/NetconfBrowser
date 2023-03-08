package com.yang.client;

import java.util.ArrayList;
import java.util.Arrays;

import com.yang.client.netconf.Device;
import com.yang.client.netconf.XML;



public class NetconfClient {
	
	private Device session;
	
	
	public NetconfClient() {
		super();
	}
	
	
	public static void main(String[] args) throws Exception {
	
		NetconfClient netconfClient = new NetconfClient();
		netconfClient.connect("127.0.0.1","admin","admin",null, 2830);
		
//		String clientCap = netconfClient.getClientCapabilities();
//	    System.out.println("Client Capabilities = " + clientCap);
	    
		String serverCap = netconfClient.getServerCapabilities();
		System.out.println("Server Capabilities = " + serverCap);
	    
		//netconfClient.keepAlive();
		
	    String rpc_reply = netconfClient.executeRPC("<get></get>");
        System.out.println("Server Rpcreply = " +rpc_reply);
	    netconfClient.close();
	}

	
	public void connect(String hostName, String userName, String password, String pemKeyFile,  int port) throws Exception {
//		session = new Device(hostName, userName, password, pemKeyFile, port);
//        session.connect();
        
        session = Device.builder()
                .hostName(hostName)
                .port(port)
                .userName(userName)
                .password(password)
                //.hostKeysFileName("hostKeysFileName") add pemKeyFile  ?????
                .strictHostKeyChecking(false)
                .build(); 
        session.connect();
	}
	
	
	public void close() throws Exception {
        session.close();
	}
	
	
	public String getServerCapabilities() {
		//return session.getServerCapability();
		return session.getNetconfSession().getServerCapability();
	}
	
	
	public String getClientCapabilities() { //????????
//		ArrayList<String> clientCapabilities = session.getDefaultClientCapabilities();
//		return Arrays.toString(clientCapabilities.toArray());
		
		return "";
	}

	
	public String executeRPC(String input) throws Exception {
		XML rpc_reply = session.executeRPC(input);
		return rpc_reply.toString();
	}
	
	
	public void keepAlive() throws Exception {
		//String input = "<get><filter type=\"subtree\"> </filter></get>";
		String input = "<get></get>";
		XML rpc_reply = session.executeRPC(input);
		return;
	}
	
	public String executeRawRPC(String input) throws Exception {//added by me
		XML rpc_reply = session.executeRawRPC(input);
		return rpc_reply.toString();
	}
	
}