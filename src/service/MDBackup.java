package service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import listeners.MessageControlListener;
import logic.Message;

public class MDBackup implements Runnable {
	
	private final static String INET_ADDRESS = "224.0.0.4";
    private final static int PORT = 8887;
    byte[] buf = new byte[256];
    
    private DatagramSocket socket;
    private DatagramPacket msgPacket;
    private InetAddress ipAddress;
    private Message msg;
    private MessageControlListener listener;
    
	public Boolean done=false;
	private int attempts=10;
	

    /*	//PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
    	int version=Integer.parseInt(msg.getParameter(1));
    	String senderId=msg.getParameter(2);
    	String fileId=msg.getParameter(3);
    	int chunkNo=Integer.parseInt(msg.getParameter(4));
    	int replicationDegree=Integer.parseInt(msg.getParameter(4));*/
	
	public MDBackup(Message msg) throws IOException{
    	ipAddress = InetAddress.getByName(INET_ADDRESS);		
    	socket = new DatagramSocket();
    	this.msg=msg;
	}

	private boolean checkResponse() throws IOException, InterruptedException {

		Thread.sleep(1000);

		if (!listener.getReceived())
    		return false;
    	
    	String message = new String(listener.buf, 0, listener.buf.length);
		
    	Message m1=new Message(null);
    	m1.setHeader(message);
    	
    	message=m1.getParameter(0);
    	
		if (message.toUpperCase().equals("STORED")){
			return true;
		}else return false;
    }

	 @Override
		public void run() {
	    	String header=msg.getHeader();
	    	String body=msg.getBody().toString();
	    	byte[] message=(header+body).getBytes();
	    	
			msgPacket = new DatagramPacket(message,message.length, ipAddress, PORT);
	    	
	  		try {
	  			
	  			listener=new MessageControlListener();

		    	Thread t1=new Thread(listener);
		    	t1.start();
		    	
		    	while (!done) {
		    		socket.send(msgPacket);
					
					System.out.println("Server sent MDB UDP: " + msg.header + msg.getBody());

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
