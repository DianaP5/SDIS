package service;

import java.io.IOException;
import java.sql.SQLException;

public class Peer {
	
	private static Integer id;//=4449;
			
	private static String MC_IP;//="224.0.0.6";
	private static Integer MC_PORT;//=8885;
	
	private static String MDB_IP;//="224.0.0.4";
	private static Integer MDB_PORT;//=8887;
					
	private static String MDR_IP;//="224.0.0.2";
	private static Integer MDR_PORT;//=8884;
							
	public static void main(String[] args) throws IOException, SQLException{
		
		 if (args.length != 7){
			  System.out.println("Error: Ivalid number of arguments [7] :"
					  +args.length);
			  return;
			 }else{
				 id=Integer.parseInt(args[0]);
				 
				 MC_IP=args[1];
				 MC_PORT=Integer.parseInt(args[2]);
				 
				 MDB_IP=args[3];
				 MDB_PORT=Integer.parseInt(args[4]);
				 
				 MDR_IP=args[5];
				 MDR_PORT=Integer.parseInt(args[6]);
			 }
			
			String header=id+" "+MC_IP+":"+MC_PORT+" "+MDB_IP+":"+MDB_PORT+" "+MDR_IP+":"+MDR_PORT;
			 System.out.println(header);
		ServerTCP s1=new ServerTCP(header);
		
			new Thread(s1).start();
	}
}
