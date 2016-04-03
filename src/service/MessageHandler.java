package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import listeners.MDRestoreListener;
import logic.Chunks;
import logic.FileSys;
import logic.Message;
import util.HashFile;


public class MessageHandler {
	// java TestApps <peer_ap> <sub_protocol> <opnd_1> <opnd_2>
	private String peerId;
	private String msgType;
	private String filePath;
	private String degree;
	private String version= "1.0";
	private String chunkNo;
	
	private static String MC_IP;//="224.0.0.3";
	private static Integer MC_PORT;//=8888;
	
	private static String MDB_IP;//="224.0.0.4";
	private static Integer MDB_PORT;//=8887;
					
	private static String MDR_IP;//="224.0.0.5";
	private static Integer MDR_PORT;//=8886;
	
	public String header;
	public ServerTCP server;
	
	public MessageHandler(String message, String header,ServerTCP server) throws IOException,
			NoSuchAlgorithmException, InterruptedException {

		this.peerId = message.split(" ")[0].split(":")[1];
		this.msgType = message.split(" ")[1];
		
		this.MC_IP = header.split(" ")[1].split(":")[0];
		this.MC_PORT = Integer.parseInt(header.split(" ")[1].split(":")[1]);
		
		this.MDB_IP = header.split(" ")[2].split(":")[0];
		this.MDB_PORT = Integer.parseInt(header.split(" ")[2].split(":")[1]);
		
		this.MDR_IP = header.split(" ")[3].split(":")[0];
		this.MDR_PORT = Integer.parseInt(header.split(" ")[3].split(":")[1]);
		
		this.server=server;
		
		switch (msgType) {
		case "BACKUP":
			
			this.filePath = message.split(" ")[2];
			System.out.println("ISTO: "+filePath);
			this.degree = message.split(" ")[3];
			
			putChunkHandler();
			break;
		case "RESTORE":
			
			this.filePath = message.split(" ")[2];
			//this.chunkNo = message.split(" ")[3];
			getChunkHandler();
			break;
		case "DELETE":
			this.filePath = message.split(" ")[2];
			deleteChunkHandler();
			break;
		case "RECLAIM":
			reclaimHandler();
			System.out.println("RECLAIM");
			break;
		default:
			System.out.println("Error: Wrong MessageType argument:" + msgType);
			break;
		}
	}

