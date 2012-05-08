package com.nymtek.po;

import java.sql.Timestamp;

public class PacketBase {
	private int id;
	private Timestamp version;
	
	private String packet_address;
	
	
	public String getPacket_address() {
		return packet_address;
	}
	public void setPacket_address(String packet_address) {
		this.packet_address = packet_address;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Timestamp getVersion() {
		return version;
	}
	public void setVersion(Timestamp version) {
		this.version = version;
	}
	
	

}
