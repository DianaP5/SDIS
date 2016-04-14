package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

import logic.Message;

public class MDRestore implements Runnable {
	
	private static String INET_ADDRESS;// = "224.0.0.4";
    private static int PORT;// = 8887;
    byte[] buf = new byte[(1000 * 64)+256];
    //private static String MC_IP;
    //private static int MC_PORT;
    
    private DatagramSocket socket;
    private DatagramPacket msgPacket;
    private InetAddress ipAddress;
    private ServerTCP server;
    
    private Message msg;
    
	public Boolean done=false;
	private int attempts=5;
 
	public MDRestore(Message msg, String ip,int p,ServerTCP server) throws IOException{

    	this.INET_ADDRESS=ip;
    	this.PORT=p;
    	this.server=server;
    	
		ipAddress = InetAddress.getByName(INET_ADDRESS);		
    	socket = new DatagramSocket();
    	this.msg=msg;    	
	}

	 @Override
		public void run() {
	    	String message=msg.getHeader();
	    	
	  		try {
	  			
		    	//while (!done) {
		    		//GETCHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
		    		//CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>
		    		
	  				
	  			
					String version = message.split(" ")[1];
					String senderId = message.split(" ")[2];
					String fileId = message.split(" ")[3] ;
					int chunkNo = Integer.parseInt(message.split(" ")[4]);
		    		
		    		byte[] chunk=getChunk(fileId,chunkNo);
		    		
		    		if (chunk != null){
		    		
		    		//System.out.println("TAMANHO     :"+chunk.length());
		    		String header="CHUNK"+" "+version+" "+server.PORT+" "+fileId+" "+chunkNo+" ";
		    		Message m1=new Message(header,chunk);
		    		
		    		byte[] msg=m1.getMessage();//.getBytes();
		    		
		    		msgPacket = new DatagramPacket(msg,msg.length, ipAddress, PORT);
		    		
		    		Random r1=new Random();
		    		int delay = r1.nextInt((400 - 0) + 1) + 0;
			    	Thread.sleep(delay);
			    	
			    	//if (!server.getRestoreListener().getReceived())
			    		socket.send(msgPacket);
			    	//else server.getRestoreListener().setReceived(false);
		    		
		    		int a=5-attempts;
					System.out.println("Server sent MDR UDP: "+a+" "+ m1.getHeader()+" "+m1.getBody());
	  		}
					//Thread.sleep(1000);
		    	//}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}

	private byte[] getChunk(String fileId, int chunkNo) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		byte[] s1 = null;
		
		File directory = new File("./Resources/Backup");

		String[] matchingFiles = directory.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.matches(fileId+" \\d+\\.bak");
			}
		});
		
		if (matchingFiles.length == 0)
			return null;
		
		File f1 = new File("./Resources/Backup/" + fileId+" "+ chunkNo + ".bak");
		
		byte[] buffer=new byte[1000*64];
        
        RandomAccessFile f = new RandomAccessFile(f1, "r");
        
				int tmp;
 			
 			  while ((tmp = f.read(buffer)) > 0 ) {
 				s1 = buffer;
 		}
		return s1;
	}   
}