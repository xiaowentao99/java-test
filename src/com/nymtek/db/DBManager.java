package com.nymtek.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.nymtek.po.*;

import org.apache.log4j.Logger;

import com.nymtek.util.CommonConfig;

public class DBManager {

	private static Connection Conn = null;

	static Logger logger = Logger.getLogger(DBManager.class.getName());

	public static Connection getConnection() {
		try {
			if (null == Conn || Conn.isClosed()) {
				createConnect();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Conn;
	}

	private static void createConnect() {

		String dbConf[] = CommonConfig.getDBConfig();
		// String driver = (null == dbConf[0] || dbConf[0].isEmpty() )?
		// dbConf[0] : "com.mysql.jdbc.Driver";
		String driver = dbConf[0]; // "com.mysql.jdbc.Driver";
		String url = dbConf[1]; // "jdbc:mysql://192.168.1.2:3306/nymtek_demo";
		String user = dbConf[2]; // "manager";
		String password = dbConf[3]; // "manager";
		try {
			Class.forName(driver);
			Conn = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			logger.error("Connected Database fail." + e.getMessage());
		} catch (SQLException e) {
			logger.error("Connected Database fail." + e.getMessage());
		}
	}

	
	public static boolean savePakcet(Packet p){

		Connection c = getConnection();
		if (null == c) {
			logger.error("save packet[" + p.getPacket_address()
					+ "] fail,connection is null");
			return false;
		}
		
		/*
	 	3  packet_address	varchar(40)	utf8_bin		否	无		  修改	  删除	 更多 
	 	4	packet_len	int(11)			否	无		  修改	  删除	 更多 
	 	5	packet_no	int(11)			否	无		  修改	  删除	 更多 
	 	6	packet_route	varchar(40)	utf8_bin		否	无		  修改	  删除	 更多 
	 	7	packet_lladdress	varchar(40)	utf8_bin		否	无		  修改	  删除	 更多 
	 	8	packet_vdd	float			否	无		  修改	  删除	 更多 
	 	9	packet_temp	float			否	无		  修改	  删除	 更多 
	 	10	packet_other	varchar(255)	utf8_bin		是	NULL
		* */
		
		String sql = "insert into packets set  packet_address=?, packet_len=?, " +
				"packet_no=?, packet_route=?, packet_lladdress=?, packet_vdd=?, " +
				"packet_temp=?, packet_other=?";
		try {
			PreparedStatement psm = c.prepareStatement(sql);
			
			psm.setString(1, p.getPacket_address());
			psm.setInt(2, p.getPacket_len());
			psm.setInt(3, p.getPacket_no());
			psm.setString(4, p.getPacket_route());
			psm.setString(5, p.getPacket_lladdress());
			psm.setFloat(6,  p.getPacket_vdd());
			psm.setFloat(7,  p.getPacket_temp());
			psm.setString(8, p.getPacket_other());
			
			boolean result = psm.execute();
			psm.close();
			return result;
			
		} catch (SQLException e) {
		 
			e.printStackTrace();
		}
		
		
		return true;
	}

	public static boolean savePacketInfo(PacketInfo p){

		Connection c = getConnection();
		if (null == c) {
			logger.error("save packet[" + p.getPacket_address()
					+ "] fail,connection is null");
			return false;
		}
		
		/*
		 * 
		 * packet_address	varchar(40)	utf8_bin		否	无		  修改	  删除	 更多 
	 	4	packet_len	int(11)			否	无		  修改	  删除	 更多 
	 	5	packet_no	int(11)			否	无		  修改	  删除	 更多 
	 	6	packet_channel	int(11)			是	NULL		  修改	  删除	 更多 
	 	7	packet_network_id	int(11)			是	NULL		  修改	  删除	 更多 
	 	8	packet_link_quality	int(11)			是	NULL		  修改	  删除	 更多 
	 	9	packet_rssi	int(11)			是	NULL		  修改	  删除	 更多 
	 	10	packet_timestamp	int(11)			是	NULL		  修改	  删除	 更多 
	 	11	packet_txpower	int(11)			是	NULL		  修改	  删除	 更多 
	 	12	packet_listen_time	int(11)			是	NULL		  修改	  删除	 更多 
	 	13	packet_transmit_time	int(11)			是	NULL		  修改	  删除	 更多 
	 	14	packet_max_transmission	int(11)			是	NULL		  修改	  删除	 更多 
	 	15	packet_mac_seqno	int(11)			是	NULL		  修改	  删除	 更多 
	 	16	packet_mac_ack
		 * 
		 * */
		
		String sql = "insert into packets_info set packet_address =?, packet_len=?, packet_no=?, " +
				"packet_channel=?, packet_network_id=?, packet_link_quality=?, packet_rssi=?, " +
				"packet_timestamp=?, packet_txpower=?, packet_listen_time=?, packet_transmit_time=?, " +
				"packet_max_transmission=?, packet_mac_seqno=?, packet_mac_ack=?";
		
	 
		try {
			PreparedStatement psm = c.prepareStatement(sql);
			
			psm.setString(1, p.getPacket_address());
			psm.setInt(2, p.getPacket_len());
			psm.setInt(3, p.getPacket_no());
			psm.setInt(4, p.getPacket_channel());
			psm.setInt(5, p.getPacket_network_id());
			psm.setInt(6, p.getPacket_link_quality());
			psm.setInt(7, p.getPacket_rssi());
			psm.setInt(8, p.getPacket_timestamp());
			psm.setInt(9, p.getPacket_txpower());
			psm.setInt(10, p.getPacket_listen_time());
			psm.setInt(11, p.getPacket_transmit_time());
			psm.setInt(12, p.getPacket_max_transmission());
			psm.setInt(13, p.getPacket_mac_squno());
			psm.setInt(14, p.getPacket_mac_ack());
			 
			boolean result = psm.execute();
			psm.close();
			return result;
			
		} catch (SQLException e) {
		 
			e.printStackTrace();
		}
		
		
		return true;
	}
	public static boolean savePacketTimestamp(PacketTimestamp p){

		Connection c = getConnection();
		if (null == c) {
			logger.error("save packet[" + p.getPacket_address()
					+ "] fail,connection is null");
			return false;
		}
		
		/*
		 * 
		 3   packet_address	varchar(40)	utf8_bin		否	无		  修改	  删除	 更多 
	 	 4	send_timestamp	timestamp			否	0000-00-00 00:00:00		  修改	  删除	 更多 
	 	 5	receive_timestamp	timestamp			否	0000-00-00 00:00:00
		 * */
		String sql = "insert into packet_timestamp  set packet_address=?, send_timestamp=?, receive_timestamp=?, count=?";
		try {
			PreparedStatement psm = c.prepareStatement(sql);
			
			psm.setString(1, p.getPacket_address());
			psm.setTimestamp(2, p.getSend());
			psm.setTimestamp(3, p.getReceive());
			psm.setLong(4, p.getCount());
			
			boolean result = psm.execute();
			psm.close();
			return result;
			
		} catch (SQLException e) {
		 
			e.printStackTrace();
		}
		return true;
	}
	
	
	public static boolean savePacketLost(PacketLost p){

		Connection c = getConnection();
		if (null == c) {
			logger.error("save packet[" + p.getPacket_address()
					+ "] fail,connection is null");
			return false;
		}
		
		/*
		 * 
		 1	id	int(11)			否	无	AUTO_INCREMENT	  修改	  删除	 更多 
		 2	version	timestamp			否	CURRENT_TIMESTAMP		  修改	  删除	 更多 
		 3	packet_lladdress	varchar(40)	utf8_bin		否	无		  修改	  删除	 更多 
		 4	last_no	int(11)			否	无		  修改	  删除	 更多 
		 5	new_no	int(11)			否	无		  修改	  删除	 更多 
		 6	count	int(11)			否	无		  修改	  删除	 更多 
		 * */
		String sql = "insert into packets_lost  set packet_lladdress=?, last_no=?, new_no=? , count=?";
		try {
			PreparedStatement psm = c.prepareStatement(sql);
			
			psm.setString(1, p.getPacket_address());
			psm.setInt(2, p.getLastNo());
			psm.setInt(3, p.getNewNo());
			psm.setInt(4, (p.getNewNo() -p.getLastNo()));
			boolean result = psm.execute();
			psm.close();
			return result;
			
		} catch (SQLException e) {
		 
			e.printStackTrace();
		}
		return true;
	}
	
	
	public static boolean savePacketTimeout(PacketTimeout p){

		Connection c = getConnection();
		if (null == c) {
			logger.error("save packet[" + p.getPacket_address()
					+ "] fail,connection is null");
			return false;
		}
		
		/*
		 * 
		 1	id	int(11)			否	无	AUTO_INCREMENT	  修改	  删除	 更多 
	 	 2	version	timestamp			否	CURRENT_TIMESTAMP		  修改	  删除	 更多 
	 	 3	packet_address	varchar(40)	utf8_bin		否	无		  修改	  删除	 更多 
	 	 4	starttime	timestamp			否	0000-00-00 00:00:00		  修改	  删除	 更多 		 
		 * */
		String sql = "insert into packet_timeout set packet_address=?, starttime=?";
		try {
			PreparedStatement psm = c.prepareStatement(sql);
			
			psm.setString(1, p.getPacket_address());
			psm.setTimestamp(2, p.getStartTime());
			boolean result = psm.execute();
			psm.close();
			return result;
			
		} catch (SQLException e) {
		 
			e.printStackTrace();
		}
		return true;
	}
	


}
