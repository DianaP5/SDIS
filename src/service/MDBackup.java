package service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import listeners.MessageControlListener;
import logic.Message;

public class MDBackup implements Runnable {
	
	private static String INET_ADDRESS;// = "224.0.0.4";
    private static int PORT;// = 8887;
    byte[] buf = new byte[256];//(1000 * 64)+256];
    
    private DatagramSocket socket;
    private DatagramPacket msgPacket;
    private InetAddress ipAddress;
    
    private Message msg;
    
	public Boolean done=false;
	private int attempts=5;
	public ServerTCP server;
	private int duration;
	
	public MDBackup(Message msg, int op2,String ip,int p,ServerTCP server) throws IOException{
    	this.INET_ADDRESS=ip;
    	this.PORT=p;
    	
		ipAddress = InetAddress.getByName(INET_ADDRESS);		
    	socket = new DatagramSocket();
    	this.msg=msg;
    	this.server=server;
    	this.duration=1000;
	}

	private boolean checkResponse(String name,String chunkNo) {
		
		if (server.db.getH1().get(name+" "+chunkNo) != null)
			if ((Integer) server.db.getH1().get(name+" "+chunkNo) == server.db.getH1().get(name))
				return true;
		
		return false;
	}

	 @Override
		public void run() {
	    	byte[] message=msg.getMessage().getBytes();
			setMsgPacket(new DatagramPacket(message,message.length, ipAddress, PORT));
	    	
	  		try {
	  			
		    	while (!done) {
		    		socket.send(getMsgPacket());
					
		    		int a=5-attempts;
					System.out.println("Server sent MDB UDP: "+a+" "+ msg.getHeader()+" "+msg.getBody());
					Thread.sleep(duration);
					
					duration=2*duration;
					
					String name=msg.getHeader().split(" ")[3];
					String chunkNo=msg.getHeader().split(" ")[4];
					
		    		if (checkResponse(name,chunkNo) || attempts <= 0){
		    			done=true;
		    		}else attempts--;
		    	}
		    	
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	private DatagramPacket getMsgPacket() {
		return msgPacket;
	}

	private void setMsgPacket(DatagramPacket msgPacket) {
		this.msgPacket = msgPacket;
	}   
}