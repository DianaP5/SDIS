package testApp;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTCP {

	String hostName;
	int PORT;//=4449;

	public ClientTCP (String msg) {
		
		try{
			//System.out.println(InetAddress.getLocalHost().getHostAddress());

			Socket socket = new Socket(msg.split(" ")[0].split(":")[0],Integer.parseInt(msg.split(" ")[0].split(":")[1]));
			
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

		    out.println(msg);
		    socket.close();
         
		} catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
}