	private int reclaimHandler() {
		String s1 = null;
		
		File directory = new File(System.getProperty("user.dir")
				+ "\\Resources\\Backup");
		
		File[] listOfFiles = directory.listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        System.out.println(file.getName());
		    }
		}
		
		 
		 return 0;
	}

	private void deleteChunkHandler() throws IOException, InterruptedException {
		
		String fileID=null;
		
		if (server.files.size() == 0)
			return;
		
		for(int i=0; i < server.files.size();i++){
			if (server.files.get(i).split(" ")[0].equals(filePath))
				fileID=server.files.get(i).split(" ")[1];
		}
		
		//int tries=5;
		//System.out.println("ISTO: "+fileID);
		
		//while(tries > 0){
			
			String header = "DELETE" + " " + version + " " + peerId + " "
					+ fileID + " ";
			
			Message m1 = new Message(header,null);
			
			MessageControl mc1=new MessageControl(m1, MC_IP, MC_PORT);
			System.out.println("NOVA THREAD MC DELETE");
			
			new Thread(mc1).start();
			
			//tries--;
		//}
	}

	private void getChunkHandler() throws IOException, InterruptedException {
		//GETCHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
		
		File f1 = new File(filePath);
		long actualFileSize = f1.length();

		double times = actualFileSize % 10;
		int nChunks = (int) Math.floor(times);
		nChunks--;
		
		String fileID=null;
		
		for(int i=0; i < server.files.size();i++){
			System.out.println(server.files.get(i).split(" ")[0]+"  "+server.files.get(i).split(" ")[1]);
			System.out.println(filePath);
			if (server.files.get(i).split(" ")[0].equals(filePath))
				fileID=server.files.get(i).split(" ")[1];
		}
		
		while(nChunks >= 0){
			String header = "GETCHUNK" + " " + version + " " + peerId + " "
					+ fileID + " " + nChunks + " ";
			
			Message m1 = new Message(header,null);
			
			MessageControl mc1=new MessageControl(m1, MC_IP, MC_PORT);
			System.out.println("NOVA THREAD MC Restore");
			
			new Thread(mc1).start();
			Thread.sleep(1000);
			
			nChunks--;
		}
	}

	private void putChunkHandler() throws IOException, InterruptedException,
			NoSuchAlgorithmException {
		FileSys f1 = createFile(peerId, filePath, degree);
		
		splitFile(f1);
		System.out.println(filePath+" "+f1.getId());
		
		server.files.add(filePath+" "+f1.getId());
		
		int numberChunks = f1.getChunksList().size();
		int i = 0;
		
		server.db.insertValue(f1.getId(),Integer.parseInt(degree));

		while (i < numberChunks) {
			// PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo>
			// <ReplicationDeg> <CRLF><CRLF><Body>
			Chunks c1 = f1.getChunksList().get(i);
			String header = "PUTCHUNK" + " " + version + " " + peerId + " "
					+ f1.getId() + " " + c1.getNumber() + " " + degree + " ";
			
			Message m1 = new Message(header, c1.getContent());

			MDBackup b1 = new MDBackup(m1, Integer.parseInt(degree),this.MDB_IP,this.MDB_PORT,server);
			 System.out.println("Nova thread chunk " + c1.getNumber());
			new Thread(b1).start();
			
			//Thread.sleep(1000);

			i++;
		}
	}
	
	private static int getNumberParts(String fileId) throws IOException {
		File directory = new File(System.getProperty("user.dir")
				+ "\\Resources\\Backup");

		String[] matchingFiles = directory.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.matches(fileId+" \\d+\\.bak");
			}
		});

		return matchingFiles.length;
	}

	private static FileSys createFile(String peerId, String path, String degree)
			throws IOException, NoSuchAlgorithmException {

		Path filePath = Paths.get(path);

		String lastModified = Long.toString(new File(path).lastModified());
		String fileName = filePath.getFileName().toString();
		String fileCreationTime = Files
				.readAttributes(filePath, BasicFileAttributes.class)
				.creationTime().toString();

		HashFile h1 = new HashFile(fileName + " " + path + " "
				+ fileCreationTime + " " + lastModified);
		String hash = h1.getHash().toString();

		FileSys f1 = new FileSys(Integer.parseInt(peerId), hash,
				Integer.parseInt(degree));

		return f1;
	}
	
	public void splitFile(FileSys file) throws IOException {
		int counter = 0;
		int eachFileSize = 10;//1000 * 64; // 64Kb
		        
		try (BufferedReader bis = new BufferedReader(
		           new InputStreamReader(
		                      new FileInputStream(filePath), "UTF-8"))) {

			int tmp;
			File f1 = new File(filePath);
			long actualFileSize = f1.length();
			//System.out.println(actualFileSize);
			
			int nChunks = 0;
			boolean multiple=false;
			
			if (actualFileSize % eachFileSize == 0)
				multiple=true;
					
			if (actualFileSize < eachFileSize)
				eachFileSize = (int) actualFileSize;
			else {
				double times = actualFileSize % eachFileSize;
				nChunks = (int) Math.floor(times);
			}

			//byte[] buffer = new byte[eachFileSize];
	        
			char[] buffer=new char[eachFileSize];
			
		while ((tmp = bis.read(buffer)) > 0 ) {
				// File newFile = new File(f1.getParent(), "new" +
				// "."+counter+".txt");
				String s1 = new String(buffer, 0, buffer.length);

				if (nChunks > 0 && (counter + 1 >= nChunks)) {
					int lastChunkSize = (int) (actualFileSize - ((nChunks - 1) * eachFileSize));
					s1 = new String(buffer, 0, lastChunkSize);
				}

				Chunks c1 = new Chunks(file.getId(), counter, s1);
				file.addChunk(c1);
				counter++;
				//System.out.println("File part " + counter + ": " + s1);
				/*
				 * try (FileOutputStream out = new FileOutputStream(newFile)) {
				 * out.write(s1.getBytes(), 0,10);//tmp is chunk size }
				 */
			}
		if (multiple){
			Chunks c1 = new Chunks(file.getId(), counter," ");
			file.addChunk(c1);
		}
		
		}
	}

}
