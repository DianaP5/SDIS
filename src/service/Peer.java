package service;

import java.io.IOException;

public class Peer {
	
	public static void main(String[] args) throws IOException{
		
		ServerTCP s1=new ServerTCP();
		
		new Thread(s1).start();
	}
}
