package listeners;

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
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

import logic.Message;
import service.MDBackup;
import service.MDRestore;
import service.MessageControl;
import service.ServerTCP;

public class MessageControlListener implements Runnable {
	
		private static String INET_ADDRESS; // = "224.0.0.3";
	    private static int PORT; //= 8888;
	    private static String MDR_IP; // = "224.0.0.3";
	    private static int MDR_PORT;
	    private static String MDB_IP; // = "224.0.0.3";
	    private static int MDB_PORT;
	    public byte[] buf = new byte[256];
	    
	    private MulticastSocket multiSocket;
	    private DatagramPacket msgPacket;
	    private InetAddress ipAddress;
	    private boolean received=false;
	    private ServerTCP server;
	    
		public MessageControlListener(String ip,int p,String ip1,int p1,String ip2,int p2,ServerTCP server) throws IOException {
			this.INET_ADDRESS=ip;
			this.PORT=p;
			
			this.server=server;
			
			this.MDR_IP=ip1;
	    	this.MDR_PORT=p1;
	    	
	    	this.MDB_IP=ip2;
	    	this.MDB_PORT=p2;
	    	
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
	    			
			        String message = new String(buf, 0, msgPacket.getLength());
			        
			        //Thread.sleep(1000);
			        
			        System.out.println("Listener received MC: " + message);
			        
			        handleMessage(message);
	    		}
	    		//multiSocket.close();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
	    	
		}

		private void handleMessage(String message) throws IOException, InterruptedException {
			//GETCHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
			String msgType = message.split(" ")[0];
			String version = message.split(" ")[1];
			String senderId = message.split(" ")[2];
			String fileId=null;
			String chunkNo=null;
			String name=null;
			
			switch (msgType) {
				case "GETCHUNK":
					
					if (Integer.parseInt(senderId) == server.PORT)
	  					break;
					
					chunkNo = message.split(" ")[4];
					fileId= message.split(" ")[3] ;
					
					Message m1=new Message(message, null);
					
					MDRestore r1=new MDRestore(m1,MDR_IP, MDR_PORT,server);
					
					new Thread(r1).start();
					
					break;
				case "STORED":
					
					if (Integer.parseInt(senderId) == server.PORT)
						break;
					
					chunkNo = message.split(" ")[4];
					fileId= message.split(" ")[3] ;
					
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
						}else server.db.incDegree(name);
					
					break;
				case "DELETE":
					
					if (Integer.parseInt(senderId) == server.PORT)
						break;
					
					String fileID= message.split(" ")[3] ;
					 
					int nChunks=getNumberParts(fileID) - 1;
					
					System.out.println(nChunks);
					
					while(nChunks >= 0){
						
						File dir = new File("./Resources/Backup");
				    	dir.mkdirs();
				    	
				        File directory = new File(dir,fileID+" "+nChunks+".bak");
						
						directory.delete();

						//REMOVED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
						String header="REMOVED"+" "+version+" "+server.PORT+" "+fileID+" "+nChunks+" ";
						Message m2=new Message(header,null);
						
						MessageControl mc2=new MessageControl(m2,INET_ADDRESS,PORT);
						
						new Thread(mc2).start();
						
						nChunks--;
					}
					break;
				case "REMOVED":
					name=fileId+" "+chunkNo;
					
					//REMOVED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
					if (server.db.getH1().get(name) != null){
						server.db.decDegree(name);
						//if ((Integer) server.db.getH1().get(name) == 0)
							//server.db.removeValue(name);
					}
					else break;
					
					if ((Integer) server.db.getH1().get(name) < (Integer) server.db.getH1().get(fileId)){
						
						int degree=(Integer) server.db.getH1().get(fileId)- (Integer) server.db.getH1().get(name);
						
						String header = "PUTCHUNK" + " " + version + " " + server.PORT + " "
								+ fileId + " " + chunkNo + " " + degree + " ";

			    		byte[] chunk=getChunk(fileId,Integer.parseInt(chunkNo));
			    		
						Message m2 = new Message(header,chunk);
						
						Random r2=new Random();
			    		int delay = r2.nextInt((400 - 0) + 1) + 0;
				    	Thread.sleep(delay);
				    	
						MDBackup b1 = new MDBackup(m2,degree,this.MDB_IP,this.MDB_PORT,server);
						 System.out.println("Nova thread chunk REMOVE " +chunkNo);
						new Thread(b1).start();
					}
						
						
					break;
			default:
				break;
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
			
		private static int getNumberParts(String fileId) throws IOException {
			File directory = new File("./Resources/Backup");

			String[] matchingFiles = directory.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.matches(fileId+" \\d+\\.bak");
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
