package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

import listeners.MDBackupListener;
import listeners.MDRestoreListener;
import listeners.MessageControlListener;
import dataBase.Db;

public class ServerTCP implements Runnable {
	
	public int PORT;
	final static int BUFF_SIZE = 256;
	private static Socket clientSocket;
	private static String header;
	
	private static String MC_IP;//="224.0.0.6";
	private static Integer MC_PORT;//=8884;
	
	private static String MDB_IP;//="224.0.0.4";
	private static Integer MDB_PORT;//=8887;
					
	private static String MDR_IP;//="224.0.0.5";
	private static Integer MDR_PORT;//=8886;
	
	private MDBackupListener b1;
	private MDRestoreListener r1;
	private MessageControlListener c1;
	
	public MessageHandler handler;
	public Db db;
	ServerSocket serverSocket;
	public ArrayList<String> files;
	//public int degree=0;
	
	public ServerTCP(String header) throws IOException, SQLException {
		
		this.PORT=Integer.parseInt(header.split(" ")[0]);
		this.header=header;
		
		this.MC_IP = header.split(" ")[1].split(":")[0];
		this.MC_PORT = Integer.parseInt(header.split(" ")[1].split(":")[1]);
		
		this.MDB_IP = header.split(" ")[2].split(":")[0];
		this.MDB_PORT = Integer.parseInt(header.split(" ")[2].split(":")[1]);
		
		this.MDR_IP = header.split(" ")[3].split(":")[0];
		this.MDR_PORT = Integer.parseInt(header.split(" ")[3].split(":")[1]);
		
		this.files=new ArrayList<String>();
		
		startListeners();
		
		serverSocket = new ServerSocket(PORT);
		
		this.db=new Db();
	}

	@Override
	public void run() {

		try {
			while(true){
			serverSocket.setReceiveBufferSize(BUFF_SIZE);

			System.out.println("waiting...");
			
			clientSocket = serverSocket.accept();
			
			//while(true){
				BufferedReader in = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
				
				String message = in.readLine();
				
				//this.degree=Integer.parseInt(message.split(" ")[3]);
				
				//id=Integer.parseInt(message.split(" ")[0].split(":")[1]);
				
				MessageHandler h1=new MessageHandler(message,header,this);
	
				
			}
			//clientSocket.close();
		} catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void startListeners() throws IOException{

		setBackupListener(new MDBackupListener(MC_IP,MC_PORT,MDB_IP, MDB_PORT,this)); 
		setRestoreListener(new MDRestoreListener(MDR_IP, MDR_PORT,this));
		setMessageControlListener(new MessageControlListener(MC_IP, MC_PORT,MDR_IP,MDR_PORT,MDB_IP,MDB_PORT,this)); 
		
		new Thread(getBackupListener()).start();
		new Thread(getRestoreListener()).start();
		new Thread(getMessageControlListener()).start();
	}

	public MDBackupListener getBackupListener() {
		return b1;
	}

	public void setBackupListener(MDBackupListener b1) {
		this.b1 = b1;
	}

	public MDRestoreListener getRestoreListener() {
		return r1;
	}

	public void setRestoreListener(MDRestoreListener r1) {
		this.r1 = r1;
	}
	
	public MessageControlListener getMessageControlListener() {
		return c1;
	}

	public void setMessageControlListener(MessageControlListener c1) {
		this.c1 = c1;
	}

}
