package com.nymtek.po;

import java.sql.Timestamp;

public class PacketTimestamp extends PacketBase {

	/*
	 4 	send_timestamp	timestamp			否	0000-00-00 00:00:00		  修改	  删除	 更多 
	 5	receive_timestamp	timestamp			否	0000-00-00 00:00:00		  修改	  删除	 更多 
	 * */
	
	private Timestamp send;
	private Timestamp receive;
	private long count;
	
	
	
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public Timestamp getSend() {
		return send;
	}
	public void setSend(Timestamp send) {
		this.send = send;
	}
	public Timestamp getReceive() {
		return receive;
	}
	public void setReceive(Timestamp receive) {
		this.receive = receive;
	}
	
	
	
}
