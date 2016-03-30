package listeners;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import logic.Message;
import service.MDRestore;

public class MessageControlListener implements Runnable {
	
		private static String INET_ADDRESS; // = "224.0.0.3";
	    private static int PORT; //= 8888;
	    private static String MDR_IP; // = "224.0.0.3";
	    private static int MDR_PORT;
	    public byte[] buf = new byte[64];
	    
	    private MulticastSocket multiSocket;
	    private DatagramPacket msgPacket;
	    private InetAddress ipAddress;
	    private boolean received=false;
	    
		public MessageControlListener(String ip,int p,String ip1,int p1) throws IOException {
			this.INET_ADDRESS=ip;
			this.PORT=p;
			
			this.MDR_IP=ip1;
	    	this.MDR_PORT=p1;
	    	
	    	ipAddress = InetAddress.getByName(INET_ADDRESS);
	    	multiSocket = new MulticastSocket(PORT);
	    }
	
	    @Override
		public void run() {
	    	try {
				multiSocket.joinGroup(ipAddress);
		        setMsgPacket(new DatagramPacket(buf, buf.length));
		        multiSocket.receive(msgPacket);
		        
		        this.setReceived(true);
		        
		        String message = new String(buf, 0, buf.length);
		        
		        //Thread.sleep(1000);
		        
		        System.out.println("Listener received MC: " + message);
		        
		        handleMessage(message);
		        
		        multiSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
		}

		private void handleMessage(String message) throws IOException {
			//GETCHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
			String msgType = message.split(" ")[0];
			String version = message.split(" ")[1];
			String senderId = message.split(" ")[2];
			String fileId = message.split(" ")[3] ;
			int chunkNo = Integer.parseInt(message.split(" ")[4]);
		
			switch (msgType) {
				case "GETCHUNK":
					Message m1=new Message(message, null);
					
					MDRestore r1=new MDRestore(m1,INET_ADDRESS, PORT, MDR_IP, MDR_PORT);
					
					break;
			default:
				break;
			}
			
		}

		public DatagramPacket getMsgPacket() {
			return msgPacket;
		}

		public void setMsgPacket(DatagramPacket msgPacket) {
			this.msgPacket = msgPacket;
		}

		public boolean getReceived() {
			return received;
		}

		public void setReceived(boolean received) {
			this.received = received;
		}
}
