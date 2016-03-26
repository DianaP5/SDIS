package util;

public class SplitParameters {
	
	private String parameter;
	
	public SplitParameters(String msg, int index){
		String[] splited=msg.split(" ");
		
		if (index < splited.length)
			parameter=splited[index];
		else parameter=splited[splited.length-1];
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	
}
