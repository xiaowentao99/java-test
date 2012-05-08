package com.nymtek.bu;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.nymtek.db.DBManager;
import com.nymtek.po.Packet;
import com.nymtek.po.PacketInfo;
import com.nymtek.po.PacketLost;
import com.nymtek.util.Tools;

public class PacketTcpServer extends Thread {
	static Logger logger = Logger.getLogger(PacketTcpServer.class.getName());
	int PORT= 30000;

	private ServerSocket ssocket=null;
	private ExecutorService executor;
	
	public PacketTcpServer(){
		super("PacketTcpServer-Thread");
	
	}

public void parsePacketInfo(byte[] data) {

		PacketInfo p = new PacketInfo();
		int offset = 0;

		p.setPacket_len(Tools.byteToInteger(data, offset, 2));
		offset += 2;

		p.setPacket_no(Tools.byteToInteger(data, offset, 4));
		offset += 4;

		p.setPacket_address(Tools.formatIp6Address(Tools.byteToHexString(data,
				offset, 16)));
		offset += 16;
		
		p.setPacket_channel(Tools.byteToInteger(data, offset, 2));
		offset +=2;
		
		p.setPacket_network_id(Tools.byteToInteger(data, offset, 2));
		offset += 2;

		p.setPacket_link_quality(Tools.byteToInteger(data, offset, 2));
		offset += 2;
		
		Integer rssi = Tools.byteToInteger(data, offset, 2);
		p.setPacket_rssi(rssi.shortValue());
		offset +=2;
		
		p.setPacket_timestamp(Tools.byteToInteger(data, offset, 2));
		offset +=2;
		
		p.setPacket_txpower(Tools.byteToInteger(data, offset, 2));
		offset += 2;
		
		p.setPacket_listen_time(Tools.byteToInteger(data, offset, 2));
		offset += 2;
		
		p.setPacket_transmit_time(Tools.byteToInteger(data, offset, 2));
		offset += 2;
		
		p.setPacket_max_transmission(Tools.byteToInteger(data, offset, 2));
		offset += 2;
		
		p.setPacket_mac_squno(Tools.byteToInteger(data, offset, 2));
		offset += 2;
		
		p.setPacket_mac_ack(Tools.byteToInteger(data, offset, 2));
		offset += 2;
 
		if (offset != data.length) {
			logger.warn("get unknow format data");
			return;
		}
		DBManager.savePacketInfo(p);
		data = null;
	}

public void parsePacket(byte[] data) {
		
		Packet p = new Packet();
		int offset = 0;

		p.setPacket_len(Tools.byteToInteger(data, offset, 2));
		offset += 2;

		p.setPacket_no(Tools.byteToInteger(data, offset, 4));
		offset += 4;

		p.setPacket_route(Tools.formatIp6Address(Tools.byteToHexString(data,
				offset, 16)));
		offset += 16;
		
		p.setPacket_lladdress(Tools.byteToHexString(data, offset, 8, 2, ":"));
		offset += 8;
		
		p.setPacket_temp(Tools.byteToFloat(data, offset));
		offset += 4;
	
		p.setPacket_vdd(Tools.byteToFloat(data, offset));
		offset += 4;
		
		
		if(data[offset] == 'n'){
			DBManager.savePakcet(p);
/*			if(ids.containsKey(p.getPacket_lladdress()) &&  ids.get(p.getPacket_lladdress()) != (p.getPacket_no()-1) ){
				PacketLost pl = new PacketLost();
				Integer l_no = ids.get(p.getPacket_lladdress()); 
				pl.setLastNo(l_no.intValue());
				
				pl.setNewNo(p.getPacket_no());
				pl.setPacket_address(p.getPacket_address());
				DBManager.savePacketLost(pl);
				logger.warn("jump id last id:"+ids.get(p.getPacket_lladdress())+" \t new id: " +p.getPacket_no()+"\tll_address:"+p.getPacket_lladdress()+" time:"+System.currentTimeMillis());
			}
			ids.put(p.getPacket_lladdress(), p.getPacket_no());
*/
		}else{
			logger.warn("get unknow format data");
		}
	}

	
	public void run(){
		try {
			ssocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(true){
			 
			try {
				logger.info("waitting for connecting....");
				Socket csocket = ssocket.accept();
				logger.info("receive client socekt :"+csocket.getRemoteSocketAddress().toString());
				new PacketTcpClient(csocket).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

}
