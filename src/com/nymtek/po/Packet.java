package com.nymtek.po;

public class Packet extends PacketBase {

	/*
	 * packet_len	int(11)			否	无		  修改	  删除	 更多 
	 5	packet_no	int(11)			否	无		  修改	  删除	 更多 
	 6	packet_route	varchar(40)	utf8_bin		否	无		  修改	  删除	 更多 
	 7	packet_lladdress	varchar(40)	utf8_bin		否	无		  修改	  删除	 更多 
	 8	packet_vdd	float			否	无		  修改	  删除	 更多 
	 9	packet_temp	float			否	无		  修改	  删除	 更多 
	 10	packet_other
	 * */
	
	private int packet_len;
	private int packet_no;
	private String packet_route;
	private String packet_lladdress;
	private float packet_vdd;
	private float packet_temp;
	private String packet_other;

	
	public int getPacket_len() {
		return packet_len;
	}
	public void setPacket_len(int packet_len) {
		this.packet_len = packet_len;
	}
	public int getPacket_no() {
		return packet_no;
	}
	public void setPacket_no(int packet_no) {
		this.packet_no = packet_no;
	}
	public String getPacket_route() {
		return packet_route;
	}
	public void setPacket_route(String packet_route) {
		this.packet_route = packet_route;
	}
	public String getPacket_lladdress() {
		return packet_lladdress;
	}
	public void setPacket_lladdress(String packet_lladdress) {
		
		this.packet_lladdress = packet_lladdress;
		String address = "aaaa::02"+this.packet_lladdress.substring(2);
		this.setPacket_address(address);
	}
	public float getPacket_vdd() {
		return packet_vdd;
	}
	public void setPacket_vdd(float packet_vdd) {
		this.packet_vdd = packet_vdd;
	}
	public float getPacket_temp() {
		return packet_temp;
	}
	public void setPacket_temp(float packet_temp) {
		this.packet_temp = packet_temp;
	}
	public String getPacket_other() {
		return packet_other;
	}
	public void setPacket_other(String packet_other) {
		this.packet_other = packet_other;
	}
	
}
