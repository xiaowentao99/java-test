package com.nymtek.bu;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.nymtek.db.DBManager;
import com.nymtek.po.Packet;
import com.nymtek.po.PacketData;
import com.nymtek.po.PacketLost;
import com.nymtek.util.CommonConfig;
import com.nymtek.util.Tools;

public class PacketServerTimeResponse extends Thread {

	static Logger logger = Logger.getLogger(PacketServerTimeResponse.class
			.getName());

	private int PORT = 30000;
	private int CLIENT_PORT=30001;

	private DatagramSocket serverSocket;
	private Map<String, Integer> ids = new HashMap<String, Integer>(10);

	private Map<String, PacketData> packetsData = new HashMap<String, PacketData>(
			10);;

	private Map<String, Long> packetTime = new HashMap<String, Long>(10);;

	private List<byte[]> packets = new LinkedList<byte[]>();

	private String Lock = "lock";
	private PacketParseHandle parseHandle = null;

	// private int delay=0;
	private long first = 0l;
	private int node_count = 0;
	private int period = 30;
	// private int nowCount =0;
	private boolean isSendRequest = false;
	private boolean isReceive = false;
	public  long lastReceive =0l;

	private Vector<String> nodes;

	public PacketServerTimeResponse(int port) {
		super("PacketServerThread");
		try {
			parseHandle = new PacketParseHandle();
			serverSocket = new DatagramSocket(port);

		} catch (SocketException e) {
			logger.error("Udp Socket listener fail at port[" + port + "]:"
					+ e.getMessage());
		}
		this.init();
	}

	private void init() {
		this.first = System.currentTimeMillis();
		logger.info("set first time:"+first);
		this.period = CommonConfig.getPeriod();
		this.node_count = CommonConfig.getNodeCount();
		this.nodes = new Vector<String>(this.node_count);
	}

