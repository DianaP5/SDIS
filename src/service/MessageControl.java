package service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import logic.Message;

public class MessageControl implements Runnable{
	
	private final static String INET_ADDRESS = "224.0.0.3";
    private final static int PORT = 8888;
    byte[] buf = new byte[256];
    
    private DatagramSocket socket;
    private DatagramPacket msgPacket;
    private InetAddress ipAddress;
    private Message msg;
    
	public MessageControl(Message msg) throws IOException {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}   
}
