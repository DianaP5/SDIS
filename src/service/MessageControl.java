package service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import logic.Message;

public class MessageControl implements Runnable{
	
	private final static String INET_ADDRESS = "224.0.0.3";
    private final static int PORT = 8888;
    
    private DatagramSocket serverSocket;
    private DatagramPacket msgPacket;
    private InetAddress ipAddress;
    private String msg;
    
    public void MessageHandler(Message msg) throws IOException {
    	String messageType=msg.getParameter(0);
    	
        switch (messageType) {
            case "PUTCHUNK":
                putChunkHandler(msg);
                break;
            case "GETCHUNK":
                System.out.println("1");
                break; 
            case "STORED":
                System.out.println("1");
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
                System.out.println("Error: Wrong MessageType argument:"+messageType);
                break;
        }
    }
    
    private void putChunkHandler(Message msg) throws IOException {
    	MDBackup b1=new MDBackup(msg);
    	new Thread(b1).start();
	}

	public void init() throws UnknownHostException, SocketException {
    	ipAddress = InetAddress.getByName(INET_ADDRESS);		
    	serverSocket = new DatagramSocket();
    }
	
	@Override
	public void run() {
		msgPacket = new DatagramPacket(msg.getBytes(),msg.getBytes().length, ipAddress, PORT);
    	
        try {
			serverSocket.send(msgPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        System.out.println("Server sent packet with msg: " + msg);
        
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
}
