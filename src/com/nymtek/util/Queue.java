package com.nymtek.util;

import java.util.Vector;

public class Queue {
	private Vector<byte[]> value ;
	private int size;
	private int top;
	private int tail;
	
	public Queue(){
		value = new Vector<byte[]>(100);
		this.size = 100;
		this.top = -1;
		this.tail = 0;
	}
	
	public void  addItem(byte[] data){
		if(((this.tail+1)%this.size) == this.top){
			value.setSize(this.size*2);
			this.size = this.size*2;
		}
	}

}
