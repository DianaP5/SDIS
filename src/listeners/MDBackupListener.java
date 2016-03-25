package listeners;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

import service.MessageControl;
import logic.Message;

public class MDBackupListener implements Runnable {
	
	private final static String INET_ADDRESS = "224.0.0.4";
    private final static int PORT = 8887;
    byte[] buf = new byte[256];
    
    private MulticastSocket multiSocket;
    private DatagramPacket msgPacket;
    private InetAddress ipAddress;
    
	public MDBackupListener() throws IOException{
    	ipAddress = InetAddress.getByName(INET_ADDRESS);
    	multiSocket = new MulticastSocket(PORT);
	}

	 @Override
		public void run() {
	    	try {
				multiSocket.joinGroup(ipAddress);
		        setMsgPacket(new DatagramPacket(buf, buf.length));
		        
		        while(true){
		        	msgPacket = new DatagramPacket(buf, buf.length);
	                multiSocket.receive(msgPacket);
	                
	                String message = new String(buf, 0, buf.length);
			        System.out.println("Listener MDB UDP: " + message);
			        
			        sendRespond(message);
		        }
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	

		}

		private void sendRespond(String msg) throws IOException, InterruptedException {
			//check if has space
			//check degree
			//store
			String[] splitedMsg=msg.split(" ");
			
			String version=splitedMsg[1];
	    	String senderId=splitedMsg[2];
	    	String fileId=splitedMsg[3];
	    	String chunkNo=splitedMsg[4];
	    	
	    	String header="STORED"+" "+version+" "+senderId+" "+fileId+" "+chunkNo+" "+Message.CRLF+Message.CRLF;
	    	Message m1=new Message(header,null);
	    	
	    	Random r1=new Random();
	    	
	    	int delay = r1.nextInt((400 - 0) + 1) + 0;
	    	
	    	Thread.sleep(delay);
	    	
			MessageControl mc1=new MessageControl(m1);
			
			new Thread(mc1).start();
		
	}

		public DatagramPacket getMsgPacket() {
			return msgPacket;
		}

		public void setMsgPacket(DatagramPacket msgPacket) {
			this.msgPacket = msgPacket;
		}
}
