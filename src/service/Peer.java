package service;

import java.io.IOException;

public class Peer {
	
	private final static Integer id=4446;
			
	private final static String MC_IP="224.0.0.3";
	private final static Integer MC_PORT=8888;
	
	private final static String MDB_IP="224.0.0.4";
	private final static Integer MDB_PORT=8887;
					
	private final static String MDR_IP="224.0.0.5";
	private final static Integer MDR_PORT=8886;
							
	public static void main(String[] args) throws IOException{
		
		String header=id+" "+MC_IP+":"+MC_PORT+" "+MDB_IP+":"+MDB_PORT+" "+MDR_IP+":"+MDR_PORT+" ";
		
		ServerTCP s1=new ServerTCP(header);
		//serverID "224.0.0.3" 8888 "224.0.0.4" 8887 "224.0.0.5" 8886 
		//MC, MDB, MDR,

		new Thread(s1).start();
	}
}
