package logic;

import java.net.DatagramPacket;

public class Message {
	
	public static final String CR = "\r";
	public static final String LF = "\n";
	public static final String CRLF = CR + LF;
	
	private DatagramPacket packet;
	public String header;
	private byte[] body;
	
	public Message(DatagramPacket packet) {
		this.packet = packet;
		header = null;
		setBody(null);
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
	
	public String getParameter(int index){
		String[] splitedHeader=header.split(" ");
		
		if (index < splitedHeader.length)
			return splitedHeader[index];
		else return splitedHeader[splitedHeader.length-1];
	}
}