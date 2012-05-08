package com.nymtek.po;

public class PacketLost extends PacketBase {
	
	private int lastNo;
	private int newNo;
	public int getLastNo() {
		return lastNo;
	}
	public void setLastNo(int lastNo) {
		this.lastNo = lastNo;
	}
	public int getNewNo() {
		return newNo;
	}
	public void setNewNo(int newNo) {
		this.newNo = newNo;
	}

}
