package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class SocketClientTest  extends Thread{
	private Socket socket;
	private String host ="localhost";
	private int port=30000;
	private SocketAddress address;
	private String name;
	OutputStream out;
	InputStream in;
	
	public SocketClientTest(String name){
		this.name = name;
		
	}
	
	private boolean connectSocket(){
		return false;
	}
	
	public void sendTest(String data){
		try {
			//OutputStream out = socket.getOutputStream();
			out.write(data.getBytes());
			
			out.flush();
			printf("write data: "+ data);
			//	out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void recevieTest(){
		try {
			if(this.socket.isInputShutdown()){
				printf("Input Shutdown");
				return ;
			}
		//	InputStream in = socket.getInputStream();
			byte[] buffer = new byte[1024];
			
			 	printf("receive data");
				int len = in.read(buffer);
				printf("recevie data :"+new String(buffer, 0,len));
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printf(Object data){
		System.out.println(data.toString());
	}
	
	public void run(){
		try {
			socket = new Socket(host, port);
		//	socket.setKeepAlive(true);
			in = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(true){
			if(!this.socket.isClosed()){
				this.sendTest("Hello , I am Client "+ name);
				this.recevieTest();
			}else{
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		printf("Quit Test");
	}
	
	public static void main(String args[]){
		int len = 100;
		for(int i=0; i< 1000; i++)
			new SocketClientTest("localhost "+i).start();
	}
}
