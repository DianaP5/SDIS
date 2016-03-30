package listeners;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import logic.Message;

public class MDRestoreListener implements Runnable {
	
	private String INET_ADDRESS; //= "224.0.0.4";
    private int PORT; // = 8887;
    byte[] buf = new byte[256];//(1000 * 64)+256];
   
    private MulticastSocket multiSocket;
    private DatagramPacket msgPacket;
    private InetAddress ipAddress;
    
	public MDRestoreListener(String ip1,int p1) throws IOException{
    	this.INET_ADDRESS=ip1;
    	this.PORT=p1;
    	
		ipAddress = InetAddress.getByName(INET_ADDRESS);
    	multiSocket = new MulticastSocket(PORT);
	}

	 @Override
		public void run() {
	    	try {
				multiSocket.joinGroup(ipAddress);

				while(true){
		        	
		        	msgPacket = new DatagramPacket(buf, buf.length);
	                multiSocket.receive(msgPacket);
	                String message = new String(buf, 0, msgPacket.getLength());
			        
			        //CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>
	                //String msgType=message.split(" ")[0];
			    	//String version=message.split(" ")[1];
			    	//String senderId=message.split(" ")[2];
			    	String fileId=message.split(" ")[3];
			    	String chunkNumber=message.split(" ")[4];
			    	String body=message.split(Message.CRLF+Message.CRLF)[1];
			    	
			    	//String b=new String(body.getBytes(),0,body.length());
			    	//String header=msgType+" "+version+" "+senderId+" "+fileId+" "+chunkNumber+" "+body+" ";
			    	
			        File f1=new File(System.getProperty("user.dir")+"\\Resources\\Restored\\tmp");
			        		
			        File newFile = new File(f1,fileId+" "+chunkNumber+".restored");
			        
			        System.out.println("Listener MDR UDP: "+ chunkNumber);
			        
			        try (FileOutputStream out = new FileOutputStream(newFile)) {
			        	out.write(body.getBytes(), 0, body.length());//tmp is chunk size
                	}
		        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	

		}

		public DatagramPacket getMsgPacket() {
			return msgPacket;
		}

		public void setMsgPacket(DatagramPacket msgPacket) {
			this.msgPacket = msgPacket;
		}
}
