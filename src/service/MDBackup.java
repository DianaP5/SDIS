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
    private static String MC_IP;
    private static int MC_PORT;
    
    private DatagramSocket socket;
    private DatagramPacket msgPacket;
    private InetAddress ipAddress;
    private MessageControlListener listener;
    
    private Message msg;
    
	public Boolean done=false;
	private int attempts=5;
	private int repDeg;
	private int replicated;
 
	public MDBackup(Message msg, int op2,String ip,int p,String ip1,int p1) throws IOException{
    	ipAddress = InetAddress.getByName(INET_ADDRESS);		
    	socket = new DatagramSocket();
    	this.msg=msg;
    	this.repDeg=op2;
    	this.MC_IP=ip;
    	this.MC_PORT=p;
    	this.INET_ADDRESS=ip1;
    	this.PORT=p1;
	}

	private boolean checkResponse() throws IOException, InterruptedException {

		if (!listener.getReceived())
    		return false;
    	
    	String message = new String(listener.buf, 0, listener.buf.length);
		
    	String msgType=message.split(" ")[0];
    	String version=message.split(" ")[1];
    	String senderId=message.split(" ")[2];
    	String fileId=message.split(" ")[3];
    	String chunkNumber=message.split(" ")[4];

    	String version1=msg.getHeader().split(" ")[1];
    	String senderId1=msg.getHeader().split(" ")[2];
    	String fileId1=msg.getHeader().split(" ")[3];
    	String chunkNumber1=msg.getHeader().split(" ")[4];
    	
    	//STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
		if (msgType.toUpperCase().equals("STORED") && version.equals(version1)&& senderId.equals(senderId1) && 
				fileId.equals(fileId1) && chunkNumber.equals(chunkNumber1)){
			replicated++;
			
			System.out.println("RECEIVED");
			
			if (repDeg == replicated)
				return true;
			else return false;
		}else return false;
    }

	 @Override
		public void run() {
	    	byte[] message=msg.getMessage().getBytes();
			msgPacket = new DatagramPacket(message,message.length, ipAddress, PORT);
	    	
	  		try {
	  			
	  			listener=new MessageControlListener(MC_IP,MC_PORT,"null",0);

		    	new Thread(listener).start();
		    	
		    	while (!done) {
		    		socket.send(msgPacket);
					
		    		int a=5-attempts;
					System.out.println("Server sent MDB UDP: "+a+" "+ msg.getHeader()+" "+msg.getBody());
					Thread.sleep(1000);
					
		    		if (checkResponse() || attempts <= 0){
		    			done=true;
		    		}else attempts--;
		    	}
		    	
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}   
}