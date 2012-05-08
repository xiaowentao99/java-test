package com.nymtek.bu;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

import com.nymtek.db.DBManager;
import com.nymtek.po.PacketInfo;
import com.nymtek.util.Tools;

public class PacketInfoServer extends Thread {

	static Logger logger = Logger.getLogger(PacketInfoServer.class.getName());

	private DatagramSocket serverSocket;

	private List<byte[]> packets = new LinkedList<byte[]>();

	private String Lock = "lock";
	private PacketInfoParseHandle parseHandle = null;

	public PacketInfoServer(int port) {
		super("PacketInfoServerThread");

		try {
			parseHandle = new PacketInfoParseHandle();
			serverSocket = new DatagramSocket(port);

		} catch (SocketException e) {
			logger.error("Udp Socket listener fail at port[" + port + "]:"
					+ e.getMessage());
		}
	}

	@Override
	public void run() {
		logger.info("start PacketInfo Server Thead");
		if (null == serverSocket) {
			logger.error("Udp Socket has not created null, UDP Server thread exit.");
			return;
		}

		if (null != parseHandle) {
			parseHandle.start();
		}

		byte[] buffer = new byte[2048];
		DatagramPacket data = new DatagramPacket(buffer, buffer.length);
		while (true) {
			try {
				serverSocket.receive(data);
				// String nodeIP =
				// Tools.formatIp6Address(data.getAddress().getHostAddress());

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

	class PacketInfoParseHandle extends Thread {

		public PacketInfoParseHandle() {
			super("PacketInfo ParseHandle Thread");
		}

		public void run() {
			logger.info("start PacketInfo ParseHandle Thread ");
			byte[] d = null;
			while (true) {
				synchronized (Lock) {
					if(PacketInfoServer.this.packets.size() > 0)
						d = PacketInfoServer.this.packets.remove(PacketInfoServer.this.packets.size()-1);
					}
				if (d == null || d.length < 1) {
					try {
						Thread.sleep(1000);
						continue;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				parsePacketInfo(d);
				d = null;
			}
		}

	}

}
