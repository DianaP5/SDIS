package logic;

import java.util.ArrayList;

public class FileSys {
	@SuppressWarnings("unused")
	private int peer;
	private String id;
	private ArrayList<Chunks> chunksList;	
	@SuppressWarnings("unused")
	private int degree;
	
	public FileSys(int peer,String id,int degree){
		this.peer=peer;
		this.setId(id);
		this.degree=degree;
		chunksList=new ArrayList<Chunks>();
	}
	
	public void addChunk(Chunks chunk){
		chunksList.add(chunk);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public ArrayList<Chunks> getChunksList(){
		return chunksList;
	}
}
