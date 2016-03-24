package service;

import java.io.IOException;

import listeners.MDBackupListener;
import logic.Message;

public class testing1 {

		public static void main(String[] args) throws IOException{
			//MessageControlListener h1=new MessageControlListener(null);
			MDBackupListener l1=new MDBackupListener();
			/*Message m1=new Message(null);
			String header="PUTCHUNK 1.2 1 2 3 2 \r\n \r\n";
			m1.setHeader(header);
			byte[] body={(byte) 000110110};
			m1.setBody(body);*/
			
			new Thread(l1).start();
			
			while(true){
				//if (l1.getMsgPacket() != null)
					Message m1=new Message(l1.getMsgPacket());
				
				//MessageHandler h1=new MessageHandler(m1);
			}
		}
}
