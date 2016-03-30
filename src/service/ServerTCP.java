package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class ServerTCP implements Runnable {
	
	private int PORT;
	final static int BUFF_SIZE = 256;
	private static Socket clientSocket;
	private static String header;

	public ServerTCP(String header) throws IOException {
		this.PORT=Integer.parseInt(header.split(" ")[0]);
		this.header=header;
		
		// creates socket
		ServerSocket serverSocket = new ServerSocket(PORT);

		//while (true) {
		serverSocket.setReceiveBufferSize(BUFF_SIZE);
		// waits for clients
		System.out.println("waiting...");
		clientSocket = serverSocket.accept();
		//}
	}

	@Override
	public void run() {

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			String message = in.readLine();
			
			//id=Integer.parseInt(message.split(" ")[0].split(":")[1]);

			MessageHandler h1=new MessageHandler(message,header);

			clientSocket.close();
		} catch (IOException | NoSuchAlgorithmException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
