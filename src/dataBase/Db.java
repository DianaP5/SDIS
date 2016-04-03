package dataBase;

import java.sql.SQLException;
import java.util.HashMap;

public class Db {
	
	private HashMap<String,Integer> h1;
	
	public Db() throws SQLException{
		setH1(new HashMap<String,Integer>());
	}
	
	public void insertValue(String name,int degree){
		getH1().put(name,degree);
	}
	
	public void removeValue(String name){
		getH1().remove(name);
	}
	
	public int incDegree(String name){
		int newValue=(Integer) getH1().get(name) + 1;
		getH1().replace(name, newValue);
		
		return newValue;
	}
	
	public int decDegree(String name){
		int newValue=(Integer) getH1().get(name) - 1;
		getH1().replace(name, newValue);
		
		return newValue;
	}
	
	public HashMap<String,Integer> getH1() {
		return h1;
	}

	public void setH1(HashMap<String,Integer> h1) {
		this.h1 = h1;
	}
	
}