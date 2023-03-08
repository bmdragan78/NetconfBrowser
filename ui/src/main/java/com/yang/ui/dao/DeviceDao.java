package com.yang.ui.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yang.ui.services.model.DeviceItem;



@Singleton
public class DeviceDao {//create prepared statements only once
	
	private static final Logger LOG = LoggerFactory.getLogger(DeviceDao.class);
	
	@Inject
	private ConnectionPoolDb connectionPool;
	
	
	public void deleteDevice(DeviceItem device) throws Exception{
		Connection con = connectionPool.getConnection();  
		PreparedStatement stmt=con.prepareStatement("delete from DEVICES where id=?");
		stmt.setInt(1, device.getId());
		int i=stmt.executeUpdate();  
		stmt.close();
		con.close();  
	}


	public void updateDevice(DeviceItem device) throws Exception{
		
		Connection con = connectionPool.getConnection();  
		if(device.getId() == 0) {//add
			PreparedStatement stmt=con.prepareStatement("insert into DEVICES(name, host, port, username, type, password, privkey) values(?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, device.getName()); 
			stmt.setString(2, device.getHost());  
			stmt.setString(3, device.getPort());
			stmt.setString(4, device.getUsername());
			stmt.setBoolean(5, device.getType());
			stmt.setString(6, device.getPassword());
			stmt.setString(7, device.getKey());
			int i=stmt.executeUpdate();  
			int dbId = 0;
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				dbId=rs.getInt(1);
				device.setId(dbId);
			}
		}else{//update
			PreparedStatement stmt=con.prepareStatement("update DEVICES set name=?, host=?, port=?, username=?, type=?, password=?, privkey=? where id=?");
			stmt.setString(1, device.getName()); 
			stmt.setString(2, device.getHost());  
			stmt.setString(3, device.getPort());
			stmt.setString(4, device.getUsername());
			stmt.setBoolean(5, device.getType());
			stmt.setString(6, device.getPassword());
			stmt.setString(7, device.getKey());
			stmt.setInt(8, device.getId());
			int i=stmt.executeUpdate();  
			stmt.close();
		}
		con.close();  
	}
	
	
	public List<DeviceItem> getDevices() throws Exception{
		List<DeviceItem> deviceList = new ArrayList<DeviceItem>();
		Connection con = connectionPool.getConnection();  
		Statement stmt1=con.createStatement();  
		ResultSet rs=stmt1.executeQuery("select * from DEVICES");  
		while(rs.next()) { 
			DeviceItem deviceItem = new DeviceItem(rs.getInt(1), rs.getString(2), rs.getString(3), Integer.parseInt(rs.getString(4)), rs.getString(5), rs.getBoolean(6), rs.getString(7), rs.getString(8), false);
			deviceList.add(deviceItem);
		}
		con.close();   
		return deviceList;  
	}
}
