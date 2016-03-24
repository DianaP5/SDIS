package service;

import java.io.IOException;

import logic.Message;

public class testing {

		public static void main(String[] args) throws IOException{
			Message m1=new Message(null);
			
			String header="PUTCHUNK 1.2 1 2 3 2 \r\n \r\n";
			m1.setHeader(header);
			
			byte[] body={(byte) 01010101010};
			m1.setBody(body);
			
			MessageHandler h1=new MessageHandler(m1);
			
					//PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
		}
}
