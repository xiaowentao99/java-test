package com.nymtek.util;

import org.apache.log4j.Logger;

public class Tools {
	static Logger logger = Logger.getLogger(Tools.class.getName());

	public static String byteToHexString(byte[] data, int offset, int len) {
		return byteToHexString(data, offset, len, 2, ":");
	}

	public static String byteToHexString(byte[] data, int offset, int len,
			int interval, String split) {

		String str = "0123456789abcdef";
		StringBuffer str_addr = new StringBuffer();
		for (int i = 0; i < len; i++) {
			byte t = data[offset + i];
			int b = t & 0x0f;
			t = (byte) (t >> 4);
			int a = t & 0x0f;
			str_addr.append(str.charAt(a));
			str_addr.append(str.charAt(b));
			if ((str_addr.length() + 1) % (2 * interval + 1) == 0
					&& (i + 1) < len) {
				str_addr.append(split);
			}
		}
		int start = str_addr.indexOf("0000:");
		int end = start;

		try {
			while (end != -1) {
				end += 5;
				String t = str_addr.substring(end, end + 5);
				if (!t.endsWith("0000:")) {
					str_addr.replace(start, end, split);
					end = -1;
					break;
				}
			}
		} catch (Exception e) {
			logger.error("String parse fail,use origin string return."
					+ e.getMessage());
		}

		return new String(str_addr);
	}

	public static Integer byteToInteger(byte[] data, int offset, int len) {
		int value = 0;
//	System.out.println("to Integer:"+data[offset]+" "+data[offset+1]+" "+data[offset+2]+" "+data[offset+3]);
//		value = data[offset];

//		value = value & 0x000000FF;

		for (int i = len-1; i >= 0; i--) {
			value = value << 8;
			value = value | (data[offset + i] & 0x000000FF);
		}
		return value;
	}

	public static int byteToInt(byte t) {
		int value = 0;
		value = value | t;
		value = value & 0x000000ff;
		return value;
	}

	public static String formatIp6Address(String address) {
		String result = "";

		String str[] = address.split(":");
		for (int i = str.length - 1; i >= 0; i--) {
			if (str[i] == null || str[i].equals("") || str[i].equals("0")) {
				break;
			}
			Integer t = Integer.parseInt(str[i], 16);
			result = ":" + Integer.toHexString(t) + result;
		}

		return "aaaa:" + result;
	}

	public static float byteToFloat(byte[] data, int offset) {
		Integer i = Tools.byteToInteger(data, offset, 4);
		return  Float.intBitsToFloat(i);
	///	return result.;
	}
	public static long byteToLong(byte[] data, int offset, int len){
		long value =0l;
		value = data[offset];
		value = value & 0x00000000000000FF;
		for (int i = 1; i < len; i++) {
			value = value << 8;
			value = value | (data[offset + i] & 0x00000000000000FF);
		}
		return value;
	}

	// public static String bytesToHex(byte[] data) {
	// StringBuffer buffer = new StringBuffer();
	// String hex = "0123456789ABCDEF";
	//
	// return null;
	// }

	public static void main(String[] args) {
		// String address = "0012:4b00:17a:fdc1";
		// System.out.print(Tools.formatIp6Address(address));
		byte[] d = { 0x41, 0x31, (byte) 0xc2, (byte) 0x8f };
		Float f = 11.11f;
		Integer j = 1093780111;
		System.out.println("int size:" + Integer.SIZE + "\t float size:"
				+ Float.SIZE + "\t Long size:" + Long.SIZE);
		System.out.println("float: " + f + "\t to int :"
				+ Float.floatToIntBits(f));

		System.out.println(Tools.byteToInteger(d, 0, 4));
		System.out.println(Tools.byteToFloat(d, 0));
		System.out.println(Integer.toHexString(j));
		System.out.println(Float.intBitsToFloat(j));
		byte[] h = new byte[8];
		long time = System.currentTimeMillis();
		for(int i=7;i>0;i--){
			h[i] = (byte) ((time >> (8*(7-i))) & 0x00000000000000FF);
		}
//		  h[7] = (byte) (time & 0x00000000000000FF);
//		  h[6] = (byte) ((time >> 8) & 0x00000000000000FF);
//		  h[5] = (byte) ((time >> 16) & 0x00000000000000FF);
//		  h[4] = (byte) ((time >> 24) & 0x00000000000000FF);
//		  h[3] = (byte) ((time >> 32) & 0x00000000000000FF);
//		  h[2] = (byte) ((time >> 40) & 0x00000000000000FF);
//		  h[1] = (byte) ((time >> 48) & 0x00000000000000FF);
//		  h[0] = (byte) ((time >> 56) & 0x00000000000000FF);

//		System.out.println(Integer.toHexString(a1) +" "+ Integer.toHexString(a2)
//				+" "+ Integer.toHexString(a3) +" "+ Integer.toHexString(a4)
//				+" "+ Integer.toHexString(a5) +" "+ Integer.toHexString(a6)
//				+" "+ Integer.toHexString(a7) +" "+ Integer.toHexString(a8));
 
		System.out.println(time+"   "+Tools.byteToLong(h, 0, 8));

	}

}
