package com.nymtek.bu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.nymtek.db.DBManager;
import com.nymtek.po.Packet;
import com.nymtek.po.PacketInfo;
import com.nymtek.po.PacketLost;
import com.nymtek.util.Tools;

public class PacketTcpClient extends Thread {

	static Logger logger = Logger.getLogger(PacketTcpClient.class.getName());
	private Map<String, Integer> ids = new HashMap<String, Integer>(10);;

	private Socket client;
	private InputStream in;
	private OutputStream out;
	private byte[] buffer;

	public PacketTcpClient(Socket c) {
		// super("Client Thread");
		client = c;
		buffer = new byte[1024];

	}

	public void parsePacket(byte[] data, int len) {

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

		if (offset <= len && data[offset] == 'n') {
			DBManager.savePakcet(p);
			if (ids.containsKey(p.getPacket_lladdress())
					&& ids.get(p.getPacket_lladdress()) != (p.getPacket_no() - 1)) {
				PacketLost pl = new PacketLost();
				Integer l_no = ids.get(p.getPacket_lladdress());
				pl.setLastNo(l_no.intValue());

				pl.setNewNo(p.getPacket_no());
				pl.setPacket_address(p.getPacket_address());
				DBManager.savePacketLost(pl);
				logger.warn("jump id last id:"
						+ ids.get(p.getPacket_lladdress()) + " \t new id: "
						+ p.getPacket_no() + "\tll_address:"
						+ p.getPacket_lladdress() + " time:"
						+ System.currentTimeMillis());
			}
			ids.put(p.getPacket_lladdress(), p.getPacket_no());

		} else {
			logger.warn("get unknow format data");
		}
	}

	public void run() {

		try {
			in = client.getInputStream();
			out = client.getOutputStream();
			client.setKeepAlive(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int len = -1;
		while (true) {
			try {
				if (this.client.isConnected() && !this.client.isInputShutdown()) {
				//	this.client.setSoTimeout(5*1000);
					len = in.read(buffer);
					if (len != -1) {
						logger.info("receive data len: " + len + " data:"
								+ new String(buffer, 0, len));
						// // to save data
					//	this.parsePacket(buffer, len);
					} else {
						try {
							Thread.sleep(1 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} else {
					if (!this.client.isConnected()) {
						logger.info("Client Scoekt["
								+ this.client.getRemoteSocketAddress()
										.toString() + "] close");
					}
					if (this.client.isInputShutdown()) {
						logger.info("Client Scoekt["
								+ this.client.getRemoteSocketAddress()
										.toString() + "]  InputShutdown close");
					}
					logger.info("Client Scoekt["
							+ this.client.getRemoteSocketAddress().toString()
							+ "] Thread Quit");
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}

		}

		try {
			this.client.close();
			this.in.close();
			this.out.close();
			buffer = null;
			logger.info("Close all stream");
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Success Quit");
	}

}
