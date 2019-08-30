package com.yangui.main.services;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yangui.main.services.model.DeviceItem;
import com.yangui.main.services.model.ErrorItem;
import com.yangui.main.services.model.ErrorTypeEnum;
import javafx.application.Platform;


@Singleton
public class QueryService extends TimerTask {
	
	private static final Logger LOG = LoggerFactory.getLogger(QueryService.class);
	
	@Inject
	SchemaService schemaSrv;
	
	@Inject
	private XmlService xmlSrv;
	
	@Inject
	private RegexService regexSrv;
	
	@Inject
	private LogService logService;
	
	
	
	@PostConstruct
	private void init() throws Exception{
		//yangClient = new NetconfClient();
//		long period = 0L;
//		try {
//			period = Conf.KEEPALIVE_PERIOD;
//		}catch( NumberFormatException ex) {
//			LOG.error("Error: keepAlivePeriod setting is not a number.Check properties file");
//			throw ex;
//		}
//		if( period > 0) {
//			Timer timer = new Timer(true);
//	        timer.scheduleAtFixedRate(this, period, period);
//	        LOG.debug("Keep alive timer started with period " + period + " ms");
//		}else
//			LOG.debug("Keep alive timer not started");
	}
	
	
	//Connects to the device and updates the connectedProperty 
//	public void toggleConnect(boolean isKey, String netconfUrl) {//this is not synchronized in order to be able to close socket when read times out
//		try {
//			boolean isConnected = connectedProperty.getValue();
//			if(isConnected) {
//				yangClient.close();
//				Platform.runLater(() -> {
//					LOG.debug("Disconnected From Device " + netconfUrl);
//					connectedProperty.set(false);
//				});
//			}else {
//					String address = regexSrv.getAddress(netconfUrl);
//					int port = regexSrv.getPort(netconfUrl);
//					String username = regexSrv.getUsername(netconfUrl);
//					String keyFile = null;
//					String password = null;
//					if(isKey)
//						keyFile = regexSrv.getPassword(netconfUrl);
//					else
//						password = regexSrv.getPassword(netconfUrl);
//					yangClient.connect(address, username, password, keyFile, port);
//					Platform.runLater(() -> {
//						LOG.debug("Connected To Device " + netconfUrl);
//						connectedProperty.set(true);
//						
//						LOG.debug("Client Capabilities " + yangClient.getClientCapabilities());
//						LOG.debug("Server Capabilities " + yangClient.getServerCapabilities());
//					});
//			}
//		} catch (Exception e) {
//			Platform.runLater(() -> {
//				LOG.error("Error Connecting To Device " + netconfUrl, e);
//				connectedProperty.set(false);
//			});
//		}
//	}
	
	  
//	public synchronized String runQuery(String queryString, DeviceItem device, String queryName) {
//		String output = null;
//		try {
//			//validate mandatory rpc parameters
//			String operation = regexSrv.getOperType(queryString);
//			QueryTypeEnum operType = QueryTypeEnum.getInstance(operation);//??????
//			if(operType == null)
//				throw new Exception("rpc name cannot be found");
//			switch (operType)
//			{
//			      case GET:
//			    	  break;
//			      case GET_CONFIG:
//			    	  regexSrv.getSource(queryString);
//			    	  break;
//			      case EDIT_CONFIG:
//			    	  regexSrv.getTarget(queryString);
//			    	  regexSrv.getConfig(queryString);
//			    	  break;
//			      case COPY_CONFIG:
//			    	  regexSrv.getSource(queryString);
//			    	  regexSrv.getTarget(queryString);
//			    	  break;
//			      case DELETE_CONFIG:
//			      case LOCK:
//			      case UNLOCK:
//			    	  regexSrv.getTarget(queryString);
//			    	  break;
//			      case CLOSE_SESSION:
//			    	  break;
//			      case KILL_SESSION:
//			    	  regexSrv.getSessionId(queryString);
//			    	  break;
//			      default:        
//			    	  break;
//			}
//			output = device.getNetconfConn().executeRPC(queryString);
//			output = xmlSrv.prettyPrint(output);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			//output = Throwables.getStackTraceAsString(e);
//			output = e.getMessage();
//		}
//		return output;
//	}
	
	public synchronized String runQuery(DeviceItem device, String queryName, String queryString) {
		LOG.debug("Running " + queryName);
		List<ErrorItem> errorList = new ArrayList<ErrorItem>();//they can come either from Schema errors or from Exceptions
		String output = null;
		try {
			output = device.getNetconfConn().executeRPC(queryString);
			output = xmlSrv.prettyPrint(output);
		}catch(Exception ex) {
			//ex.printStackTrace();
			LOG.error("", ex);
			errorList.clear();
			errorList.add(new ErrorItem(ErrorTypeEnum.QUERY, queryName, "0", "Check Error Window"));
		}
		Platform.runLater(() -> {
			if(errorList.size() > 0) {
				logService.updateQueryErrors(errorList, queryName);
				LOG.error(queryName + " returned errors");
				errorList.stream().forEach(x -> {
					LOG.error(x.toString());
				});
			}else {
				logService.clearQueryErrors(queryName);
				LOG.debug(queryName + " finished ok");
			}
		});
		return output;
	}
	


	@Override
	public synchronized void run() {//send keep alive
//		try {
//			if(connectedProperty.getValue()) 
//				yangClient.keepAlive();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
