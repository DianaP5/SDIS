package testApp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import logic.Chunks;
import logic.FileSys;
import logic.Message;
import service.MDBackup;
import service.MessageHandler;
import util.HashFile;

public class Start {
	
	private static String peerId="1"; //<IP address>:<port number>
	private static String msgType="PUTCHUNK";
	private static String op1="C:\\Users\\Ricardo\\Desktop\\7tcp.pdf";
	private static String op2="3";
	//private static ArrayList<FileSys> filesList;
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException{
		//Message m1=new Message(null);
		//java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>
		
	/*	System.out.println("Length: "+args.length);
		String header="PUTCHUNK 1.2 1 2 3 2 \r\n \r\n";
		m1.setHeader(header);
		byte[] body={(byte) 01010101010};
		m1.setBody(body);
		*/
		//String filePath=header.split(" ")[2];
		//MessageHandler h1=new MessageHandler(m1);
		
		/*if (args.length < 3 || args.length > 4)
			System.out.println("Error: Ivalid number of arguments [3,4] :"+args.length);
		else{
			peerId=args[0];
			msgType=args[1];
			op1=args[2];
			op2=args[3];
			
			//handleFile();
			handler(msgType);
		}*/
		handler(msgType);
		//handleFile();
		//splitFile(new File("C:\\Users\\Ricardo\\Desktop\\2\\7tcp.pdf"));
		//copyFile();
		//MessageHandler h1=new MessageHandler(m1);
		
				//PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
	}
	
	 public static void splitFile(FileSys file) throws IOException {
	        int counter = 0;
	        int eachFileSize = 1024 * 64; //64Kb
	        
	        byte[] buffer = new byte[eachFileSize];

	        try (BufferedInputStream bis = new BufferedInputStream(
	                new FileInputStream(op1))) {
	            //String name = "cena";
	            
	            int tmp = 0;
	            File f1=new File(op1);
	            
	            while ((tmp = bis.read(buffer)) > 0) {
	               // File newFile = new File(f1.getParent(), name + "."+counter+".txt");
	               
	                Chunks c1=new Chunks(file.getId(),counter,buffer);
	                file.addChunk(c1);
	                counter++;
	                System.out.println(buffer);
	                /*try (FileOutputStream out = new FileOutputStream(newFile)) {
	                    out.write(buffer, 0, tmp);//tmp is chunk size
	                }*/
	            }
	        }
	    }

	public static void copyFile(String source,String destination) throws IOException{
		
		Path src=Paths.get(source);
		Path dest=Paths.get(destination);
		
		//src.getFileName();
		//System.out.println(s.getFileName());
		//relative
		//Path src=Paths.get(System.getProperty("user.dir")+"\\Files\\cars.txt");
	
		Files.copy(src,dest,StandardCopyOption.REPLACE_EXISTING);
		
	}
	
	public static void readFiles(){

		Charset charset = Charset.forName("UTF-8 ");//"ISO-8859-1");//"US-ASCII");
		//Path path=(Path) FileSystems.getFileSystem(URI.create(op1));
		Path path = Paths.get(op1);
		
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		        System.out.println(line);
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
	
	public static void handler(String msgType) throws IOException, NoSuchAlgorithmException, InterruptedException {

		switch (msgType) {
            case "PUTCHUNK":
            	FileSys f1=createFile();
            	splitFile(f1);
            	
            	MessageHandler h1=new MessageHandler(null);
        		int numberChunks=f1.getChunksList().size();
        		
        		while(numberChunks > 0){
        			//PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
        			Chunks c1=f1.getChunksList().get(numberChunks-1);
        			String header=msgType+" "+"1.0"+" "+peerId+" "+f1.getId()+" "+c1.getNumber()+" "+op2+" ";
        			Message m1=new Message(header,c1.getContent());
        			
        			MDBackup b1=new MDBackup(m1);
                	new Thread(b1).start();
                	Thread.sleep(1000);
                	
                	numberChunks--;
        		}
            	
            	
            	//putChunkHandler(msg);
                break;
            case "GETCHUNK":
                System.out.println("1");
                break; 
            case "STORED":
               // storedChunkHandler(" !");
                break;
            case "CHUNK":
                System.out.println("1");
                break;
            case "DELETE":
                System.out.println("1");
                break;
            case "REMOVED":
                System.out.println("1");
                break;
            default:
            	System.out.println("Error: Wrong MessageType argument: "+msgType);
                break;
        }
    }

	private static FileSys createFile() throws IOException, NoSuchAlgorithmException {
		
		Path filePath = Paths.get(op1);
		
		String fileName=filePath.getFileName().toString();
		String fileCreationTime = Files.readAttributes(filePath, BasicFileAttributes.class).creationTime().toString();
		
		HashFile h1=new HashFile(fileName+" "+op1+" "+fileCreationTime);
		String hash=h1.getHash().toString();
		
		FileSys f1=new FileSys(Integer.parseInt(peerId),hash,Integer.parseInt(op2));
		
		return f1;
	}
	
}
