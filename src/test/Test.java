package test;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.PropertyConfigurator;

public class Test {

	public static void main(String args[]){
		PropertyConfigurator.configure("log4j.properties");
		
		long T=30000;
		try {
			NodeSender node = new NodeSender(T);
			node.start();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
