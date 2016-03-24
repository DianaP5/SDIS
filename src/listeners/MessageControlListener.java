package listeners;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MessageControlListener implements Runnable {
	
		private final static String INET_ADDRESS = "224.0.0.3";
	    private final static int PORT = 8888;
	    public byte[] buf = new byte[64];
	    
	    private MulticastSocket multiSocket;
	    private DatagramPacket msgPacket;
	    private InetAddress ipAddress;
	    private boolean received=false;
	    
		public MessageControlListener() throws IOException {
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
		        
		        Thread.sleep(1000);
		        
		        System.out.println("Listener received MC: " + message);
			} catch (IOException | InterruptedException e) {
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

		public boolean getReceived() {
			return received;
		}

		public void setReceived(boolean received) {
			this.received = received;
		}
}
