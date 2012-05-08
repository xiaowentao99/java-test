package com.nymtek.bu;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
 
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import com.nymtek.db.DBManager;
import com.nymtek.po.Packet;
import com.nymtek.po.PacketLost;
import com.nymtek.util.Tools;

public class PacketServer extends Thread {

	static Logger logger = Logger.getLogger(PacketServer.class.getName());

	private DatagramSocket serverSocket;
	private Map<String, Integer> ids = new HashMap<String, Integer>(10);;

	private List<byte[]> packets = new LinkedList<byte[]>();

	CommandServer cs ;
	
	private String Lock = "lock";
	private PacketParseHandle parseHandle = null;

	public PacketServer(int port) {
		super("PacketServerThread");
		 
		try {
			parseHandle = new PacketParseHandle();
			serverSocket = new DatagramSocket(port);
			cs = new CommandServer();

		} catch (SocketException e) {
			logger.error("Udp Socket listener fail at port[" + port + "]:"
					+ e.getMessage());
		}
	}

	@Override
	public void run() {
		logger.info("start Packet Server Thead");
		if (null == serverSocket) {
			logger.error("Udp Socket has not created null, UDP Server thread exit.");
			return;
		}

		if (null != parseHandle) {
			parseHandle.start();
		}
		cs.start();

		byte[] buffer = new byte[2048];
		DatagramPacket data = new DatagramPacket(buffer, buffer.length);
		while (true) {
			try {
				serverSocket.receive(data);
				String nodeIP =data.getAddress().getHostAddress();
				
				cs.addNode(nodeIP);
				
				byte[] rData = new byte[data.getLength()];
				System.arraycopy(data.getData(), 0, rData, 0, rData.length);
				synchronized (Lock) {
					// dataMap.put(nodeIP, rData);
					packets.add(rData);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
		
//		logger.info("receive : " );
		
//		if(offset  != data.length){
//			offset += 2;
//			Integer packet_no = Tools.byteToInteger(data, offset, 4);
//			if(packet_no != p.getPacket_no()){
//				logger.warn("get unknow format data");
//				return ;
//			}
//		}
		
		
		if(data[offset] == 'n'){
			DBManager.savePakcet(p);
			if(ids.containsKey(p.getPacket_lladdress()) &&  ids.get(p.getPacket_lladdress()) != (p.getPacket_no()-1) ){
				PacketLost pl = new PacketLost();
				Integer l_no = ids.get(p.getPacket_lladdress()); 
				pl.setLastNo(l_no.intValue());
				
				pl.setNewNo(p.getPacket_no());
				pl.setPacket_address(p.getPacket_address());
				DBManager.savePacketLost(pl);
				logger.warn("jump id last id:"+ids.get(p.getPacket_lladdress())+" \t new id: " +p.getPacket_no()+"\tll_address:"+p.getPacket_lladdress()+" time:"+System.currentTimeMillis());
			}
			ids.put(p.getPacket_lladdress(), p.getPacket_no());
		}else{
			logger.warn("get unknow format data");
		}
	}

	class PacketParseHandle extends Thread {

		public PacketParseHandle() {
			super("PacketParseHandle Thread");
		}

		public void run() {
			logger.info("start Packet ParseHandle Thread ");
			byte[] d = null;
			while (true) {
			
				synchronized (Lock) {
					if(PacketServer.this.packets.size() > 0)
						d = PacketServer.this.packets.remove(0);
				}

				if (d == null || d.length < 1) {
					try {
						Thread.sleep(1000);
						continue;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try{
					parsePacket(d);
				}catch(Exception e){
					e.printStackTrace();
				}
				d = null;
			}
		}

	}

}
