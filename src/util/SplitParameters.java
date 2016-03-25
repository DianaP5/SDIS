package util;

public class SplitParameters {
	
	public String getParameter(String msg, int index){
		String[] splited=msg.split(" ");
		
		if (index < splited.length)
			return splited[index];
		else return splited[splited.length-1];
	}
	
}
