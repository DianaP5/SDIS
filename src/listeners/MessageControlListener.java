package listeners;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import logic.Message;
import service.MDRestore;
import service.ServerTCP;

public class MessageControlListener implements Runnable {
	
		private static String INET_ADDRESS; // = "224.0.0.3";
	    private static int PORT; //= 8888;
	    private static String MDR_IP; // = "224.0.0.3";
	    private static int MDR_PORT;
	    public byte[] buf = new byte[256];
	    
	    private MulticastSocket multiSocket;
	    private DatagramPacket msgPacket;
	    private InetAddress ipAddress;
	    private boolean received=false;
	    private ServerTCP server;
	    
		public MessageControlListener(String ip,int p,String ip1,int p1,ServerTCP server) throws IOException {
			this.INET_ADDRESS=ip;
			this.PORT=p;
			
			this.server=server;
			
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
		        
	    		while(true){
			        multiSocket.receive(msgPacket);
			        
			        //this.setReceived(true);
			        
			        String message = new String(buf, 0, msgPacket.getLength());
			        
			        //Thread.sleep(1000);
			        
			        System.out.println("Listener received MC: " + message);
			        
			        handleMessage(message);
	    		}
	    		//multiSocket.close();
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
			String chunkNo=null;
			String name=null;
			
			System.out.println("TYPE:  "+msgType);
			switch (msgType) {
				case "GETCHUNK":
					chunkNo = message.split(" ")[4];
					
					Message m1=new Message(message, null);
					
					MDRestore r1=new MDRestore(m1,MDR_IP, MDR_PORT,server);
					
					new Thread(r1).start();
					
					break;
				case "STORED":
					chunkNo = message.split(" ")[4];
					
					//String msg=server.handler.header;
					
					/*String version1=msg.split(" ")[1];
			    	String senderId1=msg.split(" ")[2];
			    	String fileId1=msg.split(" ")[3];
			    	String chunkNumber1=msg.split(" ")[4];
			    	String degree1=msg.split(" ")[5];*/
			    	
			    	//STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
					//if (msgType.toUpperCase().equals("STORED") && version.equals(version1)&& senderId.equals(senderId1) && 
						//	fileId.equals(fileId1) && chunkNo.equals(chunkNumber1)){
						name=fileId+" "+chunkNo;
						
						if (server.db.getH1().get(name) == null){
							server.db.insertValue(name,1);
							System.out.println("RECEIVED");
						}
					
					break;
				case "DELETE":
					//String msg=server.handler.header;
					
					int nChunks=getNumberParts(fileId) - 1;
					
					System.out.println("TAMANHO "+nChunks);
					
					while(nChunks >= 0){
						File directory = new File(System.getProperty("user.dir")
								+ "\\Resources\\Restored\\"+fileId+" "+nChunks+".bak");
						
						directory.delete();
						nChunks--;
					}
					break;
				case "REMOVED":
					name=fileId+" "+chunkNo;
					
					//REMOVED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
					if (server.db.getH1().get(name) != null)
						server.db.decDegree(name);
					
					if ((Integer) server.db.getH1().get(name) < (Integer) server.db.getH1().get(fileId))
						
					break;
			default:
				break;
			}
			
		}
		
		private static int getNumberParts(String fileId) throws IOException {
			File directory = new File(System.getProperty("user.dir")
					+ "\\Resources\\Restored");

			String[] matchingFiles = directory.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					String str = fileId.replace("[", "\\[");
					return name.matches(str+" \\d+\\.bak");
				}
			});

			return matchingFiles.length;
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
