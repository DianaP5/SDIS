package testApp;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Start {

	private static String peerAp;
	private static String msgType;
	private static String op1;
	private static String op2;

	public static void main(String[] args) throws IOException,
			NoSuchAlgorithmException, InterruptedException {
		
		String header=null;
		
		 if (args.length < 3 || args.length > 4){
		  System.out.println("Error: Ivalid number of arguments [3,4] :"
				  +args.length);
		  return;
		 }else if (args.length == 3){
			 peerAp=args[0];
			 msgType=args[1];
			 op1=args[2];
			 header=peerAp+" "+msgType+" "+op1;
		}else{
			peerAp=args[0];
			 msgType=args[1];
			 op1=args[2];
			op2=args[3];
			header=peerAp+" "+msgType+" "+op1+" "+op2;
		}
		 
		//String header = "120.210.02:4449 BACKUP C:\\Users\\Ricardo\\Desktop\\cars.txt 1";
		//String header = "120.210.02:4449 RESTORE C:\\Users\\up201303933\\jogoPalitos.txt";
		//String header = "120.210.02:4449 DELETE C:\\Users\\Ricardo\\Desktop\\cars.txt";
		//String header = "120.210.02:4449 RECLAIM 12";
		
		@SuppressWarnings("unused")
		ClientTCP c1=new ClientTCP(header);
	}
}