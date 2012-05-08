package com.nymtek.po;

import java.sql.Timestamp;

public class PacketTimeout extends PacketBase {
	
	private Timestamp startTime;

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	
	
	
}
