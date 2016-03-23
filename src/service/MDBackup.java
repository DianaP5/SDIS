package service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import logic.Message;

public class MDBackup implements Runnable {
	
	final static int PORT=4444;
	private static Socket clientSocket;
	Message msg;
	Boolean done=false;
	private int attempts=5;
	
	public MDBackup(Message msg) throws IOException{
    	//PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
    	int version=Integer.parseInt(msg.getParameter(1));
    	String senderId=msg.getParameter(2);
    	String fileId=msg.getParameter(3);
    	int chunkNo=Integer.parseInt(msg.getParameter(4));
    	int replicationDegree=Integer.parseInt(msg.getParameter(4));
    	byte[] body=msg.getBody();
    	this.msg=msg;
    	
    	// creates socket
    	ServerSocket serverSocket = new ServerSocket(PORT);
    	
    	while (!done) {
    		serverSocket.setReceiveBufferSize(body.length);
    		clientSocket = serverSocket.accept();
    		new Thread(this).start();
    		
    		if (checkResponse() || attempts <= 0)
    			done=true;
    		else attempts--;
    	}
    	
	}

	private boolean checkResponse() {
		MessageControl mc1=new MessageControl();
		
		return false;
	}

	@Override
	public void run() {

		try {
			clientSocket =new Socket();
			
			PrintWriter streamOut = new PrintWriter(clientSocket.getOutputStream(),true);
			
			streamOut.println(msg);
			
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
