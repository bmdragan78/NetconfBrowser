package com.yangui.client;

import java.util.ArrayList;
import java.util.Arrays;
import net.juniper.netconf.Device;
import net.juniper.netconf.XML;



public class NetconfClient {
	
	private Device session;
	
	
	public NetconfClient() {
		super();
	}
	
	
	public static void main(String[] args) throws Exception {
	
		NetconfClient netconfClient = new NetconfClient();
		netconfClient.connect("127.0.0.1","draganb","parola12",null, 830);
		
//		String clientCap = netconfClient.getClientCapabilities();
//	    System.out.println("Client Capabilities = " + clientCap);
	    
		String serverCap = netconfClient.getServerCapabilities();
		System.out.println("Server Capabilities = " + serverCap);
	    
		//netconfClient.keepAlive();
		
	    String rpc_reply = netconfClient.executeRPC("<get></get>");
        System.out.println(rpc_reply);
	    netconfClient.close();
	}

	
	public void connect(String hostName, String userName, String password, String pemKeyFile,  int port) throws Exception {
		session = new Device(hostName, userName, password, pemKeyFile, port);
        session.connect();
	}
	
	
	public void close() throws Exception {
        session.close();
	}
	
	
	public String getServerCapabilities() {
		 return session.getServerCapability();
	}
	
	
	public String getClientCapabilities() {
		ArrayList<String> clientCapabilities = session.getDefaultClientCapabilities(); 
		return Arrays.toString(clientCapabilities.toArray());
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
	
}