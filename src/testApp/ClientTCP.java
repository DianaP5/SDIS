package testApp;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTCP {

	String hostName;
	int PORT=4448;

	public ClientTCP (String msg) {
		
		try{
			System.out.println(InetAddress.getLocalHost().getHostAddress());
			/*"192.168.1.69"*/
			Socket socket = new Socket(InetAddress.getLocalHost(), PORT); //ip //InetAddress.getLocalHost().getHostName()
			
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

		
	

