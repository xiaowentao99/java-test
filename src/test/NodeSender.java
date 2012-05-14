package test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.nymtek.util.Tools;


public class NodeSender extends Thread{
	static Logger logger = Logger.getLogger(NodeSender.class
			.getName());

	private int SERVER_PORT=30000;
	private int LOCAL_PORT =30001;
	
	private InetAddress RemoteAddress;
	private DatagramSocket socket;
	private DatagramPacket dataPacket = null;
	private byte[] data;
	private long T=30*1000l;
	private long startTime=0l;
	private int delay=0;
	private long severTime =0l;

	
	
	public NodeSender(long t) throws SocketException, UnknownHostException{
		socket = new DatagramSocket(LOCAL_PORT);
		socket.setReceiveBufferSize(2048);
		socket.setSoTimeout(20*1000);
		RemoteAddress = InetAddress.getLocalHost();
		
		data = new byte[1024];
		for(int i=0;i<1024;i++){
			data[i] = (byte) (i%255);
		}
		this.T = t;
	}
	
	protected void sendData(byte[] sendBuf) throws IOException {
		dataPacket = new DatagramPacket(sendBuf, sendBuf.length, RemoteAddress, SERVER_PORT);
		socket.send(dataPacket);
	}
	
	public void run(){
		logger.info("start node thread");

		int data_len = 1024;
		byte[] value = new byte[50];
		int id = 1;
		byte no=0;
		
		while(true){
			byte command[] = new byte[1024];
			command[0] = (byte)(0xFF);
			command[1] = (byte)(0xFF);
			int offset =0;
			try {
				dataPacket = new DatagramPacket(command, 0, 2, RemoteAddress, SERVER_PORT);
				socket.send(dataPacket);
				
				socket.setSoTimeout(3*1000);
				dataPacket.setData(command);
				socket.receive(dataPacket);
				offset = dataPacket.getOffset();
				logger.info("receive data, lenght: "+dataPacket.getLength());
				if(command[offset++] == (byte)0xFF && command[offset++] == (byte)0xFF){
					this.severTime = Tools.byteToLong(command, offset, 8);
					offset += 8;
					this.delay = Tools.byteToInteger(command, offset, 2);
				}else{
					logger.info("get unknow data format");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			 
			if(this.delay != 0){
				logger.info("get respones, now delay :"+this.delay);
				break;
			}else{
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
			}
		}
		try {
			Thread.sleep(this.delay*1000);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		while(true){
			int offset=0,start=0;
			int len=45,packet_len=50;
			int value_len = 43;
			no =0;
			offset =0;
			len =45;
			logger.info("send data , id:"+id +" time:"+System.currentTimeMillis());
			value[offset++] = (byte) (id);
			value[offset++] = (byte) (id>>8);
			value[offset++] = (byte) (id>>16);
			value[offset++] = (byte) (id>>24);
			value[offset++] = no;
			value[offset++] = (byte) value_len;
			value[offset++] = (byte) (value_len>>8);
			no++;
			id++;
			System.arraycopy(data, start, value, offset, value_len);
			start +=value_len;
			
			dataPacket = new DatagramPacket(value, 0, packet_len, RemoteAddress, SERVER_PORT);
			try {
				startTime = System.currentTimeMillis();
				socket.setSoTimeout(20*1000);
				socket.send(dataPacket);
				while(true){
					offset =4;
					value[offset++] = no;
					no++;
					if((start +value_len) > data_len){
						value_len = (data_len-start);
					}
					value[offset++] = (byte) value_len;
					value[offset++] = (byte) (value_len>>8);
					
					System.arraycopy(data, start, value, offset, value_len);
					offset += value_len;
					dataPacket.setData(value, 0, offset);
					socket.send(dataPacket);
					start+=value_len;
					if(start >= data_len){
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				socket.receive(dataPacket);
				logger.info("get ack message, lenght: "+dataPacket.getLength());
			} catch (Exception e1) {
				logger.info("can not get the ack message, time:"+System.currentTimeMillis());
				e1.printStackTrace();
			}
			
			try {
				logger.info("sleep time: "+(T-(System.currentTimeMillis() - startTime)));
				Thread.sleep(T-(System.currentTimeMillis() - startTime));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
}
