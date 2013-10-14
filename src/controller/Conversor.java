package controller;

public class Conversor {

	public static byte[] hexStringToByteArray(String hexString) {
		int len = hexString.length();
	    byte[] bytes = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i+1), 16));
	    }
	    
	    return bytes;
	}
	
	public static String byteArrayToHexString(byte[] bytes) {
		StringBuffer buf = new StringBuffer();
	    for(int i = 0; i < bytes.length; i++) {
	       String hex = Integer.toHexString(0x0100 + (bytes[i] & 0x00FF)).substring(1);
	       buf.append((hex.length() < 2 ? "0" : "") + hex);
	    }
	    
	    return buf.toString();
	}
}
