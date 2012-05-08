package com.nymtek.bu;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.nymtek.db.DBManager;
import com.nymtek.po.PacketTimeout;
import com.nymtek.po.PacketTimestamp;
import com.nymtek.util.Tools;

public class CommandServer extends Thread {
	static Logger logger = Logger.getLogger(PacketServer.class.getName());

	private DatagramSocket socket;
	private DatagramPacket dataPacket = null;
	private int RemotePort;
	private InetAddress RemoteAddress;
	
	public Vector<String> new_nodes;
	private Vector<String> nodes;
	private String lock = "lock";
	long sleepTime = 60*1000;
	private long startStamp = 0l;
	private long endStamp = 0l;

	public CommandServer(){
		super("Command server thread");
		try {
			
			socket = new DatagramSocket(0);
			socket.setReceiveBufferSize(2048);
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
	
		nodes = new Vector<String>(10);
		new_nodes = new Vector<String>(10);
		this.RemotePort = 30001;
	}
	
	public void addNode(String node){
		synchronized(lock){
			if(!this.nodes.contains(node))
				this.nodes.add(node);
		}
	}
	
	public byte[] receiveData() throws IOException,SocketTimeoutException{

		byte[] buffer = new byte[1024];
		DatagramPacket revPacket = new DatagramPacket(buffer, 0, 1024, RemoteAddress, RemotePort);
		socket.setSoTimeout(1000*5);
		socket.receive(revPacket);
		this.endStamp = System.currentTimeMillis();
		
		return revPacket.getData();		
	}
	
	protected void sendData(byte[] sendBuf) throws IOException {
		dataPacket = new DatagramPacket(sendBuf, sendBuf.length, RemoteAddress, RemotePort);
		this.startStamp =  System.currentTimeMillis();
		socket.send(dataPacket);
	}
	
	private void sendTimestamp() throws IOException{
		byte data[]  = new byte[10];
		data[0]=0x02;
		long now = System.currentTimeMillis();
		for(int i=7;i>0;i--){
			data[i+1] = (byte) ((now >> (8*(7-i))) & 0x00000000000000FF);
		}
		this.sendData(data);
	}
	
	private boolean saveTimestamp(byte[] data, String ip){
		PacketTimestamp p = new PacketTimestamp();
		p.setPacket_address(Tools.formatIp6Address(ip));
//		long start = Tools.byteToLong(data, 1, 8);
//		long end = System.currentTimeMillis();
		p.setSend(new Timestamp(this.endStamp));
		p.setReceive(new Timestamp(this.startStamp));
		p.setCount(this.endStamp - this.startStamp);
		this.endStamp = 0l;
		this.startStamp =0l;
		return DBManager.savePacketTimestamp(p);
	}
	
	private boolean saveTimeout(String ip){
		PacketTimeout p = new PacketTimeout();
		p.setPacket_address(ip);
		p.setStartTime(new Timestamp(this.startStamp));
		return DBManager.savePacketTimeout(p);
	}
	
	public void run(){
		logger.info("start command server thread");
		
		while(true){
			synchronized(lock){
				//this.nodes.add("aaaa::0212:4b00:017a:ff8c");
				this.new_nodes.addAll(this.nodes);
				this.nodes.clear();
			}
			
			logger.info(" start send timestamp,  nodes count: "+this.new_nodes.size());
			byte data[];
			for(int i=0; i< this.new_nodes.size(); i++){
				String ip =  this.new_nodes.remove(0);
				 data = null;
				try {
					this.RemoteAddress = InetAddress.getByName(ip);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				try {
					this.sendTimestamp();
				} catch (IOException e) {
					logger.error("IOException @CommandServer @this.sendTimestamp ip("+ip+"): "+e.toString());
					e.printStackTrace();
					continue;
				}
				try {
					data = this.receiveData();
					
				} catch (SocketTimeoutException e) {
					logger.error("SocketTimeoutException @CommandServer @this.receiveData() ip("+ip+") :  "+e.toString());
					saveTimeout(ip);
					e.printStackTrace();
					continue;
				} catch (IOException e) {
					logger.error(" IOException @CommandServer @this.receiveData() ip("+ip+") : "+e.toString());
					e.printStackTrace();
					continue;
				}
				
				if(data != null  && data.length > 1){
					this.saveTimestamp(data, ip);
				}else{
					logger.error(" receive timestamp data null , ip :"+ip);
				}
			}
			
			this.new_nodes.clear();
			logger.info(" end send timestamp,  nodes count: "+this.new_nodes.size());
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				logger.error(" InterruptedException @CommandServer @run()");
				e.printStackTrace();
			}
		}
	}
	
}
