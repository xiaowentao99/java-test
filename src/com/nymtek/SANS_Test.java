package com.nymtek;

import org.apache.log4j.PropertyConfigurator;

import com.nymtek.util.CommonConfig;
import com.nymtek.bu.*;

public class SANS_Test {

	public static void main(String args[]){
PropertyConfigurator.configure("log4j.properties");
		
		CommonConfig.instance();
		/*
		PropertyConfigurator.configure("log4j.properties");
		
		CommonConfig.instance();
		PacketServer ps = new PacketServer(30000);
		
		PacketInfoServer pis = new PacketInfoServer(40000);
		
		ps.start();
		pis.start();
		*/
		PacketTcpServer s = new PacketTcpServer();
		s.start();
	//	System.out.print(System.currentTimeMillis());
	}
	
}
