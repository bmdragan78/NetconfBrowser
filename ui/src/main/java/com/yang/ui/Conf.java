package com.yang.ui;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.FileAppender;

import java.io.File;

import org.slf4j.LoggerFactory;



public class Conf {
	
	public static String  	SCHEMA_FOLDER;
	
	public static String 	TEMPLATE_PATH;
	
	public static String 	LOGFILE_PATH;
	
	public static String 	JDBC_URL;
	
	public static String 	JDBC_USER;
	
	public static String 	JDBC_PASSWORD;
	
	public static int 		JDBC_POOL_SIZE;
	
	
	public static void  init(String[] args){
		
		String userDir = System.getProperty("user.dir");
		//if arguments are present ignore application.conf file
		if(args.length > 0) {
			System.out.println("Application launched from " + userDir);
			System.out.println("Ignoring application.conf file");
			
			String sep = File.separator;
			
			SCHEMA_FOLDER = 		userDir + sep + ".." + sep + "yangrepo" + sep + "yang";
			TEMPLATE_PATH = 		userDir + sep + ".." + sep + "yangrepo" + sep + "template" + sep + "mymodule.yang";
			LOGFILE_PATH = 			userDir + sep + ".." + sep + "yangrepo" + sep + "log" + sep + "yang-ui.log";
			String dbUrl = 			userDir + sep + ".." + sep + "db" + sep + "uidata";
			String sqlScript =		userDir + sep + ".." + sep + "db" + sep + "create.sql";
			JDBC_URL = 				"jdbc:h2:file:" + dbUrl + ";FILE_LOCK=NO;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;INIT=create schema if not exists uidata\\;runscript from '" + sqlScript + "'";
			JDBC_USER = 			"sa";
			JDBC_PASSWORD = 		"";
			JDBC_POOL_SIZE = 		2;
		}else {
			System.out.println("Application launched from " + userDir);
			System.out.println("Using application.conf file");
			
			Config conf = ConfigFactory.load();
			Config yangdb = conf.getConfig("netconfbrowser");
			SCHEMA_FOLDER = 	yangdb.getString("schema.folder");
			TEMPLATE_PATH = 	yangdb.getString("template.path");
			LOGFILE_PATH = 		yangdb.getString("logfile.path");
			
			JDBC_URL = 			yangdb.getString("jdbc.url");
			JDBC_USER = 		yangdb.getString("jdbc.user");
			JDBC_PASSWORD = 	yangdb.getString("jdbc.password");
			JDBC_POOL_SIZE = 	yangdb.getInt("jdbc.pool.size");
		}
		configureLog();
	}
	
	
	private static void configureLog() {//replaces the logback.xml configuration
		LoggerContext logCtx = (LoggerContext) LoggerFactory.getILoggerFactory();

	    PatternLayoutEncoder logEncoder = new PatternLayoutEncoder();
	    logEncoder.setContext(logCtx);
	    logEncoder.setPattern("%d{HH:mm:ss.SSS} - [%F:%L] - %msg - 		[%thread] %n");
	    logEncoder.start();

	    FileAppender logFileAppender = new FileAppender();
	    logFileAppender.setContext(logCtx);
	    logFileAppender.setName("FILE");
	    logFileAppender.setEncoder(logEncoder);
	    logFileAppender.setAppend(false);
	    logFileAppender.setFile(LOGFILE_PATH);
	    logFileAppender.start();

	    Logger log = logCtx.getLogger("ROOT");
	    log.setAdditive(false);
	    log.setLevel(Level.DEBUG);
	    log.addAppender(logFileAppender);
	    log.detachAppender("console");
	    
	    Logger log1 = logCtx.getLogger("com.dt");
	    log1.setLevel(Level.DEBUG);
	    Logger log2 = logCtx.getLogger("org.opendaylight");
	    log2.setLevel(Level.DEBUG);
	    Logger log3 = logCtx.getLogger("org.jboss");
	    log3.setLevel(Level.ERROR);
	    Logger log4 = logCtx.getLogger("io.netty");
	    log4.setLevel(Level.ERROR);
	    Logger log5 = logCtx.getLogger("com.barchart");
	    log5.setLevel(Level.ERROR);
	}
}
