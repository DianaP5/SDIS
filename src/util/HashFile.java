package util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashFile{
	
	private String file;
	private byte[] hash;
	
	public HashFile(String file) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		this.setFile(file);
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		
		md.update(file.getBytes("UTF-8")); // "UTF-16"
		
		this.setHash(md.digest());
	}
	
	public byte[] getHash() {
		return hash;
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
}
