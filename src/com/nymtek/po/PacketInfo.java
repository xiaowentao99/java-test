package com.nymtek.po;

public class PacketInfo extends PacketBase {

	/*
	 4	packet_len			int(11)			否	无		  修改	  删除	 更多 
	 5	packet_no			int(11)			否	无		  修改	  删除	 更多 
	 6	packet_channel		int(11)			是	NULL		  修改	  删除	 更多 
	 7	packet_network_id	int(11)			是	NULL		  修改	  删除	 更多 
	 8	packet_link_quality	int(11)			是	NULL		  修改	  删除	 更多 
	 9	packet_rssi			int(11)			是	NULL		  修改	  删除	 更多 
	 10	packet_timestamp	int(11)			是	NULL		  修改	  删除	 更多 
	 11	packet_txpower		int(11)			是	NULL		  修改	  删除	 更多 
	 12	packet_listen_time	int(11)			是	NULL		  修改	  删除	 更多 
	 13	packet_transmit_time	int(11)			是	NULL		  修改	  删除	 更多 
	 14	packet_max_transmission	int(11)			是	NULL		  修改	  删除	 更多 
	 15	packet_mac_seqno		int(11)			是	NULL		  修改	  删除	 更多 
	 16	packet_mac_ack			int(11)			是	NULL
	 * 
	 * */
	
	private int packet_len;
	private int packet_no;
	private int packet_channel;
	private int packet_network_id;
	private int packet_link_quality;
	private int packet_rssi;
	private int packet_timestamp;
	private int packet_txpower;
	private int packet_listen_time;
	private int packet_transmit_time;
	private int packet_max_transmission;
	private int packet_mac_squno;
	private int packet_mac_ack;
	
	
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
	public int getPacket_channel() {
		return packet_channel;
	}
	public void setPacket_channel(int packet_channel) {
		this.packet_channel = packet_channel;
	}
	public int getPacket_network_id() {
		return packet_network_id;
	}
	public void setPacket_network_id(int packet_network_id) {
		this.packet_network_id = packet_network_id;
	}
	public int getPacket_link_quality() {
		return packet_link_quality;
	}
	public void setPacket_link_quality(int packet_link_quality) {
		this.packet_link_quality = packet_link_quality;
	}
	public int getPacket_rssi() {
		return packet_rssi;
	}
	public void setPacket_rssi(int packet_rssi) {
		this.packet_rssi = packet_rssi;
	}
	public int getPacket_timestamp() {
		return packet_timestamp;
	}
	public void setPacket_timestamp(int packet_timestamp) {
		this.packet_timestamp = packet_timestamp;
	}
	public int getPacket_txpower() {
		return packet_txpower;
	}
	public void setPacket_txpower(int packet_txpower) {
		this.packet_txpower = packet_txpower;
	}
	public int getPacket_listen_time() {
		return packet_listen_time;
	}
	public void setPacket_listen_time(int packet_listen_time) {
		this.packet_listen_time = packet_listen_time;
	}
	public int getPacket_transmit_time() {
		return packet_transmit_time;
	}
	public void setPacket_transmit_time(int packet_transmit_time) {
		this.packet_transmit_time = packet_transmit_time;
	}
	public int getPacket_max_transmission() {
		return packet_max_transmission;
	}
	public void setPacket_max_transmission(int packet_max_transmission) {
		this.packet_max_transmission = packet_max_transmission;
	}
	public int getPacket_mac_squno() {
		return packet_mac_squno;
	}
	public void setPacket_mac_squno(int packet_mac_squno) {
		this.packet_mac_squno = packet_mac_squno;
	}
	public int getPacket_mac_ack() {
		return packet_mac_ack;
	}
	public void setPacket_mac_ack(int packet_mac_ack) {
		this.packet_mac_ack = packet_mac_ack;
	}
	
}
