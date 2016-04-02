package util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashFile{
	
	private String file;
	private String hash;
	
	public HashFile(String file) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		this.setFile(file);
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		
		md.update(file.getBytes("UTF-8")); // "UTF-16"
		
		byte[] digest=md.digest();
		
		//convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
         sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }
        hash=sb.toString();
	}
	
	public String getHash() {
		return hash;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
}