	public int getNewDelay(String ip) {
		int index = this.nodes.indexOf(ip);
		if (index == -1) {
			index = this.nodes.size();
			this.nodes.add(ip);
		}
		int location = (int) (((this.packetTime.get(ip) - this.first) / 1000) % this.period);
		logger.info("orianal location:"+location);
		int newDelay = (index - location) + this.period;
		return newDelay;
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
		byte[] buffer = new byte[2048];
		DatagramPacket data = new DatagramPacket(buffer, buffer.length);
		while (true) {
			try {
				data.setData(buffer);
				serverSocket.receive(data);
				lastReceive =  System.currentTimeMillis();

				String nodeIP = data.getAddress().getHostAddress();
				this.packetTime.put(nodeIP, System.currentTimeMillis());
				if(data.getLength() == 2){
					int offset = data.getOffset();
					if(buffer[offset++] == (byte)0xFF && buffer[offset++] == (byte)0xFF){
						long now = System.currentTimeMillis();
						for(int i=7;i>0;i--){
							buffer[i+offset] = (byte) ((now >> (8*(7-i))) & 0x00000000000000FF);
						}
						offset +=8;
						
						int delay = this.getNewDelay(nodeIP);
						buffer[offset++]=(byte)delay;
						buffer[offset++]=(byte)(delay>>8);
						data.setData(buffer, 0, offset);
						serverSocket.send(data);
						logger.info("respones ok, delay:"+delay+" time:"+System.currentTimeMillis());
					}
					continue;
				}
				byte[] rData = new byte[data.getLength()];
				System.arraycopy(data.getData(), 0, rData, 0, rData.length);
				if (!this.packetsData.containsKey(nodeIP)) {
					PacketData p = new PacketData(nodeIP, rData);
					this.packetsData.put(nodeIP, p);
					
				}  
				
				this.packetsData.get(nodeIP).addData(rData);
				logger.info("receive data for No:"+rData[4]+" time:"+System.currentTimeMillis()+" location:"+location());
				synchronized (Lock) {
					packets.add(rData);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private long location(){
		return (System.currentTimeMillis()-first)%(period*1000);
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
 
		if (data[offset] == 'n') {
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

	class PacketParseHandle extends Thread {
		
		private long startTime=0l;
		private long okRequestInterval =1000l;
		private long lostRequestInterval =1000l;

		public PacketParseHandle() {
			super("PacketParseHandle Thread");
		}

		public boolean isRequestTime() {
			int location = (int) (((System.currentTimeMillis() - PacketServerTimeResponse.this.first) / 1000) % PacketServerTimeResponse.this.period);
			return location > PacketServerTimeResponse.this.nodes.size();
		}

		public void sendOkRequest(List<String> ips) {
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket();
			} catch (SocketException e) {
				e.printStackTrace();
			}
			byte[] buffer = new byte[8];
			int offset =0;
			for (String ip : ips) {
				try {
					offset =0;
					buffer[offset++] = -2;
					buffer[offset++] = -2;
					buffer[offset++] = -2;
					
					InetAddress RemoteAddress = InetAddress.getByName(ip);
					DatagramPacket dataPacket = new DatagramPacket(buffer, 0,
							offset, RemoteAddress, CLIENT_PORT);
					logger.info("set okRespones to client");
					socket.send(dataPacket);
					Thread.sleep(okRequestInterval);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void sendLostRequest(List<String> ips) {
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket();
				socket.setSoTimeout(5 * 1000);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			byte[] buffer = new byte[8];
			InetAddress RemoteAddress = null;
			DatagramPacket dataPacket = null;
			for (String ip : ips) {
				try {
					PacketData p = PacketServerTimeResponse.this.packetsData
							.get(ip);
					int id = p.getId();
					int offset = 0;
					buffer[offset++] = 0x7F;
					buffer[offset++] = (byte) (id >> 24);
					buffer[offset++] = (byte) ((id >> 16) & 0xFF);
					buffer[offset++] = (byte) ((id >> 8) & 0xFF);
					buffer[offset++] = (byte) (id & 0xFF);
					Vector<Integer> lostId = p.getLostPacketNo();
					for (Integer v : lostId) {
						buffer[offset++] = v.byteValue();
					}
					RemoteAddress = InetAddress.getByName(ip);
					dataPacket = new DatagramPacket(buffer, 0, buffer.length,
							RemoteAddress, CLIENT_PORT);

					socket.send(dataPacket);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				
				try {
					Thread.sleep(lostRequestInterval);
					if(PacketServerTimeResponse.this.packetsData
							.get(ip).getLostPacketNo().size() >0){
						Thread.sleep(lostRequestInterval);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private boolean checkIsReceive(){
			return (System.currentTimeMillis()-lastReceive) < 1000;
//			if((System.currentTimeMillis()-lastReveive) > 1000){
//				return true;
//			}
//			return false;
		}
		
		private synchronized void processData(){
			for (Iterator<String> iter = PacketServerTimeResponse.this.packetsData
					.keySet().iterator(); iter.hasNext();) {
				String key = iter.next();
				logger.info(packetsData.get(key).toString());
			}
		}
		
		private long location(){
			return (System.currentTimeMillis()-first)%(period*1000);
		}
		 
		public void run() {
			logger.info("start Packet ParseHandle Thread ");
			System.out.println(System.currentTimeMillis() - first);
		 	long sleeptime = ((System.currentTimeMillis() - first)%(period*1000));
			try {
				logger.info(" Packet ParseHandle Sleep for jump location :"+((node_count*1000)-sleeptime));
				Thread.sleep((node_count*1000)-sleeptime);  //// jump to the send request location in one cycle;
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			logger.info("now location:"+location());
			while (true) {
				startTime = System.currentTimeMillis();
				logger.info("[while] location:"+location()+" start: "+startTime+ " ----"+(lastReceive-startTime));

				if(checkIsReceive()){
					try {
						logger.info(" Packet ParseHandle , is  Receive time, sleep 1000ms");
						Thread.sleep(1000);
						continue;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				

				List<String> okRequest = new ArrayList<String>();
				List<String> lostRequest = new ArrayList<String>();

				synchronized (Lock) {
					for (Iterator<String> iter = PacketServerTimeResponse.this.packetsData
							.keySet().iterator(); iter.hasNext();) {
						String key = iter.next();
						if (PacketServerTimeResponse.this.packetsData.get(key)
								.getLostPacketNo().size() > 0) {
							lostRequest.add(key);
						} else {
							okRequest.add(key);
						}
					}
				}
				logger.info("get okRequest : "+okRequest.size()+"  get LostRequest: "+lostRequest.size());
				try {
					this.sendOkRequest(okRequest);
				} catch (Exception e) {
					logger.info("send ok reqest error :" + e.toString());
					e.printStackTrace();
				}

				try {
					this.sendLostRequest(lostRequest);
				} catch (Exception e) {
					logger.info("send lost reqest error :" + e.toString());
					e.printStackTrace();
				}
				
				okRequest.clear();
				synchronized (Lock) {
					 for( int i=0;i<lostRequest.size(); i++){
						 if (PacketServerTimeResponse.this.packetsData.get(lostRequest.get(i))
									.getLostPacketNo().size() > 0) {
							 okRequest.add(lostRequest.get(i));
							}
					 }
				}
				
				try {
					this.sendOkRequest(okRequest);
				} catch (Exception e) {
					logger.info("send lost reqest error :" + e.toString());
					e.printStackTrace();
				}
				
				/*
				 * SaveData();
				 * ProcessData();
				 * */
				processData();

				PacketServerTimeResponse.this.packetsData.clear();
				long waitTime = period*1000 - (System.currentTimeMillis()  - this.startTime);
				
				if(waitTime < 0){
					logger.info("Pares Thread run too time over period : "+(System.currentTimeMillis()  - this.startTime));
				}else{
					try {
						logger.info("parse data time(ms):"+(System.currentTimeMillis()  - this.startTime));
						Thread.sleep(period*1000 - (System.currentTimeMillis()  - this.startTime));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
}
