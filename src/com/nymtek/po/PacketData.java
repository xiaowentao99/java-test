package com.nymtek.po;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.nymtek.bu.PacketServerTimeResponse;
import com.nymtek.util.Tools;

public class PacketData  extends PacketBase{
	
	static Logger logger = Logger.getLogger(PacketServerTimeResponse.class.getName());

	private Vector<byte[]> data;
	private byte[] data2[];
	private int len=0;
	private int children_len=0;
	
	public PacketData(String ip, byte[] value){
		this.setPacket_address(ip);
		int id = Tools.byteToInteger(value, 0, 4);
		logger.info("create new PacketData id:"+id);
		this.setId(id);
		data2 = new byte[50][];
		this.data = new Vector<byte[]>(30);
		this.data.setSize(50);
	}
	
	public void addData(byte[] value){
		int offset=0;
		int id = Tools.byteToInteger(value, offset, 4);
		offset += 4;
		int no = Tools.byteToInt(value[offset++]);
		int len = Tools.byteToInteger(value, offset, 2);
		offset +=2;
		if(this.getId() != id){
			logger.error("get wrong id packet. this id :"+this.getId() +" get id :"+ id);
			return;
		}
		if(len != (value.length - offset)){
			logger.error("packet lenght  wrong , need: " +len +" get :"+ (value.length -offset)+" no:"+no);
			return;
		}
		//this.data[no] = value;
		this.data.setElementAt(value, no);
	//	this.data.add(no, value);
	}
	
	public Vector<Integer> getLostPacketNo(){
		Vector<Integer> lostNo = new Vector<Integer>();
		for(int i=0; i>this.data.size(); i++){
			if(this.data.get(i) == null){
				lostNo.add(i);
			}
		}
		return lostNo;
	}
	
	public String toString(){
		return "[node] id:"+this.getId()+" len:"+this.children_len;
	}
	
	public byte[] getData(){
		return null;
	}

}
