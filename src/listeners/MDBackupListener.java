package listeners;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

import logic.Message;
import service.MessageControl;
import service.ServerTCP;

public class MDBackupListener implements Runnable {
	
	private static String INET_ADDRESS; //= "224.0.0.4";
    private static int PORT; // = 8887;
    byte[] buf = new byte[256];//(1000 * 64)+256];
   
    private static String MC_IP;
    private static int MC_PORT;
    
    private MulticastSocket multiSocket;
    private DatagramPacket msgPacket;
    private InetAddress ipAddress;
    private int attempts=5;
    private ServerTCP server;
    
	public MDBackupListener(String ip,int p,ServerTCP server) throws IOException{
    	this.INET_ADDRESS=ip;
    	this.PORT=p;
    	this.server=server;
    	
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
			        
			        ////PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
	                String msgType=message.split(" ")[0];
			    	String version=message.split(" ")[1];
			    	String senderId=message.split(" ")[2];
			    	String fileId=message.split(" ")[3];
			    	String chunkNumber=message.split(" ")[4];
			    	String degree=message.split(" ")[5];
			    	String body=message.split(Message.CRLF+Message.CRLF)[1];
			    	//System.out.println("LENGTH: "+body.length());
			    	
			    	//String b=new String(body.getBytes(),0,body.length());
			    	//String header=msgType+" "+version+" "+senderId+" "+fileId+" "+chunkNumber+" "+body+" ";
			    	
			        File f1=new File(System.getProperty("user.dir")+"\\Resources\\Backup");
			        //Path src=Paths.get(System.getProperty("user.dir")+"\\Files");
			        		
			        File newFile = new File(f1,fileId+" "+chunkNumber+".bak");
			        
			        System.out.println("Listener MDB UDP: "+ chunkNumber);
			        
			    	if (server.db.getH1().get(fileId) != null)
			    		break;
			    	else server.db.insertValue(fileId+" "+chunkNumber, Integer.parseInt(degree));
			        
			        try (FileOutputStream out = new FileOutputStream(newFile)) {
			        	out.write(body.getBytes(), 0, body.length());//tmp is chunk size
                	}
			        //while(attempts > 100){
			        	sendRespond(message);
			        	//attempts--;
			        	//Thread.sleep(100);
			       // }
			        
			     //   attempts=0;
		        }
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void sendRespond(String msg) throws IOException, InterruptedException {
			String[] splitedMsg=msg.split(" ");
			
			String version=splitedMsg[1];
	    	String senderId=splitedMsg[2];
	    	String fileId=splitedMsg[3];
	    	String chunkNo=splitedMsg[4];
	    	
	    	//STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
	    	String header="STORED"+" "+version+" "+senderId+" "+fileId+" "+chunkNo+" ";//+Message.CRLF+Message.CRLF;
	    	Message m1=new Message(header,null);
	    	
	    	Random r1=new Random();
	    	
	    	int delay = r1.nextInt((400 - 0) + 1) + 0;
	    	
	    	Thread.sleep(delay);
	    	
			MessageControl mc1=new MessageControl(m1,MC_IP,MC_PORT);
			
			new Thread(mc1).start();
		
	}

		public DatagramPacket getMsgPacket() {
			return msgPacket;
		}

		public void setMsgPacket(DatagramPacket msgPacket) {
			this.msgPacket = msgPacket;
		}
}
