package service;

import java.io.IOException;

import logic.Message;

public class MessageHandler {

	public MessageHandler(Message msg) throws IOException {
    	String messageType=msg.getParameter(0);
    	
        switch (messageType) {
            case "PUTCHUNK":
                putChunkHandler(msg);
                break;
            case "GETCHUNK":
                System.out.println("1");
                break; 
            case "STORED":
                storedChunkHandler(msg);
                break;
            case "CHUNK":
                System.out.println("1");
                break;
            case "DELETE":
                System.out.println("1");
                break;
            case "REMOVED":
                System.out.println("1");
                break;
            default:
                System.out.println("Error: Wrong MessageType argument:"+messageType);
                break;
        }
    }
    
    private void storedChunkHandler(Message msg) throws IOException {
		MessageControl mc1=new MessageControl(msg);
		new Thread(mc1).start();
	}

	private void putChunkHandler(Message msg) throws IOException {
    	MDBackup b1=new MDBackup(msg,1);
    	new Thread(b1).start();
	}

}
