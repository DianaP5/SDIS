package testApp;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTCP {

	String hostName;
	int PORT=4444;

	public ClientTCP (String msg) {
		
		try{
			Socket socket = new Socket(InetAddress.getLocalHost(), PORT);
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
		    //write to server
		    out.println(msg);
		    
		    socket.close();
         
		} catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
}

		
	

