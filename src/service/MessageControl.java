package service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import logic.Message;

public class MessageControl /*implements Runnable*/{
	
	private final static String INET_ADDRESS = "224.0.0.3";
    private final static int PORT = 8888;
    byte[] buf = new byte[50];
    
    private DatagramSocket socket;
    private MulticastSocket multiSocket;
    private DatagramPacket msgPacket;
    private InetAddress ipAddress;
    private Message msg;
    
	public MessageControl(Message msg) throws IOException {
    	ipAddress = InetAddress.getByName(INET_ADDRESS);		
    	socket = new DatagramSocket();
    	multiSocket = new MulticastSocket(PORT);
    	this.msg=msg;
    	//new Thread(this).start();
    }
	
	public boolean send() throws IOException{
		DatagramPacket msgPacket = new DatagramPacket(msg.getBody(),
                msg.getBody().length, ipAddress, PORT);
        socket.send(msgPacket);
        
        System.out.println("Server sent MC UDP: " + msg.header);
        
        return true;
      //  Thread.sleep(1000);
	}
	
	public DatagramPacket receive() throws IOException{
		multiSocket.joinGroup(ipAddress);
		
        msgPacket = new DatagramPacket(buf, buf.length);
        multiSocket.receive(msgPacket);
            
        //String msg = new String(buf, 0, buf.length);
        System.out.println("Client received MC UDP: " + msg.getHeader());
        
        return msgPacket;
	}
	
	
   /* @Override
	public void run() {
    	byte[] body=msg.getBody();
    	
		msgPacket = new DatagramPacket(body,body.length, ipAddress, PORT);
    	
  		try {
			socket.send(msgPacket);
			
	        System.out.println("Server sent packet with msg: " + msg);
	        
			Thread.sleep(1000);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}   */
}
