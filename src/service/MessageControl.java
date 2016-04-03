package service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import logic.Message;

public class MessageControl implements Runnable{
	
	private static String INET_ADDRESS; // = "224.0.0.3";
    private static int PORT; // = 8888;
    byte[] buf = new byte[256];
    
    private DatagramSocket socket;
    private DatagramPacket msgPacket;
    private InetAddress ipAddress;
    private Message msg;
    
	public MessageControl(Message msg,String ip,int port) throws IOException {
		this.INET_ADDRESS=ip;
		this.PORT=port;
		
    	ipAddress = InetAddress.getByName(INET_ADDRESS);		
    	socket = new DatagramSocket();
    	this.msg=msg;
    }
	
    @Override
	public void run() {
    	byte[] message=msg.getHeader().getBytes();
    	
		msgPacket = new DatagramPacket(message,message.length, ipAddress, PORT);
    	
  		try {
			socket.send(msgPacket);
			
			String s1 = new String(message, 0, message.length);
			
			System.out.println("Server sent MC: " + s1);
	        
			//Thread.sleep(1000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}   
}
