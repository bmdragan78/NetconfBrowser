package com.yangui.main.dao;

import java.sql.Connection;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yangui.gui.Conf;



@Singleton
public class ConnectionPoolDb {
	
	private static final Logger LOG = LoggerFactory.getLogger(ConnectionPoolDb.class);
	
	private JdbcConnectionPool connectionPool;

	
	@PostConstruct
	private void init() throws Exception{
		LOG.debug("Connecting to H2 Database URL " + Conf.JDBC_URL + " User " + Conf.JDBC_USER + " Password " + Conf.JDBC_PASSWORD);
		
		Class.forName("org.h2.Driver");  
		connectionPool = JdbcConnectionPool.create(Conf.JDBC_URL, Conf.JDBC_USER, Conf.JDBC_PASSWORD);
		connectionPool.setMaxConnections(Conf.JDBC_POOL_SIZE);
		
		LOG.debug("Connected to H2 Database using a Connection Pool of size " + Conf.JDBC_POOL_SIZE);
	}
	
	
	public Connection getConnection() throws Exception {
		return connectionPool.getConnection();
	}
}
