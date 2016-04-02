package dataBase;

import java.sql.SQLException;
import java.util.HashMap;

public class Db {
	
	private HashMap h1;
	
	public Db() throws SQLException{
		setH1(new HashMap<String,Integer>());
	}
	
	public void insertValue(String name,int degree){
		getH1().put(name,degree);
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
	
	public HashMap getH1() {
		return h1;
	}

	public void setH1(HashMap h1) {
		this.h1 = h1;
	}
	
}