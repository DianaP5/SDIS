package listeners;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	

		}

		private void sendRespond(String msg) throws IOException {
			//check if has space
			//check degree
			//store
			Message m1=new Message(null);
			m1.setHeader(msg);
			
			//String msgType=m1.getParameter(0);
			String version=m1.getParameter(1);
	    	String senderId=m1.getParameter(2);
	    	String fileId=m1.getParameter(3);
	    	String chunkNo=m1.getParameter(4);
	    	//String replicationDegree=m1.getParameter(5);
	    	
	    	String header="STORED"+" "+version+" "+senderId+" "+fileId+" "+chunkNo+" "+Message.CRLF+Message.CRLF;
	    	m1.setHeader(header);
	    	
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
